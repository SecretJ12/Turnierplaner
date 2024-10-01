package de.secretj12.turnierplaner.resources;


import de.secretj12.turnierplaner.db.entities.Match;
import de.secretj12.turnierplaner.db.entities.Set;
import de.secretj12.turnierplaner.db.entities.Tournament;
import de.secretj12.turnierplaner.db.entities.competition.Competition;
import de.secretj12.turnierplaner.db.entities.groups.Group;
import de.secretj12.turnierplaner.db.entities.knockout.NextMatch;
import de.secretj12.turnierplaner.db.repositories.CompetitionRepository;
import de.secretj12.turnierplaner.db.repositories.MatchRepository;
import de.secretj12.turnierplaner.db.repositories.SetRepository;
import de.secretj12.turnierplaner.db.repositories.TournamentRepository;
import de.secretj12.turnierplaner.enums.NumberSets;
import de.secretj12.turnierplaner.model.user.jUserSet;
import de.secretj12.turnierplaner.model.user.jUserTeamGroupResult;
import de.secretj12.turnierplaner.tools.GroupTools;
import io.quarkus.security.UnauthorizedException;
import io.smallrye.mutiny.tuples.Tuple2;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RolesAllowed("reporter")
@Path("/tournament/{tourId}/competition/{compId}/set/{matchId}")
public class SetResource {
    @Inject
    TournamentRepository tournaments;
    @Inject
    CompetitionRepository competitions;
    @Inject
    SetRepository setRepository;
    @Inject
    MatchRepository matchRepository;

    @Inject
    GroupTools groupTools;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Transactional
    public String updateMatches(@PathParam("tourId") String tourId, @PathParam("compId") String compId,
                                @PathParam("matchId") UUID matchId,
                                List<jUserSet> sets) {
        Tournament tournament = tournaments.getByName(tourId);
        Competition competition = competitions.getByName(tourId, compId);
        if (tournament == null)
            throw new NotFoundException("Tournament was not found");
        if (competition == null)
            throw new NotFoundException("Competition was not found");

        Instant beginGamePhase = tournament.getBeginGamePhase();
        if (beginGamePhase != null && beginGamePhase.isAfter(Instant.now()))
            throw new UnauthorizedException("Cannot update matches before game phase has begun");

        Match match = matchRepository.findById(matchId);
        if (match == null)
            throw new InternalServerErrorException("Could find match");

        for (jUserSet jSet : sets) {
            if (jSet.getIndex() > 4)
                throw new BadRequestException("Invalid set index");

            Set.SetKey setKey = new Set.SetKey();
            setKey.setMatch(match);
            setKey.setIndex(jSet.getIndex());
            Set set = setRepository.findById(setKey);

            if (set != null && jSet.getScoreA() == 0 && jSet.getScoreB() == 0) {
                setRepository.delete(set);
            } else {
                if (set == null) {
                    set = new Set();
                    set.setKey(setKey);
                }

                set.setScoreA(jSet.getScoreA());
                set.setScoreB(jSet.getScoreB());
                setRepository.persist(set);
            }
        }

        match.setFinished(true);
        match.setWinner(findWinner(match, competition.getNumberSets()));
        matchRepository.persist(match);

        adjustNext(match);
        return "Updated matches";
    }

    private void adjustNext(Match match) {
        if (!match.getFinished())
            return;

        if (match.getPreviousOfA() != null) {
            for (NextMatch nm : match.getPreviousOfA()) {
                Match nMatch = nm.getNextMatch();
                nMatch.setTeamA(match.getWinner() ^ nm.isWinner() ? match.getTeamB() : match.getTeamA());
                matchRepository.persist(nMatch);
                adjustNext(nMatch);
            }
        }
        if (match.getPreviousOfB() != null) {
            for (NextMatch nm : match.getPreviousOfB()) {
                Match nMatch = nm.getNextMatch();
                nMatch.setTeamB(match.getWinner() ^ nm.isWinner() ? match.getTeamB() : match.getTeamA());
                matchRepository.persist(nMatch);
                adjustNext(nMatch);
            }
        }
        if (match.getGroup() != null && groupTools.isFinished(match.getGroup().getGroup())) {
            Group group = match.getGroup().getGroup();
            List<jUserTeamGroupResult> results = groupTools.determineGropuResults(group);

            for (var fog : group.getFinalOfGroupA()) {
                Match fin = fog.getNextMatch();
                fin.setTeamA(results.get(fog.getPos()).getTeam());
                adjustNext(fin);
            }
            for (var fog : group.getFinalOfGroupB()) {
                Match fin = fog.getNextMatch();
                fin.setTeamB(results.get(fog.getPos()).getTeam());
                adjustNext(fin);
            }
        }
    }

    private boolean findWinner(Match match, NumberSets numberSets) {
        int dif = 0;
        for (byte i = 0; i < 2; i++) {
            Set.SetKey setKey = new Set.SetKey();
            setKey.setMatch(match);
            setKey.setIndex(i);
            Set set = setRepository.findById(setKey);

            if (set == null)
                throw new BadRequestException("Set " + i + " is null");
            isValidSet(set);
            dif += calcDif(set);
        }

        Set set3 = setRepository.findById(match, (byte) 2);
        switch (numberSets) {
            case THREE -> {
                if (Math.abs(dif) == 2)
                    isNull(set3);
                else {
                    isValidTiebreak(set3);
                    dif += calcDif(set3);
                }
            }
            case FIVE -> {
                isValidSet(set3);
                dif += calcDif(set3);
            }
        }
        Set set4 = setRepository.findById(match, (byte) 3);
        switch (numberSets) {
            case THREE -> isNull(set4);
            case FIVE -> {
                isValidSet(set4);
                dif += calcDif(set4);
            }
        }
        Set set5 = setRepository.findById(match, (byte) 4);
        switch (numberSets) {
            case THREE -> isNull(set5);
            case FIVE -> {
                isValidTiebreak(set5);
                dif += calcDif(set4);
            }
        }
        if (dif == 0)
            throw new InternalServerErrorException("Dif should 0");
        return dif > 0;
    }

    private int calcDif(Set set) {
        if (set.getScoreA() > set.getScoreB())
            return 1;
        else
            return -1;
    }

    private static final java.util.Set<Tuple2<Byte, Byte>> validResults = java.util.Set.of(
        Tuple2.of((byte) 6, (byte) 0),
        Tuple2.of((byte) 6, (byte) 1),
        Tuple2.of((byte) 6, (byte) 2),
        Tuple2.of((byte) 6, (byte) 3),
        Tuple2.of((byte) 6, (byte) 4),
        Tuple2.of((byte) 7, (byte) 5),
        Tuple2.of((byte) 7, (byte) 6),
        Tuple2.of((byte) 0, (byte) 6),
        Tuple2.of((byte) 1, (byte) 6),
        Tuple2.of((byte) 2, (byte) 6),
        Tuple2.of((byte) 3, (byte) 6),
        Tuple2.of((byte) 4, (byte) 6),
        Tuple2.of((byte) 5, (byte) 7),
        Tuple2.of((byte) 6, (byte) 7)
    );

    private void isValidSet(Set set) {
        notNull(set);
        byte scoreA = set.getScoreA();
        byte scoreB = set.getScoreB();
        if (!validResults.contains(Tuple2.of(scoreA, scoreB)))
            throw new BadRequestException("Invalid result");
    }

    private void isValidTiebreak(Set set) {
        notNull(set);
        int scoreA = set.getScoreA();
        int scoreB = set.getScoreB();
        boolean valid = (scoreA == 10 && scoreB <= 8)
            || (scoreB == 10 && scoreA <= 8)
            || (scoreA == scoreB + 2 && scoreB >= 9)
            || (scoreB == scoreA + 2 && scoreA >= 9);
        if (!valid)
            throw new BadRequestException("Invalid tiebreak");
    }

    private void notNull(Set set) {
        if (set == null)
            throw new BadRequestException("Set is not null");
    }

    private void isNull(Set set) {
        if (set != null)
            throw new BadRequestException("Set is null");
    }

}

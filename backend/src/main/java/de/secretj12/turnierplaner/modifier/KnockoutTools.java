package de.secretj12.turnierplaner.modifier;

import de.secretj12.turnierplaner.db.entities.Match;
import de.secretj12.turnierplaner.db.entities.competition.Competition;
import de.secretj12.turnierplaner.db.entities.competition.Team;
import de.secretj12.turnierplaner.db.entities.knockout.NextMatch;
import de.secretj12.turnierplaner.db.repositories.MatchRepository;
import de.secretj12.turnierplaner.db.repositories.NextMatchRepository;
import de.secretj12.turnierplaner.resources.jsonEntities.user.knockout.jUserKnockoutMatchResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.InternalServerErrorException;

import java.util.Arrays;
import java.util.UUID;

@ApplicationScoped
public class KnockoutTools {

    @Inject
    MatchRepository matches;
    @Inject
    NextMatchRepository nextMatches;

public Match updateKnockoutTree(Competition competition, jUserKnockoutMatchResponse tree) {
    if(tree == null){
        return null;
    }
    Match match = new Match();
    match.setBegin(tree.getBegin());
    match.setEnd(tree.getEnd());
    if(!tree.getTeamA_id().isEmpty()){
        UUID teamA_id = UUID.fromString(tree.getTeamA_id());
        match.setTeamA(Team.findById(teamA_id));
    }
    if(!tree.getTeamB_id().isEmpty()){
        UUID teamB_id = UUID.fromString(tree.getTeamB_id());
        match.setTeamB(Team.findById(teamB_id));
    }
    match.setFinished(tree.getFinished());
    match.setCompetition(competition);
    match.setWinner(tree.getWinner());


    NextMatch nMatch = new NextMatch();
    nMatch.setNextMatch(match);
    Match a = updateKnockoutTree(competition, tree.getPreviousA());
    nMatch.setPreviousA(a);
    Match b = updateKnockoutTree(competition, tree.getPreviousB());
    nMatch.setPreviousB(b);

    nextMatches.persist(nMatch);
    matches.persist(match);
    return match;
}

    /// TODO - to be deleted?
//    public void generateKnockoutTree(Competition competition, int size, Team[] teams) {
//        Match finale = matches.getFinal(competition);
//
//        if (finale == null) {
//            generateTree(competition, true, size, false, teams);
//        } else {
//            int curSize = 0;
//            Match curMatch = finale;
//            while (curMatch.getDependentOn() != null) {
//                curMatch = curMatch.getDependentOn().getPreviousA();
//                curSize++;
//            }
//
//            if (curSize != size) {
//                matches.deleteByComp(competition);
//
//                generateTree(competition, true, size, false, teams);
//            } else {
//                updateTree(size, false, finale, teams);
//            }
//        }
//    }
//
//    private Match generateTree(Competition competition, boolean finale, int size, boolean reversed, Team[] teams) {
//        var splits = split(size, teams);
//
//        Match match = new Match();
//        match.setCompetition(competition);
//        if (size == 0) {
//            match.setTeamA(teams[reversed ? 1 : 0]);
//            match.setTeamB(teams[reversed ? 0 : 1]);
//        }
//        matches.persist(match);
//
//        if (size > 0) {
//            Match matchA = generateTree(competition, false, size - 1, false, reversed ? splits.y : splits.x);
//            Match matchB = generateTree(competition, false, size - 1, true, reversed ? splits.x : splits.y);
//
//            NextMatch nMatch = new NextMatch();
//            nMatch.setPreviousA(matchA);
//            nMatch.setPreviousB(matchB);
//            nMatch.setNextMatch(match);
//
//            if (finale) {
//                Match thirdPlace = new Match();
//                thirdPlace.setCompetition(competition);
//                matches.persist(thirdPlace);
//
//                NextMatch nextMatchThird = new NextMatch();
//                nextMatchThird.setPreviousA(matchA);
//                nextMatchThird.setPreviousB(matchB);
//                nextMatchThird.setNextMatch(thirdPlace);
//                nextMatchThird.setWinner(false);
//                nextMatches.persist(nextMatchThird);
//            }
//
//            nextMatches.persist(nMatch);
//        }
//
//        return match;
//    }
//
//    private void updateTree(int size, boolean reversed, Match match, Team[] teams) {
//        var splits = split(size, teams);
//
//        if (size == 0) {
//            match.setTeamA(teams[reversed ? 1 : 0]);
//            match.setTeamB(teams[reversed ? 0 : 1]);
//            matches.persist(match);
//        }
//
//        if (size > 0) {
//            updateTree(size - 1, false, match.getDependentOn().getPreviousA(), reversed ? splits.y : splits.x);
//            updateTree(size - 1, true, match.getDependentOn().getPreviousB(), reversed ? splits.x : splits.y);
//        }
//    }

    public Team[] knockoutOrder(Competition competition) {
        Match finale = matches.getFinal(competition);

        return knockoutOrder(finale, false);
    }

    private Team[] knockoutOrder(Match match, boolean reversed) {
        Team[] A;
        Team[] B;
        if (match.getDependentOn() == null) {
            A = new Team[]{match.getTeamA()};
            B = new Team[]{match.getTeamB()};
        } else {
            A = knockoutOrder(match.getDependentOn().getPreviousA(), false);
            B = knockoutOrder(match.getDependentOn().getPreviousB(), true);
        }

        if (reversed) {
            return merge(B, A);
        } else {
            return merge(A, B);
        }
    }

    private Pair<Team[], Team[]> split(int size, Team[] teams) {
        if (teams.length < 2) {
            throw new InternalServerErrorException("Wrong size of teams list");
        }
        if (size == 0) {
            return new Pair<>(new Team[]{teams[0]}, new Team[]{teams[1]});
        }

        int count = (int) Math.pow(2, size + 1);
        var fst = split(size - 1, Arrays.copyOfRange(teams, 0, count / 2));
        var snd = split(size - 1, copyOfRangeReversed(teams, count / 2, count));

        return new Pair<>(concatRev(fst.x, snd.x), concatRev(fst.y, snd.y));
    }

    private Team[] merge(Team[] teamsA, Team[] teamsB) {
        if (teamsA.length == 1 && teamsB.length == 1) {
            return new Team[]{teamsA[0], teamsB[0]};
        }
        int lA = teamsA.length;
        int lB = teamsB.length;

        Team[] x = merge(Arrays.copyOfRange(teamsA, 0, lA / 2), Arrays.copyOfRange(teamsB, 0, lB / 2));
        Team[] y = merge(Arrays.copyOfRange(teamsA, lA / 2, lA), Arrays.copyOfRange(teamsB, lB / 2, lB));

        return concat(x, y);
    }

    public record Pair<X, Y>(X x, Y y) {
    }

    private static Team[] copyOfRangeReversed(Team[] array, int x, int y) {
        Team[] rev = new Team[y - x];
        for (int i = 0; i < y - x; i++)
            rev[i] = array[y - i - 1];
        return rev;
    }

    private static Team[] concatRev(Team[] a, Team[] b) {
        Team[] merge = Arrays.copyOf(a, a.length + b.length);
        for (int i = 0; i < b.length; i++)
            merge[i + a.length] = b[b.length - i - 1];
        return merge;
    }

    private static Team[] concat(Team[] a, Team[] b) {
        Team[] merge = Arrays.copyOf(a, a.length + b.length);
        System.arraycopy(b, 0, merge, a.length, b.length);
        return merge;
    }
}

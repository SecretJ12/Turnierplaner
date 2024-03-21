package de.secretj12.turnierplaner.modifier;

import de.secretj12.turnierplaner.db.entities.Match;
import de.secretj12.turnierplaner.db.entities.competition.Competition;
import de.secretj12.turnierplaner.db.entities.competition.Team;
import de.secretj12.turnierplaner.db.entities.knockout.NextMatch;
import de.secretj12.turnierplaner.db.repositories.MatchRepository;
import de.secretj12.turnierplaner.db.repositories.NextMatchRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.InternalServerErrorException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class KnockoutTools {

    @Inject
    MatchRepository matches;
    @Inject
    NextMatchRepository nextMatches;

    public void generateKnockoutTree(Competition competition, int size, List<Team> teams) {
        Match finale = matches.getFinal(competition);

        if (finale == null) {
            generateTree(competition, true, size, teams, false);
        } else {
            int curSize = 0;
            Match curMatch = finale;
            while (curMatch.getDependentOn() != null) {
                curMatch = curMatch.getDependentOn().getPreviousA();
                curSize++;
            }

            if (curSize != size) {
                matches.deleteByComp(competition);

                generateTree(competition, true, size, teams, false);
            } else {
                updateTree(size, teams, false, finale);
            }
        }
    }

    private Match generateTree(Competition competition, boolean finale, int size, List<Team> teams, boolean reversed) {
        var splits = split(size, teams);

        Match match = new Match();
        if (size == 0) {
            match.setTeamA(teams.get(reversed ? 1 : 0));
            match.setTeamB(teams.get(reversed ? 0 : 1));
        }
        match.setCompetition(competition);
        matches.persist(match);

        if (size > 0) {
            Match matchA = generateTree(competition, false, size - 1, reversed ? splits.y : splits.x, false);
            Match matchB = generateTree(competition, false, size - 1, reversed ? splits.x : splits.y, true);

            NextMatch nMatch = new NextMatch();
            nMatch.setPreviousA(matchA);
            nMatch.setPreviousB(matchB);
            nMatch.setNextMatch(match);

            if (finale) {
                Match thirdPlace = new Match();
                thirdPlace.setCompetition(competition);
                matches.persist(thirdPlace);

                NextMatch nextMatchThird = new NextMatch();
                nextMatchThird.setPreviousA(matchA);
                nextMatchThird.setPreviousB(matchB);
                nextMatchThird.setNextMatch(thirdPlace);
                nextMatchThird.setWinner(false);
                nextMatches.persist(nextMatchThird);
            }

            nextMatches.persist(nMatch);
        }

        return match;
    }

    private void updateTree(int size, List<Team> teams, boolean reversed, Match match) {
        var splits = split(size, teams);

        if (size == 0) {
            match.setTeamA(teams.get(reversed ? 1 : 0));
            match.setTeamB(teams.get(reversed ? 0 : 1));
            matches.persist(match);
        }

        if (size > 0) {
            updateTree(size - 1, reversed ? splits.y : splits.x, false, match.getDependentOn().getPreviousA());
            updateTree(size - 1, reversed ? splits.x : splits.y, true, match.getDependentOn().getPreviousB());
        }
    }

    public List<Team> knockoutOrder(Competition competition) {
        Match finale = matches.getFinal(competition);

        return knockoutOrder(finale, false).stream().filter(Objects::nonNull).toList();
    }

    private List<Team> knockoutOrder(Match match, boolean reversed) {
        List<Team> A;
        List<Team> B;
        if (match.getDependentOn() == null) {
            A = new ArrayList<>();
            A.add(match.getTeamA());
            B = new ArrayList<>();
            B.add(match.getTeamB());
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

    private Pair<List<Team>, List<Team>> split(int size, List<Team> teams) {
        if (teams.size() < 2) {
            throw new InternalServerErrorException("Wrong size of teams list");
        }
        if (size == 0) {
            List<Team> a = new ArrayList<>();
            a.add(teams.get(0));
            List<Team> b = new ArrayList<>();
            b.add(teams.get(1));
            return new Pair<>(a, b);
        }

        int count = (int) Math.pow(2, size + 1);
        var fst = split(size - 1, teams.subList(0, count / 2));
        var snd = split(size - 1, teams.subList(count / 2, count).reversed());

        fst.x.addAll(snd.x.reversed());
        fst.y.addAll(snd.y.reversed());

        return new Pair<>(fst.x, fst.y);
    }

    private List<Team> merge(List<Team> teamsA, List<Team> teamsB) {
        if (teamsA.size() == 1 && teamsB.size() == 1) {
            teamsA = new ArrayList<>(teamsA);
            teamsA.add(teamsB.getFirst());
            return teamsA;
        }
        int lA = teamsA.size();
        int lB = teamsB.size();

        List<Team> x = new ArrayList<>(merge(teamsA.subList(0, lA / 2), teamsB.subList(0, lB / 2)));
        List<Team> y = merge(teamsA.subList(lA / 2, lA), teamsB.subList(lB / 2, lB));

        x.addAll(y);
        return x;
    }

    public record Pair<X, Y>(X x, Y y) {
    }

}

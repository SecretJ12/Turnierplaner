package de.secretj12.turnierplaner.modifier;

import de.secretj12.turnierplaner.db.entities.Match;
import de.secretj12.turnierplaner.db.entities.competition.Competition;
import de.secretj12.turnierplaner.db.entities.competition.Team;
import de.secretj12.turnierplaner.db.entities.knockout.NextMatch;
import de.secretj12.turnierplaner.db.repositories.CompetitionRepository;
import de.secretj12.turnierplaner.db.repositories.MatchRepository;
import de.secretj12.turnierplaner.db.repositories.NextMatchRepository;
import de.secretj12.turnierplaner.db.repositories.TeamRepository;
import de.secretj12.turnierplaner.resources.jsonEntities.user.knockout.jUserKnockoutMatch;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class KnockoutTools {

    @Inject
    MatchRepository matches;
    @Inject
    NextMatchRepository nextMatches;
    @Inject
    TeamRepository teams;
    @Inject
    CompetitionRepository competitions;

    public Match updateKnockoutTree(Competition competition, jUserKnockoutMatch tree, Match match, int number) {
        if (tree == null)
            return null;

        if (tree.getPreviousA() == null || tree.getPreviousB() == null)
            deletePrevious(match);

        Match a, b;
        if (match.getDependentOn() != null) {
            a = match.getDependentOn().getPreviousA();
            updateKnockoutTree(competition, tree.getPreviousA(), a, number - 1);
            b = match.getDependentOn().getPreviousB();
            updateKnockoutTree(competition, tree.getPreviousB(), b, number - 1);
        } else if (tree.getPreviousA() != null && tree.getPreviousB() != null) {
            NextMatch nMatch = new NextMatch();
            a = updateKnockoutTree(competition, tree.getPreviousA(), new Match(), number - 1);
            b = updateKnockoutTree(competition, tree.getPreviousB(), new Match(), number - 1);
            nMatch.setPreviousA(a);
            nMatch.setPreviousB(b);
            nMatch.setNextMatch(match);
            nextMatches.persist(nMatch);
            match.setDependentOn(nMatch);
        } else
            a = b = null;

        if (tree.getPreviousA() == null) {
            match.setTeamA(tree.getTeamA() == null ? null : teams.findById(tree.getTeamA().getId()));
            match.setTeamB(tree.getTeamB() == null ? null : teams.findById(tree.getTeamB().getId()));
        } else if (tree.getPreviousA().getPreviousA() == null) {
            match.setTeamA(selectTeam(a));
            match.setTeamB(selectTeam(b));
        }
        match.setCompetition(competition);
        match.setFinished(false);
        match.setWinner(true);
        match.setNumber(number);
        matches.persist(match);

        return match;
    }

    private Team selectTeam(Match match) {
        if (match == null)
            return null;
        if (match.getTeamA() == null && match.getTeamB() != null)
            return match.getTeamB();
        if (match.getTeamB() == null && match.getTeamA() != null)
            return match.getTeamA();
        return null;
    }

    public void updateThirdPlace(Competition competition, Match finale, int total) {
        Match thirdPlace = competition.getThirdPlace();

        // cover deletion of third place
        if (competition.getTeams().size() <= 3) {
            if (thirdPlace != null) {
                matches.delete(thirdPlace);
            }
            return;
        }

        if (thirdPlace == null) {
            // create third place if didn't exist before
            thirdPlace = new Match();
            thirdPlace.setNumber(total - 1);
            thirdPlace.setCompetition(competition);
            NextMatch nMatch = new NextMatch();
            nMatch.setWinner(false);
            nMatch.setNextMatch(thirdPlace);

            nMatch.setPreviousA(finale.getDependentOn().getPreviousA());
            nMatch.setPreviousB(finale.getDependentOn().getPreviousB());

            matches.persist(thirdPlace);
            nextMatches.persist(nMatch);
            competition.setThirdPlace(thirdPlace);
            competitions.persist(competition);
        } else {
            // update third place if already existing
            NextMatch nMatch = thirdPlace.getDependentOn();

            nMatch.setPreviousA(finale.getDependentOn().getPreviousA());
            nMatch.setPreviousB(finale.getDependentOn().getPreviousB());

            nextMatches.persist(nMatch);

            thirdPlace.setNumber(total - 1);
            matches.persist(thirdPlace);
        }
    }

    private void deletePrevious(Match match) {
        if (match.getDependentOn() != null) {
            deletePrevious(match.getDependentOn().getPreviousA());
            matches.delete(match.getDependentOn().getPreviousA());
            deletePrevious(match.getDependentOn().getPreviousB());
            matches.delete(match.getDependentOn().getPreviousB());
            nextMatches.delete(match.getDependentOn());
        }
    }
}

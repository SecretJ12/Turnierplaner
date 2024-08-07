package de.secretj12.turnierplaner.modifier;

import de.secretj12.turnierplaner.db.entities.Match;
import de.secretj12.turnierplaner.db.entities.competition.Competition;
import de.secretj12.turnierplaner.db.entities.competition.Team;
import de.secretj12.turnierplaner.db.entities.knockout.NextMatch;
import de.secretj12.turnierplaner.db.repositories.MatchRepository;
import de.secretj12.turnierplaner.db.repositories.NextMatchRepository;
import de.secretj12.turnierplaner.resources.jsonEntities.user.knockout.jUserKnockoutMatch;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class KnockoutTools {

    @Inject
    MatchRepository matches;
    @Inject
    NextMatchRepository nextMatches;

    public Match updateKnockoutTree(Competition competition, jUserKnockoutMatch tree, Match match) {
        if (tree == null) {
            return null;
        }
        match.setTeamA(Team.findById(tree.getTeamA()));
        match.setTeamB(Team.findById(tree.getTeamB()));

        match.setFinished(false);
        match.setCompetition(competition);
        match.setWinner(true);
        matches.persist(match);

        if (tree.getPreviousA() == null || tree.getPreviousB() == null) {
            deletePrevious(match);
        }

        if (match.getDependentOn() != null) {
            Match a = match.getDependentOn().getPreviousA();
            updateKnockoutTree(competition, tree.getPreviousA(), a);
            Match b = match.getDependentOn().getPreviousB();
            updateKnockoutTree(competition, tree.getPreviousB(), b);
        } else if (tree.getPreviousA() != null && tree.getPreviousB() != null) {
            NextMatch nMatch = new NextMatch();
            Match a = updateKnockoutTree(competition, tree.getPreviousA(), new Match());
            Match b = updateKnockoutTree(competition, tree.getPreviousB(), new Match());
            nMatch.setPreviousA(a);
            nMatch.setPreviousB(b);
            nMatch.setNextMatch(match);
            nextMatches.persist(nMatch);
        }

        return match;
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

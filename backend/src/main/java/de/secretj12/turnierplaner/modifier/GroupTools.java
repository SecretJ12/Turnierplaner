package de.secretj12.turnierplaner.modifier;

import de.secretj12.turnierplaner.db.entities.Match;
import de.secretj12.turnierplaner.db.entities.competition.Competition;
import de.secretj12.turnierplaner.db.entities.competition.Team;
import de.secretj12.turnierplaner.db.entities.groups.FinalOfGroup;
import de.secretj12.turnierplaner.db.entities.groups.Group;
import de.secretj12.turnierplaner.db.entities.groups.MatchOfGroup;
import de.secretj12.turnierplaner.db.entities.knockout.NextMatch;
import de.secretj12.turnierplaner.db.repositories.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.*;

@ApplicationScoped
public class GroupTools {

    @Inject
    MatchRepository matchRepository;
    @Inject
    GroupRepository groupsRepository;
    @Inject
    MatchOfGroupRepository matchOfGroupRepository;
    @Inject
    TeamRepository teamRepository;
    @Inject
    FinalOfGroupRepository finalOfGroupRepository;
    @Inject
    NextMatchRepository nextMatchRepository;

    public void generateMatches(Competition competition, List<Set<Team>> nGroups) {
        List<Group> groups = competition.getGroups();
        int prevGroupsSize = groups.size();
        createGroups(competition, groups, nGroups);
        createKnockoutTree(competition, prevGroupsSize, groups);
    }

    private void createGroups(Competition competition, List<Group> exGroups, List<Set<Team>> nGroups) {
        for (int index = 0; index < nGroups.size(); index++) {
            Set<Team> nGroup = nGroups.get(index);

            Group group;
            Set<Team> teams;
            // Take existing group or create it
            if (index < exGroups.size()) {
                group = exGroups.get(index);
                teams = teamsOfGroup(group);
            } else {
                group = new Group();
                group.setIndex(index);
                group.setCompetition(competition);
                groupsRepository.persist(group);
                exGroups.add(group);
                teams = new HashSet<>();
            }

            // delete all matches with no longer existing users
            group.getMatches().stream()
                .filter(m -> !nGroup.contains(m.getTeamA()) || !nGroup.contains(m.getTeamB()))
                .forEach(matchRepository::delete);

            // create the match
            List<Team> nGroupList = new ArrayList<>(nGroup);
            for (int i = 0; i < nGroup.size(); i++) {
                for (int j = i + 1; j < nGroup.size(); j++) {
                    // skip already existing matches
                    if (teams.contains(nGroupList.get(i)) && teams.contains(nGroupList.get(j)))
                        continue;

                    Match m = new Match();
                    m.setCompetition(competition);
                    m.setTeamA(nGroupList.get(i));
                    m.setTeamB(nGroupList.get(j));
                    matchRepository.persist(m);

                    MatchOfGroup mog = new MatchOfGroup();
                    mog.setGroup(group);
                    mog.setMatch(m);
                    matchOfGroupRepository.persist(mog);
                }
            }
        }

        // delete all groups no longer existing
        while (exGroups.size() > nGroups.size()) {
            exGroups.getLast().getMatches().forEach(matchRepository::delete);
            groupsRepository.delete(exGroups.getLast());
            exGroups.removeLast();
        }
    }

    private void createKnockoutTree(Competition competition, int prevGroupsSize, List<Group> groups) {
        List<Match> nonGroupMatches = matchRepository.nonGroupMatches(competition);
        if (groups.size() == 1) { // no finale needed
            // delete all matches not belonging to a group
            nonGroupMatches.forEach(matchRepository::delete);
        } else if (groups.size() == 2) { // finale and game for third place needed
            if (prevGroupsSize == 2) // matches already correct
                return;

            // delete all non finals of deleted groups and further matches
            nonGroupMatches.stream().filter(m -> m.getFinalOfGroup() == null).forEach(matchRepository::delete);

            if (prevGroupsSize == 1) // create new finale
                finaleOfGroups(competition, groups.get(0), groups.get(1), 1);
            // create third place match
            finaleOfGroups(competition, groups.get(0), groups.get(1), 2);
        } else { // knockout tree needed
            if (prevGroupsSize == 2) // delete third place match
                nonGroupMatches.stream()
                    .filter(m -> m.getFinalOfGroup() != null && m.getFinalOfGroup().getPos() == 2)
                    .forEach(matchRepository::delete); // should only find one

            List<Match> matches = new ArrayList<>();
            for (int i = 0; i < groups.size() / 2; i++)
                matches.add(finaleOfGroups(competition, groups.get(2 * i), groups.get(2 * i + 1), 1));

            do {
                List<Match> newMatches = new ArrayList<>();
                for (int i = 0; i < matches.size() / 2; i++) {
                    Match m = new Match();
                    m.setCompetition(competition);
                    matchRepository.persist(m);

                    NextMatch nextMatch = new NextMatch();
                    nextMatch.setPreviousA(matches.get(2 * i));
                    nextMatch.setPreviousB(matches.get(2 * i + 1));
                    nextMatch.setNextMatch(m);
                    nextMatchRepository.persist(nextMatch);
                }

                if (matches.size() == 2) {
                    Match m = new Match();
                    m.setCompetition(competition);
                    matchRepository.persist(m);

                    NextMatch nextMatch = new NextMatch();
                    nextMatch.setPreviousA(matches.get(0));
                    nextMatch.setPreviousB(matches.get(1));
                    nextMatch.setNextMatch(m);
                    nextMatch.setWinner(false);
                    nextMatchRepository.persist(nextMatch);
                } else {
                    matches = newMatches;
                }
            } while (matches.size() > 2);
        }
    }

    private Match finaleOfGroups(Competition competition, Group g1, Group g2, int pos) {
        Match finale = new Match();
        finale.setCompetition(competition);
        matchRepository.persist(finale);

        FinalOfGroup fog = new FinalOfGroup();
        fog.setGroupA(g1);
        fog.setGroupB(g2);
        fog.setPos(1);
        fog.setNextMatch(finale);
        finalOfGroupRepository.persist(fog);

        return finale;
    }

    public Set<Team> teamsOfGroup(Group group) {
        return teamRepository.teamsOfGroup(group);
    }
}

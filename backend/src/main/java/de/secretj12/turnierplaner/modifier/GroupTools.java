package de.secretj12.turnierplaner.modifier;

import de.secretj12.turnierplaner.db.entities.Match;
import de.secretj12.turnierplaner.db.entities.competition.Competition;
import de.secretj12.turnierplaner.db.entities.competition.Team;
import de.secretj12.turnierplaner.db.entities.groups.Group;
import de.secretj12.turnierplaner.db.entities.groups.MatchOfGroup;
import de.secretj12.turnierplaner.db.repositories.GroupRepository;
import de.secretj12.turnierplaner.db.repositories.MatchOfGroupRepository;
import de.secretj12.turnierplaner.db.repositories.MatchRepository;
import de.secretj12.turnierplaner.db.repositories.TeamRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public void generateGroupMatches(Competition competition, List<Set<Team>> nGroups) {
        List<Group> exGroups = competition.getGroups();

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
                teams = new HashSet<>();
            }

            // delete all matches with no longer existing users
            group.getMatches().stream().filter(m -> !nGroup.contains(m.getTeamA()) || !nGroup.contains(m.getTeamB())).forEach(matchRepository::delete);

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

        for (int i = nGroups.size(); i < exGroups.size(); i++) {
            groupsRepository.delete(exGroups.get(i));
        }
    }

    public Set<Team> teamsOfGroup(Group group) {
        return teamRepository.teamsOfGroup(group);
    }
}

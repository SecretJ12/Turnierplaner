package de.secretj12.turnierplaner.modifier;

import de.secretj12.turnierplaner.db.entities.Match;
import de.secretj12.turnierplaner.db.entities.competition.Competition;
import de.secretj12.turnierplaner.db.entities.competition.Team;
import de.secretj12.turnierplaner.db.entities.groups.Group;
import de.secretj12.turnierplaner.db.entities.groups.MatchOfGroup;
import de.secretj12.turnierplaner.db.repositories.GroupRepository;
import de.secretj12.turnierplaner.db.repositories.MatchOfGroupRepository;
import de.secretj12.turnierplaner.db.repositories.MatchRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class GroupTools {

    @Inject
    MatchRepository matchRepository;
    @Inject
    GroupRepository groupsRepository;
    @Inject
    MatchOfGroupRepository matchOfGroupRepository;

    public void generateGroupMatches(Competition competition, List<List<Team>> nGroups) {
        // TODO check for deletion of existing matches
        for (List<Team> group : nGroups) {
            Group g = new Group();
            g.setCompetition(competition);
            groupsRepository.persist(g);

            for (int i = 0; i < group.size(); i++) {
                for (int j = i + 1; i < group.size(); j++) {
                    Match m = new Match();
                    m.setCompetition(competition);
                    m.setTeamA(group.get(i));
                    m.setTeamB(group.get(j));
                    matchRepository.persist(m);

                    MatchOfGroup mog = new MatchOfGroup();
                    mog.setGroup(g);
                    mog.setMatch(m);
                    matchOfGroupRepository.persist(mog);
                }
            }
        }
    }
}

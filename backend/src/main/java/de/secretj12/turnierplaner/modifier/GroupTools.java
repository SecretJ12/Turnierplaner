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
import jakarta.ws.rs.BadRequestException;

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
    @Inject
    CompetitionRepository competitions;

    // TODO please add tests for me
    public void generateMatches(Competition competition, List<Set<Team>> nGroups) {
        competition.setFinale(null);
        competition.setThirdPlace(null);

        List<Group> groups = competition.getGroups();
        int prevGroupsSize = groups.size();
        createGroups(competition, groups, nGroups);
        createKnockoutTree(competition, prevGroupsSize, groups);
    }

    private void createGroups(Competition competition, List<Group> exGroups, List<Set<Team>> nGroups) {
        // delete all groups no longer existing
        while (exGroups.size() > nGroups.size()) {
            groupsRepository.delete(exGroups.removeLast());
        }

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
                    m.setNumber(index);
                    matchRepository.persist(m);

                    MatchOfGroup mog = new MatchOfGroup();
                    mog.setGroup(group);
                    mog.setMatch(m);
                    matchOfGroupRepository.persist(mog);
                }
            }
        }
    }

    private void createKnockoutTree(Competition competition, int prevGroupsSize, List<Group> groups) {
        if (groups.size() == 1) {
            competition.setTotal(0);
            competitions.persist(competition);
        }
        if (groups.size() == 2) { // finale and game for third place needed
            Match finale = finaleOfGroups(competition, groups.get(0), groups.get(1), 1, 0);
            competition.setFinale(finale);

            // create third place match
            Match thirdPlace = finaleOfGroups(competition, groups.get(0), groups.get(1), 2, 0);
            competition.setThirdPlace(thirdPlace);
            competition.setTotal(1);
            competitions.persist(competition);
        } else if (groups.size() > 2) { // knockout tree needed
            if (prevGroupsSize == 2) // delete previous game for third place
                matchRepository.delete(finaleOfGroups(competition, groups.get(0), groups.get(1), 2, -1));

            List<Match> matches = new ArrayList<>();
            for (int i = 0; i < groups.size() / 2; i++)
                matches.add(finaleOfGroups(competition, groups.get(2 * i), groups.get(2 * i + 1), 1, 0));

            int counter = 0;
            do {
                counter++;
                List<Match> newMatches = new ArrayList<>();
                for (int i = 0; i < matches.size() / 2; i++) {
                    Optional<NextMatch> exNext = matches.get(i).getPreviousOfA() == null ? Optional.empty() : matches
                        .get(2 * i).getPreviousOfA()
                        .stream().filter(NextMatch::isWinner)
                        .findFirst();
                    if (exNext.isEmpty()) {
                        Match m = new Match();
                        m.setNumber(counter);
                        m.setCompetition(competition);
                        matchRepository.persist(m);
                        newMatches.add(m);
                        if (matches.size() == 2)
                            competition.setFinale(m);

                        NextMatch nextMatch = new NextMatch();
                        nextMatch.setPreviousA(matches.get(2 * i));
                        nextMatch.setPreviousB(matches.get(2 * i + 1));
                        nextMatch.setNextMatch(m);
                        nextMatchRepository.persist(nextMatch);
                    } else {
                        Match m = exNext.get().getNextMatch();
                        m.setNumber(counter);
                        matchRepository.persist(m);
                        newMatches.add(m);
                        if (matches.size() == 2)
                            competition.setFinale(exNext.get().getNextMatch());
                    }
                }

                if (matches.size() == 2) {
                    Optional<NextMatch> exNext = matches.getFirst().getPreviousOfA() == null ? Optional
                        .empty() : matches.getFirst().getPreviousOfA()
                            .stream().filter(nm -> !nm.isWinner())
                            .findAny();
                    if (exNext.isEmpty()) {
                        Match m = new Match();
                        m.setNumber(counter);
                        m.setCompetition(competition);
                        matchRepository.persist(m);
                        competition.setThirdPlace(m);

                        NextMatch nextMatch = new NextMatch();
                        nextMatch.setPreviousA(matches.get(0));
                        nextMatch.setPreviousB(matches.get(1));
                        nextMatch.setNextMatch(m);
                        nextMatch.setWinner(false);
                        nextMatchRepository.persist(nextMatch);
                    } else {
                        Match m = exNext.get().getNextMatch();
                        m.setNumber(counter);
                        matchRepository.persist(m);
                        competition.setThirdPlace(m);
                    }
                }
                matches = newMatches;
            } while (matches.size() > 1);
            competition.setTotal(counter + 1);
            competitions.persist(competition);
        } else {
            throw new BadRequestException("Invalid group size");
        }
    }

    private Match finaleOfGroups(Competition competition, Group g1, Group g2, int pos, int number) {
        Optional<FinalOfGroup> exFin = g1.getFinalOfGroupA() == null ? Optional.empty() : g1.getFinalOfGroupA().stream()
            .filter(fog -> fog.getPos() == pos)
            .findFirst();
        if (exFin.isEmpty()) {
            Match finale = new Match();
            finale.setNumber(number);
            finale.setCompetition(competition);
            matchRepository.persist(finale);

            FinalOfGroup fog = new FinalOfGroup();
            fog.setGroupA(g1);
            fog.setGroupB(g2);
            fog.setPos(pos);
            fog.setNextMatch(finale);
            finalOfGroupRepository.persist(fog);

            return finale;
        } else {
            Match match = exFin.get().getNextMatch();
            match.setNumber(number);
            matchRepository.persist(match);
            return match;
        }
    }

    public Set<Team> teamsOfGroup(Group group) {
        return teamRepository.teamsOfGroup(group);
    }
}

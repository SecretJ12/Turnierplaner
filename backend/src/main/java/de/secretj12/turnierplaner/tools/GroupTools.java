package de.secretj12.turnierplaner.tools;

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
        Match thirdP = competition.getThirdPlace();
        competition.setFinale(null);
        competition.setThirdPlace(null);

        if (thirdP != null) {
            if (thirdP.getDependentOn() != null) {
                NextMatch nm = thirdP.getDependentOn();
                nm.setNextMatch(null);
                thirdP.setDependentOn(null);
                nextMatchRepository.delete(nm);
            }
            if (thirdP.getFinalOfGroup() != null) {
                FinalOfGroup fog = thirdP.getFinalOfGroup();
                fog.setNextMatch(null);
                thirdP.setFinalOfGroup(null);
                finalOfGroupRepository.delete(fog);
            }
        }

        List<Group> groups = competition.getGroups();
        createGroups(competition, groups, nGroups);
        createKnockoutTree(competition, groups, thirdP);
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

    private void createKnockoutTree(Competition competition, List<Group> groups, Match thirdP) {
        if (groups.size() == 1) {
            competition.setTotal(0);
            competitions.persist(competition);
            if (thirdP != null)
                matchRepository.delete(thirdP);
        } else if (groups.size() == 2) { // finale and game for third place needed
            Match finale = finaleOfGroups(competition, groups.get(0), groups.get(1));
            competition.setFinale(finale);

            // create third place match
            if (thirdP == null)
                thirdP = new Match();
            thirdP.setNumber(0);
            thirdP.setDependentOn(null);
            thirdP.setCompetition(competition);
            matchRepository.persist(thirdP);

            FinalOfGroup fog = new FinalOfGroup();
            fog.setPos(2);
            fog.setGroupA(groups.get(0));
            fog.setGroupB(groups.get(1));
            fog.setNextMatch(thirdP);
            finalOfGroupRepository.persist(fog);

            competition.setTotal(1);
            competition.setThirdPlace(thirdP);
            competitions.persist(competition);
        } else if (groups.size() > 2) { // knockout tree needed
            List<Match> matches = new ArrayList<>();
            for (int i = 0; i < groups.size() / 2; i++)
                matches.add(finaleOfGroups(competition, groups.get(2 * i), groups.get(2 * i + 1)));

            int counter = 0;
            do {
                counter++;
                List<Match> newMatches = new ArrayList<>();
                for (int i = 0; i < matches.size() / 2; i++) {
                    Optional<NextMatch> exNext = matches.get(2 * i).getPreviousOfA() == null ? Optional
                        .empty() : matches
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
                    if (thirdP == null)
                        thirdP = new Match();
                    thirdP.setNumber(counter);
                    thirdP.setCompetition(competition);
                    matchRepository.persist(thirdP);

                    NextMatch nextMatch = new NextMatch();
                    nextMatch.setNextMatch(thirdP);
                    nextMatch.setPreviousA(matches.get(0));
                    nextMatch.setPreviousB(matches.get(1));
                    nextMatch.setWinner(false);
                    nextMatchRepository.persist(nextMatch);

                    competition.setThirdPlace(thirdP);
                }
                matches = newMatches;
            } while (matches.size() > 1);
            competition.setTotal(counter + 1);
            competitions.persist(competition);
        } else {
            throw new BadRequestException("Invalid group size");
        }
    }

    private Match finaleOfGroups(Competition competition, Group g1, Group g2) {
        Optional<FinalOfGroup> exFin = g1.getFinalOfGroupA() == null ? Optional.empty() : g1.getFinalOfGroupA().stream()
            .filter(fog -> fog.getPos() == 1)
            .findFirst();
        if (exFin.isEmpty()) {
            Match finale = new Match();
            finale.setNumber(0);
            finale.setCompetition(competition);
            matchRepository.persist(finale);

            FinalOfGroup fog = new FinalOfGroup();
            fog.setGroupA(g1);
            fog.setGroupB(g2);
            fog.setPos(1);
            fog.setNextMatch(finale);
            finalOfGroupRepository.persist(fog);

            return finale;
        } else {
            Match match = exFin.get().getNextMatch();
            match.setNumber(0);
            matchRepository.persist(match);
            return match;
        }
    }

    public Set<Team> teamsOfGroup(Group group) {
        return teamRepository.teamsOfGroup(group);
    }
}
package de.secretj12.turnierplaner.model.user;

import de.secretj12.turnierplaner.db.entities.competition.Team;

public class jUserTeamGroupResult {

    private jUserTeam team;
    private int rank;
    private int matchesWon;
    private int matchesLost;
    private int setsWon;
    private int setsLost;
    private int gamesWon;
    private int gamesLost;

    public jUserTeamGroupResult(Team team) {
        this.team = new jUserTeam(team);
    }

    public void matchWon() {
        this.matchesWon++;
    }

    public void matchLost() {
        this.matchesLost++;
    }

    public void setWon() {
        this.setsWon++;
    }

    public void setLost() {
        this.setsLost++;
    }

    public void gamesWon(int gamesWon) {
        this.gamesWon += gamesWon;
    }

    public void gamesLost(int gamesLost) {
        this.gamesLost += gamesLost;
    }

    public jUserTeam getTeam() {
        return team;
    }

    public void setTeam(jUserTeam team) {
        this.team = team;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getGamesWon() {
        return gamesWon;
    }

    public void setGamesWon(int gamesWon) {
        this.gamesWon = gamesWon;
    }

    public int getGamesLost() {
        return gamesLost;
    }

    public void setGamesLost(int gamesLost) {
        this.gamesLost = gamesLost;
    }

    public int getSetsWon() {
        return setsWon;
    }

    public void setSetsWon(int setsWon) {
        this.setsWon = setsWon;
    }

    public int getSetsLost() {
        return setsLost;
    }

    public void setSetsLost(int setsLost) {
        this.setsLost = setsLost;
    }

    public int getMatchesWon() {
        return matchesWon;
    }

    public void setMatchesWon(int matchesWon) {
        this.matchesWon = matchesWon;
    }

    public int getMatchesLost() {
        return matchesLost;
    }

    public void setMatchesLost(int matchesLost) {
        this.matchesLost = matchesLost;
    }
}

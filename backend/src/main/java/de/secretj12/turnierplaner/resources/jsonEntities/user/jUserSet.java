package de.secretj12.turnierplaner.resources.jsonEntities.user;

import de.secretj12.turnierplaner.db.entities.Set;

public class jUserSet {
    private long id;
    private int scoreA;
    private int scoreB;

    public jUserSet(Set set){
        this.scoreA = set.getScoreA();
        this.scoreB = set.getScoreB();
        this.id = set.getKey().getId();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getScoreA() {
        return scoreA;
    }

    public void setScoreA(int scoreA) {
        this.scoreA = scoreA;
    }

    public int getScoreB() {
        return scoreB;
    }

    public void setScoreB(int scoreB) {
        this.scoreB = scoreB;
    }
}

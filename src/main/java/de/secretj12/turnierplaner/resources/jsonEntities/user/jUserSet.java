package de.secretj12.turnierplaner.resources.jsonEntities.user;

import de.secretj12.turnierplaner.db.entities.Set;

public class jUserSet {
    private long index;
    private int scoreA;
    private int scoreB;

    public jUserSet(Set set) {
        this.scoreA = set.getScoreA();
        this.scoreB = set.getScoreB();
        this.index = set.getKey().getIndex();
    }

    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
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

package de.secretj12.turnierplaner.resources.jsonEntities.user.competition;

import de.secretj12.turnierplaner.resources.jsonEntities.director.competition.jDirectorValidSex;

import java.time.LocalDate;

public class jUserConfigPlayerA {
    private jDirectorValidSex sex;
    private boolean hasMinAge;
    private LocalDate minAge;
    private boolean hasMaxAge;
    private LocalDate maxAge;

    public jDirectorValidSex getSex() {
        return sex;
    }

    public void setSex(jDirectorValidSex sex) {
        this.sex = sex;
    }

    public boolean isHasMinAge() {
        return hasMinAge;
    }

    public void setHasMinAge(boolean hasMinAge) {
        this.hasMinAge = hasMinAge;
    }

    public LocalDate getMinAge() {
        return minAge;
    }

    public void setMinAge(LocalDate minAge) {
        this.minAge = minAge;
    }

    public boolean isHasMaxAge() {
        return hasMaxAge;
    }

    public void setHasMaxAge(boolean hasMaxAge) {
        this.hasMaxAge = hasMaxAge;
    }

    public LocalDate getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(LocalDate maxAge) {
        this.maxAge = maxAge;
    }
}

package de.secretj12.turnierplaner.resources.jsonEntities.user;

public class jUserPlayerSignUpForm {

    private jPlayer playerA;
    private jPlayer playerB;

    public static class jPlayer {
        private String firstName;
        private String lastName;

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }
    }

    public jPlayer getPlayerA() {
        return playerA;
    }

    public void setPlayerA(jPlayer playerA) {
        this.playerA = playerA;
    }

    public jPlayer getPlayerB() {
        return playerB;
    }

    public void setPlayerB(jPlayer playerB) {
        this.playerB = playerB;
    }
}

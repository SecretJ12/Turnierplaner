package de.secretj12.turnierplaner.resources.jsonEntities.user;

import de.secretj12.turnierplaner.db.entities.Player;

import java.time.LocalDate;

public class jUserPlayerRegistrationForm {

    String firstName;
    String lastName;
    jUserSex sex;
    LocalDate birthday;
    String email;
    String phone;

    public jUserPlayerRegistrationForm() {
    }

    public jUserPlayerRegistrationForm(Player player) {
        this.firstName = player.getFirstName();
        this.lastName = player.getLastName();
        this.sex = switch (player.getSex()) {
            case MALE -> jUserSex.MALE;
            case FEMALE -> jUserSex.FEMALE;
        };
        this.birthday = player.getBirthday();
        this.email = player.getEmail();
        this.phone = player.getPhone();
    }

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

    public jUserSex getSex() {
        return sex;
    }

    public void setSex(jUserSex sex) {
        this.sex = sex;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}

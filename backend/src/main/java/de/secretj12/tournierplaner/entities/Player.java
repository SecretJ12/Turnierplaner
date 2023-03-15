package de.secretj12.tournierplaner.entities;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "players")
@NamedQueries({
        @NamedQuery(name="filter",
                query="SELECT p FROM Player p " +
                        "WHERE p.firstName like CONCAT('%', ?1, '%') OR p.lastName = CONCAT('%', ?1, '%')" +
                        "OR CONCAT(p.firstName, ' ', p.lastName) like CONCAT('%', ?1, '%')")
})
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;

    @Column(name = "sex")
    private SexType sex;

    @Column(name = "birthday")
    private LocalDate birthday;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "mail_verified")
    private boolean mailVerified;

    @Column(name = "admin_verified")
    private boolean adminVerified;

    @ManyToMany
    @JoinTable(
            name = "participating_in",
            joinColumns = {
                    @JoinColumn(name = "competition_id")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "player_id")
            }
    )
    private List<Competition> competitions;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public SexType getSex() {
        return sex;
    }

    public void setSex(SexType sex) {
        this.sex = sex;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Competition> getCompetitions() {
        return competitions;
    }

    public void setCompetitions(List<Competition> competitions) {
        this.competitions = competitions;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isMailVerified() {
        return mailVerified;
    }

    public void setMailVerified(boolean mail_verified) {
        this.mailVerified = mail_verified;
    }

    public boolean isAdminVerified() {
        return adminVerified;
    }

    public void setAdminVerified(boolean admin_verified) {
        this.adminVerified = admin_verified;
    }
}

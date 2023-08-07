package de.secretj12.turnierplaner.db.entities.competition;

import de.secretj12.turnierplaner.db.entities.Match;
import de.secretj12.turnierplaner.db.entities.Tournament;
import de.secretj12.turnierplaner.db.entities.groups.Group;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

// TODO add attributes to set something like a plan published bit -> implement everywhere
@Entity
@Table(name = "competitions")
@NamedQueries({
               @NamedQuery(name = "compByName", query = """
                       SELECT c FROM Competition c LEFT JOIN Tournament t ON c.tournament = t
                       WHERE t.name = ?1 AND c.name = ?2"""),
               @NamedQuery(name = "listByName", query = """
                       SELECT c FROM Competition c LEFT JOIN Tournament t ON c.tournament = t WHERE t.name=  ?1""")})
public class Competition {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "name")
    private String name;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;

    @Column(name = "description")
    private String description;
    @Column(name = "type")
    private CompetitionType type;

    @Column(name = "mode")
    private CompetitionMode mode;
    @Column(name = "signup")
    private CompetitionSignUp signup;

    @Column(name = "playerA_sex")
    private Sex playerASex;
    @Column(name = "playerA_has_min_age")
    private boolean playerAhasMinAge;
    @Column(name = "playerA_min_age")
    private LocalDate playerAminAge;
    @Column(name = "playerA_has_max_age")
    private boolean playerAhasMaxAge;
    @Column(name = "playerA_max_age")
    private LocalDate playerAmaxAge;

    @Column(name = "playerB_different")
    private boolean playerBdifferent;
    @Column(name = "playerB_sex")
    private Sex playerBSex;
    @Column(name = "playerB_has_min_age")
    private boolean playerBhasMinAge;
    @Column(name = "playerB_min_age")
    private LocalDate playerBminAge;
    @Column(name = "playerB_has_max_age")
    private boolean playerBhasMaxAge;
    @Column(name = "playerB_max_age")
    private LocalDate playerBmaxAge;

    @OneToMany(mappedBy = "competition")
    private List<Team> teams;

    @OneToMany(mappedBy = "competition")
    private List<Match> matches;

    @OneToMany(mappedBy = "competition")
    private List<Group> groups;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CompetitionType getType() {
        return type;
    }

    public void setType(CompetitionType type) {
        this.type = type;
    }

    public List<Match> getMatches() {
        return matches;
    }

    public void setMatches(List<Match> matches) {
        this.matches = matches;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }

    public CompetitionMode getMode() {
        return mode;
    }

    public void setMode(CompetitionMode mode) {
        this.mode = mode;
    }

    public CompetitionSignUp getSignup() {
        return signup;
    }

    public void setSignup(CompetitionSignUp signup) {
        this.signup = signup;
    }

    public Sex getPlayerASex() {
        return playerASex;
    }

    public void setPlayerASex(Sex playerASex) {
        this.playerASex = playerASex;
    }

    public boolean playerAhasMinAge() {
        return playerAhasMinAge;
    }

    public void setPlayerAhasMinAge(boolean playerAhasMinAge) {
        this.playerAhasMinAge = playerAhasMinAge;
    }

    public LocalDate getPlayerAminAge() {
        return playerAminAge;
    }

    public void setPlayerAminAge(LocalDate playerAminAge) {
        this.playerAminAge = playerAminAge;
    }

    public boolean playerAhasMaxAge() {
        return playerAhasMaxAge;
    }

    public void setPlayerAhasMaxAge(boolean playerAhasMaxAge) {
        this.playerAhasMaxAge = playerAhasMaxAge;
    }

    public LocalDate getPlayerAmaxAge() {
        return playerAmaxAge;
    }

    public void setPlayerAmaxAge(LocalDate playerAmaxAge) {
        this.playerAmaxAge = playerAmaxAge;
    }

    public boolean isPlayerBdifferent() {
        return playerBdifferent;
    }

    public void setPlayerBdifferent(boolean playerBequalsA) {
        this.playerBdifferent = playerBequalsA;
    }

    public Sex getPlayerBSex() {
        return playerBSex;
    }

    public void setPlayerBSex(Sex playerBSex) {
        this.playerBSex = playerBSex;
    }

    public boolean playerBhasMinAge() {
        return playerBhasMinAge;
    }

    public void setPlayerBhasMinAge(boolean playerBhasMinAge) {
        this.playerBhasMinAge = playerBhasMinAge;
    }

    public LocalDate getPlayerBminAge() {
        return playerBminAge;
    }

    public void setPlayerBminAge(LocalDate playerBminAge) {
        this.playerBminAge = playerBminAge;
    }

    public boolean playerBhasMaxAge() {
        return playerBhasMaxAge;
    }

    public void setPlayerBhasMaxAge(boolean playerBhasMaxAge) {
        this.playerBhasMaxAge = playerBhasMaxAge;
    }

    public LocalDate getPlayerBmaxAge() {
        return playerBmaxAge;
    }

    public void setPlayerBmaxAge(LocalDate playerBmaxAge) {
        this.playerBmaxAge = playerBmaxAge;
    }
}

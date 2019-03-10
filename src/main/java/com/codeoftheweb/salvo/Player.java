package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


// tells Spring to create a Player table for this class
@Entity
public class Player {

    // says that the id instance variable holds the database key for this class and generates
    // GeneratedValue and GenericGenerator tell JPA to use whatever ID generator is provided by the database system
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    // one Player may have many gamePlayers
    @OneToMany(mappedBy="player", fetch=FetchType.EAGER)
    private Set<GamePlayer> gamePlayers;

    // one player may have many scores
    @OneToMany(mappedBy="player", fetch=FetchType.EAGER)
    private Set<Score> scores = new HashSet<>();

    private String userName;
    private String password;


    // constructors


    public Player(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public Player() {

    }

    // setters
    public void setId(long id) {
        this.id = id;
    }

    public void setGamePlayers(Set<GamePlayer> gamePlayers) {
        this.gamePlayers = gamePlayers;
    }

    public void setScores(Set<Score> scores) {
        this.scores = scores;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // getters
    public long getId() {
        return id;
    }

    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public Set<Score> getScores() {
        return scores;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    // methods
    public void addScore(Score score) {
        score.setPlayer(this);
        this.scores.add(score);
    }

    public Score getScore(Game game) {
        return scores.stream()
                .filter(x -> x.getGame().getId() == game.getId())
                .findFirst().orElse(null);
    }
}

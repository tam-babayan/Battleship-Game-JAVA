package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    private Set<GamePlayer> gamePlayers;

    @OneToMany(mappedBy="game", fetch=FetchType.EAGER)
    private Set<Score> scores = new HashSet<>();

    private Date created;

    private GameState state = GameState.WAIT_FOR_SHIPS;

    // constructor
    public Game(Date date) {
        this.created = date;
    }

    public Game() {
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

    public Date getCreated() {
        return created;
    }

    public GameState getState() {
        return state;
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

    public void setCreated(Date created) {
        this.created = created;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    // methods
    public void addScore(Score score) {
        score.setGame(this);
        this.scores.add(score);
    }
}
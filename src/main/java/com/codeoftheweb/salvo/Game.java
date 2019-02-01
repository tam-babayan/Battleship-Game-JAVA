package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    private Set<GamePlayer> gamePlayers;

    private Date created;


    // constructor
    public Game(Date date) {
        this.created = date;
    }

    public Game() {}

    // ??
//    public void addGamePlayer(GamePlayer gamePlayer) {
//        gamePlayer.setGame(this);
//        gamePlayers.add(gamePlayer);
//    }


    // getters
    public Date getDate() {
        return created;
    }

    public long getId() {
        return id;
    }

    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }
}
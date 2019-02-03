package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    private Set<GamePlayer> gamePlayers;

    private String userName;


    // constructors
    public Player(String userName) {
        this.userName = userName;
    }

    public Player() {

    }

    // ??
//    public void addGamePlayer(GamePlayer gamePlayer) {
//        gamePlayer.setPlayer(this);
//        gamePlayers.add(gamePlayer);
//    }


    // setters
    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    // getters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String toString() {
        return userName;
    }
}

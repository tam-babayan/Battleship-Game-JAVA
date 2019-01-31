package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
public class GamePlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id")
    private Player player;

    private Date created;

    @OneToMany(mappedBy="gamePlayer", fetch=FetchType.EAGER)
    private Set<Ship> ships = new HashSet<>();

    // constructors
    public GamePlayer() {
    }

    public GamePlayer(Game game, Player player) {
        this.game = game;
        this.player = player;
        this.created = new Date();
    }

    // getters
    public Game getGame() {
        return game;
    }

    public Player getPlayer() {
        return player;
    }

    public long getId() {
        return id;
    }

    public Date getDate() {
        return created;
    }

    public Set<Ship> getShips() {
        return ships;
    }

    // setters
    public void setGame(Game game) {
        this.game = game;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setDate(Date date) {
        this.created = date;
    }

    // methods

    public void  addShip(Ship ship) {
        this.ships.add(ship);
    }
}


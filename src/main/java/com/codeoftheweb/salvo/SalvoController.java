package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class SalvoController {

    @Autowired
    private GameRepository gameRepo;
    @Autowired
    private GamePlayerRepository gamePlayerRepo;
    @Autowired
    private ShipRepository shipRepository;

    @RequestMapping(value = "/api/game_view/{gamePlayerId}", method = RequestMethod.GET)
    public Map<String, Object> getGameInfo(@PathVariable long gamePlayerId) {
        GamePlayer gamePlayer = gamePlayerRepo.findOne(gamePlayerId);

//        List<GamePlayer> gamePlayers = gamePlayerRepo.findAll();

        Map<String, Object> result = new HashMap<>();
        result.put("id", gamePlayer.getGame().getId());
        result.put("date", gamePlayer.getGame().getDate());
        result.put("gamePlayers", getListOfGamePlayers(gamePlayer.getGame().getGamePlayers()));
        result.put("ships", getShipInfo(gamePlayer));

        return result;
    }


    public List<Map<String, Object>> getShipInfo(GamePlayer gamePlayer) {
        List<Map<String, Object>> result = new ArrayList<>();
        System.out.println(gamePlayer.getShips());

        gamePlayer.getShips().forEach(ship -> {
            Map<String, Object> tempMap = new HashMap<>();
            tempMap.put("type", ship.getShipType());
            tempMap.put("locations", ship.getShipLocations());

            result.add(tempMap);
        });
        System.out.println(result);
        return result;
    }

    @RequestMapping("api/games")
    public List<Map<String, Object>> getAllGames() {
        List<Map<String, Object>> result = new ArrayList<>();

        gameRepo.findAll().forEach(game -> {

            Map<String, Object> tempMap = new HashMap<>();
            tempMap.put("gameId", game.getId());
            tempMap.put("date", game.getDate());
            tempMap.put("gamePlayers", getListOfGamePlayers(game.getGamePlayers()));

//            System.out.println(tempMap);

            result.add(tempMap);
        });

        return result;
    }

    private List<Map<String, Object>> getListOfGamePlayers(Set<GamePlayer> gamePlayers) {
        List<Map<String, Object>> gamePlayersList = new ArrayList<>();
        gamePlayers.forEach(gamePlayer -> {

            Map<String, Object> result = new HashMap<>();
            result.put("id", gamePlayer.getId());
            result.put("player", getPlayerInfo(gamePlayer));

            gamePlayersList.add(result);
        });

        return gamePlayersList;
    }


    private Map<String, Object> getPlayerInfo(GamePlayer gamePlayer) {
        Map<String, Object> result = new HashMap<>();
        result.put("id", gamePlayer.getPlayer().getId());
        result.put("email", gamePlayer.getPlayer().getUserName());

        return result;
    }
}

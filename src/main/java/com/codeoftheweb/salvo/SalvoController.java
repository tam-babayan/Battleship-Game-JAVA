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
    private ScoreRepository scoreRepository;

    @RequestMapping(value = "/api/game_view/{gamePlayerId}", method = RequestMethod.GET)
    public Map<String, Object> getGameInfo(@PathVariable long gamePlayerId) {
        GamePlayer gamePlayer = gamePlayerRepo.findOne(gamePlayerId);

        Map<String, Object> result = new HashMap<>();
        result.put("id", gamePlayer.getGame().getId());
        result.put("date", gamePlayer.getGame().getCreated());
        result.put("gamePlayers", getListOfGamePlayers(gamePlayer.getGame().getGamePlayers()));
        result.put("ships", getShipInfo(gamePlayer));
        result.put("salvoes", getSalvoInfo(gamePlayer.getGame().getGamePlayers()));

        return result;
    }

    private List<Map<String, Object>> getSalvoInfo(Set<GamePlayer> gamePlayers) {
        List<Map<String, Object>> result =  new ArrayList<>();
        gamePlayers.forEach(gamePlayer -> {
            gamePlayer.getSalvoes().forEach(salvo -> {
                Map<String, Object> tempMap = new HashMap<>();
                tempMap.put("turn", salvo.getTurn());
                tempMap.put("player", salvo.getGamePlayer().getId());
                tempMap.put("locations", salvo.getSalvoLocations());
                result.add(tempMap);
            });
        });

        return result;
    }

    private List<Map<String, Object>> getShipInfo(GamePlayer gamePlayer) {
        List<Map<String, Object>> result = new ArrayList<>();

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
            tempMap.put("date", game.getCreated());
            tempMap.put("gamePlayers", getListOfGamePlayers(game.getGamePlayers()));

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
            result.put("score", gamePlayer.getScore().getScore());

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

//    private List<String> getScore(GamePlayer gamePlayer) {
//        scoreRepository.findAll().forEach(score -> {
//            if (score.getPlayer() === gamePlayer) {
//
//            }
//        });
//        return null;
//    }
}

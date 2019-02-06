package com.codeoftheweb.salvo;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
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
    @Autowired
    private PlayerRepository playerRepository;

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

    @RequestMapping("api/games/scores")
    public List<Map<String, Object>> createLeaderBoardJSON () {
        List<Map<String, Object>> result = new ArrayList<>();
        playerRepository.findAll().forEach(player -> {
            Map<String, Object> tempMap = new HashMap<>();
            tempMap.put("player_id", player.getId());
            tempMap.put("player_email", player.getUserName());
            tempMap.put("wins", getWinsCount(player, 1));
            tempMap.put("losses", getWinsCount(player, 0));
            tempMap.put("ties", getWinsCount(player, 0.5));
            tempMap.put("total", getTotal(player));

            result.add(tempMap);
        });

        return result;
    }

    private double getWinsCount(Player player, double playerScore) {
        double count = 0;
        for (Score score : player.getScores()) {
            if(score.getScore() == playerScore) {
                count++;
            }
        }
        return count;
    }

    private double getTotal(Player player) {
        double sum = 0;
        for( Score score : player.getScores()) {
            sum += score.getScore();
        }
        return sum;
    }

    @RequestMapping("api/games")
    public Map<String, Object> getAllGames (Authentication authentication) {
        Map<String, Object> result = new HashMap<>();

        if(!isGuest(authentication)) {
            Player currentPlayer = getCurrentPlayer(authentication);
            result.put("player", getPlayerInfo(currentPlayer));
        } else {
            result.put("player", null);
        }

        result.put("games", getListOfGames());

        return result;
    }


    private List<Map<String, Object>> getListOfGames() {
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
            result.put("player", getPlayerInfo(gamePlayer.getPlayer()));
            result.put("score", gamePlayer.getScore().getScore());

            gamePlayersList.add(result);
        });

        return gamePlayersList;
    }

    private Map<String, Object> getPlayerInfo(Player player) {
        Map<String, Object> result = new HashMap<>();
        result.put("id", player.getId());
        result.put("email", player.getUserName());

        return result;
    }

    public Player getCurrentPlayer(Authentication authentication) {
        return playerRepository.findByUserName(authentication.getName());
    }

    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }
}

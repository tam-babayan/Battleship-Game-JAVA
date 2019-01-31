package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class SalvoController {

    @Autowired
    private GameRepository gameRepo;
    @Autowired
    private GamePlayerRepository GamePlayerRepo;

    @RequestMapping(value="/api/game_view/{playerId}", method = RequestMethod.GET)
    public Map<String, Object> getGameInfo(@PathVariable String playerId) {
        GamePlayerRepo.findAll().forEach(game -> {

            Map<String, Object> result = new HashMap<>();
            result.put("id", game.getId());
            result.put("date", game.getDate());
            result.put("gamePlayers", getListOfGamePlayers(game.getPlayer().gamePlayers));
            System.out.println(result);
        });

        return null;
    }



    @RequestMapping("api/games")
    public List<Map<String, Object>> getAllGames() {
        List<Map<String, Object>> result = new ArrayList<>();

        gameRepo.findAll().forEach(game -> {

            Map<String, Object> tempMap = new HashMap<>();
            tempMap.put("gameId", game.getId());
            tempMap.put("date", game.getDate());
            tempMap.put("gamePlayers", getListOfGamePlayers(game.gamePlayers));

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


    private Map<String, Object> getPlayerInfo (GamePlayer gamePlayer) {
        Map<String, Object> result = new HashMap<>();
        result.put("id", gamePlayer.getPlayer().getId());
        result.put("email", gamePlayer.getPlayer().getUserName());

        return result;
    }
}

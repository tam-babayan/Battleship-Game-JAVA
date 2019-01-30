package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class SalvoController {

//    @Autowired
//    private GamePlayerRepository gpRepo;

    @Autowired
    private GameRepository gameRepo;

//    @RequestMapping("api/games")
//    public List<Object> getAll() {
//        List<Object> result = new ArrayList<>();
//
//        gameRepo.findAll().forEach(game -> {
//            result.add(game.getId());
//        });
//
//        return result;
//    }

    @RequestMapping("api/games")
    public List<Map<String, Object>> getAllSecond(){
        List<Map<String, Object>> result = new ArrayList<>();

        gameRepo.findAll().forEach(game -> {

            Map<String,Object> tempMap = new HashMap<>();
            tempMap.put("gameId", game.getId());
            tempMap.put("date", game.getDate());
            tempMap.put("gamePlayers", getListOfGamePlayers(game.gamePlayers));

            System.out.println(tempMap);

            result.add(tempMap);
        });


        return result;
    }

    @Autowired
    private PlayerRepository playerRepo;

    public List<Object> getListOfGamePlayers(Set<GamePlayer> gamePlayersSet){
        List<Map<String, Object>> newResult = new ArrayList<>();
        playerRepo.findAll().forEach(gamePlayer -> {
            Map<String,Object> temp = new HashMap<>();
            temp.put("id", gamePlayer.getUserName());

            newResult.add(temp);
        });

        return newResult;
    }
}

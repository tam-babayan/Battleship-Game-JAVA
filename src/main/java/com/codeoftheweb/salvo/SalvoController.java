package com.codeoftheweb.salvo;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
    @Autowired
    private ShipRepository shipRepository;
    @Autowired
    private SalvoRepository salvoRepository;


    @RequestMapping(path = "api/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createUser(@RequestParam String userName, String password) {
        if (userName.isEmpty()) {
            return new ResponseEntity<>(makeMap("error", "No name"), HttpStatus.FORBIDDEN);
        }
        Player player = playerRepository.findByUserName(userName);
        if (player != null) {
            return new ResponseEntity<>(makeMap("error", "Username already exists"), HttpStatus.CONFLICT);
        }
        Player newPlayer = playerRepository.save(new Player(userName, password));
        return new ResponseEntity<>(makeMap("Player", newPlayer.getUserName()), HttpStatus.CREATED);
    }

    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    @RequestMapping(path = "/api/games", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createGame(Authentication authentication) {
        if(!isGuest(authentication)) {
            Game newGame = new Game(new Date());
            gameRepo.save(newGame);
            GamePlayer newGamePlayer = new GamePlayer(newGame, this.getCurrentPlayer(authentication));
            gamePlayerRepo.save(newGamePlayer);
            return new ResponseEntity<>(makeMap("gpid", newGamePlayer.getId()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(makeMap("error", "Username is not registered"), HttpStatus.UNAUTHORIZED);
        }
    }

    @RequestMapping(path = "/api/game/{gameId}/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> joinGame (Authentication authentication, @PathVariable long gameId) {

        if (isGuest(authentication)) {
            return new ResponseEntity<>(makeMap("error", "Username is not registered"), HttpStatus.UNAUTHORIZED);
        }
        Game currentGame = gameRepo.findOne(gameId);

        if (currentGame == null){
            return new ResponseEntity<>(makeMap("error", "No such game"), HttpStatus.FORBIDDEN);
        }

        if (currentGame.getGamePlayers().iterator().next().getPlayer().getId() == this.getCurrentPlayer(authentication).getId()) {
            return new ResponseEntity<>(makeMap("error", "Can not join your Game"), HttpStatus.FORBIDDEN);
        }

        if (currentGame.getGamePlayers().size() == 1) {
            GamePlayer newGamePlayer = new GamePlayer(currentGame, this.getCurrentPlayer(authentication));
            gamePlayerRepo.save(newGamePlayer);
            return new ResponseEntity<>(makeMap("gpid", newGamePlayer.getId()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(makeMap("error", "Game is full"), HttpStatus.FORBIDDEN);
        }
    }


    @RequestMapping(value = "/games/players/{gamePlayerId}/ships", method = RequestMethod.POST)
    public ResponseEntity<Void> addShips (Authentication authentication, @PathVariable long gamePlayerId,
                                                        @RequestBody List<Ship> ships) {

        GamePlayer gamePlayer = gamePlayerRepo.findOne(gamePlayerId);

        if (isGuest(authentication) || !this.getCurrentPlayer(authentication).getGamePlayers().contains(gamePlayer)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (gamePlayer.getShips().size() == 5) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        ships.forEach(ship -> {
            gamePlayer.addShip(ship);
            shipRepository.save(ship);
        });

        gamePlayerRepo.save(gamePlayer);

        return new ResponseEntity<>(HttpStatus.OK);
    }


    @RequestMapping(value = "/games/players/{gamePlayerId}/salvos", method = RequestMethod.POST)
    public ResponseEntity<Void> fireSalvo (Authentication authentication, @PathVariable long gamePlayerId,
                                           @RequestBody List<Salvo> salvos) {
        GamePlayer gamePlayer = gamePlayerRepo.findOne(gamePlayerId);
        if (isGuest(authentication) || !getCurrentPlayer(authentication).getGamePlayers().contains(gamePlayer)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (salvos.size() == 5) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Set turnList =  new HashSet();
        salvos.forEach(salvo -> {
            int turn = salvo.getTurn();
            turnList.add(turn);
        });
        if (turnList.size() != 1) {

            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        salvos.forEach(salvo -> {
            gamePlayer.addSalvo(salvo);
            salvoRepository.save(salvo);
        });

        gamePlayerRepo.save(gamePlayer);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @RequestMapping(value = "/api/game_view/{gamePlayerId}", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getGameInfo(Authentication authentication, @PathVariable long gamePlayerId) {
        GamePlayer gamePlayer = gamePlayerRepo.findOne(gamePlayerId);

        Map<String, Object> result = new HashMap<>();
        if(!isGuest(authentication) && getCurrentPlayer(authentication).getGamePlayers().contains(gamePlayer)) {
            result.put("id", gamePlayer.getGame().getId());
            result.put("date", gamePlayer.getGame().getCreated());
            result.put("gamePlayers", getListOfGamePlayers(gamePlayer.getGame().getGamePlayers()));
            result.put("ships", getShipInfo(gamePlayer));
            result.put("salvos", getSalvoInfo(gamePlayer.getGame().getGamePlayers()));

            return new ResponseEntity<>(result, HttpStatus.OK);
        } else {
            result.put("error", "You have no permission");

            return new ResponseEntity<>(result, HttpStatus.FORBIDDEN);
        }
    }


    private List<Map<String, Object>> getSalvoInfo(Set<GamePlayer> gamePlayers) {
        List<Map<String, Object>> result =  new ArrayList<>();
        gamePlayers.forEach(gamePlayer -> {
            gamePlayer.getsalvos().forEach(salvo -> {
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


    @RequestMapping(path = "api/games", method = RequestMethod.GET)
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
            result.put("score", gamePlayer.getScore() != null ? gamePlayer.getScore().getScore() : null);

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


    private Player getCurrentPlayer(Authentication authentication) {
        return playerRepository.findByUserName(authentication.getName());
    }


    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }
}


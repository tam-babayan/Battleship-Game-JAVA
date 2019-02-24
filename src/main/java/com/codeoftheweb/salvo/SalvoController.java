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
    private GameRepository gameRepository;
    @Autowired
    private GamePlayerRepository gamePlayerRepo;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private ShipRepository shipRepository;
    @Autowired
    private SalvoRepository salvoRepository;
    @Autowired
    private ScoreRepository scoreRepository;


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
        if (isGuest(authentication)) {
            return new ResponseEntity<>(makeMap("error", "Username is not registered"), HttpStatus.UNAUTHORIZED);
        }

        Game newGame = new Game(new Date());
        gameRepository.save(newGame);
        GamePlayer newGamePlayer = new GamePlayer(newGame, this.getCurrentPlayer(authentication));
        gamePlayerRepo.save(newGamePlayer);
        return new ResponseEntity<>(makeMap("gpid", newGamePlayer.getId()), HttpStatus.OK);

    }

    @RequestMapping(path = "/api/game/{gameId}/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> joinGame(Authentication authentication, @PathVariable long gameId) {
        if (isGuest(authentication)) {
            return new ResponseEntity<>(makeMap("error", "Username is not registered"), HttpStatus.UNAUTHORIZED);
        }
        Game currentGame = gameRepository.findOne(gameId);

        if (currentGame == null) {
            return new ResponseEntity<>(makeMap("error", "No such game"), HttpStatus.FORBIDDEN);
        }

        if (currentGame.getGamePlayers().iterator().next().getPlayer().getId() == this.getCurrentPlayer(authentication).getId()) {
            return new ResponseEntity<>(makeMap("error", "Can not join your Game"), HttpStatus.FORBIDDEN);
        }

        if (currentGame.getGamePlayers().size() != 1) {
            return new ResponseEntity<>(makeMap("error", "Game is full"), HttpStatus.FORBIDDEN);
        }

        GamePlayer newGamePlayer = new GamePlayer(currentGame, this.getCurrentPlayer(authentication));
        gamePlayerRepo.save(newGamePlayer);
        return new ResponseEntity<>(makeMap("gpid", newGamePlayer.getId()), HttpStatus.OK);

    }


    @RequestMapping(value = "/games/players/{gamePlayerId}/ships", method = RequestMethod.POST)
    public ResponseEntity<Void> addShip(Authentication authentication, @PathVariable long gamePlayerId,
                                        @RequestBody Ship ship) {
        GamePlayer gamePlayer = gamePlayerRepo.findOne(gamePlayerId);

        if (isGuest(authentication) || !this.getCurrentPlayer(authentication).getGamePlayers().contains(gamePlayer)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        if (gamePlayer.getGame().getState() != GameState.PLACE_SHIPS) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        ShipType type = ShipType.value(ship.getShipType());

        if (gamePlayer.getShips()
                .stream()
                .anyMatch(existingShip -> type == ShipType.value(existingShip.getShipType()))
                || ShipType.value(ship.getShipType()) == ShipType.UNKNOWN
                || ShipType.getLegth(type) != ship.getShipLocations().size()
                || gamePlayer.getShips().size() >= 5
        ) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        gamePlayer.addShip(ship);
        shipRepository.save(ship);

        if (gamePlayer.getGame().getGamePlayers().size() == 2
                && gamePlayer.getGame().getGamePlayers()
                .stream()
                .allMatch(gamePlayer1 -> gamePlayer1.getShips().size() == 5)
        ) {
            gamePlayer.getGame().setState(GameState.WAIT_FOR_SALVOS);
        }

        gamePlayerRepo.save(gamePlayer);

        return new ResponseEntity<>(HttpStatus.OK);
    }


    @RequestMapping(value = "/games/players/{gamePlayerId}/salvos", method = RequestMethod.POST)
    public ResponseEntity<Void> fireSalvo(Authentication authentication, @PathVariable long gamePlayerId,
                                          @RequestBody Salvo salvo) {
        GamePlayer gamePlayer = gamePlayerRepo.findOne(gamePlayerId);
        if (isGuest(authentication) || !getCurrentPlayer(authentication).getGamePlayers().contains(gamePlayer)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        if (gamePlayer.getGame().getState() != GameState.WAIT_FOR_SALVOS) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        Set<Salvo> opponentSalvos = gamePlayer.getGame().getGamePlayers().stream()
                .filter(gamePlayer1 -> gamePlayer1.getId() != gamePlayer.getId())
                .findFirst().orElse(new GamePlayer())
                .getsalvos();

        Set<Salvo> salvos = gamePlayer.getsalvos();

        boolean canFire = salvos.size() == 0
                || opponentSalvos.stream()
                .anyMatch(salvo1 -> salvo1.getTurn() == salvo.getTurn() || Math.abs(salvo1.getTurn() - salvo.getTurn()) == 1);

        if (!canFire || salvo.getSalvoLocations().size() > 5) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        gamePlayer.addSalvo(salvo);
        salvoRepository.save(salvo);

        gamePlayerRepo.save(gamePlayer);
        Optional<Salvo> opponentLatestSalvo = opponentSalvos.stream().max(Comparator.comparingInt(Salvo::getTurn));

        if (opponentLatestSalvo.isPresent() && opponentLatestSalvo.get().getTurn() == salvo.getTurn()) {
            changeStateToGameOver(gamePlayer.getGame());

            gameRepository.save(gamePlayer.getGame());
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }


    private void changeStateToGameOver(Game game) {
        List<GamePlayer> winners = new ArrayList<>();
        game.getGamePlayers().forEach(gamePlayer -> {
            List<Map<String, Object>> ships = getOpponentSunkShips(game.getGamePlayers(), gamePlayer.getId());
            if (ships.stream().allMatch(ship -> ship.get("type") != null) && ships.size() == 5) {
                winners.add(gamePlayer);
            }
        });

        if (winners.size() == 2) {
            winners.forEach(gamePlayer -> {
                Score score = new Score(0.5);
                gamePlayer.getGame().addScore(score);
                gamePlayer.getPlayer().addScore(score);
                scoreRepository.save(score);
            });
        }
        if (winners.size() == 1) {
            GamePlayer winner = winners.get(0);

            Score score = new Score(1);
            winner.getGame().addScore(score);
            winner.getPlayer().addScore(score);
            scoreRepository.save(score);
            System.out.println("winner " + winner.getPlayer().getUserName());
        }

        if (!winners.isEmpty()) {
            game.setState(GameState.GAME_OVER);
        }
    }


    @RequestMapping(value = "/api/game_view/{gamePlayerId}", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getGameInfo(Authentication authentication, @PathVariable long gamePlayerId) {
        GamePlayer gamePlayer = gamePlayerRepo.findOne(gamePlayerId);

        Map<String, Object> result = new HashMap<>();
        if (isGuest(authentication) || !getCurrentPlayer(authentication).getGamePlayers().contains(gamePlayer)) {
            result.put("error", "You have no permission");

            return new ResponseEntity<>(result, HttpStatus.FORBIDDEN);
        }

        result.put("id", gamePlayer.getGame().getId());
        result.put("date", gamePlayer.getGame().getCreated());
        result.put("gamePlayers", getListOfGamePlayers(gamePlayer.getGame().getGamePlayers()));
        result.put("ships", getShipInfo(gamePlayer));
        result.put("salvos", getSalvoInfo(gamePlayer.getGame().getGamePlayers()));
        result.put("opponentShips", getOpponentSunkShips(gamePlayer.getGame().getGamePlayers(), gamePlayerId));
        return new ResponseEntity<>(result, HttpStatus.OK);

    }

    private List<Map<String, Object>> getOpponentSunkShips(Set<GamePlayer> gamePlayers, long gamePlayerId) {
        List<Map<String, Object>> ships = new ArrayList<>();
        Optional<GamePlayer> opponent = gamePlayers
                .stream()
                .filter(gamePlayer -> gamePlayer.getId() != gamePlayerId)
                .findFirst();

        GamePlayer currentPlayer = gamePlayers
                .stream()
                .filter(gamePlayer -> gamePlayer.getId() == gamePlayerId)
                .findFirst()
                .orElse(new GamePlayer());

        opponent.ifPresent(gamePlayer -> gamePlayer.getShips().forEach(ship -> {
            Map<String, Object> tempShip = new HashMap<>();
            List<String> hitLocations = getHitedLocations(ship, currentPlayer.getsalvos());

            if (hitLocations.size() > 0) {
                tempShip.put("locations", hitLocations);
                tempShip.put("type", hitLocations.size() == ship.getShipLocations().size() ? ship.getShipType() : null);

                ships.add(tempShip);
            }
        }));

        return ships;
    }

    private List<String> getHitedLocations(Ship ship, Set<Salvo> salvos) {
        List<String> locations = new ArrayList<>();

        ship.getShipLocations().forEach(location -> {
            salvos.forEach(salvo -> {
                if (salvo.getSalvoLocations().contains(location)) {
                    locations.add(location);
                }
            });
        });

        return locations;
    }

    private List<Map<String, Object>> getSalvoInfo(Set<GamePlayer> gamePlayers) {
        List<Map<String, Object>> result = new ArrayList<>();
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
    public List<Map<String, Object>> createLeaderBoardJSON() {
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
            if (score.getScore() == playerScore) {
                count++;
            }
        }
        return count;
    }


    private double getTotal(Player player) {
        double sum = 0;
        for (Score score : player.getScores()) {
            sum += score.getScore();
        }
        return sum;
    }


    @RequestMapping(path = "api/games", method = RequestMethod.GET)
    public Map<String, Object> getAllGames(Authentication authentication) {
        Map<String, Object> result = new HashMap<>();

        if (!isGuest(authentication)) {
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

        gameRepository.findAll().forEach(game -> {

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
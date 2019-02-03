package com.codeoftheweb.salvo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class SalvoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SalvoApplication.class, args);
    }

    @Bean
    public CommandLineRunner initDat(PlayerRepository playerRepository, GameRepository gameRepository,
                                     GamePlayerRepository gamePlayerRepository, ShipRepository shipRepository,
                                     SalvoRepository salvoRepository) {
        return (args) -> {

            // initialize and save Player
            Player firstPlayer = new Player("j.bauer@ctu.gov ");
            Player secondPlayer = new Player("c.obrian@ctu.gov");
            Player thirdPlayer = new Player("kim_bauer@mail.com");
            Player fourthPlayer = new Player("t.almeida@ctu.gov");
            Player fifthPlayer = new Player("c.obrian@ctu.gov");
            Player sixthPlayer = new Player("kim_bauer@mail.com");
            playerRepository.save(firstPlayer);
            playerRepository.save(secondPlayer);
            playerRepository.save(thirdPlayer);
            playerRepository.save(fourthPlayer);
            playerRepository.save(fifthPlayer);
            playerRepository.save(sixthPlayer);


            // initialize and save Game
            Game firstGame = new Game(new Date());
            Game secondGame = new Game(new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1)));
            Game thirdGame = new Game(new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(2)));
            gameRepository.save(firstGame);
            gameRepository.save(secondGame);
            gameRepository.save(thirdGame);


            // initialize and save new GamePlayer
            GamePlayer gp1 = new GamePlayer(firstGame, firstPlayer);
            GamePlayer gp2 = new GamePlayer(firstGame, secondPlayer);
            GamePlayer gp3 = new GamePlayer(secondGame, thirdPlayer);
            GamePlayer gp4 = new GamePlayer(secondGame, fourthPlayer);
            GamePlayer gp5 = new GamePlayer(thirdGame, fifthPlayer);
            GamePlayer gp6 = new GamePlayer(thirdGame, sixthPlayer);
            gamePlayerRepository.save(gp1);
            gamePlayerRepository.save(gp2);
            gamePlayerRepository.save(gp3);
            gamePlayerRepository.save(gp4);
            gamePlayerRepository.save(gp5);
            gamePlayerRepository.save(gp6);


            // initialize ship locations
            List<String> shipLocation1_0 = new ArrayList<>();
            List<String> shipLocation1_1 = new ArrayList<>();
            List<String> shipLocation1_2 = new ArrayList<>();
            List<String> shipLocation1_3 = new ArrayList<>();
            List<String> shipLocation2_0 = new ArrayList<>();
            List<String> shipLocation2_1 = new ArrayList<>();
            List<String> shipLocation2_2 = new ArrayList<>();
            List<String> shipLocation2_3 = new ArrayList<>();
            List<String> shipLocation3_0 = new ArrayList<>();
            List<String> shipLocation3_1 = new ArrayList<>();
            List<String> shipLocation3_2 = new ArrayList<>();
            List<String> shipLocation3_3 = new ArrayList<>();
            List<String> shipLocation4_0 = new ArrayList<>();
            List<String> shipLocation4_1 = new ArrayList<>();
            List<String> shipLocation4_2 = new ArrayList<>();
            List<String> shipLocation4_3 = new ArrayList<>();
            List<String> shipLocation5 = new ArrayList<>();
            List<String> shipLocation6 = new ArrayList<>();


            //create location for ships
            shipLocation1_0.add("H2");
            shipLocation1_0.add("H3");
            shipLocation1_0.add("H4");
            shipLocation1_1.add("B9");
            shipLocation1_1.add("C9");
            shipLocation1_2.add("B3");
            shipLocation1_2.add("C3");
            shipLocation1_2.add("D3");
            shipLocation1_2.add("E3");
            shipLocation1_3.add("I7");

            shipLocation2_0.add("G2");
            shipLocation2_0.add("G3");
            shipLocation2_0.add("G4");
            shipLocation2_1.add("B9");
            shipLocation2_1.add("B10");
            shipLocation2_2.add("B3");
            shipLocation2_2.add("C3");
            shipLocation2_2.add("D3");
            shipLocation2_2.add("E3");
            shipLocation2_3.add("I7");

            shipLocation3_0.add("C6");
            shipLocation3_0.add("C7");
            shipLocation3_1.add("B9");
            shipLocation3_1.add("B10");
            shipLocation3_2.add("B3");
            shipLocation3_2.add("E3");
            shipLocation3_2.add("F3");
            shipLocation3_2.add("G3");
            shipLocation3_3.add("I7");

            shipLocation4_0.add("G7");
            shipLocation4_0.add("H7");
            shipLocation4_1.add("B9");
            shipLocation4_1.add("B10");
            shipLocation4_2.add("B3");
            shipLocation4_2.add("E3");
            shipLocation4_2.add("F3");
            shipLocation4_2.add("G3");
            shipLocation4_3.add("I7");

            shipLocation5.add("A2");
            shipLocation5.add("A3");
            shipLocation5.add("A4");

            shipLocation6.add("E1");
            shipLocation6.add("F1");
            shipLocation6.add("G1");

            // initialize and save ship
            Ship ship1_0 = new Ship(shipLocation1_0, "Destroyer");
            Ship ship1_1 = new Ship(shipLocation1_1, "Submarine");
            Ship ship1_2 = new Ship(shipLocation1_2, "Patrol Boat");
            Ship ship1_3 = new Ship(shipLocation1_3, "Patrol Boat");
            Ship ship2_0 = new Ship(shipLocation2_0, "Patrol Boat");
            Ship ship2_1 = new Ship(shipLocation2_1, "Submarine");
            Ship ship2_2 = new Ship(shipLocation2_2, "Patrol Boat");
            Ship ship2_3 = new Ship(shipLocation2_3, "Patrol Boat");
            Ship ship3_0 = new Ship(shipLocation3_0, "Patrol Boat");
            Ship ship3_1 = new Ship(shipLocation3_1, "Submarine");
            Ship ship3_2 = new Ship(shipLocation3_2, "Patrol Boat");
            Ship ship3_3 = new Ship(shipLocation3_3, "Patrol Boat");
            Ship ship4_0 = new Ship(shipLocation4_0, "Patrol Boat");
            Ship ship4_1 = new Ship(shipLocation4_1, "Submarine");
            Ship ship4_2 = new Ship(shipLocation4_2, "Patrol Boat");
            Ship ship4_3 = new Ship(shipLocation4_3, "Patrol Boat");
            Ship ship5 = new Ship(shipLocation5, "Patrol Boat");
            Ship ship6 = new Ship(shipLocation6, "Submarine");


            // adding ship into GamePlayer
            gp1.addShip(ship1_0);
            gp1.addShip(ship1_1);
            gp1.addShip(ship1_2);
            gp1.addShip(ship1_3);
            gp2.addShip(ship2_0);
            gp2.addShip(ship2_1);
            gp2.addShip(ship2_2);
            gp2.addShip(ship2_3);
            gp3.addShip(ship3_0);
            gp3.addShip(ship3_1);
            gp3.addShip(ship3_2);
            gp3.addShip(ship3_3);
            gp4.addShip(ship4_0);
            gp4.addShip(ship4_1);
            gp4.addShip(ship4_2);
            gp4.addShip(ship4_3);
            gp5.addShip(ship5);
            gp6.addShip(ship6);

            // saving ships in shipRepository
            shipRepository.save(ship1_0);
            shipRepository.save(ship1_1);
            shipRepository.save(ship1_2);
            shipRepository.save(ship1_3);
            shipRepository.save(ship2_0);
            shipRepository.save(ship2_1);
            shipRepository.save(ship2_2);
            shipRepository.save(ship2_3);
            shipRepository.save(ship3_0);
            shipRepository.save(ship3_1);
            shipRepository.save(ship3_2);
            shipRepository.save(ship3_3);
            shipRepository.save(ship4_0);
            shipRepository.save(ship4_1);
            shipRepository.save(ship4_2);
            shipRepository.save(ship4_3);
            shipRepository.save(ship5);
            shipRepository.save(ship6);


            // initialize salvo locations
            List<String> salvoLocation1_0 = new ArrayList<>();
            List<String> salvoLocation1_1 = new ArrayList<>();
            List<String> salvoLocation2_0 = new ArrayList<>();
            List<String> salvoLocation2_1 = new ArrayList<>();
            List<String> salvoLocation3_0 = new ArrayList<>();
            List<String> salvoLocation3_1 = new ArrayList<>();
            List<String> salvoLocation4_0 = new ArrayList<>();
            List<String> salvoLocation4_1 = new ArrayList<>();
            List<String> salvoLocation5 = new ArrayList<>();
            List<String> salvoLocation6 = new ArrayList<>();

            //create location for salvoes
            salvoLocation1_0.add("H9");
            salvoLocation1_1.add("A6");
            salvoLocation2_0.add("A1");
            salvoLocation2_1.add("B10");
            salvoLocation3_0.add("C8");
            salvoLocation3_1.add("B1");
            salvoLocation4_0.add("C8");
            salvoLocation4_1.add("D7");
            salvoLocation5.add("E1");
            salvoLocation6.add("I9");

            // initialize and save salvoes
            Salvo salvo1_0 = new Salvo(gp1, salvoLocation1_0, 1);
            Salvo salvo1_1 = new Salvo(gp1, salvoLocation1_1, 2);
            Salvo salvo2_0 = new Salvo(gp2, salvoLocation2_0, 1);
            Salvo salvo2_1 = new Salvo(gp2, salvoLocation2_1, 2);
            Salvo salvo3_0 = new Salvo(gp3, salvoLocation3_0, 1);
            Salvo salvo3_1 = new Salvo(gp3, salvoLocation3_1, 2);
            Salvo salvo4_0 = new Salvo(gp4, salvoLocation4_0, 1);
            Salvo salvo4_1 = new Salvo(gp4, salvoLocation4_1, 2);
            Salvo salvo5 = new Salvo(gp5, salvoLocation6, 1);
            Salvo salvo6 = new Salvo(gp6, salvoLocation6, 2);

            // adding salvo into GamePlayer
            gp1.addSalvo(salvo1_0);
            gp1.addSalvo(salvo1_1);
            gp2.addSalvo(salvo2_0);
            gp2.addSalvo(salvo2_1);
            gp3.addSalvo(salvo3_0);
            gp3.addSalvo(salvo3_1);
            gp4.addSalvo(salvo4_0);
            gp4.addSalvo(salvo4_1);
            gp5.addSalvo(salvo5);
            gp6.addSalvo(salvo6);

            // saving salvoes in salvoRepository
            salvoRepository.save(salvo1_0);
            salvoRepository.save(salvo1_1);
            salvoRepository.save(salvo2_0);
            salvoRepository.save(salvo2_1);
            salvoRepository.save(salvo3_0);
            salvoRepository.save(salvo3_1);
            salvoRepository.save(salvo4_0);
            salvoRepository.save(salvo4_1);
            salvoRepository.save(salvo5);
            salvoRepository.save(salvo6);

        };
    }
}


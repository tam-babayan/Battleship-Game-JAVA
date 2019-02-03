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
									 GamePlayerRepository gamePlayerRepository, ShipRepository shipRepository) {
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
			List<String> location1_0 = new ArrayList<>();
			List<String> location1_1 = new ArrayList<>();
			List<String> location1_2 = new ArrayList<>();
			List<String> location1_3 = new ArrayList<>();
			List<String> location2_0  = new ArrayList<>();
			List<String> location2_1  = new ArrayList<>();
			List<String> location2_2 = new ArrayList<>();
			List<String> location2_3 = new ArrayList<>();
			List<String> location3_0  = new ArrayList<>();
			List<String> location3_1  = new ArrayList<>();
			List<String> location3_2 = new ArrayList<>();
			List<String> location3_3 = new ArrayList<>();
			List<String> location4_0  = new ArrayList<>();
			List<String> location4_1  = new ArrayList<>();
			List<String> location4_2 = new ArrayList<>();
			List<String> location4_3 = new ArrayList<>();
			List<String> location5  = new ArrayList<>();
			List<String> location6  = new ArrayList<>();


			//create location for ships
			location1_0.add("H2");
			location1_0.add("H3");
			location1_0.add("H4");
			location1_1.add("B9");
			location1_1.add("C9");
			location1_2.add("B3");
			location1_2.add("C3");
			location1_2.add("D3");
			location1_2.add("E3");
			location1_3.add("I7");

			location2_0.add("G2");
			location2_0.add("G3");
			location2_0.add("G4");
			location2_1.add("B9");
			location2_1.add("B10");
			location2_2.add("B3");
			location2_2.add("C3");
			location2_2.add("D3");
			location2_2.add("E3");
			location2_3.add("I7");

			location3_0.add("C6");
			location3_0.add("C7");
			location3_1.add("B9");
			location3_1.add("B10");
			location3_2.add("B3");
			location3_2.add("E3");
			location3_2.add("F3");
			location3_2.add("G3");
			location3_3.add("I7");

			location4_0.add("G7");
			location4_0.add("H7");
			location4_1.add("B9");
			location4_1.add("B10");
			location4_2.add("B3");
			location4_2.add("E3");
			location4_2.add("F3");
			location4_2.add("G3");
			location4_3.add("I7");

			location5.add("A2");
			location5.add("A3");
			location5.add("A4");

			location6.add("E1");
			location6.add("F1");
			location6.add("G1");

			// initialize and save ship
			Ship ship1_0 = new Ship(location1_0, "Destroyer");
			Ship ship1_1 = new Ship(location1_1, "Submarine");
			Ship ship1_2 = new Ship(location1_2, "Patrol Boat");
			Ship ship1_3 = new Ship(location1_3, "Patrol Boat");
			Ship ship2_0 = new Ship(location2_0, "Patrol Boat");
			Ship ship2_1 = new Ship(location2_1, "Submarine");
			Ship ship2_2 = new Ship(location2_2, "Patrol Boat");
			Ship ship2_3 = new Ship(location2_3, "Patrol Boat");
			Ship ship3_0 = new Ship(location3_0, "Patrol Boat");
			Ship ship3_1 = new Ship(location3_1, "Submarine");
			Ship ship3_2 = new Ship(location3_2, "Patrol Boat");
			Ship ship3_3 = new Ship(location3_3, "Patrol Boat");
			Ship ship4_0 = new Ship(location4_0, "Patrol Boat");
			Ship ship4_1 = new Ship(location4_1, "Submarine");
			Ship ship4_2 = new Ship(location4_2, "Patrol Boat");
			Ship ship4_3 = new Ship(location4_3, "Patrol Boat");
			Ship ship5 = new Ship(location5, "Patrol Boat");
			Ship ship6 = new Ship(location6, "Submarine");


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
		};
	}
}


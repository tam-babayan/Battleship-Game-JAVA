package com.codeoftheweb.salvo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}

	@Bean
	public CommandLineRunner initDat(PlayerRepository playerRepository, GameRepository gameRepository,
									 GamePlayerRepository gamePlayerRepository, ShipRepository shipRepository) {
		return (args) -> {

			// First Player
			// initialize and save Player
			Player firstPlayer = new Player("j.bauer@ctu.gov ");
			playerRepository.save(firstPlayer);

			// initialize and save Game
			Game firstGame = new Game(new Date());
			gameRepository.save(firstGame);

			// create location for ship
			List<String> location1  = new ArrayList<>();
			location1.add("H2");
			location1.add("H3");
			location1.add("H4");

			// initialize and save ship
			Ship ship1 = new Ship(location1, "Destroyer");

			// initialize and save new GamePlayer
			GamePlayer gp1 = new GamePlayer(firstGame, firstPlayer);
			gamePlayerRepository.save(gp1);

			// adding ship into GamePlayer
			gp1.addShip(ship1);
			shipRepository.save(ship1);

			// saving the GamePlayer
			gamePlayerRepository.save(gp1);




			// Second Player
			// initialize and save Player
			Player secondPlayer = new Player("c.obrian@ctu.gov");
			playerRepository.save(secondPlayer);

			// initialize and save Game
			Game secondGame = new Game(new Date());
			gameRepository.save(secondGame);

			// create location for ship
			List<String> location2  = new ArrayList<>();
			location2.add("B5");
			location2.add("C5");
			location2.add("D5");

			// initialize and save ship
			Ship ship2 = new Ship(location2, "Patrol Boat");

			// initialize and save new GamePlayer
			GamePlayer gp2 = new GamePlayer(secondGame, secondPlayer);
			gamePlayerRepository.save(gp2);

			// adding ship into GamePlayer
			gp2.addShip(ship2);
			shipRepository.save(ship2);

			// saving the GamePlayer
			gamePlayerRepository.save(gp2);




			// Third player
			// initialize and save Player
			Player thirdPlayer = new Player("kim_bauer@mail.com");
			playerRepository.save(thirdPlayer);

			// initialize and save Game
			Game thirdGame = new Game(new Date());
			gameRepository.save(thirdGame);

			// create location for ship
			List<String> location3  = new ArrayList<>();
			location3.add("C6");
			location3.add("C7");

			// initialize and save ship
			Ship ship3 = new Ship(location3, "Submarine");

			// initialize and save new GamePlayer
			GamePlayer gp3 = new GamePlayer(thirdGame, thirdPlayer);
			gamePlayerRepository.save(gp3);

			// adding ship into GamePlayer
			gp3.addShip(ship3);
			shipRepository.save(ship3);

			// saving the GamePlayer
			gamePlayerRepository.save(gp3);




			// Forth player
			// initialize and save Player
			Player forthPlayer = new Player("t.almeida@ctu.gov");
			playerRepository.save(forthPlayer);

			// initialize and save Game
			Game forthGame = new Game(new Date());
			gameRepository.save(forthGame);

			// create location for ship
			List<String> location4  = new ArrayList<>();
			location4.add("G6");
			location4.add("H7");

			// initialize and save ship
			Ship ship4 = new Ship(location4, "Patrol Boat ");

			// initialize and save new GamePlayer
			GamePlayer gp4 = new GamePlayer(forthGame, forthPlayer);
			gamePlayerRepository.save(gp4);

			// adding ship into GamePlayer
			gp4.addShip(ship4);
			shipRepository.save(ship4);

			// saving the GamePlayer
			gamePlayerRepository.save(gp4);
		};
	}
}


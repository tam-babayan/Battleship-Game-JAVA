package com.codeoftheweb.salvo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}

	@Bean
	public CommandLineRunner initDat(PlayerRepository playerRepository, GameRepository gameRepository, GamePlayerRepository gamePlayerRepository) {
		return (args) -> {
			// save a couple of players
			Player firstPlayer = new Player("j.bauer@ctu.gov");
			playerRepository.save(firstPlayer);
			Game firstGame = new Game(new Date());
			gameRepository.save(firstGame);
			GamePlayer gp1 = new GamePlayer(firstGame, firstPlayer);
			gamePlayerRepository.save(gp1);

			Player secondPlayer = new Player("c.obrian@ctu.gov");
			playerRepository.save(secondPlayer);
			GamePlayer gp2 = new GamePlayer(firstGame, secondPlayer);
			gamePlayerRepository.save(gp2);

			Player thirdPlayer = new Player("j.bauer@ctu.gov");
			playerRepository.save(thirdPlayer);
			Game secondGame = new Game(new Date());
			gameRepository.save(secondGame);
			GamePlayer gp3 = new GamePlayer(secondGame, thirdPlayer);
			gamePlayerRepository.save(gp3);

			Player forthPlayer = new Player("k.deur@ctu.gov");
			playerRepository.save(forthPlayer);
			GamePlayer gp4 = new GamePlayer(secondGame, forthPlayer);
			gamePlayerRepository.save(gp4);
		};
	}


}


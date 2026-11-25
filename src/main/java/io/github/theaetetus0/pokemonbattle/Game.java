package io.github.theaetetus0.pokemonbattle;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

/** The whole game: choose a starter, then battle wild Pokemon until the player stops or loses. */
public class Game {

  static final int POTIONS_PER_BATTLE = 3;

  private final GameData data;
  private final Console console;
  private final Random random;
  private final CaughtLog caughtLog;

  public Game(GameData data, Console console, Random random, CaughtLog caughtLog) {
    this.data = data;
    this.console = console;
    this.random = random;
    this.caughtLog = caughtLog;
  }

  public void play() {
    console.println("\nWelcome to Pokemon!");
    Player player = null;

    try {
      int starterIndex = chooseStarter();
      player = new Player(data.species().get(starterIndex).create());
      Pokemon pokemon = player.getPokemon();

      while (true) {
        player.getBag().setPotions(POTIONS_PER_BATTLE);
        Pokemon wild = chooseWild(starterIndex).create();

        Battle battle = new Battle(pokemon, wild, player.getBag(), data.typeChart(), console, random);
        Battle.Outcome outcome = battle.run();

        if (outcome == Battle.Outcome.CAUGHT) {
          player.addCaught(new CaughtPokemon(wild.getName(), battle.getPokeballsThrown()));
        } else if (outcome == Battle.Outcome.LOST) {
          console.println("Game Over!");
          break;
        }

        console.println("\nWould you like to find another wild Pokemon?");
        console.println("1 - Yes");
        console.println("2 - No");
        console.println("\nChoose an option:");
        if (console.readChoice(2) == 2) {
          break;
        }

        pokemon.restore();
        console.println("\nNurse Joy healed " + pokemon.getName() + "!");
      }

    } catch (NoSuchElementException e) {
      // input ended (e.g. Ctrl+D) - still save what was caught
      console.println("\nNo more input, ending the game.");
    }

    if (player != null) {
      save(player.getCaught());
    }
    console.println("\nThank you for playing Pokemon!");
  }

  /** Shows the starter menu and returns the index of the chosen species. */
  private int chooseStarter() {
    List<Species> species = data.species();

    console.println("\nChoose a Pokemon!  (1-" + species.size() + ")");
    for (String line : data.typeChart().describe()) {
      console.println(line);
    }
    console.println("");

    for (int i = 0; i < species.size(); i++) {
      Species s = species.get(i);
      console.println((i + 1) + " - " + s.name() + ", Type: " + s.type() + ", Health: " + s.health()
          + ", Attack: " + s.attack() + ", Defence: " + s.defence());
    }
    console.println("\nChoose A Pokemon: ");

    return console.readChoice(species.size()) - 1;
  }

  /** A random species, never the same one as the player's starter. */
  Species chooseWild(int starterIndex) {
    int index = random.nextInt(data.species().size() - 1);
    if (index >= starterIndex) {
      index++;
    }
    return data.species().get(index);
  }

  private void save(List<CaughtPokemon> caught) {
    try {
      caughtLog.append(caught);
    } catch (IOException e) {
      console.println("An error occurred when writing to file: " + e.getMessage());
    }
  }
}

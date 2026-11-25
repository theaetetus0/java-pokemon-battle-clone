package io.github.theaetetus0.pokemonbattle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import io.github.theaetetus0.pokemonbattle.TestData.FixedRandom;
import io.github.theaetetus0.pokemonbattle.TestData.TestConsole;

class GameTest {

  @TempDir
  Path tempDir;

  private final GameData data = GameData.loadDefault();

  private Path caughtFile() {
    return tempDir.resolve("caught.txt");
  }

  private TestConsole play(String input, FixedRandom random) {
    TestConsole console = new TestConsole(input);
    new Game(data, console, random, new CaughtLog(caughtFile())).play();
    return console;
  }

  @Test
  void starterMenuListsEverySpeciesAndTheTypeChart() {
    String output = play("", new FixedRandom(0, 0.0)).output();

    assertTrue(output.contains("Choose a Pokemon!  (1-10)"));
    assertTrue(output.contains("Fire is strong against Grass, but weak against Water"));
    assertTrue(output.contains("1 - Charmander, Type: Fire, Health: 39, Attack: 52, Defence: 43"));
    assertTrue(output.contains("10 - Talonflame, Type: Fire, Health: 78, Attack: 81, Defence: 71"));
  }

  @Test
  void catchingAPokemonSavesIt() throws IOException {
    // choose Charmander, throw a Pokeball (always catches), then stop
    String output = play("1\n6\n2\n", new FixedRandom(0, 0.0)).output();

    assertTrue(output.contains("You encountered a wild Squirtle!"));
    assertTrue(output.contains("Thank you for playing Pokemon!"));
    List<String> lines = Files.readAllLines(caughtFile());
    assertEquals("Pokemon Caught: Squirtle, Pokeballs Used: 1", lines.get(1));
  }

  @Test
  void catchesFromSeveralBattlesAreSavedTogether() throws IOException {
    play("1\n6\n1\n6\n2\n", new FixedRandom(0, 0.0));

    List<String> lines = Files.readAllLines(caughtFile());
    assertEquals(3, lines.size(), "one session header and two catches");
  }

  @Test
  void pokemonIsHealedAndPotionsRefilledBetweenBattles() {
    // battle 1: use a potion after the wild Pokemon's first hit, then catch it; battle 2: check the menu
    String output = play("1\n1\n5\n6\n1\n6\n2\n", new FixedRandom(0, 0.0)).output();

    assertTrue(output.contains("Nurse Joy healed Charmander!"));
    String secondBattle = output.substring(output.lastIndexOf("You encountered"));
    assertTrue(secondBattle.contains("Health: 39/39"));
    assertTrue(secondBattle.contains("Potion, Heal 20 Health (3 left)"));
  }

  @Test
  void caughtPokemonAreSavedWhenInputEnds() throws IOException {
    String output = play("1\n6\n", new FixedRandom(0, 0.0)).output();

    assertTrue(output.contains("No more input, ending the game."));
    assertTrue(Files.readString(caughtFile()).contains("Pokemon Caught: Squirtle"));
  }

  @Test
  void nothingIsSavedIfNothingWasCaught() {
    play("1\n", new FixedRandom(0, 0.99));
    assertFalse(Files.exists(caughtFile()));
  }

  @Test
  void wildPokemonIsNeverTheStarter() {
    Game game = new Game(data, new TestConsole(""), new FixedRandom(0, 0.0), new CaughtLog(caughtFile()));
    for (int starter = 0; starter < data.species().size(); starter++) {
      assertNotEquals(data.species().get(starter), game.chooseWild(starter));
    }
  }

  @Test
  void everyOtherSpeciesCanBeTheWildPokemon() {
    int starter = 3;
    for (int roll = 0; roll < data.species().size() - 1; roll++) {
      Game game = new Game(data, new TestConsole(""), new FixedRandom(roll, 0.0), new CaughtLog(caughtFile()));
      Species wild = game.chooseWild(starter);
      int expected = roll < starter ? roll : roll + 1;
      assertEquals(data.species().get(expected), wild);
    }
  }
}

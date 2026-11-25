package io.github.theaetetus0.pokemonbattle;

import java.nio.file.Path;
import java.util.Random;
import java.util.Scanner;

public final class Main {

  private static final Path CAUGHT_FILE = Path.of("caughtPokemon.txt");

  private Main() {
  }

  public static void main(String[] args) {
    GameData data;
    try {
      data = GameData.loadDefault();
    } catch (GameDataException e) {
      System.err.println("Could not load game data: " + e.getMessage());
      System.exit(1);
      return;
    }

    Console console = new Console(new Scanner(System.in), System.out);
    new Game(data, console, new Random(), new CaughtLog(CAUGHT_FILE)).play();
  }
}

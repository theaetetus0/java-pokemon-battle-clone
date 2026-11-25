package io.github.theaetetus0.pokemonbattle;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

/** Shared helpers for building Pokemon, consoles and predictable randomness in tests. */
final class TestData {

  static final Move TACKLE = Move.attack("Tackle", "Normal");
  static final Move EMBER = Move.attack("Ember", "Fire");
  static final Move WATER_GUN = Move.attack("Water Gun", "Water");
  static final Move VINE_WHIP = Move.attack("Vine Whip", "Grass");
  static final Move POWER_UP = new Move("Power Up", "Normal", MoveCategory.RAISE_ATTACK, 10);
  static final Move GUARD = new Move("Guard", "Normal", MoveCategory.RAISE_DEFENCE, 5);

  // move index for each of the 4 moves every test species has
  static final int NORMAL_ATTACK = 0;
  static final int TYPE_ATTACK = 1;
  static final int ATTACK_BUFF = 2;
  static final int DEFENCE_BUFF = 3;

  private static final Map<String, Move> TYPE_MOVES = Map.of("Fire", EMBER, "Water", WATER_GUN, "Grass", VINE_WHIP);

  private TestData() {
  }

  /** A species with moves: Tackle, a move of its own type, Power Up (+10 attack), Guard (+5 defence). */
  static Species species(String name, String type, int health, int attack, int defence) {
    return new Species(name, type, health, attack, defence,
        List.of(TACKLE, TYPE_MOVES.get(type), POWER_UP, GUARD));
  }

  static Pokemon pokemon(String name, String type, int health, int attack, int defence) {
    return species(name, type, health, attack, defence).create();
  }

  /** The Fire / Water / Grass type chart. */
  static TypeChart typeChart() {
    TypeChart chart = new TypeChart();
    chart.set("Fire", "Water", 0.5);
    chart.set("Fire", "Grass", 2);
    chart.set("Water", "Fire", 2);
    chart.set("Water", "Grass", 0.5);
    chart.set("Grass", "Water", 2);
    chart.set("Grass", "Fire", 0.5);
    return chart;
  }

  /** A console that reads the given text and records everything printed. */
  static final class TestConsole extends Console {

    private final ByteArrayOutputStream output;

    private TestConsole(String input, ByteArrayOutputStream output) {
      super(new Scanner(input), new PrintStream(output, true, StandardCharsets.UTF_8));
      this.output = output;
    }

    TestConsole(String input) {
      this(input, new ByteArrayOutputStream());
    }

    String output() {
      return output.toString(StandardCharsets.UTF_8);
    }
  }

  /** Random that always returns the same values, so battles play out predictably. */
  static final class FixedRandom extends Random {

    private final int intValue;
    private final double doubleValue;

    /**
     * @param intValue    returned by nextInt(bound), limited to bound - 1
     * @param doubleValue returned by nextDouble(); 0.0 always catches, 0.99 never does
     */
    FixedRandom(int intValue, double doubleValue) {
      this.intValue = intValue;
      this.doubleValue = doubleValue;
    }

    @Override
    public int nextInt(int bound) {
      return Math.min(intValue, bound - 1);
    }

    @Override
    public double nextDouble() {
      return doubleValue;
    }
  }

  /** The same line repeated, e.g. to keep choosing the same move. */
  static String repeat(String line, int times) {
    return (line + "\n").repeat(times);
  }
}

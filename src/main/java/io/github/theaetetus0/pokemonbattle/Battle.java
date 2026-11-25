package io.github.theaetetus0.pokemonbattle;

import java.util.List;
import java.util.Random;

/** One battle between the player's Pokemon and a wild Pokemon. */
public class Battle {

  public enum Outcome {
    WON, CAUGHT, LOST
  }

  // catch chance ranges from MIN (wild Pokemon at full health) to MIN + RANGE (almost fainted)
  static final double MIN_CATCH_CHANCE = 0.1;
  static final double CATCH_CHANCE_RANGE = 0.8;

  private final Pokemon playerPokemon;
  private final Pokemon wildPokemon;
  private final Bag bag;
  private final TypeChart typeChart;
  private final Console console;
  private final Random random;

  private int pokeballsThrown;

  public Battle(Pokemon playerPokemon, Pokemon wildPokemon, Bag bag, TypeChart typeChart, Console console,
      Random random) {
    this.playerPokemon = playerPokemon;
    this.wildPokemon = wildPokemon;
    this.bag = bag;
    this.typeChart = typeChart;
    this.console = console;
    this.random = random;
  }

  public int getPokeballsThrown() {
    return pokeballsThrown;
  }

  /** Plays turns until one Pokemon faints or the wild Pokemon is caught. */
  public Outcome run() {
    console.println("\nYou encountered a wild " + wildPokemon.getName() + "!");

    while (true) {

      // ---- player's turn ----
      printStatus();
      printOptions();
      int choice = chooseOption();
      List<Move> moves = playerPokemon.getMoves();

      if (choice <= moves.size()) {
        useMove(playerPokemon, wildPokemon, moves.get(choice - 1), false);
      } else if (choice == potionOption()) {
        int healed = bag.usePotion(playerPokemon);
        console.println("\nYou take a potion from your bag! (" + bag.getPotions() + " left)");
        console.println(playerPokemon.getName() + " recovered " + healed + " health!");
      } else if (throwPokeball()) {
        console.println("Gotcha! The wild " + wildPokemon.getName() + " was caught!");
        return Outcome.CAUGHT;
      }

      if (wildPokemon.isFainted()) {
        console.println("\nThe wild " + wildPokemon.getName() + " fainted!");
        return Outcome.WON;
      }

      // ---- wild Pokemon's turn: a random move ----
      List<Move> wildMoves = wildPokemon.getMoves();
      useMove(wildPokemon, playerPokemon, wildMoves.get(random.nextInt(wildMoves.size())), true);

      if (playerPokemon.isFainted()) {
        console.println("\nYour " + playerPokemon.getName() + " fainted!");
        return Outcome.LOST;
      }
    }
  }

  /** Performs a move, printing what happens. */
  void useMove(Pokemon attacker, Pokemon defender, Move move, boolean attackerIsWild) {

    String attackerName = (attackerIsWild ? "The wild " : "") + attacker.getName();
    String defenderName = (attackerIsWild ? "" : "The wild ") + defender.getName();

    console.println("\n" + attackerName + " used " + move.name() + "!");

    switch (move.category()) {
      case ATTACK -> {
        double effectiveness = typeChart.effectiveness(move.type(), defender.getType());
        if (effectiveness > 1) {
          console.println("That was super effective!");
        } else if (effectiveness < 1) {
          console.println("That was not very effective.");
        }
        int damage = calculateDamage(attacker, defender, effectiveness);
        defender.takeDamage(damage);
        console.println(defenderName + " took " + damage + " damage!");
      }
      case RAISE_ATTACK -> {
        if (attacker.raiseAttack(move.amount()) > 0) {
          console.println(attackerName + "'s attack rose!");
        } else {
          console.println(attackerName + "'s attack won't go any higher!");
        }
      }
      case RAISE_DEFENCE -> {
        if (attacker.raiseDefence(move.amount()) > 0) {
          console.println(attackerName + "'s defence rose!");
        } else {
          console.println(attackerName + "'s defence won't go any higher!");
        }
      }
    }
  }

  /** Damage an attack does, given the type effectiveness multiplier. Always at least 1. */
  static int calculateDamage(Pokemon attacker, Pokemon defender, double effectiveness) {
    double damageRatio = (double) attacker.getAttack() / defender.getDefence();
    return (int) Math.round(1 + damageRatio * attacker.getAttack() / 5 * effectiveness);
  }

  /** Probability of catching a wild Pokemon: higher when its health is lower. */
  static double catchChance(Pokemon wildPokemon) {
    double healthRatio = (double) wildPokemon.getHealth() / wildPokemon.getMaxHealth();
    return MIN_CATCH_CHANCE + CATCH_CHANCE_RANGE * (1 - healthRatio);
  }

  /** Asks for an option, refusing a potion when one can't be used so the turn isn't wasted. */
  int chooseOption() {
    while (true) {
      int choice = console.readChoice(pokeballOption());

      if (choice == potionOption() && !bag.hasPotion()) {
        console.println("You're out of potions! Choose another move: ");
      } else if (choice == potionOption() && playerPokemon.getHealth() == playerPokemon.getMaxHealth()) {
        console.println(playerPokemon.getName() + " is already at full health! Choose another move: ");
      } else {
        return choice;
      }
    }
  }

  private boolean throwPokeball() {
    pokeballsThrown++;
    console.println("\nYou threw a Pokeball!");
    if (random.nextDouble() < catchChance(wildPokemon)) {
      return true;
    }
    console.println("Oh no! The wild " + wildPokemon.getName() + " broke free!");
    return false;
  }

  private int potionOption() {
    return playerPokemon.getMoves().size() + 1;
  }

  private int pokeballOption() {
    return playerPokemon.getMoves().size() + 2;
  }

  private void printStatus() {
    console.println("\n\nYour Pokemon: " + playerPokemon.getName());
    console.println("Attack: " + playerPokemon.getAttack() + ", Defence: " + playerPokemon.getDefence());
    console.println("Health: " + playerPokemon.getHealth() + "/" + playerPokemon.getMaxHealth());

    console.println("\nWild Pokemon: " + wildPokemon.getName());
    console.println("Attack: " + wildPokemon.getAttack() + ", Defence: " + wildPokemon.getDefence());
    console.println("Health: " + wildPokemon.getHealth() + "/" + wildPokemon.getMaxHealth());
  }

  private void printOptions() {
    console.println("\nMoves:  (1-" + pokeballOption() + ")");

    List<Move> moves = playerPokemon.getMoves();
    for (int i = 0; i < moves.size(); i++) {
      Move move = moves.get(i);
      console.println((i + 1) + " - " + move.name() + ", Type: " + move.type()
          + ", Category: " + move.category().label());
    }
    console.println(potionOption() + " - Potion, Heal " + Bag.POTION_HEAL_AMOUNT + " Health ("
        + bag.getPotions() + " left)");
    console.println(pokeballOption() + " - Throw A Pokeball");

    console.println("\nChoose A Move: ");
  }
}

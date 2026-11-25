package io.github.theaetetus0.pokemonbattle;

import java.util.List;

/** The fixed data for a kind of Pokemon: its type, starting stats and moves. */
public record Species(String name, String type, int health, int attack, int defence, List<Move> moves) {

  public static final int MAX_MOVES = 4;

  public Species {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("species name must not be blank");
    }
    if (type == null || type.isBlank()) {
      throw new IllegalArgumentException(name + " must have a type");
    }
    if (health <= 0 || attack <= 0 || defence <= 0) {
      throw new IllegalArgumentException(name + " must have positive health, attack and defence");
    }
    if (moves.isEmpty() || moves.size() > MAX_MOVES) {
      throw new IllegalArgumentException(name + " must have between 1 and " + MAX_MOVES + " moves");
    }
    moves = List.copyOf(moves);
  }

  /** A new Pokemon of this species at full health. */
  public Pokemon create() {
    return new Pokemon(this);
  }
}

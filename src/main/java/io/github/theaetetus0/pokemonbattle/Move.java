package io.github.theaetetus0.pokemonbattle;

import java.util.Objects;

/**
 * A move a Pokemon can use in battle.
 *
 * @param amount how much a buff move raises its stat by; always 0 for attacks
 */
public record Move(String name, String type, MoveCategory category, int amount) {

  public Move {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("move name must not be blank");
    }
    if (type == null || type.isBlank()) {
      throw new IllegalArgumentException(name + " must have a type");
    }
    Objects.requireNonNull(category, "category");
    if (category.isBuff() && amount <= 0) {
      throw new IllegalArgumentException(name + " is a buff and needs a positive amount");
    }
    if (!category.isBuff() && amount != 0) {
      throw new IllegalArgumentException(name + " is an attack and should not have an amount");
    }
  }

  public static Move attack(String name, String type) {
    return new Move(name, type, MoveCategory.ATTACK, 0);
  }
}

package io.github.theaetetus0.pokemonbattle;

/** What a move does when it is used. */
public enum MoveCategory {

  ATTACK("Attack"),
  RAISE_ATTACK("Attack Buff"),
  RAISE_DEFENCE("Defence Buff");

  private final String label;

  MoveCategory(String label) {
    this.label = label;
  }

  /** Name shown on the move menu. */
  public String label() {
    return label;
  }

  public boolean isBuff() {
    return this != ATTACK;
  }
}

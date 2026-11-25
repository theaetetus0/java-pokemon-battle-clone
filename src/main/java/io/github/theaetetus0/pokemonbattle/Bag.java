package io.github.theaetetus0.pokemonbattle;

/** The player's items. */
public class Bag {

  public static final int POTION_HEAL_AMOUNT = 20;

  private int potions;

  public int getPotions() {
    return potions;
  }

  public void setPotions(int potions) {
    if (potions < 0) {
      throw new IllegalArgumentException("potions must not be negative");
    }
    this.potions = potions;
  }

  public boolean hasPotion() {
    return potions > 0;
  }

  /** Uses one potion on the Pokemon and returns how much health it recovered. */
  public int usePotion(Pokemon pokemon) {
    if (!hasPotion()) {
      throw new IllegalStateException("no potions left");
    }
    potions--;
    return pokemon.heal(POTION_HEAL_AMOUNT);
  }
}

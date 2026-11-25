package io.github.theaetetus0.pokemonbattle;

import java.util.List;

/** A Pokemon in the game: its species plus its current health and (possibly buffed) stats. */
public class Pokemon {

  // stats can be buffed up to this multiple of their starting value
  private static final int MAX_STAT_MULTIPLIER = 2;

  private final Species species;
  private int health;
  private int attack;
  private int defence;

  public Pokemon(Species species) {
    this.species = species;
    restore();
  }

  public Species getSpecies() {
    return species;
  }

  public String getName() {
    return species.name();
  }

  public String getType() {
    return species.type();
  }

  public List<Move> getMoves() {
    return species.moves();
  }

  public int getHealth() {
    return health;
  }

  public int getMaxHealth() {
    return species.health();
  }

  public int getAttack() {
    return attack;
  }

  public int getDefence() {
    return defence;
  }

  public boolean isFainted() {
    return health <= 0;
  }

  // health never drops below 0
  public void takeDamage(int damage) {
    health = Math.max(0, health - damage);
  }

  // heal, never above max health; returns the amount actually healed
  public int heal(int amount) {
    int newHealth = Math.min(health + amount, getMaxHealth());
    int healed = newHealth - health;
    health = newHealth;
    return healed;
  }

  // raise attack, capped at MAX_STAT_MULTIPLIER x base; returns the amount actually gained
  public int raiseAttack(int amount) {
    int newAttack = Math.min(attack + amount, species.attack() * MAX_STAT_MULTIPLIER);
    int gained = newAttack - attack;
    attack = newAttack;
    return gained;
  }

  // raise defence, capped at MAX_STAT_MULTIPLIER x base; returns the amount actually gained
  public int raiseDefence(int amount) {
    int newDefence = Math.min(defence + amount, species.defence() * MAX_STAT_MULTIPLIER);
    int gained = newDefence - defence;
    defence = newDefence;
    return gained;
  }

  // back to full health, with any buffs removed
  public void restore() {
    health = species.health();
    attack = species.attack();
    defence = species.defence();
  }
}

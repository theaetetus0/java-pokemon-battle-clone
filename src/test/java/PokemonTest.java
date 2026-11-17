import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PokemonTest {

  // health 40, attack 50, defence 30
  private Pokemon pokemon;

  @BeforeEach
  void setUp() {
    pokemon = new Charmander("Fire", "Charmander", 40, 50, 30);
  }

  @Test
  void startsAtFullHealthWithBaseStats() {
    assertEquals(40, pokemon.getHealth());
    assertEquals(40, pokemon.getMaxHealth());
    assertEquals(50, pokemon.getAttack());
    assertEquals(30, pokemon.getDefence());
    assertFalse(pokemon.isFainted());
  }

  @Test
  void takeDamageReducesHealth() {
    pokemon.takeDamage(15);
    assertEquals(25, pokemon.getHealth());
    assertFalse(pokemon.isFainted());
  }

  @Test
  void healthNeverDropsBelowZero() {
    pokemon.takeDamage(100);
    assertEquals(0, pokemon.getHealth());
    assertTrue(pokemon.isFainted());
  }

  @Test
  void faintsAtExactlyZeroHealth() {
    pokemon.takeDamage(40);
    assertTrue(pokemon.isFainted());
  }

  @Test
  void healRestoresHealthAndReturnsAmountHealed() {
    pokemon.takeDamage(20);
    assertEquals(10, pokemon.heal(10));
    assertEquals(30, pokemon.getHealth());
  }

  @Test
  void healNeverGoesAboveMaxHealth() {
    pokemon.takeDamage(5);
    assertEquals(5, pokemon.heal(20));
    assertEquals(40, pokemon.getHealth());
  }

  @Test
  void healAtFullHealthDoesNothing() {
    assertEquals(0, pokemon.heal(20));
    assertEquals(40, pokemon.getHealth());
  }

  @Test
  void raiseAttackReturnsAmountGained() {
    assertEquals(10, pokemon.raiseAttack(10));
    assertEquals(60, pokemon.getAttack());
  }

  @Test
  void attackIsCappedAtDoubleBase() {
    assertEquals(50, pokemon.raiseAttack(80));
    assertEquals(100, pokemon.getAttack());
    assertEquals(0, pokemon.raiseAttack(10));
    assertEquals(100, pokemon.getAttack());
  }

  @Test
  void raiseDefenceReturnsAmountGained() {
    assertEquals(5, pokemon.raiseDefence(5));
    assertEquals(35, pokemon.getDefence());
  }

  @Test
  void defenceIsCappedAtDoubleBase() {
    assertEquals(30, pokemon.raiseDefence(50));
    assertEquals(60, pokemon.getDefence());
    assertEquals(0, pokemon.raiseDefence(5));
    assertEquals(60, pokemon.getDefence());
  }
}

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class BattleTest {

  private final PrintStream originalOut = System.out;
  private final ByteArrayOutputStream output = new ByteArrayOutputStream();

  private Pokemon charmander;
  private Pokemon squirtle;
  private Pokemon bulbasaur;

  @BeforeEach
  void setUp() {
    // capture battle messages so they can be checked and don't clutter test output
    System.setOut(new PrintStream(output));

    charmander = new Charmander("Fire", "Charmander", 39, 52, 43);
    squirtle = new Squirtle("Water", "Squirtle", 44, 48, 65);
    bulbasaur = new Bulbasaur("Grass", "Bulbasaur", 45, 49, 49);
  }

  @AfterEach
  void restoreOutput() {
    System.setOut(originalOut);
  }

  // ---- type effectiveness ----

  @ParameterizedTest
  @CsvSource({
      "Fire,  Grass, 2.0",
      "Water, Fire,  2.0",
      "Grass, Water, 2.0",
      "Grass, Fire,  0.5",
      "Fire,  Water, 0.5",
      "Water, Grass, 0.5",
      "Fire,  Fire,  1.0",
      "Water, Water, 1.0",
      "Grass, Grass, 1.0",
  })
  void typeMatchups(String attackType, String defendType, double expected) {
    Pokemon attacker = new Charmander(attackType, "Attacker", 50, 50, 50);
    Pokemon defender = new Charmander(defendType, "Defender", 50, 50, 50);
    assertEquals(expected, Main.calculateEffectiveness(attacker, defender));
  }

  @Test
  void typeMatchupsIgnoreCase() {
    Pokemon attacker = new Charmander("fire", "Attacker", 50, 50, 50);
    Pokemon defender = new Charmander("GRASS", "Defender", 50, 50, 50);
    assertEquals(2.0, Main.calculateEffectiveness(attacker, defender));
  }

  @Test
  void superEffectiveMessageIsShown() {
    Main.checkEffectiveness(squirtle, charmander);
    assertTrue(output.toString().contains("super effective"));
  }

  @Test
  void notVeryEffectiveMessageIsShown() {
    Main.checkEffectiveness(charmander, squirtle);
    assertTrue(output.toString().contains("not very effective"));
  }

  // ---- attacking moves ----

  @Test
  void normalAttackDealsDamage() {
    Main.useMove(1, charmander, squirtle, false);
    assertTrue(squirtle.getHealth() < squirtle.getMaxHealth());
  }

  @Test
  void superEffectiveTypeAttackBeatsNormalAttack() {
    int normal = damageDealt(1, squirtle, charmander);
    int typed = damageDealt(2, squirtle, charmander);
    assertTrue(typed > normal, "type attack " + typed + " should beat normal attack " + normal);
  }

  @Test
  void notVeryEffectiveTypeAttackIsWeakerThanNormalAttack() {
    int normal = damageDealt(1, charmander, squirtle);
    int typed = damageDealt(2, charmander, squirtle);
    assertTrue(typed < normal, "type attack " + typed + " should be weaker than normal attack " + normal);
  }

  @Test
  void neutralTypeAttackMatchesNormalAttack() {
    Pokemon otherBulbasaur = Main.choosePokemon(3);
    assertEquals(damageDealt(1, bulbasaur, otherBulbasaur), damageDealt(2, bulbasaur, otherBulbasaur));
  }

  @Test
  void higherAttackDealsMoreDamage() {
    int before = damageDealt(1, charmander, bulbasaur);
    charmander.raiseAttack(20);
    int after = damageDealt(1, charmander, bulbasaur);
    assertTrue(after > before);
  }

  @Test
  void higherDefenceTakesLessDamage() {
    int before = damageDealt(1, charmander, bulbasaur);
    bulbasaur.raiseDefence(20);
    int after = damageDealt(1, charmander, bulbasaur);
    assertTrue(after < before);
  }

  @Test
  void attacksAlwaysDealAtLeastOneDamage() {
    Pokemon weak = new Charmander("Fire", "Weak", 50, 1, 50);
    Pokemon tank = new Squirtle("Water", "Tank", 50, 50, 999);
    assertTrue(damageDealt(1, weak, tank) >= 1);
    assertTrue(damageDealt(2, weak, tank) >= 1);
  }

  // ---- buff moves ----

  @Test
  void attackBuffRaisesAttackersAttack() {
    int before = charmander.getAttack();
    Main.useMove(3, charmander, squirtle, false);
    assertEquals(before + charmander.move3(), charmander.getAttack());
    assertEquals(squirtle.getMaxHealth(), squirtle.getHealth(), "buffs should not deal damage");
  }

  @Test
  void defenceBuffRaisesAttackersDefence() {
    int before = charmander.getDefence();
    Main.useMove(4, charmander, squirtle, false);
    assertEquals(before + charmander.move4(), charmander.getDefence());
  }

  @Test
  void buffAtCapSaysItWontGoHigher() {
    charmander.raiseAttack(1000);
    Main.useMove(3, charmander, squirtle, false);
    assertTrue(output.toString().contains("won't go any higher"));
  }

  @Test
  void wildPokemonIsNamedAsWild() {
    Main.useMove(1, squirtle, charmander, true);
    assertTrue(output.toString().contains("The wild Squirtle used"));
  }

  // ---- catching ----

  @Test
  void catchChanceIsLowestAtFullHealth() {
    assertEquals(0.1, Main.catchChance(squirtle), 1e-9);
  }

  @Test
  void catchChanceIsHighestAtZeroHealth() {
    squirtle.takeDamage(squirtle.getMaxHealth());
    assertEquals(0.9, Main.catchChance(squirtle), 1e-9);
  }

  @Test
  void catchChanceRisesAsHealthFalls() {
    double full = Main.catchChance(squirtle);
    squirtle.takeDamage(squirtle.getMaxHealth() / 2);
    double half = Main.catchChance(squirtle);
    assertTrue(half > full);
    assertTrue(half < 0.9);
  }

  // use a move, return the damage it dealt, then heal the defender back up
  private int damageDealt(int move, Pokemon attacker, Pokemon defender) {
    int before = defender.getHealth();
    Main.useMove(move, attacker, defender, false);
    int damage = before - defender.getHealth();
    defender.heal(damage);
    return damage;
  }
}

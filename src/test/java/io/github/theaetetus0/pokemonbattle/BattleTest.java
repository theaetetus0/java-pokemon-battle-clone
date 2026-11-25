package io.github.theaetetus0.pokemonbattle;

import static io.github.theaetetus0.pokemonbattle.TestData.ATTACK_BUFF;
import static io.github.theaetetus0.pokemonbattle.TestData.DEFENCE_BUFF;
import static io.github.theaetetus0.pokemonbattle.TestData.NORMAL_ATTACK;
import static io.github.theaetetus0.pokemonbattle.TestData.TYPE_ATTACK;
import static io.github.theaetetus0.pokemonbattle.TestData.pokemon;
import static io.github.theaetetus0.pokemonbattle.TestData.repeat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.theaetetus0.pokemonbattle.TestData.FixedRandom;
import io.github.theaetetus0.pokemonbattle.TestData.TestConsole;

class BattleTest {

  private Pokemon charmander;
  private Pokemon squirtle;
  private Pokemon bulbasaur;
  private Bag bag;

  @BeforeEach
  void setUp() {
    charmander = pokemon("Charmander", "Fire", 39, 52, 43);
    squirtle = pokemon("Squirtle", "Water", 44, 48, 65);
    bulbasaur = pokemon("Bulbasaur", "Grass", 45, 49, 49);
    bag = new Bag();
    bag.setPotions(3);
  }

  private final Map<Battle, TestConsole> consoles = new HashMap<>();

  private Battle battle(Pokemon player, Pokemon wild, String input, FixedRandom random) {
    TestConsole console = new TestConsole(input);
    Battle battle = new Battle(player, wild, bag, TestData.typeChart(), console, random);
    consoles.put(battle, console);
    return battle;
  }

  /** Everything the battle has printed so far. */
  private String output(Battle battle) {
    return consoles.get(battle).output();
  }

  // ---- damage ----

  /** Uses a move, returns the damage it dealt, then heals the defender back up. */
  private int damageDealt(Pokemon attacker, Pokemon defender, int moveIndex) {
    Battle battle = battle(attacker, defender, "", new FixedRandom(0, 0.5));
    int before = defender.getHealth();
    battle.useMove(attacker, defender, attacker.getMoves().get(moveIndex), false);
    int damage = before - defender.getHealth();
    defender.heal(damage);
    return damage;
  }

  @Test
  void damageFormulaIsUnchanged() {
    // 1 + (attack / defence) * attack / 5 * effectiveness, rounded
    assertEquals(9, Battle.calculateDamage(charmander, squirtle, 1.0));   // 1 + 0.80 * 52 / 5 = 9.32
    assertEquals(22, Battle.calculateDamage(squirtle, charmander, 2.0));  // 1 + 1.12 * 48 / 5 * 2 = 22.43
  }

  @Test
  void normalAttackDealsDamage() {
    assertTrue(damageDealt(charmander, squirtle, NORMAL_ATTACK) > 0);
  }

  @Test
  void superEffectiveTypeAttackBeatsNormalAttack() {
    int normal = damageDealt(squirtle, charmander, NORMAL_ATTACK);
    int typed = damageDealt(squirtle, charmander, TYPE_ATTACK);
    assertTrue(typed > normal, "type attack " + typed + " should beat normal attack " + normal);
  }

  @Test
  void notVeryEffectiveTypeAttackIsWeakerThanNormalAttack() {
    int normal = damageDealt(charmander, squirtle, NORMAL_ATTACK);
    int typed = damageDealt(charmander, squirtle, TYPE_ATTACK);
    assertTrue(typed < normal, "type attack " + typed + " should be weaker than normal attack " + normal);
  }

  @Test
  void neutralTypeAttackMatchesNormalAttack() {
    Pokemon otherBulbasaur = pokemon("Bulbasaur", "Grass", 45, 49, 49);
    assertEquals(damageDealt(bulbasaur, otherBulbasaur, NORMAL_ATTACK),
        damageDealt(bulbasaur, otherBulbasaur, TYPE_ATTACK));
  }

  @Test
  void higherAttackDealsMoreDamage() {
    int before = damageDealt(charmander, bulbasaur, NORMAL_ATTACK);
    charmander.raiseAttack(20);
    assertTrue(damageDealt(charmander, bulbasaur, NORMAL_ATTACK) > before);
  }

  @Test
  void higherDefenceTakesLessDamage() {
    int before = damageDealt(charmander, bulbasaur, NORMAL_ATTACK);
    bulbasaur.raiseDefence(20);
    assertTrue(damageDealt(charmander, bulbasaur, NORMAL_ATTACK) < before);
  }

  @Test
  void attacksAlwaysDealAtLeastOneDamage() {
    Pokemon weak = pokemon("Weak", "Fire", 50, 1, 50);
    Pokemon tank = pokemon("Tank", "Water", 50, 50, 999);
    assertTrue(damageDealt(weak, tank, NORMAL_ATTACK) >= 1);
    assertTrue(damageDealt(weak, tank, TYPE_ATTACK) >= 1);
  }

  @Test
  void effectivenessMessagesAreShown() {
    Battle battle = battle(squirtle, charmander, "", new FixedRandom(0, 0.5));

    battle.useMove(squirtle, charmander, squirtle.getMoves().get(TYPE_ATTACK), false);
    assertTrue(output(battle).contains("super effective"));

    battle.useMove(charmander, squirtle, charmander.getMoves().get(TYPE_ATTACK), true);
    assertTrue(output(battle).contains("not very effective"));
  }

  @Test
  void normalMovesShowNoEffectivenessMessage() {
    Battle battle = battle(squirtle, charmander, "", new FixedRandom(0, 0.5));
    battle.useMove(squirtle, charmander, squirtle.getMoves().get(NORMAL_ATTACK), false);
    assertFalse(output(battle).contains("effective"));
  }

  @Test
  void wildPokemonIsNamedAsWild() {
    Battle battle = battle(charmander, squirtle, "", new FixedRandom(0, 0.5));
    battle.useMove(squirtle, charmander, squirtle.getMoves().get(NORMAL_ATTACK), true);
    assertTrue(output(battle).contains("The wild Squirtle used Tackle!"));
  }

  // ---- buffs ----

  @Test
  void attackBuffRaisesAttackersAttackOnly() {
    Battle battle = battle(charmander, squirtle, "", new FixedRandom(0, 0.5));
    battle.useMove(charmander, squirtle, charmander.getMoves().get(ATTACK_BUFF), false);

    assertEquals(52 + 10, charmander.getAttack());
    assertEquals(squirtle.getMaxHealth(), squirtle.getHealth(), "buffs should not deal damage");
    assertTrue(output(battle).contains("Charmander's attack rose!"));
  }

  @Test
  void defenceBuffRaisesAttackersDefence() {
    Battle battle = battle(charmander, squirtle, "", new FixedRandom(0, 0.5));
    battle.useMove(charmander, squirtle, charmander.getMoves().get(DEFENCE_BUFF), false);
    assertEquals(43 + 5, charmander.getDefence());
  }

  @Test
  void buffAtCapSaysItWontGoHigher() {
    charmander.raiseAttack(1000);
    Battle battle = battle(charmander, squirtle, "", new FixedRandom(0, 0.5));
    battle.useMove(charmander, squirtle, charmander.getMoves().get(ATTACK_BUFF), false);
    assertTrue(output(battle).contains("won't go any higher"));
  }

  // ---- catching ----

  @Test
  void catchChanceIsLowestAtFullHealth() {
    assertEquals(0.1, Battle.catchChance(squirtle), 1e-9);
  }

  @Test
  void catchChanceIsHighestAtZeroHealth() {
    squirtle.takeDamage(squirtle.getMaxHealth());
    assertEquals(0.9, Battle.catchChance(squirtle), 1e-9);
  }

  @Test
  void catchChanceRisesAsHealthFalls() {
    double full = Battle.catchChance(squirtle);
    squirtle.takeDamage(squirtle.getMaxHealth() / 2);
    double half = Battle.catchChance(squirtle);
    assertTrue(half > full);
    assertTrue(half < 0.9);
  }

  // ---- choosing an option ----

  @Test
  void potionAllowedWhenHurtAndPotionsLeft() {
    charmander.takeDamage(10);
    assertEquals(5, battle(charmander, squirtle, "5\n", new FixedRandom(0, 0.5)).chooseOption());
  }

  @Test
  void potionRefusedWhenNoneLeft() {
    charmander.takeDamage(10);
    bag.setPotions(0);
    Battle battle = battle(charmander, squirtle, "5\n1\n", new FixedRandom(0, 0.5));
    assertEquals(1, battle.chooseOption());
    assertTrue(output(battle).contains("out of potions"));
  }

  @Test
  void potionRefusedAtFullHealth() {
    Battle battle = battle(charmander, squirtle, "5\n2\n", new FixedRandom(0, 0.5));
    assertEquals(2, battle.chooseOption());
    assertTrue(output(battle).contains("already at full health"));
  }

  @Test
  void optionsGoUpToPokeball() {
    Battle battle = battle(charmander, squirtle, "7\n6\n", new FixedRandom(0, 0.5));
    assertEquals(6, battle.chooseOption(), "4 moves + potion + Pokeball = 6 options");
  }

  // ---- whole battles ----

  @Test
  void playerWinsByAttacking() {
    // wild Squirtle only ever uses Guard, so it never hurts Charmander
    Battle battle = battle(charmander, squirtle, repeat("1", 50), new FixedRandom(DEFENCE_BUFF, 0.99));

    assertEquals(Battle.Outcome.WON, battle.run());
    assertTrue(squirtle.isFainted());
    assertEquals(charmander.getMaxHealth(), charmander.getHealth());
    assertTrue(output(battle).contains("The wild Squirtle fainted!"));
  }

  @Test
  void playerCatchesWildPokemon() {
    Battle battle = battle(charmander, squirtle, "6\n", new FixedRandom(0, 0.0));

    assertEquals(Battle.Outcome.CAUGHT, battle.run());
    assertEquals(1, battle.getPokeballsThrown());
    assertTrue(output(battle).contains("Gotcha! The wild Squirtle was caught!"));
  }

  @Test
  void wildPokemonBreaksFreeThenFaints() {
    Battle battle = battle(charmander, squirtle, "6\n6\n" + repeat("1", 50), new FixedRandom(DEFENCE_BUFF, 0.99));

    assertEquals(Battle.Outcome.WON, battle.run());
    assertEquals(2, battle.getPokeballsThrown());
    assertTrue(output(battle).contains("broke free"));
  }

  @Test
  void playerLosesWhenTheirPokemonFaints() {
    // the player only buffs, while the wild Pokemon keeps using Tackle
    Battle battle = battle(charmander, squirtle, repeat("3", 50), new FixedRandom(NORMAL_ATTACK, 0.99));

    assertEquals(Battle.Outcome.LOST, battle.run());
    assertTrue(charmander.isFainted());
    assertTrue(output(battle).contains("Your Charmander fainted!"));
  }

  @Test
  void potionHealsAndUsesOneUp() {
    charmander.takeDamage(30);
    Battle battle = battle(charmander, squirtle, "5\n6\n", new FixedRandom(DEFENCE_BUFF, 0.0));

    assertEquals(Battle.Outcome.CAUGHT, battle.run());
    assertEquals(9 + Bag.POTION_HEAL_AMOUNT, charmander.getHealth());
    assertEquals(2, bag.getPotions());
    assertTrue(output(battle).contains("Charmander recovered 20 health!"));
  }

  @Test
  void menuListsMovesFromData() {
    Battle battle = battle(charmander, squirtle, "6\n", new FixedRandom(0, 0.0));
    battle.run();

    String output = output(battle);
    assertTrue(output.contains("1 - Tackle, Type: Normal, Category: Attack"));
    assertTrue(output.contains("2 - Ember, Type: Fire, Category: Attack"));
    assertTrue(output.contains("3 - Power Up, Type: Normal, Category: Attack Buff"));
    assertTrue(output.contains("4 - Guard, Type: Normal, Category: Defence Buff"));
    assertTrue(output.contains("5 - Potion, Heal 20 Health (3 left)"));
    assertTrue(output.contains("6 - Throw A Pokeball"));
  }
}

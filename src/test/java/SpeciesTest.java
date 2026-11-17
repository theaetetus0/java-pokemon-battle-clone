import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class SpeciesTest {

  private static final int NUM_POKEMON = 10;
  private static final Set<String> TYPES = Set.of("Fire", "Water", "Grass");

  @Test
  void everyNumberGivesADifferentPokemon() {
    Set<String> names = new HashSet<>();
    for (int i = 1; i <= NUM_POKEMON; i++) {
      names.add(Main.choosePokemon(i).getName());
    }
    assertEquals(NUM_POKEMON, names.size());
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
  void everyPokemonHasValidData(int number) {
    Pokemon p = Main.choosePokemon(number);

    assertTrue(TYPES.contains(p.getType()), p.getName() + " has unknown type " + p.getType());
    assertTrue(p.getMaxHealth() > 0);
    assertTrue(p.getAttack() > 0);
    assertTrue(p.getDefence() > 0);
    assertEquals(p.getMaxHealth(), p.getHealth(), "should start at full health");

    assertFalse(p.move1Name().isBlank());
    assertFalse(p.move2Name().isBlank());
    assertFalse(p.move3Name().isBlank());
    assertFalse(p.move4Name().isBlank());
    assertTrue(p.move3() > 0, "attack buff should be positive");
    assertTrue(p.move4() > 0, "defence buff should be positive");
  }

  @Test
  void eachCallGivesAFreshPokemon() {
    Pokemon first = Main.choosePokemon(1);
    first.takeDamage(10);
    Pokemon second = Main.choosePokemon(1);

    assertNotSame(first, second);
    assertEquals(second.getMaxHealth(), second.getHealth());
  }

  @ParameterizedTest
  @ValueSource(ints = {0, -1, NUM_POKEMON + 1})
  void invalidNumberThrows(int number) {
    assertThrows(IllegalArgumentException.class, () -> Main.choosePokemon(number));
  }
}

package io.github.theaetetus0.pokemonbattle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class TypeChartTest {

  private final TypeChart chart = TestData.typeChart();

  @ParameterizedTest
  @CsvSource({
      "Fire,   Grass,  2.0",
      "Water,  Fire,   2.0",
      "Grass,  Water,  2.0",
      "Grass,  Fire,   0.5",
      "Fire,   Water,  0.5",
      "Water,  Grass,  0.5",
      "Fire,   Fire,   1.0",
      "Water,  Water,  1.0",
      "Grass,  Grass,  1.0",
      "Normal, Fire,   1.0",
      "Normal, Water,  1.0",
      "Normal, Grass,  1.0",
  })
  void matchups(String attackType, String defendType, double expected) {
    assertEquals(expected, chart.effectiveness(attackType, defendType));
  }

  @Test
  void typeNamesIgnoreCase() {
    assertEquals(2.0, chart.effectiveness("fire", "GRASS"));
  }

  @Test
  void unlistedTypesAreNeutral() {
    assertEquals(1.0, chart.effectiveness("Ghost", "Fire"));
    assertEquals(1.0, chart.effectiveness("Fire", "Ghost"));
  }

  @Test
  void knownTypes() {
    assertTrue(chart.isKnownType("Fire"));
    assertTrue(chart.isKnownType("grass"));
    assertTrue(chart.isKnownType("Normal"));
    assertFalse(chart.isKnownType("Fier"));
  }

  @Test
  void describesEachTypesStrengthsAndWeaknesses() {
    assertEquals(List.of(
        "Fire is strong against Grass, but weak against Water",
        "Water is strong against Fire, but weak against Grass",
        "Grass is strong against Water, but weak against Fire"),
        chart.describe());
  }

  @Test
  void describesTypesWithOnlyStrengthsOrOnlyWeaknesses() {
    TypeChart small = new TypeChart();
    small.set("Electric", "Water", 2);
    small.set("Bug", "Fire", 0.5);

    assertEquals(List.of("Electric is strong against Water", "Bug is weak against Fire"), small.describe());
  }

  @Test
  void negativeMultiplierIsRejected() {
    assertThrows(IllegalArgumentException.class, () -> chart.set("Fire", "Ice", -1));
  }
}

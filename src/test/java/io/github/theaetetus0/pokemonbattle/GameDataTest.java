package io.github.theaetetus0.pokemonbattle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.StringReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

class GameDataTest {

  // ---- the real data files ----

  @Test
  void bundledDataLoads() {
    GameData data = GameData.loadDefault();
    assertEquals(10, data.species().size());
  }

  @Test
  void bundledSpeciesAreValid() {
    GameData data = GameData.loadDefault();
    Set<String> names = new HashSet<>();

    for (Species species : data.species()) {
      assertTrue(names.add(species.name()), "duplicate species " + species.name());
      assertTrue(data.typeChart().isKnownType(species.type()), species.name());
      assertEquals(4, species.moves().size(), species.name() + " should have 4 moves");

      // each species has a normal attack, an attack of its own type, an attack buff and a defence buff
      List<Move> moves = species.moves();
      assertEquals(MoveCategory.ATTACK, moves.get(0).category(), species.name());
      assertEquals("Normal", moves.get(0).type(), species.name());
      assertEquals(MoveCategory.ATTACK, moves.get(1).category(), species.name());
      assertEquals(species.type(), moves.get(1).type(), species.name());
      assertEquals(MoveCategory.RAISE_ATTACK, moves.get(2).category(), species.name());
      assertEquals(MoveCategory.RAISE_DEFENCE, moves.get(3).category(), species.name());
    }
  }

  @Test
  void bundledDataKeepsOriginalStats() {
    Species charmander = GameData.loadDefault().species().get(0);
    assertEquals(new Species("Charmander", "Fire", 39, 52, 43, List.of(
        Move.attack("Scratch", "Normal"),
        Move.attack("Ember", "Fire"),
        new Move("Dragon Dance", "Normal", MoveCategory.RAISE_ATTACK, 10),
        new Move("Defence Curl", "Normal", MoveCategory.RAISE_DEFENCE, 5))), charmander);
  }

  @Test
  void bundledTypeChartMatchesTheGame() {
    TypeChart chart = GameData.loadDefault().typeChart();
    assertEquals(TestData.typeChart().describe(), chart.describe());
  }

  // ---- loading small hand-written files ----

  private static final String MOVES = """
      name,type,category,amount
      Tackle,Normal,attack,
      Ember,Fire,attack,
      Growl,Normal,raise_attack,5
      """;

  private static final String POKEMON = """
      name,type,health,attack,defence,move1,move2,move3,move4
      # comments and blank lines are ignored

      Charmander,Fire,39,52,43,Tackle,Ember,Growl,
      Squirtle,Water,44,48,65,Tackle,,,
      """;

  private static final String TYPES = """
      attacker,defender,multiplier
      Water,Fire,2
      Fire,Water,0.5
      """;

  private static GameData load(String moves, String pokemon, String types) {
    return GameData.load(new StringReader(moves), new StringReader(pokemon), new StringReader(types));
  }

  private static String loadError(String moves, String pokemon, String types) {
    return assertThrows(GameDataException.class, () -> load(moves, pokemon, types)).getMessage();
  }

  @Test
  void loadsValidFiles() {
    GameData data = load(MOVES, POKEMON, TYPES);

    assertEquals(2, data.species().size());
    Species charmander = data.species().get(0);
    assertEquals("Charmander", charmander.name());
    assertEquals(List.of("Tackle", "Ember", "Growl"), charmander.moves().stream().map(Move::name).toList());
    assertEquals(1, data.species().get(1).moves().size(), "blank move columns are skipped");
    assertEquals(2.0, data.typeChart().effectiveness("Water", "Fire"));
  }

  @Test
  void moveNamesIgnoreCase() {
    String pokemon = POKEMON.replace("Tackle,Ember", "tackle,EMBER");
    assertEquals(3, load(MOVES, pokemon, TYPES).species().get(0).moves().size());
  }

  @Test
  void unknownMoveIsReportedWithLineNumber() {
    String pokemon = POKEMON.replace("Tackle,,,", "Splash,,,");
    assertEquals("pokemon.csv line 5: unknown move \"Splash\" (add it to moves.csv)",
        loadError(MOVES, pokemon, TYPES));
  }

  @Test
  void unknownPokemonTypeIsReported() {
    String pokemon = POKEMON.replace("Squirtle,Water", "Squirtle,Watr");
    assertTrue(loadError(MOVES, pokemon, TYPES).contains("unknown type \"Watr\""));
  }

  @Test
  void unknownMoveTypeIsReported() {
    String moves = MOVES.replace("Ember,Fire", "Ember,Fyre");
    assertEquals("moves.csv line 3: unknown type \"Fyre\" (add it to types.csv)", loadError(moves, POKEMON, TYPES));
  }

  @Test
  void badNumberIsReported() {
    String pokemon = POKEMON.replace("39,52,43", "39,lots,43");
    assertEquals("pokemon.csv line 4: attack must be a whole number, not \"lots\"",
        loadError(MOVES, pokemon, TYPES));
  }

  @Test
  void nonPositiveStatIsReported() {
    String pokemon = POKEMON.replace("39,52,43", "0,52,43");
    assertTrue(loadError(MOVES, pokemon, TYPES).contains("positive health, attack and defence"));
  }

  @Test
  void wrongNumberOfColumnsIsReported() {
    String pokemon = POKEMON.replace("Tackle,,,", "Tackle,,");
    assertEquals("pokemon.csv line 5: expected 9 values but found 8", loadError(MOVES, pokemon, TYPES));
  }

  @Test
  void wrongHeaderIsReported() {
    String moves = MOVES.replace("category", "kind");
    assertTrue(loadError(moves, POKEMON, TYPES).startsWith("moves.csv line 1: header should be"));
  }

  @Test
  void missingFirstMoveIsReported() {
    String pokemon = POKEMON.replace("Tackle,,,", ",,,");
    assertEquals("pokemon.csv line 5: missing move1", loadError(MOVES, pokemon, TYPES));
  }

  @Test
  void duplicateMoveIsReported() {
    String moves = MOVES + "tackle,Normal,attack,\n";
    assertEquals("moves.csv line 5: there is already a move called tackle", loadError(moves, POKEMON, TYPES));
  }

  @Test
  void duplicatePokemonIsReported() {
    String pokemon = POKEMON + "Charmander,Fire,1,1,1,Tackle,,,\n";
    assertTrue(loadError(MOVES, pokemon, TYPES).contains("already a Pokemon called Charmander"));
  }

  @Test
  void unknownCategoryIsReported() {
    String moves = MOVES.replace("Ember,Fire,attack", "Ember,Fire,special");
    assertTrue(loadError(moves, POKEMON, TYPES).contains("category must be"));
  }

  @Test
  void buffWithoutAmountIsReported() {
    String moves = MOVES.replace("raise_attack,5", "raise_attack,");
    assertTrue(loadError(moves, POKEMON, TYPES).contains("needs a positive amount"));
  }

  @Test
  void attackWithAmountIsReported() {
    String moves = MOVES.replace("Ember,Fire,attack,", "Ember,Fire,attack,40");
    assertTrue(loadError(moves, POKEMON, TYPES).contains("should not have an amount"));
  }

  @Test
  void badMultiplierIsReported() {
    String types = TYPES.replace("Water,Fire,2", "Water,Fire,double");
    assertTrue(loadError(MOVES, POKEMON, types).startsWith("types.csv line 2:"));
  }

  @Test
  void emptyFileIsReported() {
    assertEquals("moves.csv is empty", loadError("", POKEMON, TYPES));
  }

  @Test
  void needsAtLeastTwoSpecies() {
    String pokemon = POKEMON.replace("Squirtle,Water,44,48,65,Tackle,,,\n", "");
    assertTrue(loadError(MOVES, pokemon, TYPES).contains("at least 2"));
  }
}

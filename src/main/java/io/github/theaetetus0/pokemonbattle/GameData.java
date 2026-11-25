package io.github.theaetetus0.pokemonbattle;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/** All the Pokemon, moves and type matchups, loaded from the CSV files in src/main/resources/data. */
public record GameData(List<Species> species, TypeChart typeChart) {

  private static final String DATA_FOLDER = "/data/";
  private static final String MOVES_FILE = "moves.csv";
  private static final String POKEMON_FILE = "pokemon.csv";
  private static final String TYPES_FILE = "types.csv";

  private static final List<String> MOVE_COLUMNS = List.of("name", "type", "category", "amount");
  private static final List<String> POKEMON_COLUMNS =
      List.of("name", "type", "health", "attack", "defence", "move1", "move2", "move3", "move4");
  private static final List<String> TYPE_COLUMNS = List.of("attacker", "defender", "multiplier");

  public GameData {
    species = List.copyOf(species);
    if (species.size() < 2) {
      throw new GameDataException("there must be at least 2 Pokemon species");
    }
  }

  /** Loads the data files bundled with the game. */
  public static GameData loadDefault() {
    try (Reader moves = openResource(MOVES_FILE);
        Reader pokemon = openResource(POKEMON_FILE);
        Reader types = openResource(TYPES_FILE)) {
      return load(moves, pokemon, types);
    } catch (IOException e) {
      throw new GameDataException("could not close data files: " + e.getMessage(), e);
    }
  }

  /** Loads data from the given moves, pokemon and types CSV contents. */
  public static GameData load(Reader moves, Reader pokemon, Reader types) {
    TypeChart typeChart = readTypeChart(types);
    Map<String, Move> movesByName = readMoves(moves, typeChart);
    List<Species> species = readSpecies(pokemon, movesByName, typeChart);
    return new GameData(species, typeChart);
  }

  private static TypeChart readTypeChart(Reader reader) {
    TypeChart chart = new TypeChart();
    for (CsvFile.Row row : CsvFile.read(TYPES_FILE, reader, TYPE_COLUMNS)) {
      double multiplier = row.decimal("multiplier");
      if (multiplier < 0) {
        throw row.error("multiplier must not be negative");
      }
      chart.set(row.text("attacker"), row.text("defender"), multiplier);
    }
    return chart;
  }

  private static Map<String, Move> readMoves(Reader reader, TypeChart typeChart) {
    Map<String, Move> moves = new LinkedHashMap<>();

    for (CsvFile.Row row : CsvFile.read(MOVES_FILE, reader, MOVE_COLUMNS)) {
      String name = row.text("name");
      String type = row.text("type");
      if (!typeChart.isKnownType(type)) {
        throw row.error("unknown type \"" + type + "\" (add it to " + TYPES_FILE + ")");
      }

      MoveCategory category;
      try {
        category = MoveCategory.valueOf(row.text("category").toUpperCase(Locale.ROOT));
      } catch (IllegalArgumentException e) {
        throw row.error("category must be attack, raise_attack or raise_defence");
      }

      Move move;
      try {
        move = new Move(name, type, category, row.optionalInteger("amount"));
      } catch (IllegalArgumentException e) {
        throw row.error(e.getMessage());
      }

      if (moves.putIfAbsent(key(name), move) != null) {
        throw row.error("there is already a move called " + name);
      }
    }
    return moves;
  }

  private static List<Species> readSpecies(Reader reader, Map<String, Move> movesByName, TypeChart typeChart) {
    List<Species> species = new ArrayList<>();
    Set<String> names = new HashSet<>();

    for (CsvFile.Row row : CsvFile.read(POKEMON_FILE, reader, POKEMON_COLUMNS)) {
      String name = row.text("name");
      if (!names.add(key(name))) {
        throw row.error("there is already a Pokemon called " + name);
      }

      String type = row.text("type");
      if (!typeChart.isKnownType(type)) {
        throw row.error("unknown type \"" + type + "\" (add it to " + TYPES_FILE + ")");
      }

      List<Move> moves = new ArrayList<>();
      for (int i = 1; i <= Species.MAX_MOVES; i++) {
        String moveName = i == 1 ? row.text("move1") : row.optionalText("move" + i);
        if (moveName.isEmpty()) {
          continue;
        }
        Move move = movesByName.get(key(moveName));
        if (move == null) {
          throw row.error("unknown move \"" + moveName + "\" (add it to " + MOVES_FILE + ")");
        }
        moves.add(move);
      }

      try {
        species.add(new Species(name, type, row.integer("health"), row.integer("attack"),
            row.integer("defence"), moves));
      } catch (IllegalArgumentException e) {
        throw row.error(e.getMessage());
      }
    }
    return species;
  }

  private static Reader openResource(String fileName) {
    InputStream stream = GameData.class.getResourceAsStream(DATA_FOLDER + fileName);
    if (stream == null) {
      throw new GameDataException("data file " + DATA_FOLDER + fileName + " not found");
    }
    return new InputStreamReader(stream, StandardCharsets.UTF_8);
  }

  private static String key(String name) {
    return name.toLowerCase(Locale.ROOT);
  }
}

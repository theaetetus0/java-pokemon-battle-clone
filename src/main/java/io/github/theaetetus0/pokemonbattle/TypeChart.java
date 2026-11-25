package io.github.theaetetus0.pokemonbattle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** How effective each attacking type is against each defending type. Type names ignore case. */
public final class TypeChart {

  /** The neutral type: its moves always do normal damage. */
  public static final String NORMAL = "Normal";

  // attacking type -> defending type -> multiplier, all keys lower-case
  private final Map<String, Map<String, Double>> multipliers = new HashMap<>();

  // lower-case key -> name as first written, in the order types were added
  private final Map<String, String> typeNames = new LinkedHashMap<>();

  public void set(String attackType, String defendType, double multiplier) {
    if (multiplier < 0) {
      throw new IllegalArgumentException("multiplier must not be negative");
    }
    remember(attackType);
    remember(defendType);
    multipliers.computeIfAbsent(key(attackType), k -> new HashMap<>()).put(key(defendType), multiplier);
  }

  /** Damage multiplier for a move of attackType hitting a Pokemon of defendType; 1 if not listed. */
  public double effectiveness(String attackType, String defendType) {
    return multipliers.getOrDefault(key(attackType), Map.of()).getOrDefault(key(defendType), 1.0);
  }

  /** True for Normal and any type that appears in the chart. */
  public boolean isKnownType(String type) {
    return key(type).equals(key(NORMAL)) || typeNames.containsKey(key(type));
  }

  /** One line per attacking type, e.g. "Fire is strong against Grass, but weak against Water". */
  public List<String> describe() {
    List<String> lines = new ArrayList<>();

    for (String type : typeNames.values()) {
      if (!multipliers.containsKey(key(type))) {
        continue;
      }
      List<String> strong = new ArrayList<>();
      List<String> weak = new ArrayList<>();
      for (String other : typeNames.values()) {
        double multiplier = effectiveness(type, other);
        if (multiplier > 1) {
          strong.add(other);
        } else if (multiplier < 1) {
          weak.add(other);
        }
      }

      if (!strong.isEmpty() && !weak.isEmpty()) {
        lines.add(type + " is strong against " + String.join(", ", strong)
            + ", but weak against " + String.join(", ", weak));
      } else if (!strong.isEmpty()) {
        lines.add(type + " is strong against " + String.join(", ", strong));
      } else if (!weak.isEmpty()) {
        lines.add(type + " is weak against " + String.join(", ", weak));
      }
    }
    return lines;
  }

  private void remember(String type) {
    if (type == null || type.isBlank()) {
      throw new IllegalArgumentException("type must not be blank");
    }
    typeNames.putIfAbsent(key(type), type);
  }

  private static String key(String type) {
    return type.trim().toLowerCase(Locale.ROOT);
  }
}

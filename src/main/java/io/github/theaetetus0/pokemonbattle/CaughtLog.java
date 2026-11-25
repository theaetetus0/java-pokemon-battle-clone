package io.github.theaetetus0.pokemonbattle;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/** The text file that records what was caught in each session. */
public class CaughtLog {

  private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

  private final Path file;

  public CaughtLog(Path file) {
    this.file = file;
  }

  /** Appends a session to the file. Does nothing if nothing was caught. */
  public void append(List<CaughtPokemon> caught) throws IOException {
    if (caught.isEmpty()) {
      return;
    }

    try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8,
        StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {

      writer.write("--- Session " + LocalDateTime.now().format(TIMESTAMP) + " ---");
      writer.newLine();

      for (CaughtPokemon pokemon : caught) {
        writer.write("Pokemon Caught: " + pokemon.name() + ", Pokeballs Used: " + pokemon.pokeballsUsed());
        writer.newLine();
      }
    }
  }
}

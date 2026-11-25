package io.github.theaetetus0.pokemonbattle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class CaughtLogTest {

  @TempDir
  Path tempDir;

  @Test
  void nothingIsWrittenWhenNothingWasCaught() throws IOException {
    Path file = tempDir.resolve("caught.txt");
    new CaughtLog(file).append(List.of());
    assertFalse(Files.exists(file));
  }

  @Test
  void writesSessionHeaderAndOneLinePerCatch() throws IOException {
    Path file = tempDir.resolve("caught.txt");
    new CaughtLog(file).append(List.of(new CaughtPokemon("Squirtle", 1), new CaughtPokemon("Treecko", 3)));

    List<String> lines = Files.readAllLines(file);
    assertEquals(3, lines.size());
    assertTrue(lines.get(0).matches("--- Session \\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2} ---"), lines.get(0));
    assertEquals("Pokemon Caught: Squirtle, Pokeballs Used: 1", lines.get(1));
    assertEquals("Pokemon Caught: Treecko, Pokeballs Used: 3", lines.get(2));
  }

  @Test
  void laterSessionsAreAppended() throws IOException {
    Path file = tempDir.resolve("caught.txt");
    CaughtLog log = new CaughtLog(file);
    log.append(List.of(new CaughtPokemon("Squirtle", 1)));
    log.append(List.of(new CaughtPokemon("Froakie", 2)));

    List<String> lines = Files.readAllLines(file);
    assertEquals(4, lines.size());
    assertEquals("Pokemon Caught: Squirtle, Pokeballs Used: 1", lines.get(1));
    assertEquals("Pokemon Caught: Froakie, Pokeballs Used: 2", lines.get(3));
  }
}

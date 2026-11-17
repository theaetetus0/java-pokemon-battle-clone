import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class SaveFileTest {

  @TempDir
  Path tempDir;

  @Test
  void nothingIsWrittenWhenNothingWasCaught() {
    File file = tempDir.resolve("caught.txt").toFile();
    Main.writeList(file, new ArrayList<>(), new ArrayList<>());
    assertFalse(file.exists());
  }

  @Test
  void writesSessionHeaderAndOneLinePerCatch() throws IOException {
    File file = tempDir.resolve("caught.txt").toFile();
    Main.writeList(file, new ArrayList<>(List.of("Squirtle", "Treecko")), new ArrayList<>(List.of(1, 3)));

    List<String> lines = Files.readAllLines(file.toPath());
    assertEquals(3, lines.size());
    assertTrue(lines.get(0).matches("--- Session \\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2} ---"), lines.get(0));
    assertEquals("Pokemon Caught: Squirtle, Pokeballs Used: 1", lines.get(1));
    assertEquals("Pokemon Caught: Treecko, Pokeballs Used: 3", lines.get(2));
  }

  @Test
  void laterSessionsAreAppended() throws IOException {
    File file = tempDir.resolve("caught.txt").toFile();
    Main.writeList(file, new ArrayList<>(List.of("Squirtle")), new ArrayList<>(List.of(1)));
    Main.writeList(file, new ArrayList<>(List.of("Froakie")), new ArrayList<>(List.of(2)));

    List<String> lines = Files.readAllLines(file.toPath());
    assertEquals(4, lines.size());
    assertEquals("Pokemon Caught: Squirtle, Pokeballs Used: 1", lines.get(1));
    assertEquals("Pokemon Caught: Froakie, Pokeballs Used: 2", lines.get(3));
  }
}

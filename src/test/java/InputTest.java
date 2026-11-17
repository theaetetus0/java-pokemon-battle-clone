import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.NoSuchElementException;
import java.util.Scanner;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InputTest {

  private final PrintStream originalOut = System.out;
  private final ByteArrayOutputStream output = new ByteArrayOutputStream();

  @BeforeEach
  void captureOutput() {
    System.setOut(new PrintStream(output));
  }

  @AfterEach
  void restoreOutput() {
    System.setOut(originalOut);
  }

  private static Scanner input(String text) {
    return new Scanner(text);
  }

  // ---- checkUserInput ----

  @Test
  void acceptsNumberInRange() {
    assertEquals(3, Main.checkUserInput(input("3\n"), 6));
  }

  @Test
  void acceptsBothEndsOfRange() {
    assertEquals(1, Main.checkUserInput(input("1\n"), 6));
    assertEquals(6, Main.checkUserInput(input("6\n"), 6));
  }

  @Test
  void asksAgainAfterOutOfRangeNumbers() {
    assertEquals(2, Main.checkUserInput(input("0\n7\n-1\n2\n"), 6));
    assertTrue(output.toString().contains("not valid"));
  }

  @Test
  void asksAgainAfterNonNumbers() {
    assertEquals(4, Main.checkUserInput(input("abc\nfour\n4\n"), 6));
    assertTrue(output.toString().contains("not valid"));
  }

  @Test
  void throwsWhenInputRunsOut() {
    // Main relies on this to save caught Pokemon when input ends (e.g. Ctrl+D)
    assertThrows(NoSuchElementException.class, () -> Main.checkUserInput(input("9\n"), 6));
  }

  // ---- chooseUserMove ----

  @Test
  void potionAllowedWhenHurtAndPotionsLeft() {
    Pokemon pokemon = Main.choosePokemon(1);
    pokemon.takeDamage(10);
    assertEquals(5, Main.chooseUserMove(input("5\n"), pokemon, 3));
  }

  @Test
  void potionRejectedWhenNoneLeft() {
    Pokemon pokemon = Main.choosePokemon(1);
    pokemon.takeDamage(10);
    assertEquals(1, Main.chooseUserMove(input("5\n1\n"), pokemon, 0));
    assertTrue(output.toString().contains("out of potions"));
  }

  @Test
  void potionRejectedAtFullHealth() {
    Pokemon pokemon = Main.choosePokemon(1);
    assertEquals(2, Main.chooseUserMove(input("5\n2\n"), pokemon, 3));
    assertTrue(output.toString().contains("already at full health"));
  }

  @Test
  void otherMovesAlwaysAllowed() {
    Pokemon pokemon = Main.choosePokemon(1);
    assertEquals(6, Main.chooseUserMove(input("6\n"), pokemon, 0));
  }
}

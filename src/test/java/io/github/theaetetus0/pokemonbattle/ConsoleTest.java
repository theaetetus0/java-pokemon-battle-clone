package io.github.theaetetus0.pokemonbattle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

import io.github.theaetetus0.pokemonbattle.TestData.TestConsole;

class ConsoleTest {

  @Test
  void acceptsNumberInRange() {
    TestConsole console = new TestConsole("3\n");
    assertEquals(3, console.readChoice(6));
    assertFalse(console.output().contains("not valid"));
  }

  @Test
  void acceptsBothEndsOfRange() {
    assertEquals(1, new TestConsole("1\n").readChoice(6));
    assertEquals(6, new TestConsole("6\n").readChoice(6));
  }

  @Test
  void asksAgainAfterOutOfRangeNumbers() {
    TestConsole console = new TestConsole("0\n7\n-1\n2\n");
    assertEquals(2, console.readChoice(6));
    assertEquals(3, console.output().split("not valid", -1).length - 1, "should complain once per bad number");
  }

  @Test
  void asksAgainAfterNonNumbers() {
    TestConsole console = new TestConsole("abc\nfour\n4\n");
    assertEquals(4, console.readChoice(6));
    assertTrue(console.output().contains("Enter a number from 1 to 6"));
  }

  @Test
  void throwsWhenInputRunsOut() {
    // Game relies on this to save caught Pokemon when input ends (e.g. Ctrl+D)
    assertThrows(NoSuchElementException.class, () -> new TestConsole("9\n").readChoice(6));
  }
}

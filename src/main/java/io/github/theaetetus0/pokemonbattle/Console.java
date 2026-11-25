package io.github.theaetetus0.pokemonbattle;

import java.io.PrintStream;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

/** Reads the player's choices and prints game text. */
public class Console {

  private final Scanner scanner;
  private final PrintStream out;

  public Console(Scanner scanner, PrintStream out) {
    this.scanner = scanner;
    this.out = out;
  }

  public void println(String text) {
    out.println(text);
  }

  /**
   * Reads a number from 1 to max, asking again until the input is valid.
   *
   * @throws NoSuchElementException if the input ends (e.g. Ctrl+D)
   */
  public int readChoice(int max) {
    while (true) {
      try {
        int choice = scanner.nextInt();
        if (choice >= 1 && choice <= max) {
          return choice;
        }
      } catch (InputMismatchException e) {
        scanner.nextLine();
      }
      out.println("This input is not valid. Enter a number from 1 to " + max + ": ");
    }
  }
}

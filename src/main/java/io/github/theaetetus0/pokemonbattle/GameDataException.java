package io.github.theaetetus0.pokemonbattle;

/** Thrown when a data file is missing or contains something invalid. */
public class GameDataException extends RuntimeException {

  public GameDataException(String message) {
    super(message);
  }

  public GameDataException(String message, Throwable cause) {
    super(message, cause);
  }
}

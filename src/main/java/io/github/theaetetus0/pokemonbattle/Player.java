package io.github.theaetetus0.pokemonbattle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** The player: their Pokemon, their bag, and what they've caught this session. */
public class Player {

  private final Pokemon pokemon;
  private final Bag bag = new Bag();
  private final List<CaughtPokemon> caught = new ArrayList<>();

  public Player(Pokemon pokemon) {
    this.pokemon = pokemon;
  }

  public Pokemon getPokemon() {
    return pokemon;
  }

  public Bag getBag() {
    return bag;
  }

  public List<CaughtPokemon> getCaught() {
    return Collections.unmodifiableList(caught);
  }

  public void addCaught(CaughtPokemon pokemon) {
    caught.add(pokemon);
  }
}

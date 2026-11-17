# java-pokemon-battle-clone

A turn-based Pokemon battle game for the terminal, written in Java.

Pick a starter, battle random wild Pokemon, and try to catch them. Caught
Pokemon are appended to `caughtPokemon.txt` at the end of each session.

```
You encountered a wild Cyndaquil!

Your Pokemon: Squirtle
Attack: 48, Defence: 65
Health: 44/44

Wild Pokemon: Cyndaquil
Attack: 52, Defence: 43
Health: 39/39

Moves:  (1-6)
1 - Tackle, Type: Normal, Category: Attack
2 - Water Gun, Type: Water, Category: Attack
3 - Rain Dance, Type: Normal, Category: Attack Buff
4 - Protect, Type: Normal, Category: Defence Buff
5 - Potion, Heal 20 Health (3 left)
6 - Throw A Pokeball

Choose A Move:
2

Squirtle used Water Gun!
That was super effective!
The wild Cyndaquil took 22 damage!

The wild Cyndaquil used Coil!
The wild Cyndaquil's defence rose!
```

## Requirements

Java 17 or newer. You don't need to install Maven, because the project
includes the Maven Wrapper (`./mvnw`), which downloads Maven the first time
you run it.

## Running

The quickest way to play is to compile and run it straight from source:

```sh
./run.sh
```

Or build a jar with Maven and run that:

```sh
./mvnw package
java -jar target/java-pokemon-battle-clone-0.1.0.jar
```

On Windows, use `mvnw.cmd` instead of `./mvnw`.

## How to play

1. **Choose a starter** from the list of 10 Pokemon by typing its number.
2. **A wild Pokemon appears.** It is always a different species from yours.
3. **Pick a move each turn.** You always move first, then the wild Pokemon
   uses a random move.

| # | Option | What it does |
|---|--------|--------------|
| 1 | Normal attack | Deals damage based on your attack and the opponent's defence |
| 2 | Type attack | Like a normal attack, but multiplied by type effectiveness |
| 3 | Attack buff | Raises your attack |
| 4 | Defence buff | Raises your defence |
| 5 | Potion | Heals 20 health. You get 3 per battle, and can't use one at full health |
| 6 | Pokeball | Tries to catch the wild Pokemon |

4. **The battle ends** when the wild Pokemon faints, you catch it, or your
   Pokemon faints. If your Pokemon faints, it's game over.
5. After a win, you can look for another wild Pokemon. Your Pokemon is fully
   healed before each battle.

### Types

Type attacks do **2×** damage when they're super effective, **0.5×** when
they're not very effective, and **1×** otherwise.

| Attacking type | Strong against | Weak against |
|----------------|----------------|--------------|
| Fire | Grass | Water |
| Water | Fire | Grass |
| Grass | Water | Fire |

### Stats and catching

- Buffs can raise attack and defence to at most **2×** their starting value.
- The chance to catch a Pokemon goes from **10%** at full health up to
  **90%** when it is almost fainted, so weaken it first.
- When you quit, or when input ends (Ctrl+D), the Pokemon you caught that
  session are added to `caughtPokemon.txt` with the number of Pokeballs each
  one took.

## Running the tests

```sh
./mvnw test
```

The tests use JUnit 5 and cover:

| Test class | What it checks |
|------------|----------------|
| `PokemonTest` | Damage, fainting, healing limits, and the attack and defence caps |
| `SpeciesTest` | All 10 species have valid types, stats and moves, and invalid numbers are rejected |
| `BattleTest` | Type matchups, damage from each move, buffs, and catch chance |
| `InputTest` | Invalid input is rejected and the player is asked again, and potions are refused when they can't be used |
| `SaveFileTest` | Format of `caughtPokemon.txt`, and that each session is appended |

## Project layout

```
src/main/java/
  Main.java        game loop, battle logic, type chart, input and saving
  Pokemon.java     base class with stats, damage, healing and buffs
  <Species>.java   one class per Pokemon, defining its move names and buff amounts
src/test/java/     JUnit tests
run.sh             compile and run without Maven
mvnw, mvnw.cmd     Maven Wrapper
```

## Roadmap

- Load Pokemon and moves from a data file instead of one class per species
- Split `Main` into `Battle`, `TypeChart` and `Player` classes
- Rebalance damage so it scales with attack ÷ defence, with some random spread
- Speed, levels and XP, a team of up to 6 with switching, and status effects
- More types, trainer battles, a shop, and saving and loading

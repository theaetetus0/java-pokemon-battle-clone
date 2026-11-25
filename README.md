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

## Changing the Pokemon, moves and types

All game content is in three CSV files in
[`src/main/resources/data/`](src/main/resources/data/), so you can add or
change Pokemon without touching any Java code.

| File | One row per | Columns |
|------|-------------|---------|
| `pokemon.csv` | species, in starter menu order | `name,type,health,attack,defence,move1,move2,move3,move4` |
| `moves.csv` | move | `name,type,category,amount` |
| `types.csv` | type matchup | `attacker,defender,multiplier` |

- A Pokemon's moves refer to moves by name. `move1` is required, and the
  rest can be left blank.
- A move's `category` is `attack`, `raise_attack` or `raise_defence`. Buff
  moves need an `amount`, and attacks leave it blank.
- Any type pair that isn't in `types.csv` does normal (1×) damage, and
  `Normal` moves always do 1×. The type hints on the starter menu are
  generated from this file.
- Lines starting with `#` are comments.

The files are checked when the game starts. A mistake stops the game with a
message that names the file and line, for example:

```
Could not load game data: pokemon.csv line 7: unknown move "Ember Blast" (add it to moves.csv)
```

## Running the tests

```sh
./mvnw test
```

The tests use JUnit 5 and cover:

| Test class | What it checks |
|------------|----------------|
| `PokemonTest` | Damage, fainting, healing limits, the attack and defence caps, and restoring |
| `GameDataTest` | The bundled data loads and matches the original stats, and bad CSV files give clear errors |
| `TypeChartTest` | Type matchups, case-insensitive type names, and the starter menu type hints |
| `BattleTest` | The damage formula, effectiveness, buffs, catch chance, potion rules, and whole battles that are won, lost and caught |
| `ConsoleTest` | Invalid input is rejected and the player is asked again |
| `GameTest` | The full game: starter menu, wild Pokemon choice, healing between battles, and saving catches |
| `CaughtLogTest` | Format of `caughtPokemon.txt`, and that each session is appended |

Battles use random numbers, so the tests pass in a fixed `Random` to make
each battle play out the same way every time.

## Project layout

All code is in the `io.github.theaetetus0.pokemonbattle` package.

```
src/main/java/io/github/theaetetus0/pokemonbattle/
  Main.java           starts the game
  Game.java           starter menu, the loop of wild battles, and saving at the end
  Battle.java         one battle: turns, moves, damage, potions and catching
  Pokemon.java        a Pokemon's current health and buffed stats
  Species.java        a species' fixed data: type, base stats and moves
  Move.java           a move's name, type, category and buff amount
  MoveCategory.java   attack, attack buff or defence buff
  TypeChart.java      type effectiveness table
  Player.java         the player's Pokemon, bag and caught list
  Bag.java            potions
  Console.java        reading choices and printing text
  CaughtLog.java      writes caughtPokemon.txt
  GameData.java       loads and checks the CSV files
  CsvFile.java        small CSV reader used by GameData
src/main/resources/data/
  pokemon.csv, moves.csv, types.csv
src/test/java/        JUnit tests
run.sh                compile and run without Maven
mvnw, mvnw.cmd        Maven Wrapper
```

## Roadmap

- Rebalance damage so it scales with attack ÷ defence, with some random spread
- Speed, levels and XP, a team of up to 6 with switching, and status effects
- More types, trainer battles, a shop, and saving and loading

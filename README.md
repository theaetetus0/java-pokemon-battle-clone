# java-pokemon-battle-clone

A turn-based Pokemon battle game for the terminal, written in Java.

Pick a starter, battle random wild Pokemon, and try to catch them. Caught
Pokemon are appended to `caughtPokemon.txt` at the end of each session.

## Running

Requires Java 17 or newer.

```sh
./run.sh
```

Or with Maven:

```sh
mvn package
java -jar target/java-pokemon-battle-clone-0.1.0.jar
```

## Project layout

- `src/main/java/Main.java` - game loop, battle logic, input handling
- `src/main/java/Pokemon.java` - base class with stats, damage, healing and buffs
- `src/main/java/<Species>.java` - one class per Pokemon, defining its move names and buff amounts

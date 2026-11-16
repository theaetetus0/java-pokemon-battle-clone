import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

class Main {

  private static final int NUM_POKEMON = 10;
  private static final int POTIONS_PER_BATTLE = 3;
  private static final int POTION_HEAL_AMOUNT = 20;

  // catch chance ranges from MIN (wild Pokemon at full health) to MIN + RANGE (almost fainted)
  private static final double MIN_CATCH_CHANCE = 0.1;
  private static final double CATCH_CHANCE_RANGE = 0.8;

  public static void main(String[] args) {

    Scanner scan = new Scanner(System.in);

    // store caught Pokemon and the pokeballs used to catch each one
    ArrayList<String> pokemonCaught = new ArrayList<String>();
    ArrayList<Integer> pokeballUsed = new ArrayList<Integer>();

    File writeFile = new File("caughtPokemon.txt");

    System.out.println("\nWelcome to Pokemon!");

    try {
      listPokemon();
      int userChooseInput = checkUserInput(scan, NUM_POKEMON);

      while (true) {

        // a fresh (fully healed) copy of the user's Pokemon for each battle
        Pokemon userPokemon = choosePokemon(userChooseInput);

        // let computer choose a Pokemon that is different from user chosen Pokemon
        int computerChooseInput;
        do {
          computerChooseInput = (int) (Math.random() * NUM_POKEMON) + 1;
        } while (computerChooseInput == userChooseInput);

        Pokemon computerPokemon = choosePokemon(computerChooseInput);
        System.out.println("\nYou encountered a wild " + computerPokemon.getName() + "!");

        boolean caught = false;
        int pokeballThrown = 0;
        int potionsLeft = POTIONS_PER_BATTLE;

        while (true) {

          // ---- user's turn ----
          printStatus(userPokemon, computerPokemon);
          listMoves(userPokemon, potionsLeft);
          int userMoveInput = chooseUserMove(scan, userPokemon, potionsLeft);

          if (userMoveInput <= 4) {
            useMove(userMoveInput, userPokemon, computerPokemon, false);

          } else if (userMoveInput == 5) {
            potionsLeft--;
            System.out.println("\nYou take a potion from your bag! (" + potionsLeft + " left)");
            int healed = userPokemon.heal(POTION_HEAL_AMOUNT);
            System.out.println(userPokemon.getName() + " recovered " + healed + " health!");

          } else {
            pokeballThrown++;
            System.out.println("\nYou threw a Pokeball!");
            if (Math.random() < catchChance(computerPokemon)) {
              caught = true;
            } else {
              System.out.println("Oh no! The wild " + computerPokemon.getName() + " broke free!");
            }
          }

          if (caught || computerPokemon.isFainted()) {
            break;
          }

          // ---- computer's turn: random move between 1 and 4 ----
          int computerMoveInput = (int) (Math.random() * 4) + 1;
          useMove(computerMoveInput, computerPokemon, userPokemon, true);

          if (userPokemon.isFainted()) {
            break;
          }
        }

        if (caught) {
          pokemonCaught.add(computerPokemon.getName());
          pokeballUsed.add(pokeballThrown);
          System.out.println("Gotcha! The wild " + computerPokemon.getName() + " was caught!");

        } else if (computerPokemon.isFainted()) {
          System.out.println("\nThe wild " + computerPokemon.getName() + " fainted!");

        } else {
          System.out.println("\nYour " + userPokemon.getName() + " fainted!");
          System.out.println("Game Over!");
          break;
        }

        System.out.println("\nWould you like to find another wild Pokemon?");
        System.out.println("1 - Yes");
        System.out.println("2 - No");
        System.out.println("\nChoose an option:");

        if (checkUserInput(scan, 2) == 2) {
          break;
        }

        System.out.println("\nNurse Joy healed " + userPokemon.getName() + "!");
      }

    } catch (NoSuchElementException e) {
      // input ended (e.g. Ctrl+D) - still save what was caught
      System.out.println("\nNo more input, ending the game.");
    }

    writeList(writeFile, pokemonCaught, pokeballUsed);
    System.out.println("\nThank you for playing Pokemon!");
  }

  // perform one of the 4 moves for attacker against defender
  public static void useMove(int move, Pokemon attacker, Pokemon defender, boolean attackerIsWild) {

    String attackerName = (attackerIsWild ? "The wild " : "") + attacker.getName();
    String defenderName = (attackerIsWild ? "" : "The wild ") + defender.getName();

    // ratio of the attacker's attack to the defender's defence
    double damageRatio = (double) attacker.getAttack() / defender.getDefence();
    int damage;

    switch (move) {

      case 1: // normal attack
        System.out.println("\n" + attackerName + " used " + attacker.move1Name() + "!");
        damage = (int) Math.round(1 + (damageRatio * attacker.getAttack() / 5));
        defender.takeDamage(damage);
        System.out.println(defenderName + " took " + damage + " damage!");
        break;

      case 2: // special "type" attack
        System.out.println("\n" + attackerName + " used " + attacker.move2Name() + "!");
        damage = (int) Math.round(
            1 + (damageRatio * attacker.getAttack() / 5 * checkEffectiveness(attacker, defender)));
        defender.takeDamage(damage);
        System.out.println(defenderName + " took " + damage + " damage!");
        break;

      case 3: // attack buff
        System.out.println("\n" + attackerName + " used " + attacker.move3Name() + "!");
        if (attacker.raiseAttack(attacker.move3()) > 0) {
          System.out.println(attackerName + "'s attack rose!");
        } else {
          System.out.println(attackerName + "'s attack won't go any higher!");
        }
        break;

      case 4: // defence buff
        System.out.println("\n" + attackerName + " used " + attacker.move4Name() + "!");
        if (attacker.raiseDefence(attacker.move4()) > 0) {
          System.out.println(attackerName + "'s defence rose!");
        } else {
          System.out.println(attackerName + "'s defence won't go any higher!");
        }
        break;
    }
  }

  // probability of catching a wild Pokemon, higher when its health is lower
  public static double catchChance(Pokemon wildPokemon) {
    double healthRatio = (double) wildPokemon.getHealth() / wildPokemon.getMaxHealth();
    return MIN_CATCH_CHANCE + CATCH_CHANCE_RANGE * (1 - healthRatio);
  }

  // ask for the user's move, rejecting potions that can't be used without using up the turn
  public static int chooseUserMove(Scanner scan, Pokemon userPokemon, int potionsLeft) {

    while (true) {
      int input = checkUserInput(scan, 6);

      if (input == 5 && potionsLeft == 0) {
        System.out.println("You're out of potions! Choose another move: ");
      } else if (input == 5 && userPokemon.getHealth() == userPokemon.getMaxHealth()) {
        System.out.println(userPokemon.getName() + " is already at full health! Choose another move: ");
      } else {
        return input;
      }
    }
  }

  public static void printStatus(Pokemon userPokemon, Pokemon computerPokemon) {

    System.out.println("\n\nYour Pokemon: " + userPokemon.getName());
    System.out.println("Attack: " + userPokemon.getAttack() + ", Defence: " + userPokemon.getDefence());
    System.out.println("Health: " + userPokemon.getHealth() + "/" + userPokemon.getMaxHealth());

    System.out.println("\nWild Pokemon: " + computerPokemon.getName());
    System.out.println("Attack: " + computerPokemon.getAttack() + ", Defence: " + computerPokemon.getDefence());
    System.out.println("Health: " + computerPokemon.getHealth() + "/" + computerPokemon.getMaxHealth());
  }

  // give list of pokemon to user, built from the same data used to create them
  public static void listPokemon() {

    System.out.println("\nChoose a Pokemon!  (1-" + NUM_POKEMON + ")");
    System.out.println("Fire is strong against Grass, but weak against Water");
    System.out.println("Water is strong against Fire, but weak against Grass");
    System.out.println("Grass is strong against Water, but weak against Fire\n");

    for (int i = 1; i <= NUM_POKEMON; i++) {
      Pokemon p = choosePokemon(i);
      System.out.println(i + " - " + p.getName() + ", Type: " + p.getType() + ", Health: " + p.getMaxHealth()
          + ", Attack: " + p.getAttack() + ", Defence: " + p.getDefence());
    }

    System.out.println("\nChoose A Pokemon: ");
  }

  // method to return Pokemon parameters to create Pokemon object
  public static Pokemon choosePokemon(int input) {

    // Stats are taken from Nintendo's Pokemon Game
    switch (input) {
      case 1:
        return new Charmander("Fire", "Charmander", 39, 52, 43);
      case 2:
        return new Squirtle("Water", "Squirtle", 44, 48, 65);
      case 3:
        return new Bulbasaur("Grass", "Bulbasaur", 45, 49, 49);
      case 4:
        return new Cyndaquil("Fire", "Cyndaquil", 39, 52, 43);
      case 5:
        return new Oshawott("Water", "Oshawott", 55, 55, 45);
      case 6:
        return new Treecko("Grass", "Treecko", 40, 45, 35);
      case 7:
        return new Chimchar("Fire", "Chimchar", 44, 58, 44);
      case 8:
        return new Froakie("Water", "Froakie", 41, 56, 40);
      case 9:
        return new Turtwig("Grass", "Turtwig", 55, 68, 64);
      case 10:
        return new Talonflame("Fire", "Talonflame", 78, 81, 71);
      default:
        throw new IllegalArgumentException("No Pokemon with number " + input);
    }
  }

  // method for listing possible moves for user Pokemon
  public static void listMoves(Pokemon userPokemon, int potionsLeft) {

    System.out.println("\nMoves:  (1-6)");

    System.out.println("1 - " + userPokemon.move1Name() + ", Type: Normal, Category: Attack");
    System.out.println("2 - " + userPokemon.move2Name() + ", Type: " + userPokemon.getType() + ", Category: Attack");
    System.out.println("3 - " + userPokemon.move3Name() + ", Type: Normal, Category: Attack Buff");
    System.out.println("4 - " + userPokemon.move4Name() + ", Type: Normal, Category: Defence Buff");
    System.out.println("5 - Potion, Heal " + POTION_HEAL_AMOUNT + " Health (" + potionsLeft + " left)");
    System.out.println("6 - Throw A Pokeball");

    System.out.println("\nChoose A Move: ");
  }

  // check if a move is super effective, not very effective, or neutral
  public static double checkEffectiveness(Pokemon attackingPokemon, Pokemon defendingPokemon) {

    double effectiveness = calculateEffectiveness(attackingPokemon, defendingPokemon);

    if (effectiveness == 2) {
      System.out.println("That was super effective!");
    } else if (effectiveness == 0.5) {
      System.out.println("That was not very effective.");
    }

    return effectiveness;
  }

  // calculate effectiveness of special "type" attack, if super effective 2x, if
  // not very effective 0.5x, otherwise 1x
  public static double calculateEffectiveness(Pokemon attackingPokemon, Pokemon defendingPokemon) {

    String attackType = attackingPokemon.getType();
    String defendType = defendingPokemon.getType();

    if (beats(attackType, defendType)) {
      return 2;
    } else if (beats(defendType, attackType)) {
      return 0.5;
    }
    return 1;
  }

  // Fire beats Grass, Water beats Fire, Grass beats Water
  private static boolean beats(String attackType, String defendType) {
    return (attackType.equalsIgnoreCase("Fire") && defendType.equalsIgnoreCase("Grass"))
        || (attackType.equalsIgnoreCase("Water") && defendType.equalsIgnoreCase("Fire"))
        || (attackType.equalsIgnoreCase("Grass") && defendType.equalsIgnoreCase("Water"));
  }

  // read an integer from 1 to max, asking again until the input is valid
  public static int checkUserInput(Scanner scan, int max) {

    while (true) {
      try {
        int userInput = scan.nextInt();

        if (userInput > 0 && userInput <= max) {
          return userInput;
        }
        System.out.println("This input is not valid. Enter a number from 1 to " + max + ": ");

      } catch (InputMismatchException e) {
        System.out.println("This input is not valid. Enter a number from 1 to " + max + ": ");
        scan.nextLine();
      }
    }
  }

  // append this session's caught Pokemon to the text file
  public static void writeList(File fileName, ArrayList<String> pokemonCaught, ArrayList<Integer> pokeballUsed) {

    if (pokemonCaught.isEmpty()) {
      return;
    }

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {

      String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
      writer.write("--- Session " + timestamp + " ---");
      writer.newLine();

      for (int index = 0; index < pokemonCaught.size(); index++) {
        writer.write("Pokemon Caught: " + pokemonCaught.get(index) + ", Pokeballs Used: " + pokeballUsed.get(index));
        writer.newLine();
      }

    } catch (IOException e) {
      System.out.println("An error occurred when writing to file: " + e.getMessage());
    }
  }

}

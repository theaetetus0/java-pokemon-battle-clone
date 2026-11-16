
public class Oshawott extends Pokemon {

  // class constructor
  public Oshawott(String newType, String newName, int newHealth, int newAttack, int newDefence) {
    super(newType, newName, newHealth, newAttack, newDefence);

  }

  public String move1Name() {
    return "Double Hit";

  }

  public String move2Name() {
    return "Surf";

  }

  // attack buff
  public int move3() {
    return 6;

  }

  public String move3Name() {
    return "Shell Smash";

  }

  // defence buff
  public int move4() {
    return 9;

  }

  public String move4Name() {
    return "Harden";

  }

}
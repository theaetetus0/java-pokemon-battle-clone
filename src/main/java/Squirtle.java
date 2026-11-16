
public class Squirtle extends Pokemon {

  // class constructor
  public Squirtle(String newType, String newName, int newHealth, int newAttack, int newDefence) {
    super(newType, newName, newHealth, newAttack, newDefence);

  }

  public String move1Name() {
    return "Tackle";

  }

  public String move2Name() {
    return "Water Gun";

  }

  // attack buff
  public int move3() {
    return 5;

  }

  public String move3Name() {
    return "Rain Dance";

  }

  // defence buff
  public int move4() {
    return 10;

  }

  public String move4Name() {
    return "Protect";

  }

}
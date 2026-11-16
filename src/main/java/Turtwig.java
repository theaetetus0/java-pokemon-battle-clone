
public class Turtwig extends Pokemon {

  // class constructor
  public Turtwig(String newType, String newName, int newHealth, int newAttack, int newDefence) {
    super(newType, newName, newHealth, newAttack, newDefence);

  }

  public String move1Name() {
    return "Body Slam";

  }

  public String move2Name() {
    return "Vine Whip";

  }

  // attack buff
  public int move3() {
    return 5;

  }

  public String move3Name() {
    return "Bulk Up";

  }

  // defence buff
  public int move4() {
    return 10;

  }

  public String move4Name() {
    return "Endure";

  }

}
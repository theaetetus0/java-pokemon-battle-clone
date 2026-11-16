
public class Chimchar extends Pokemon {

  // class constructor
  public Chimchar(String newType, String newName, int newHealth, int newAttack, int newDefence) {
    super(newType, newName, newHealth, newAttack, newDefence);

  }

  public String move1Name() {
    return "Giga Impact";

  }

  public String move2Name() {
    return "Fire Punch";

  }

  // attack buff
  public int move3() {
    return 8;

  }

  public String move3Name() {
    return "Double Edge";

  }

  // defence buff
  public int move4() {
    return 7;

  }

  public String move4Name() {
    return "Iron Defence";

  }

}
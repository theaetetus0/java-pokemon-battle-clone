
public class Cyndaquil extends Pokemon {

  // class constructor
  public Cyndaquil(String newType, String newName, int newHealth, int newAttack, int newDefence) {
    super(newType, newName, newHealth, newAttack, newDefence);

  }

  public String move1Name() {
    return "Quick Attack";

  }

  public String move2Name() {
    return "Flame Wheel";

  }

  // attack buff
  public int move3() {
    return 9;

  }

  public String move3Name() {
    return "Sword Dance";

  }

  // defence buff
  public int move4() {
    return 6;

  }

  public String move4Name() {
    return "Coil";

  }

}

public class Froakie extends Pokemon {

  // class constructor
  public Froakie(String newType, String newName, int newHealth, int newAttack, int newDefence) {
    super(newType, newName, newHealth, newAttack, newDefence);

  }

  public String move1Name() {
    return "Hyper Beam";

  }

  public String move2Name() {
    return "Water Fall";

  }

  // attack buff
  public int move3() {
    return 9;

  }

  public String move3Name() {
    return "Howl";

  }

  // defence buff
  public int move4() {
    return 6;

  }

  public String move4Name() {
    return "Barrier";

  }

}
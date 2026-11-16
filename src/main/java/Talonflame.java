
public class Talonflame extends Pokemon {

  // class constructor
  public Talonflame(String newType, String newName, int newHealth, int newAttack, int newDefence) {
    super(newType, newName, newHealth, newAttack, newDefence);

  }

  public String move1Name() {
    return "Feint";

  }

  public String move2Name() {
    return "Flare Blitz";

  }

  // attack buff
  public int move3() {
    return 10;

  }

  public String move3Name() {
    return "Agility";

  }

  // defence buff
  public int move4() {
    return 10;

  }

  public String move4Name() {
    return "Substitute";

  }

}
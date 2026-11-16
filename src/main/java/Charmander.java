
public class Charmander extends Pokemon {

  // class constructor
  public Charmander(String newType, String newName, int newHealth, int newAttack, int newDefence) {
    super(newType, newName, newHealth, newAttack, newDefence);

  }

  public String move1Name() {
    return "Scratch";

  }

  public String move2Name() {
    return "Ember";

  }

  // attack buff
  public int move3() {
    return 10;

  }

  public String move3Name() {
    return "Dragon Dance";

  }

  // defence buff
  public int move4() {
    return 5;

  }

  public String move4Name() {
    return "Defence Curl";

  }

}
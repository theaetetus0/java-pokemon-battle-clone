
public class Bulbasaur extends Pokemon {

  // class constructor
  public Bulbasaur(String newType, String newName, int newHealth, int newAttack, int newDefence) {
    super(newType, newName, newHealth, newAttack, newDefence);

  }

  public String move1Name() {
    return "Scratch";

  }

  public String move2Name() {
    return "Razor Leaf";

  }

  // attack buff
  public int move3() {
    return 7;

  }

  public String move3Name() {
    return "Growth";

  }

  // defence buff
  public int move4() {
    return 8;

  }

  public String move4Name() {
    return "Smoke Screen";

  }

}
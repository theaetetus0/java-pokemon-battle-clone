
public class Treecko extends Pokemon {

  // class constructor
  public Treecko(String newType, String newName, int newHealth, int newAttack, int newDefence) {
    super(newType, newName, newHealth, newAttack, newDefence);

  }

  public String move1Name() {
    return "Tackle";

  }

  public String move2Name() {
    return "Seed Bomb";

  }

  // attack buff
  public int move3() {
    return 9;

  }

  public String move3Name() {
    return "Nasty Plot";

  }

  // defence buff
  public int move4() {
    return 6;

  }

  public String move4Name() {
    return "Cotton Guard";

  }

}
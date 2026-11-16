abstract class Pokemon {

  // stats can be buffed up to this multiple of their starting value
  private static final int MAX_STAT_MULTIPLIER = 2;

  private final String type;
  private final String name;
  private final int maxHealth;
  private final int baseAttack;
  private final int baseDefence;
  protected int health;
  protected int attack;
  protected int defence;

  // class constructor for Pokemon objects
  public Pokemon(String newType, String newName, int newHealth, int newAttack, int newDefence) {
    type = newType;
    name = newName;
    maxHealth = newHealth;
    baseAttack = newAttack;
    baseDefence = newDefence;
    health = newHealth;
    attack = newAttack;
    defence = newDefence;
  }

  public String getType() {
    return type;
  }

  public String getName() {
    return name;
  }

  public int getHealth() {
    return health;
  }

  public int getMaxHealth() {
    return maxHealth;
  }

  public boolean isFainted() {
    return health <= 0;
  }

  // health never drops below 0
  public void takeDamage(int damage) {
    health = Math.max(0, health - damage);
  }

  public int getAttack() {
    return attack;
  }

  public int getDefence() {
    return defence;
  }

  // raise attack, capped at MAX_STAT_MULTIPLIER x base; returns the amount actually gained
  public int raiseAttack(int amount) {
    int newAttack = Math.min(attack + amount, baseAttack * MAX_STAT_MULTIPLIER);
    int gained = newAttack - attack;
    attack = newAttack;
    return gained;
  }

  // raise defence, capped at MAX_STAT_MULTIPLIER x base; returns the amount actually gained
  public int raiseDefence(int amount) {
    int newDefence = Math.min(defence + amount, baseDefence * MAX_STAT_MULTIPLIER);
    int gained = newDefence - defence;
    defence = newDefence;
    return gained;
  }

  // heal Pokemon, never above max health; returns the amount actually healed
  public int heal(int amount) {
    int newHealth = Math.min(health + amount, maxHealth);
    int healed = newHealth - health;
    health = newHealth;
    return healed;
  }

  // abstract methods for Pokemon
  abstract String move1Name();

  abstract String move2Name();

  abstract int move3();

  abstract String move3Name();

  abstract int move4();

  abstract String move4Name();

}

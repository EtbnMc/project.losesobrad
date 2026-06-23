package Model;
public class GameCharacter {

    private String name;
    private int health;
    private int maxHealth;
    private int attack;
    private int positionX;
    private int positionY;


    public GameCharacter(String name, int health, int attack, int positionX, int positionY) {
        this.name = name;
        this.health = health;
        this.maxHealth = health;
        this.attack = attack;
        this.positionX = positionX;
        this.positionY = positionY;
    }

    public String getName(){
        return name; }
    public int getHealth(){
        return health; }
    public int getMaxHealth(){
        return maxHealth; }
    public int getAttack(){
        return attack; }
    public int getPositionX(){
        return positionX; }
    public int getPositionY(){
        return positionY; }

    public void setPositionX(int x){
        this.positionX = x; }
    public void setPositionY(int y){
        this.positionY = y; }

    public void receiveDamage(int damage) {
        this.health = Math.max(0, this.health - damage);
    }

    public void healFully() {
        this.health = this.maxHealth;
    }

    public boolean isAlive() {
        return this.health > 0;
    }

    public void moveTo(int newX, int newY) {
        this.positionX = newX;
        this.positionY = newY;
    }

    public int performAttack(GameCharacter target) {
        target.receiveDamage(this.attack);
        return this.attack;
    }

    public String toString() {
        return name + " [HP: " + health + "/" + maxHealth + " | ATK: " + attack + " | Pos: (" + positionX + "," + positionY + ")]";
    }
}

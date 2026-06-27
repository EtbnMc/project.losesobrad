package Model;

public class Chest extends Enemy {
    private static final long serialVersionUID = 1L;

    public static final char Symbol = 'C';

    private boolean looted = false;

    public Chest(String name, int positionX, int positionY) {

        //weaker than a normal enemy, it relies on surprise rather than strength
        super(name, 25, 8, positionX, positionY);
    }

    public boolean isLooted() {
        return looted; }


    //called once after defeat to hand over the loot exactly one time
    public Key collectLoot() {
        if (looted) {
            return null;
        }
        looted = true;
        return new Key("Rusty Key");
    }

    public String toString() {
        return "Chest - " + super.toString();
    }
}

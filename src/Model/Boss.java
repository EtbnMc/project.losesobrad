package Model;
import java.io.Serializable; //save
public class Boss extends Enemy implements Serializable {
private static final long serialVersionUID = 1L;
    public static final char Symbol = 'B';

    private boolean defeated = false;

    public Boss(String name, int positionX, int positionY) {
        super(name, 150, 40, positionX, positionY);
    }

    public void receiveDamage(int damage) {
        super.receiveDamage(damage);
        if (!isAlive() && !defeated) {
            defeated = true;
        }
    }

    public boolean wasDefeated() {
        return defeated;
    }

    public char getRandomMoveDirection() {
        return ' ';
    }

    public String toString() {
        return "Boss  " + super.toString();
    }
}

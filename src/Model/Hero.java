package Model;
import java.io.Serializable;
public class Hero extends GameCharacter implements Serializable{
    private static final long serialVersionUID = 1L;

    public static final char Symbol = 'H';

    public Hero(String name, int positionX, int positionY) {
        super(name, 150, 25, positionX, positionY);
    }

    public int[] calculateDestination(char direction) {
        int nextX = getPositionX();
        int nextY = getPositionY();


        char upperDirection = direction;
        if (direction >= 'a' && direction <= 'z') {
            upperDirection = (char) (direction - 32);
        }

        switch (upperDirection) {
            case 'W': nextX -= 1; break;
            case 'S': nextX += 1; break;
            case 'A': nextY -= 1; break;
            case 'D': nextY += 1; break;
            default: break;
        }

        return new int[] { nextX, nextY };
    }

    public String toString() {
        return "HERO - " + super.toString();
    }
}

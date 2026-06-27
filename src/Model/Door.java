package Model;
import java.io.Serializable;

public class Door implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final char Symbol = 'D';
    public static final char OpenSymbol = '/';

    private int positionX;
    private int positionY;
    private boolean open;

    public Door(int positionX, int positionY) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.open = false;
    }

    public int getPositionX() {
        return positionX; }
    public int getPositionY() {
        return positionY; }
    public boolean isOpen() {
        return open; }

    //returns true if the key successfully opened the door
    public boolean tryOpen(Hero hero) {
        if (open) {
            return true;
        }
        if (hero.hasKey()) {
            hero.consumeKey();
            open = true;
            return true;
        }
        return false;
    }
}

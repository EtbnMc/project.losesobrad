package Model;
import java.io.Serializable;
import java.util.Random;

public class Enemy extends GameCharacter implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final char Symbol = 'E';

    private static final Random RANDOM = new Random();

    public Enemy(String name, int positionX, int positionY) {
        super(name, 35, 20, positionX, positionY);
    }

    protected Enemy(String name, int health, int attack, int positionX, int positionY) {
        super(name, health, attack, positionX, positionY);
    }

    public int attackHero(Hero hero) {
        return performAttack(hero);
    }

    public char getRandomMoveDirection() {
        char[] directions = { 'W', 'A', 'S', 'D', ' ' };
        return directions[RANDOM.nextInt(directions.length)];
    }
}
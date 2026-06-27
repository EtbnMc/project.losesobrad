package Model;
import java.io.Serializable;

public abstract class Item implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;

    public Item(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    //subclasses define what happens when the hero uses the item
    public abstract void use(Hero hero);
}

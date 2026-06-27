package Model;

public class Key extends Item {
    private static final long serialVersionUID = 1L;

    public static final char Symbol = 'K';

    public Key(String name) {
        super(name);
    }

    //using a key by itself does nothing, Door checks the hero's inventory
    public void use(Hero hero) {
        //no direct effect, kept for Item contract
    }
}
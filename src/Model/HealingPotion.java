package Model;

public class HealingPotion extends Item {
    private static final long serialVersionUID = 1L;

    private static final int Heal = 150;

    public HealingPotion() {
        super("Healing Potion");
    }

    //infinite potion, never consumed, just restores some HP each use
    public void use(Hero hero) {
        hero.heal(Heal);
    }

    public int getHealAmount() {
        return Heal;
    }
}

package project.roguelike.items;

public class Medkit extends Item {
    private final int healAmmount;

    public Medkit() {
        super("medkit", "Medkit", ItemType.CONSUMABLE);
        this.healAmmount = 10;
    }

    public int getHealAmmount() {
        return healAmmount;
    }
}

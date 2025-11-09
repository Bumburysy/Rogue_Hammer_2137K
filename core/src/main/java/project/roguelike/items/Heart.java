package project.roguelike.items;

public class Heart extends Item {
    private final int healAmmount;

    public Heart() {
        super("heart", "Heart", ItemType.CONSUMABLE);
        this.healAmmount = 2;
    }

    public int getHealAmmount() {
        return healAmmount;
    }
}

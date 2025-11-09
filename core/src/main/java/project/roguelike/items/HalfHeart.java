package project.roguelike.items;

public class HalfHeart extends Item {
    private final int healAmmount;

    public HalfHeart() {
        super("half_heart", "Half Heart", ItemType.CONSUMABLE);
        this.healAmmount = 1;
    }

    public int getHealAmmount() {
        return healAmmount;
    }
}

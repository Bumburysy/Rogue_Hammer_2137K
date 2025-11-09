package project.roguelike.items;

public class PassiveItem extends Item {
    private final float speedMultiplier;
    private final float shootCooldownMultiplier;
    private final int maxHpBonus;

    public PassiveItem(String id, String name, float speedMultiplier, float shootCooldownMultiplier, int maxHpBonus) {
        super(id, name, ItemType.PASSIVE);
        this.speedMultiplier = speedMultiplier;
        this.shootCooldownMultiplier = shootCooldownMultiplier;
        this.maxHpBonus = maxHpBonus;
    }

    public float getSpeedMultiplier() {
        return speedMultiplier;
    }

    public float getShootCooldownMultiplier() {
        return shootCooldownMultiplier;
    }

    public int getMaxHpBonus() {
        return maxHpBonus;
    }
}
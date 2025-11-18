package project.roguelike.items.consumableItems;

import project.roguelike.items.Item;

public abstract class ConsumableItem extends Item {
    private final int healAmount;

    protected ConsumableItem(String id, String name, int healAmount) {
        super(id, name, ItemType.CONSUMABLE);
        this.healAmount = healAmount;
    }

    public int getHealAmount() {
        return healAmount;
    }

    public abstract void onConsume();
}
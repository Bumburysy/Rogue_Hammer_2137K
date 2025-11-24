package project.roguelike.items.consumableItems;

import project.roguelike.entities.Player;
import project.roguelike.items.Item;

public abstract class ConsumableItem extends Item {
    protected ConsumableItem(String id, String name) {
        super(id, name, ItemType.CONSUMABLE);
    }

    public abstract void onConsume(Player player);
}
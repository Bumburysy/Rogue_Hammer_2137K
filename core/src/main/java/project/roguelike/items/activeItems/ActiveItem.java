package project.roguelike.items.activeItems;

import project.roguelike.items.Item;

public abstract class ActiveItem extends Item {
    protected ActiveItem(String id, String name) {
        super(id, name, ItemType.ACTIVE);
    }

    public void onUse() {
    }
}

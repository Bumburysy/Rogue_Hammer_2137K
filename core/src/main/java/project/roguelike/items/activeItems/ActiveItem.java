package project.roguelike.items.activeItems;

import project.roguelike.entities.Player;
import project.roguelike.items.Item;

public abstract class ActiveItem extends Item {
    protected float cooldown;
    protected float currentCooldown;

    protected ActiveItem(String id, String name, float cooldown) {
        super(id, name, ItemType.ACTIVE);
        this.cooldown = cooldown;
        this.currentCooldown = 0f;
    }

    public void update(float delta) {
        if (currentCooldown > 0) {
            currentCooldown -= delta;
            if (currentCooldown < 0) {
                currentCooldown = 0;
            }
        }
    }

    public boolean canUse() {
        return currentCooldown <= 0;
    }

    public void use(Player player) {
        if (!canUse()) {
            return;
        }
        onUse(player);
        currentCooldown = cooldown;
    }

    protected abstract void onUse(Player player);

    public float getCurrentCooldown() {
        return currentCooldown;
    }

    public float getCooldown() {
        return cooldown;
    }
}

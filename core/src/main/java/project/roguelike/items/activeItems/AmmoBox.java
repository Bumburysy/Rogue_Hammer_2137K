package project.roguelike.items.activeItems;

import com.badlogic.gdx.graphics.Texture;
import project.roguelike.core.GameConfig;
import project.roguelike.entities.Player;
import project.roguelike.items.weapons.Weapon;

public class AmmoBox extends ActiveItem {
    private static final float COOLDOWN = 45f;
    private static final String ITEM_ID = "ammo_box";
    private static final String ITEM_NAME = "Ammo Box";

    public AmmoBox() {
        super(ITEM_ID, ITEM_NAME, COOLDOWN);
        this.texture = new Texture("textures/ammo_box.png");
        setTexture(texture, GameConfig.TILE_SIZE / 2f);
    }

    @Override
    protected void onUse(Player player) {
        int weaponsReloaded = 0;

        for (Weapon weapon : player.getWeapons()) {
            if (weapon.getCurrentAmmo() < weapon.getMagazineSize() || weapon.isReloading()) {
                if (weapon.isReloading()) {
                    weapon.setReloadProgress(weapon.getReloadTime());
                }

                weapon.forceReload();
                weaponsReloaded++;
            }
        }

        if (weaponsReloaded > 0) {
            System.out.println("Used Ammo Box! Reloaded " + weaponsReloaded + " weapon(s)");
        } else {
            System.out.println("Used Ammo Box! All weapons already loaded");
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        if (texture != null) {
            texture.dispose();
        }
    }
}
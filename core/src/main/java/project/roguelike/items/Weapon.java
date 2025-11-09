package project.roguelike.items;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import project.roguelike.core.GameConfig;

public class Weapon extends ActiveItem {
    private final float baseCooldown;
    private float currentCooldown;
    private final float bulletSpeed;
    private final float damage;
    private final float magazineSize;
    private final float reloadTime;
    private final boolean isAutomatic;
    protected Texture texture;
    private float timeSinceLastShot = 0f;

    private int currentAmmo;
    private float reloadProgress;
    private boolean isReloading;

    public Weapon(String id, String name, float cooldown, float bulletSpeed,
            float damage, float magazineSize, float reloadTime, boolean isAutomatic) {
        super(id, name);
        this.baseCooldown = cooldown;
        this.currentCooldown = cooldown;
        this.bulletSpeed = bulletSpeed;
        this.damage = damage;
        this.magazineSize = magazineSize;
        this.reloadTime = reloadTime;
        this.isAutomatic = isAutomatic;
        this.currentAmmo = (int) magazineSize;
        this.reloadProgress = 0;
        this.isReloading = false;
    }

    public void update(float delta) {
        timeSinceLastShot += delta;

        if (isReloading) {
            reloadProgress += delta;
            if (reloadProgress >= reloadTime) {
                completeReload();
            }
        }
    }

    public void render(SpriteBatch batch, Vector2 playerPos, float rotationDeg, boolean flipX, float playerWidth,
            float playerHeight) {
        if (texture == null) {
            return;
        }

        float texW = texture.getWidth();
        float texH = texture.getHeight();
        float targetW = texW + playerWidth * GameConfig.WEAPON_WIDTH_RATIO;
        float aspect = texH / texW;
        float targetH = targetW * aspect;

        float offsetX = playerWidth * GameConfig.OFFSET_X_RATIO;
        if (flipX) {
            offsetX = -offsetX;
        }
        float offsetY = playerHeight * GameConfig.OFFSET_Y_RATIO;
        float drawX = playerPos.x + offsetX - targetW / 2f;
        float drawY = playerPos.y + offsetY - targetH / 2f;
        float drawRotation = rotationDeg;

        float originX = targetW / 2f;
        float originY = targetH / 2f;

        batch.draw(texture,
                drawX,
                drawY,
                originX, originY,
                targetW, targetH,
                1f, 1f,
                drawRotation,
                0, 0,
                (int) texW, (int) texH,
                false, flipX);
    }

    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }

    public void startReload() {
        if (!isReloading && currentAmmo < magazineSize) {
            isReloading = true;
            reloadProgress = 0;
            System.out.println("Reloading!");
        }
    }

    private void completeReload() {
        currentAmmo = (int) magazineSize;
        isReloading = false;
        reloadProgress = 0;
        System.out.println("Reloading complete!");
    }

    public boolean canShoot() {
        return !isReloading && currentAmmo > 0 && timeSinceLastShot >= currentCooldown;
    }

    public void shoot() {
        if (canShoot()) {
            currentAmmo--;
            timeSinceLastShot = 0f;
            System.out.println("Ammo: " + currentAmmo);
        }
    }

    public boolean isAutomatic() {
        return isAutomatic;
    }

    public int getCurrentAmmo() {
        return currentAmmo;
    }

    public boolean isReloading() {
        return isReloading;
    }

    public float getReloadProgress() {
        return reloadProgress / reloadTime;
    }

    public float getCooldown() {
        return currentCooldown;
    }

    public float getBulletSpeed() {
        return bulletSpeed;
    }

    public float getDamage() {
        return damage;
    }

    public float getMagazineSize() {
        return magazineSize;
    }

    public float getReloadTime() {
        return reloadTime;
    }

    public void updateCooldownMultiplier(float multiplier) {
        this.currentCooldown = this.baseCooldown * multiplier;
    }

    public Vector2 getMuzzlePosition(Vector2 playerPos, float rotationDeg, boolean flipX,
            float playerWidth, float playerHeight) {
        float texW = texture.getWidth();
        float targetW = texW + playerWidth * GameConfig.WEAPON_WIDTH_RATIO;
        float offsetX = playerWidth * GameConfig.OFFSET_X_RATIO;
        if (flipX) {
            offsetX = -offsetX;
        }
        float offsetY = playerHeight * GameConfig.OFFSET_Y_RATIO;
        float weaponCenterX = playerPos.x + offsetX;
        float weaponCenterY = playerPos.y + offsetY;
        float muzzleDistance = targetW / 2f;
        float angleRad = (float) Math.toRadians(rotationDeg);
        return new Vector2(
                weaponCenterX + muzzleDistance * (float) Math.cos(angleRad),
                weaponCenterY + muzzleDistance * (float) Math.sin(angleRad));
    }
}

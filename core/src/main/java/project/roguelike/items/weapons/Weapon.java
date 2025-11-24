package project.roguelike.items.weapons;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import project.roguelike.core.GameConfig;
import project.roguelike.items.Item;

public abstract class Weapon extends Item {
    private static final float MUZZLE_DISTANCE_RATIO = 0.5f;

    private final float baseCooldown;
    private float currentCooldown;
    private final float bulletSpeed;
    private final float damage;
    private int magazineSize;
    private final int baseMagazineSize;
    private final float reloadTime;
    private final boolean isAutomatic;

    protected Texture texture;

    private float timeSinceLastShot = 0f;
    private int currentAmmo;
    private float reloadProgress = 0f;
    private boolean isReloading = false;

    protected Weapon(String id, String name, float cooldown, float bulletSpeed,
            float damage, int magazineSize, float reloadTime, boolean isAutomatic) {
        super(id, name, ItemType.WEAPON);
        this.baseCooldown = cooldown;
        this.currentCooldown = cooldown;
        this.bulletSpeed = bulletSpeed;
        this.damage = damage;
        this.magazineSize = magazineSize;
        this.baseMagazineSize = magazineSize;
        this.reloadTime = reloadTime;
        this.isAutomatic = isAutomatic;
        this.currentAmmo = magazineSize;
    }

    public void update(float delta) {
        timeSinceLastShot += delta;

        if (isReloading) {
            updateReload(delta);
        }
    }

    public void render(SpriteBatch batch, Vector2 playerPos, float rotationDeg, boolean flipX,
            float playerWidth, float playerHeight) {
        if (texture == null) {
            return;
        }

        WeaponRenderData data = calculateRenderData(playerPos, rotationDeg, flipX, playerWidth, playerHeight);

        batch.draw(texture,
                data.drawX, data.drawY,
                data.originX, data.originY,
                data.targetW, data.targetH,
                1f, 1f,
                data.drawRotation,
                0, 0,
                (int) data.texW, (int) data.texH,
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
            reloadProgress = 0f;
        }
    }

    public boolean canShoot() {
        return !isReloading && currentAmmo > 0 && timeSinceLastShot >= currentCooldown;
    }

    public void shoot() {
        if (!canShoot()) {
            return;
        }
        currentAmmo--;
        timeSinceLastShot = 0f;
    }

    public void updateCooldownMultiplier(float multiplier) {
        this.currentCooldown = this.baseCooldown * multiplier;
    }

    public Vector2 getMuzzlePosition(Vector2 playerPos, float rotationDeg, boolean flipX,
            float playerWidth, float playerHeight) {
        float targetW = calculateWeaponWidth(playerWidth);
        float offsetX = calculateOffsetX(playerWidth, flipX);
        float offsetY = playerHeight * GameConfig.OFFSET_Y_RATIO;

        float pivotX = playerPos.x + offsetX;
        float pivotY = playerPos.y + offsetY;
        float muzzleDistance = targetW * MUZZLE_DISTANCE_RATIO;

        float finalAngle = flipX ? (180f - rotationDeg) : rotationDeg;
        float angleRad = (float) Math.toRadians(finalAngle);
        float direction = flipX ? -1f : 1f;

        float muzzleX = pivotX + direction * muzzleDistance * (float) Math.cos(angleRad);
        float muzzleY = pivotY + muzzleDistance * (float) Math.sin(angleRad);

        return new Vector2(muzzleX, muzzleY);
    }

    private void updateReload(float delta) {
        reloadProgress += delta;
        if (reloadProgress >= reloadTime) {
            completeReload();
        }
    }

    private void completeReload() {
        currentAmmo = magazineSize;
        isReloading = false;
        reloadProgress = 0f;
    }

    private WeaponRenderData calculateRenderData(Vector2 playerPos, float rotationDeg, boolean flipX,
            float playerWidth, float playerHeight) {
        WeaponRenderData data = new WeaponRenderData();

        data.texW = texture.getWidth();
        data.texH = texture.getHeight();
        data.targetW = calculateWeaponWidth(playerWidth);
        data.targetH = data.targetW * (data.texH / data.texW);

        float offsetX = calculateOffsetX(playerWidth, flipX);
        float offsetY = playerHeight * GameConfig.OFFSET_Y_RATIO;

        data.drawX = playerPos.x + offsetX - data.targetW / 2f;
        data.drawY = playerPos.y + offsetY - data.targetH / 2f;
        data.drawRotation = rotationDeg;
        data.originX = data.targetW / 2f;
        data.originY = data.targetH / 2f;

        return data;
    }

    private float calculateWeaponWidth(float playerWidth) {
        return texture.getWidth() + playerWidth * GameConfig.WEAPON_WIDTH_RATIO;
    }

    private float calculateOffsetX(float playerWidth, boolean flipX) {
        float offsetX = playerWidth * GameConfig.OFFSET_X_RATIO;
        return flipX ? -offsetX : offsetX;
    }

    private static class WeaponRenderData {
        float texW, texH;
        float targetW, targetH;
        float drawX, drawY;
        float drawRotation;
        float originX, originY;
    }

    public boolean isAutomatic() {
        return isAutomatic;
    }

    public int getCurrentAmmo() {
        return currentAmmo;
    }

    public int getMagazineSize() {
        return magazineSize;
    }

    public boolean isReloading() {
        return isReloading;
    }

    public float getReloadProgress() {
        return reloadTime > 0 ? reloadProgress / reloadTime : 0f;
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

    public float getReloadTime() {
        return reloadTime;
    }

    public float getRawReloadProgress() {
        return reloadProgress;
    }

    public void setReloadProgress(float progress) {
        this.reloadProgress = progress;
    }

    public void setMagazineSize(int newSize) {
        int ammoDifference = newSize - this.magazineSize;
        this.magazineSize = newSize;

        if (ammoDifference > 0 && !isReloading) {
            this.currentAmmo = Math.min(this.currentAmmo + ammoDifference, this.magazineSize);
        }
    }

    public int getBaseMagazineSize() {
        return baseMagazineSize;
    }
}

package project.roguelike.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import project.roguelike.core.GameConfig;
import project.roguelike.core.GameStatistics;
import project.roguelike.core.InputManager;
import project.roguelike.core.InputState;
import project.roguelike.core.SoundManager;
import project.roguelike.items.Item;
import project.roguelike.items.activeItems.ActiveItem;
import project.roguelike.items.consumableItems.ConsumableItem;
import project.roguelike.items.currencyItems.CurrencyItem;
import project.roguelike.items.passiveItems.PassiveItem;
import project.roguelike.items.weapons.Weapon;
import project.roguelike.rooms.EndRoom;
import project.roguelike.rooms.Room;
import project.roguelike.rooms.ShopRoom;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Player {
    private static final float FRAME_DURATION = 0.08f;
    private static final float DAMAGE_FLASH_DURATION = 0.2f;
    private static final float DEATH_ROTATION_SPEED = 180f;
    private static final int BASE_MAX_HEALTH = 10;
    private static final float BASE_SPEED = 400f;

    private final Texture spriteSheet;
    private final TextureRegion[] frames;
    private final TextureRegion[] framesFlipped;
    private float stateTime = 0f;
    private boolean facingLeft = false;

    private final Vector2 position;
    private final Rectangle bounds;
    private float currentSpeed = BASE_SPEED;

    private int maxHealth = BASE_MAX_HEALTH;
    private int currentHealth = 10;
    private boolean isDead = false;
    private boolean deathTriggered = false;
    private float damageFlashTimer = 0f;
    private float deathTimer = 0f;
    private float deathRotation = 0f;

    private final List<PassiveItem> passiveItems = new ArrayList<>();
    private final List<ActiveItem> activeItems = new ArrayList<>();
    private final List<Weapon> weapons = new ArrayList<>();
    private int activeIndex = -1;
    private int weaponIndex = -1;
    private float damageMultiplier = 1f;
    private float reloadSpeedMultiplier = 1f;
    private float bulletSpeedMultiplier = 1f;
    private float fireRateMultiplier = 1f;
    private float magazineSizeMultiplier = 1f;
    private boolean levelTransitionRequested = false;

    private int keys = 0;
    private int coins = 0;

    private final List<Bullet> bullets = new ArrayList<>();

    private GameStatistics statistics;

    private final Vector2 tmpMouse = new Vector2();

    public Player(float x, float y, float width, float height) {
        this.spriteSheet = new Texture("textures/player_spritesheet.png");
        TextureRegion[][] tmp = TextureRegion.split(spriteSheet, 32, 32);
        int frameCount = tmp[0].length;

        this.frames = new TextureRegion[frameCount];
        this.framesFlipped = new TextureRegion[frameCount];
        for (int i = 0; i < frameCount; i++) {
            frames[i] = tmp[0][i];
            framesFlipped[i] = new TextureRegion(frames[i]);
            framesFlipped[i].flip(true, false);
        }

        this.position = new Vector2(x, y);
        this.bounds = new Rectangle(x - width / 2f, y - height / 2f, width, height);
    }

    public void setStatistics(GameStatistics statistics) {
        this.statistics = statistics;
    }

    public void update(float delta, Room currentRoom, Viewport viewport, Vector2 worldMouse, InputManager input) {
        stateTime += delta;

        if (isDead) {
            updateDeath(delta);
            return;
        }

        InputState state = input.getState();

        updateMovement(delta, currentRoom, state);
        updateMouseTracking(worldMouse);
        updateWeapon(delta, worldMouse, state);
        updateActiveItems(delta, state);
        updateBullets(delta, currentRoom);
        updateDamageFlash(delta);
        handleItemPickup(currentRoom, state);
        handleChestOpen(currentRoom, state);
        tryUseHatch(currentRoom, state);
    }

    public void render(SpriteBatch batch) {
        TextureRegion region = getCurrentFrame();

        if (damageFlashTimer > 0 || isDead) {
            batch.setColor(1f, 0.4f, 0.4f, 1f);
        }

        batch.draw(
                region,
                position.x - bounds.width / 2f,
                position.y - bounds.height / 2f,
                bounds.width / 2f,
                bounds.height / 2f,
                bounds.width,
                bounds.height,
                1f, 1f,
                deathRotation);

        batch.setColor(1f, 1f, 1f, 1f);

        renderWeapon(batch);
        renderBullets(batch);
    }

    public void takeDamage(int amount) {
        if (isDead) {
            return;
        }

        if (amount <= 0) {
            return;
        }

        currentHealth -= amount;
        if (currentHealth < 0) {
            currentHealth = 0;
        }

        damageFlashTimer = DAMAGE_FLASH_DURATION;

        SoundManager.playPlayerHit();

        if (statistics != null) {
            statistics.onDamageTaken(amount);
        }

        if (currentHealth == 0) {
            die();
        }
    }

    public void heal(int amount) {
        currentHealth = Math.min(currentHealth + amount, maxHealth);
    }

    public void pickUpItem(Item item) {
        if (item == null) {
            return;
        }

        switch (item.getType()) {
            case CONSUMABLE:
                if (item instanceof ConsumableItem) {
                    ConsumableItem consumable = (ConsumableItem) item;
                    consumable.onConsume(this);
                }
                break;
            case PASSIVE:
                if (item instanceof PassiveItem) {
                    addPassiveItem((PassiveItem) item);
                }
                break;
            case ACTIVE:
                if (item instanceof ActiveItem) {
                    addActiveItem((ActiveItem) item);
                }
                break;
            case WEAPON:
                if (item instanceof Weapon) {
                    addWeapon((Weapon) item);
                }
                break;
            case CURRENCY:
                if (item instanceof CurrencyItem) {
                    CurrencyItem currency = (CurrencyItem) item;
                    currency.onPickup(this);
                }
                break;
        }
        SoundManager.playItemPickup();
    }

    public void addPassiveItem(PassiveItem item) {
        if (item == null) {
            return;
        }
        passiveItems.add(item);
        recomputeStatsFromPassives();
    }

    public void addActiveItem(ActiveItem item) {
        if (item == null) {
            return;
        }
        activeItems.add(item);

        if (activeIndex < 0) {
            activeIndex = 0;
        }

    }

    public void addWeapon(Weapon weapon) {
        if (weapon == null) {
            return;
        }
        weapons.add(weapon);

        applyBoostsToWeapon(weapon);

        if (weaponIndex < 0) {
            weaponIndex = 0;
        }
    }

    public void switchWeapons(int dir) {
        if (weapons.isEmpty()) {
            weaponIndex = -1;
            return;
        }

        int prevIndex = weaponIndex;
        weaponIndex = (weaponIndex + dir) % weapons.size();
        if (weaponIndex < 0) {
            weaponIndex += weapons.size();
        }

        if (weaponIndex != prevIndex) {
            SoundManager.playItemChange();
        }
    }

    public void dispose() {
        if (spriteSheet != null) {
            spriteSheet.dispose();
        }

        for (Bullet bullet : bullets) {
            bullet.dispose();
        }

        for (Weapon weapon : weapons) {
            weapon.dispose();
        }
    }

    private void updateMovement(float delta, Room currentRoom, InputState state) {
        Vector2 moveDir = state.getMoveDirection();
        if (!moveDir.isZero()) {
            position.add(moveDir.cpy().scl(currentSpeed * delta));
        }

        clampToRoomBounds(currentRoom);
        bounds.setPosition(position.x - bounds.width / 2f, position.y - bounds.height / 2f);
    }

    private void clampToRoomBounds(Room currentRoom) {
        float minX = currentRoom.getPosition().x + bounds.width / 2f;
        float maxX = currentRoom.getPosition().x + GameConfig.ROOM_WIDTH - bounds.width / 2f;
        float minY = currentRoom.getPosition().y + bounds.height / 2f;
        float maxY = currentRoom.getPosition().y + GameConfig.ROOM_HEIGHT - bounds.height / 2f;

        position.x = Math.max(minX, Math.min(maxX, position.x));
        position.y = Math.max(minY, Math.min(maxY, position.y));
    }

    private void updateMouseTracking(Vector2 worldMouse) {
        tmpMouse.set(worldMouse);
        facingLeft = tmpMouse.x < position.x;
    }

    private void updateWeapon(float delta, Vector2 worldMouse, InputState state) {
        Weapon equipped = getEquippedWeapon();
        if (equipped == null) {
            return;
        }

        if (equipped.isReloading()) {
            float boostedDelta = delta / reloadSpeedMultiplier;
            float currentProgress = equipped.getRawReloadProgress();
            equipped.setReloadProgress(currentProgress + boostedDelta);
        }

        equipped.update(delta);

        boolean shouldShoot = equipped.isAutomatic()
                ? state.isShootPressed()
                : state.isShootJustPressed();

        if (shouldShoot && equipped.canShoot()) {
            fireWeapon(equipped, worldMouse);
        }

        if (state.isReloadPressed()) {
            SoundManager.playReload();
            equipped.startReload();

        }
    }

    private void fireWeapon(Weapon weapon, Vector2 worldMouse) {
        float angle = (float) Math.toDegrees(
                Math.atan2(worldMouse.y - position.y, worldMouse.x - position.x));

        Vector2 muzzlePos = weapon.getMuzzlePosition(
                position, angle, facingLeft, bounds.width, bounds.height);
        Vector2 shootDir = worldMouse.cpy().sub(muzzlePos).nor();

        weapon.shoot();

        SoundManager.playShot();

        float finalDamage = weapon.getDamage() * damageMultiplier;

        float finalBulletSpeed = weapon.getBulletSpeed() * bulletSpeedMultiplier;

        Bullet bullet = new Bullet(muzzlePos.x, muzzlePos.y, shootDir);
        bullet.setSpeed(finalBulletSpeed);
        bullet.setDamage(finalDamage);
        bullet.setStatistics(statistics);
        bullets.add(bullet);

        if (statistics != null) {
            statistics.onBulletFired();
        }
    }

    private void updateBullets(float delta, Room currentRoom) {
        for (Iterator<Bullet> iter = bullets.iterator(); iter.hasNext();) {
            Bullet bullet = iter.next();
            bullet.update(delta);
            bullet.checkRoomBounds(currentRoom);

            if (!bullet.isActive()) {
                bullet.dispose();
                iter.remove();
            }
        }
    }

    private void updateDamageFlash(float delta) {
        if (damageFlashTimer > 0) {
            damageFlashTimer -= delta;
        }
    }

    private void updateDeath(float delta) {
        deathTimer += delta;
        deathRotation = Math.min(90f, deathTimer * DEATH_ROTATION_SPEED);
    }

    private void handleItemPickup(Room currentRoom, InputState state) {
        if (!state.isUsePressed()) {
            return;
        }

        Item nearby = currentRoom.getNearbyItem(this);
        if (nearby != null) {
            if (currentRoom instanceof ShopRoom) {
                ShopRoom shop = (ShopRoom) currentRoom;
                int idx = shop.getShopItems().indexOf(nearby);
                if (idx >= 0) {
                    int price = shop.itemPrices.get(idx);
                    if (getCoins() >= price) {
                        spendCoins(price);
                        SoundManager.playItemBuy();
                        pickUpItem(nearby);
                        shop.getShopItems().set(idx, null);
                    }
                    return;
                }
            }
            pickUpItem(nearby);
            currentRoom.removeItem(nearby);
        }
    }

    private void handleChestOpen(Room currentRoom, InputState state) {
        if (!state.isUsePressed()) {
            return;
        }
        for (Chest chest : currentRoom.getChests()) {
            if (!chest.isOpened() && !chest.isRemoved()
                    && getBounds().overlaps(chest.getBounds())) {
                chest.tryOpen(this, currentRoom.getItems());
                if (chest.isOpened()) {
                    SoundManager.playOpenChest();
                }
                break;
            }
        }
    }

    public void tryUseHatch(Room currentRoom, InputState state) {
        if (currentRoom instanceof EndRoom) {
            EndRoom endRoom = (EndRoom) currentRoom;
            if (endRoom.isOnHatch() && state.isUsePressed()) {
                requestLevelTransition();
            }
        }
    }

    private TextureRegion getCurrentFrame() {
        int idx = (int) (stateTime / FRAME_DURATION) % frames.length;
        return facingLeft ? framesFlipped[idx] : frames[idx];
    }

    private void renderWeapon(SpriteBatch batch) {
        Weapon equipped = getEquippedWeapon();
        if (equipped != null) {
            float angle = (float) Math.toDegrees(
                    Math.atan2(tmpMouse.y - position.y, tmpMouse.x - position.x));
            equipped.render(batch, position, angle, facingLeft, bounds.width, bounds.height);
        }
    }

    private void renderBullets(SpriteBatch batch) {
        for (Bullet bullet : bullets) {
            if (bullet.isActive()) {
                bullet.render(batch);
            }
        }
    }

    private void die() {
        SoundManager.playPlayerDeath();
        isDead = true;
        deathTriggered = true;
        deathTimer = 0;
    }

    private void recomputeStatsFromPassives() {
        float speedMul = 1f;
        float damageMul = 1f;
        float reloadSpeedMul = 1f;
        float bulletSpeedMul = 1f;
        float fireRateMul = 1f;
        float magazineSizeMul = 1f;
        int hpBonus = 0;

        for (PassiveItem item : passiveItems) {
            speedMul *= item.getSpeedMultiplier();
            damageMul *= item.getDamageMultiplier();
            reloadSpeedMul *= item.getReloadSpeedMultiplier();
            bulletSpeedMul *= item.getBulletSpeedMultiplier();
            fireRateMul *= item.getFireRateMultiplier();
            magazineSizeMul *= item.getMagazineSizeMultiplier();
            hpBonus += item.getMaxHpBonus();
        }

        this.currentSpeed = BASE_SPEED * speedMul;
        this.damageMultiplier = damageMul;
        this.reloadSpeedMultiplier = reloadSpeedMul;
        this.bulletSpeedMultiplier = bulletSpeedMul;
        this.fireRateMultiplier = fireRateMul;
        this.magazineSizeMultiplier = magazineSizeMul;

        int prevMaxHealth = this.maxHealth;
        this.maxHealth = BASE_MAX_HEALTH + hpBonus;
        int healthDelta = this.maxHealth - prevMaxHealth;

        if (healthDelta > 0) {
            this.currentHealth = Math.min(this.currentHealth + healthDelta, this.maxHealth);
        }

        reapplyWeaponBoosts();
    }

    private void applyBoostsToWeapon(Weapon weapon) {
        weapon.updateCooldownMultiplier(fireRateMultiplier);

        int baseMagSize = weapon.getBaseMagazineSize();
        int boostedMagSize = Math.round(baseMagSize * magazineSizeMultiplier);
        weapon.setMagazineSize(boostedMagSize);

    }

    private void reapplyWeaponBoosts() {
        for (Weapon weapon : weapons) {
            applyBoostsToWeapon(weapon);
        }

    }

    public Rectangle getBounds() {
        return bounds;
    }

    public Vector2 getPosition() {
        return position;
    }

    public List<Bullet> getBullets() {
        return bullets;
    }

    public int getHealth() {
        return currentHealth;
    }

    public boolean isDead() {
        return isDead;
    }

    public Weapon getEquippedWeapon() {
        if (weaponIndex >= 0 && weaponIndex < weapons.size()) {
            return weapons.get(weaponIndex);
        }
        return null;
    }

    public Weapon getCurrentWeapon() {
        return getEquippedWeapon();
    }

    public Texture getCurrentWeaponTexture() {
        Weapon weapon = getCurrentWeapon();
        return weapon != null ? weapon.getTexture() : null;
    }

    public List<PassiveItem> getPassiveItems() {
        return passiveItems;
    }

    public int getActiveItemsCount() {
        return activeItems.size();
    }

    public ActiveItem getActiveItem() {
        if (activeIndex >= 0 && activeIndex < activeItems.size()) {
            return activeItems.get(activeIndex);
        }
        return null;
    }

    public List<Weapon> getWeapons() {
        return weapons;
    }

    public int getWeaponIndex() {
        return weaponIndex;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public List<ActiveItem> getActiveItems() {
        return activeItems;
    }

    public int getActiveItemIndex() {
        return activeIndex;
    }

    public ActiveItem getSelectedActiveItem() {
        if (activeIndex >= 0 && activeIndex < activeItems.size()) {
            return activeItems.get(activeIndex);
        }
        return null;
    }

    public boolean hasJustDied() {
        if (deathTriggered) {
            deathTriggered = false;
            return true;
        }
        return false;
    }

    private void updateActiveItems(float delta, InputState state) {
        for (ActiveItem item : activeItems) {
            item.update(delta);
        }

        if (state.isSelectActiveItemPrevPressed() && !activeItems.isEmpty()) {
            switchActiveItems(-1);
        } else if (state.isSelectActiveItemNextPressed() && !activeItems.isEmpty()) {
            switchActiveItems(1);
        }

        if (state.isUseActiveItemPressed() && activeIndex >= 0 && activeIndex < activeItems.size()) {
            ActiveItem selected = activeItems.get(activeIndex);
            if (selected.canUse()) {
                selected.use(this);
            }
        }
    }

    public void switchActiveItems(int dir) {
        if (activeItems.isEmpty()) {
            activeIndex = -1;
            return;
        }

        int prevIndex = activeIndex;
        activeIndex = (activeIndex + dir) % activeItems.size();
        if (activeIndex < 0) {
            activeIndex += activeItems.size();
        }

        if (activeIndex != prevIndex) {
            SoundManager.playItemChange();
        }
    }

    public void addKeys(int amount) {
        keys += amount;
    }

    public void addCoins(int amount) {
        coins += amount;
    }

    public boolean hasKeys(int amount) {
        return keys >= amount;
    }

    public boolean hasCoins(int amount) {
        return coins >= amount;
    }

    public void spendKeys(int amount) {
        if (hasKeys(amount)) {
            keys -= amount;
        }
    }

    public void spendCoins(int amount) {
        if (hasCoins(amount)) {
            coins -= amount;
        }
    }

    public int getKeys() {
        return keys;
    }

    public int getCoins() {
        return coins;
    }

    public boolean isLevelTransitionRequested() {
        return levelTransitionRequested;
    }

    public void requestLevelTransition() {
        this.levelTransitionRequested = true;
    }

    public void resetLevelTransitionRequest() {
        this.levelTransitionRequested = false;
    }
}
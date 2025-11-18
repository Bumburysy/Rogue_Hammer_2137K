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
import project.roguelike.items.Item;
import project.roguelike.items.activeItems.ActiveItem;
import project.roguelike.items.passiveItems.PassiveItem;
import project.roguelike.items.weapons.Weapon;
import project.roguelike.rooms.Room;

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
    private int currentHealth = maxHealth;
    private boolean isDead = false;
    private boolean deathTriggered = false;
    private float damageFlashTimer = 0f;
    private float deathTimer = 0f;
    private float deathRotation = 0f;

    private final List<PassiveItem> passiveItems = new ArrayList<>();
    private final List<ActiveItem> activeItems = new ArrayList<>();
    private int activeIndex = -1;
    private float cooldownMultiplier = 1f;

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

        updateMovement(delta, currentRoom, input);
        updateMouseTracking(worldMouse);
        updateWeapon(delta, worldMouse, input);
        updateBullets(delta, currentRoom);
        updateDamageFlash(delta);
        handleItemPickup(currentRoom, input);
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

        currentHealth -= amount;
        damageFlashTimer = DAMAGE_FLASH_DURATION;

        if (statistics != null) {
            statistics.onDamageTaken(amount);
        }

        if (currentHealth <= 0) {
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
        }
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

        if (item instanceof Weapon) {
            ((Weapon) item).updateCooldownMultiplier(cooldownMultiplier);
        }
    }

    public void switchActive(int dir) {
        if (activeItems.isEmpty()) {
            activeIndex = -1;
            return;
        }

        activeIndex = (activeIndex + dir) % activeItems.size();
        if (activeIndex < 0) {
            activeIndex += activeItems.size();
        }
    }

    public void dispose() {
        if (spriteSheet != null) {
            spriteSheet.dispose();
        }

        for (Bullet bullet : bullets) {
            bullet.dispose();
        }

        for (ActiveItem item : activeItems) {
            if (item instanceof Weapon) {
                ((Weapon) item).dispose();
            }
        }
    }

    private void updateMovement(float delta, Room currentRoom, InputManager input) {
        Vector2 moveDir = input.getMoveDirection();
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

    private void updateWeapon(float delta, Vector2 worldMouse, InputManager input) {
        ActiveItem equipped = getEquippedActive();
        if (!(equipped instanceof Weapon)) {
            return;
        }

        Weapon weapon = (Weapon) equipped;
        weapon.update(delta);

        boolean shouldShoot = weapon.isAutomatic()
                ? input.isShootPressed()
                : input.isShootJustPressed();

        if (shouldShoot && weapon.canShoot()) {
            fireWeapon(weapon, worldMouse);
        }

        if (input.isReloadPressed()) {
            weapon.startReload();
        }
    }

    private void fireWeapon(Weapon weapon, Vector2 worldMouse) {
        float angle = (float) Math.toDegrees(
                Math.atan2(worldMouse.y - position.y, worldMouse.x - position.x));

        Vector2 muzzlePos = weapon.getMuzzlePosition(
                position, angle, facingLeft, bounds.width, bounds.height);
        Vector2 shootDir = worldMouse.cpy().sub(muzzlePos).nor();

        weapon.shoot();

        Bullet bullet = new Bullet(muzzlePos.x, muzzlePos.y, shootDir);
        bullet.setSpeed(weapon.getBulletSpeed());
        bullet.setDamage(weapon.getDamage());
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
        deathRotation = Math.min(180f, deathTimer * DEATH_ROTATION_SPEED);
    }

    private void handleItemPickup(Room currentRoom, InputManager input) {
        if (!input.isPickupPressed()) {
            return;
        }

        Item nearby = currentRoom.getNearbyItem(this);
        if (nearby != null) {
            pickUpItem(nearby);
            currentRoom.removeItem(nearby);
        }
    }

    private TextureRegion getCurrentFrame() {
        int idx = (int) (stateTime / FRAME_DURATION) % frames.length;
        return facingLeft ? framesFlipped[idx] : frames[idx];
    }

    private void renderWeapon(SpriteBatch batch) {
        ActiveItem equipped = getEquippedActive();
        if (equipped instanceof Weapon) {
            float angle = (float) Math.toDegrees(
                    Math.atan2(tmpMouse.y - position.y, tmpMouse.x - position.x));
            ((Weapon) equipped).render(batch, position, angle, facingLeft, bounds.width, bounds.height);
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
        isDead = true;
        deathTriggered = true;
        deathTimer = 0;
    }

    private void recomputeStatsFromPassives() {
        float speedMul = 1f;
        float cdMul = 1f;
        int hpBonus = 0;

        for (PassiveItem item : passiveItems) {
            speedMul *= item.getSpeedMultiplier();
            cdMul *= item.getShootCooldownMultiplier();
            hpBonus += item.getMaxHpBonus();
        }

        this.currentSpeed = BASE_SPEED * speedMul;
        this.cooldownMultiplier = cdMul;

        int prevMaxHealth = this.maxHealth;
        this.maxHealth = BASE_MAX_HEALTH + hpBonus;
        int healthDelta = this.maxHealth - prevMaxHealth;

        if (healthDelta > 0) {
            this.currentHealth = Math.min(this.currentHealth + healthDelta, this.maxHealth);
        }

        ActiveItem equipped = getEquippedActive();
        if (equipped instanceof Weapon) {
            ((Weapon) equipped).updateCooldownMultiplier(cooldownMultiplier);
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

    public ActiveItem getEquippedActive() {
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
}
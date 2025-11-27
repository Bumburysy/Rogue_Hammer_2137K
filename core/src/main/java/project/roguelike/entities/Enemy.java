package project.roguelike.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import project.roguelike.core.GameConfig;
import project.roguelike.core.SoundManager;
import project.roguelike.items.Item;
import project.roguelike.items.ItemFactory;
import project.roguelike.items.currencyItems.Coin;
import project.roguelike.items.currencyItems.Key;
import project.roguelike.rooms.Room;
import java.util.Random;

public abstract class Enemy {
    private static final float DAMAGE_FLASH_DURATION = 0.2f;
    private static final float DEATH_ROTATION_DURATION = 1f;
    private static final float MAX_DEATH_ROTATION = 90f;
    protected static final float DEFAULT_COLLISION_PUSH_STRENGTH = 0.5f;
    protected static final float DEFAULT_ATTACK_RANGE_TOLERANCE = 20f;

    protected float visualSizeMultiplier = 1.0f;
    protected float collisionWidthMultiplier = 0.7f;
    protected float collisionHeightMultiplier = 0.8f;

    protected final Vector2 position;
    protected final Vector2 velocity;
    protected final Rectangle bounds;
    protected float width;
    protected float height;

    protected State currentState;
    protected Animation<TextureRegion> walkAnimation;
    protected Animation<TextureRegion> attackAnimation;
    protected Animation<TextureRegion> idleAnimation;
    protected Animation<TextureRegion> dieAnimation;
    protected float stateTime;

    protected int health;
    protected int maxHealth;
    protected int damage;
    protected float speed;
    protected float agroRange;
    protected float attackRange;
    protected float attackCooldown;
    protected float attackTimer;

    protected boolean active = false;
    protected boolean dead = false;
    protected boolean dying = false;
    protected float damageFlashTimer = 0f;
    protected float deathTimer = 0f;
    protected float deathRotation = 0f;
    protected float attackSwingTime = 0f;
    protected float attackSwingDuration = 0.6f;

    protected Room parentRoom;
    protected Type type;

    public enum Type {
        GOBLIN, ORC, BOSS
    }

    public enum State {
        IDLE, ATTACK, DIE
    }

    protected Enemy(Vector2 spawnPos) {
        this.position = new Vector2(spawnPos);
        this.velocity = new Vector2();
        this.bounds = new Rectangle();
        this.currentState = State.IDLE;
    }

    protected abstract void loadAnimations();

    protected abstract void performAttack(Player player);

    protected void updateAI(float delta, Player player) {
        if (player == null || dead) {
            return;
        }

        attackTimer += delta;
        Vector2 toPlayer = new Vector2(player.getPosition()).sub(position);
        float distance = toPlayer.len();

        if (distance > agroRange) {
            setIdleState();
        } else if (distance <= attackRange) {
            handleAttackBehavior(player);
        } else {
            handleChaseBehavior(delta, toPlayer);
        }
    }

    protected void setIdleState() {
        velocity.setZero();
        currentState = State.IDLE;
    }

    protected void handleAttackBehavior(Player player) {
        velocity.setZero();
        if (attackTimer >= attackCooldown) {
            performAttack(player);
            attackTimer = 0f;
        }
        currentState = State.IDLE;
    }

    protected void handleChaseBehavior(float delta, Vector2 toPlayer) {
        if (attackTimer >= attackCooldown) {
            moveTowardsPlayer(toPlayer);
            Vector2 nextPos = calculateNextPosition(delta);
            avoidCollisions(nextPos);
            clampToRoomBounds(nextPos);
            position.set(nextPos);
        } else {
            setIdleState();
        }
    }

    protected void triggerAttackSwing(float duration) {
        attackSwingTime = 0f;
        attackSwingDuration = Math.max(0.01f, duration);
    }

    protected void updateAttackSwing(float delta) {
        if (attackSwingTime < attackSwingDuration) {
            attackSwingTime += delta;
            if (attackSwingTime > attackSwingDuration)
                attackSwingTime = attackSwingDuration;
        }
    }

    protected float getAttackRotation(boolean facingRight, float maxAngleDegrees) {
        if (attackSwingTime <= 0f || attackSwingDuration <= 0f)
            return 0f;
        float t = Math.min(1f, attackSwingTime / attackSwingDuration);
        float phase = (float) (Math.sin(Math.PI * t) * (2f * t - 1f));
        return phase * maxAngleDegrees * (facingRight ? -1f : 1f);
    }

    protected void moveTowardsPlayer(Vector2 toPlayer) {
        if (Math.abs(toPlayer.x) > Math.abs(toPlayer.y)) {
            velocity.set(Math.signum(toPlayer.x) * speed, 0);
        } else {
            velocity.set(0, Math.signum(toPlayer.y) * speed);
        }
        currentState = State.IDLE;
        updateFacingDirection();
    }

    protected Vector2 calculateNextPosition(float delta) {
        return position.cpy().add(velocity.cpy().scl(delta));
    }

    protected void avoidCollisions(Vector2 nextPos) {
        if (parentRoom == null) {
            return;
        }

        Rectangle futureBounds = new Rectangle(
                nextPos.x - bounds.width / 2f,
                nextPos.y - bounds.height / 2f,
                bounds.width, bounds.height);

        Vector2 totalPush = new Vector2();
        for (Enemy other : parentRoom.getEnemies()) {
            if (other != this && other.isActive() && !other.isDead()) {
                if (futureBounds.overlaps(other.getBounds())) {
                    Vector2 pushDir = new Vector2(nextPos).sub(other.getPosition()).nor();
                    totalPush.add(pushDir);
                }
            }
        }

        nextPos.add(totalPush.scl(getCollisionPushStrength()));
    }

    protected void clampToRoomBounds(Vector2 nextPos) {
        if (parentRoom == null) {
            return;
        }

        float minX = parentRoom.getPosition().x + bounds.width / 2f;
        float maxX = parentRoom.getPosition().x + GameConfig.ROOM_WIDTH - bounds.width / 2f;
        float minY = parentRoom.getPosition().y + bounds.height / 2f;
        float maxY = parentRoom.getPosition().y + GameConfig.ROOM_HEIGHT - bounds.height / 2f;

        nextPos.x = Math.max(minX, Math.min(maxX, nextPos.x));
        nextPos.y = Math.max(minY, Math.min(maxY, nextPos.y));
    }

    protected void updateFacingDirection() {
    }

    protected float getCollisionPushStrength() {
        return DEFAULT_COLLISION_PUSH_STRENGTH;
    }

    protected float getAttackRangeTolerance() {
        return DEFAULT_ATTACK_RANGE_TOLERANCE;
    }

    protected void initializeSize() {
        this.width = visualSizeMultiplier * GameConfig.TILE_SIZE;
        this.height = visualSizeMultiplier * GameConfig.TILE_SIZE;

        float collisionWidth = collisionWidthMultiplier * GameConfig.TILE_SIZE;
        float collisionHeight = collisionHeightMultiplier * GameConfig.TILE_SIZE;

        this.bounds.setSize(collisionWidth, collisionHeight);
        this.bounds.setPosition(
                position.x - collisionWidth / 2f,
                position.y - collisionHeight / 2f);
    }

    protected void updateBoundsPosition() {
        bounds.setPosition(
                position.x - bounds.width / 2f,
                position.y - bounds.height / 2f);
    }

    public void update(float delta, Player player) {
        if (!active || dead) {
            return;
        }

        updateDamageFlash(delta);

        if (dying) {
            updateDeath(delta);
            return;
        }

        stateTime += delta;
        updateAttackSwing(delta);
        updateAI(delta, player);
        updateBoundsPosition();
    }

    private void updateDamageFlash(float delta) {
        if (damageFlashTimer > 0) {
            damageFlashTimer -= delta;
        }
    }

    private void updateDeath(float delta) {
        deathTimer += delta;
        deathRotation = Math.min(
                MAX_DEATH_ROTATION,
                deathTimer / DEATH_ROTATION_DURATION * MAX_DEATH_ROTATION);

        if (deathTimer >= DEATH_ROTATION_DURATION) {
            finalizeDeath();
        }
    }

    private void finalizeDeath() {
        dead = true;
        dying = false;
        if (parentRoom != null) {
            parentRoom.onEnemyDeath(this);
        }
    }

    public abstract void render(SpriteBatch batch);

    protected TextureRegion getCurrentFrame() {
        switch (currentState) {
            case ATTACK:
                return attackAnimation != null
                        ? attackAnimation.getKeyFrame(stateTime, false)
                        : (idleAnimation != null ? idleAnimation.getKeyFrame(stateTime, true) : null);
            case DIE:
                return dieAnimation != null
                        ? dieAnimation.getKeyFrame(stateTime, false)
                        : (idleAnimation != null ? idleAnimation.getKeyFrame(stateTime, true) : null);
            case IDLE:
            default:
                return idleAnimation != null
                        ? idleAnimation.getKeyFrame(stateTime, true)
                        : null;
        }
    }

    public void takeDamage(float amount) {
        if (dead) {
            return;
        }

        health -= amount;
        damageFlashTimer = DAMAGE_FLASH_DURATION;
        SoundManager.playEnemyHit();

        if (parentRoom != null && parentRoom.getStatistics() != null) {
            parentRoom.getStatistics().onDamageDealt((int) amount);
        }

        if (health <= 0) {
            die();
        }
    }

    protected void die() {
        if (dead || dying) {
            return;
        }

        dying = true;
        currentState = State.DIE;
        stateTime = 0;
        deathTimer = 0;

        if (parentRoom != null) {
            Random rand = new Random();
            Vector2 dropPos = new Vector2(position);

            switch (type) {
                case GOBLIN:
                    parentRoom.getItems().add(new Coin(dropPos, 1));
                    break;
                case ORC:
                    if (rand.nextFloat() < 0.8f) {
                        parentRoom.getItems().add(new Coin(dropPos, 1));
                        parentRoom.getItems().add(new Coin(dropPos, 1));
                    } else {
                        parentRoom.getItems().add(new Key(dropPos, 1));
                    }
                    break;
                case BOSS:
                    for (int i = 0; i < 5; i++) {
                        parentRoom.getItems().add(new Coin(dropPos, 1));
                    }
                    parentRoom.getItems().add(new Key(dropPos, 1));
                    if (rand.nextFloat() < 0.5f) {
                        Item randomItem = ItemFactory.createRandomItem(dropPos);
                        if (randomItem != null) {
                            parentRoom.getItems().add(randomItem);
                        }
                    }
                    if (rand.nextFloat() < 0.4f) {
                        parentRoom.addChest(new project.roguelike.entities.Chest(dropPos.cpy()));
                    }
                    break;
            }
        }

        SoundManager.playEnemyDeath();

        if (parentRoom != null && parentRoom.getStatistics() != null) {
            parentRoom.getStatistics().onEnemyKilled();
        }
    }

    public void activate(Room room) {
        this.parentRoom = room;
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public void dispose() {
    }

    protected Vector2 getDrawPosition() {
        return new Vector2(
                position.x - width / 2f,
                position.y - height / 2f);
    }

    public boolean isDead() {
        return dead;
    }

    public boolean isActive() {
        return active;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public State getState() {
        return currentState;
    }

    public int getHealth() {
        return health;
    }

    public Room getParentRoom() {
        return parentRoom;
    }

    public void setParentRoom(Room parentRoom) {
        this.parentRoom = parentRoom;
    }
}

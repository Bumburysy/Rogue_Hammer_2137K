package project.roguelike.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import project.roguelike.core.GameConfig;
import project.roguelike.items.ActiveItem;
import project.roguelike.items.HalfHeart;
import project.roguelike.items.Heart;
import project.roguelike.items.Item;
import project.roguelike.items.Medkit;
import project.roguelike.items.PassiveItem;
import project.roguelike.items.Weapon;
import project.roguelike.rooms.Room;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Player {
    private Texture spriteSheet;
    private TextureRegion[] frames;
    private TextureRegion[] framesFlipped;
    private final Vector2 tmpMouse = new Vector2();
    private final Vector2 tmpShootDir = new Vector2();
    private float frameDuration = 0.08f;
    private float stateTime = 0f;
    private boolean facingLeft = false;
    private Vector2 position;
    private float baseSpeed = 400f;
    private float currentSpeed = baseSpeed;
    private int maxHealth = 10;
    private int currentHealth = maxHealth;
    private Rectangle bounds;
    private int baseMaxHealth = 10;
    private final List<PassiveItem> passiveItems = new ArrayList<>();
    private final List<ActiveItem> activeItems = new ArrayList<>();
    private int activeIndex = -1;
    private List<Bullet> bullets;
    private float cooldownMultiplier = 1f;

    public Player(float x, float y, float width, float height) {
        spriteSheet = new Texture("textures/player_spritesheet.png");
        TextureRegion[][] tmp = TextureRegion.split(spriteSheet, 32, 32);
        int frameCount = tmp[0].length;
        frames = new TextureRegion[frameCount];
        framesFlipped = new TextureRegion[frameCount];
        for (int i = 0; i < frameCount; i++) {
            frames[i] = tmp[0][i];
            framesFlipped[i] = new TextureRegion(frames[i]);
            framesFlipped[i].flip(true, false);
        }
        position = new Vector2(x, y);
        bounds = new Rectangle(x - width / 2f, y - height / 2f, width, height);
        bullets = new ArrayList<>();
    }

    private Vector2 getInputDirection(int up, int down, int left, int right) {
        Vector2 dir = new Vector2(0, 0);
        if (Gdx.input.isKeyPressed(up)) {
            dir.y += 1;
        }
        if (Gdx.input.isKeyPressed(down)) {
            dir.y -= 1;
        }
        if (Gdx.input.isKeyPressed(left)) {
            dir.x -= 1;
        }
        if (Gdx.input.isKeyPressed(right)) {
            dir.x += 1;
        }
        return dir.isZero() ? null : dir.nor();
    }

    public void update(float delta, Room currentRoom, Viewport viewport) {
        stateTime += delta;

        Vector2 moveDir = getInputDirection(Input.Keys.W, Input.Keys.S, Input.Keys.A, Input.Keys.D);

        if (moveDir != null) {
            position.add(moveDir.scl(currentSpeed * delta));
        }

        float minX = currentRoom.getPosition().x + bounds.height / 2;
        float maxX = currentRoom.getPosition().x + GameConfig.ROOM_WIDTH - bounds.height / 2;
        float minY = currentRoom.getPosition().y + bounds.height / 2;
        float maxY = currentRoom.getPosition().y + GameConfig.ROOM_HEIGHT - bounds.height / 2;
        position.x = Math.max(minX, Math.min(maxX, position.x));
        position.y = Math.max(minY, Math.min(maxY, position.y));
        bounds.setPosition(position.x - bounds.width / 2f, position.y - bounds.height / 2f);
        tmpMouse.set(Gdx.input.getX(), Gdx.input.getY());
        viewport.unproject(tmpMouse);
        tmpShootDir.set(tmpMouse).sub(position).nor();

        if (tmpMouse.x < position.x) {
            facingLeft = true;
        } else if (tmpMouse.x > position.x) {
            facingLeft = false;
        }

        ActiveItem equipped = getEquippedActive();
        if (equipped instanceof Weapon) {
            Weapon weapon = (Weapon) equipped;
            weapon.update(delta);

            float angle = (float) Math.toDegrees(Math.atan2(tmpMouse.y - position.y, tmpMouse.x - position.x));

            if (((weapon.isAutomatic() && Gdx.input.isButtonPressed(Input.Buttons.LEFT)) ||
                    (!weapon.isAutomatic() && Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)))) {

                if (weapon.canShoot()) {
                    Vector2 muzzlePos = weapon.getMuzzlePosition(position, angle, facingLeft,
                            bounds.width, bounds.height);
                    weapon.shoot();
                    bullets.add(new Bullet(muzzlePos.x, muzzlePos.y, tmpShootDir));
                }
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
                weapon.startReload();
            }
        }

        Iterator<Bullet> iter = bullets.iterator();
        while (iter.hasNext()) {
            Bullet bullet = iter.next();
            bullet.update(delta);
            Vector2 bulletPos = bullet.getPosition();
            Vector2 roomPos = currentRoom.getPosition();

            if (bulletPos.x < roomPos.x ||
                    bulletPos.x > roomPos.x + GameConfig.ROOM_WIDTH ||
                    bulletPos.y < roomPos.y ||
                    bulletPos.y > roomPos.y + GameConfig.ROOM_HEIGHT) {
                bullet.dispose();
                iter.remove();
            }
        }
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void render(SpriteBatch batch) {
        int idx = (int) (stateTime / frameDuration) % frames.length;
        TextureRegion region = facingLeft ? framesFlipped[idx] : frames[idx];
        batch.draw(region,
                position.x - bounds.width / 2f,
                position.y - bounds.height / 2f,
                bounds.width,
                bounds.height);

        ActiveItem equipped = getEquippedActive();
        if (equipped instanceof Weapon) {
            float angle = (float) Math.toDegrees(Math.atan2(
                    tmpMouse.y - position.y,
                    tmpMouse.x - position.x));
            ((Weapon) equipped).render(batch, position, angle, facingLeft, bounds.width, bounds.height);
        }

        for (Bullet bullet : bullets) {
            bullet.render(batch);
        }

    }

    public void dispose() {
        if (spriteSheet != null)
            spriteSheet.dispose();
        for (Bullet b : bullets) {
            b.dispose();
        }
        for (ActiveItem item : activeItems) {
            if (item instanceof Weapon) {
                ((Weapon) item).dispose();
            }
        }
    }

    public List<Bullet> getBullets() {
        return bullets;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void takeDamage(int amount) {
        currentHealth -= amount;
        System.out.println("Player HP: " + currentHealth);
    }

    public void heal(int amount) {
        currentHealth += amount;
        if (currentHealth > maxHealth) {
            currentHealth = maxHealth;
        }
        System.out.println("Healed " + amount + " HP. Current HP: " + currentHealth);
    }

    public int getHealth() {
        return currentHealth;
    }

    public void pickUpItem(Item item) {
        if (item == null)
            return;
        switch (item.getType()) {
            case CONSUMABLE:
                if (item instanceof Heart)
                    heal(((Heart) item).getHealAmmount());
                else if (item instanceof HalfHeart)
                    heal(((HalfHeart) item).getHealAmmount());
                else if (item instanceof Medkit)
                    heal(((Medkit) item).getHealAmmount());
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
        System.out.println("Equipped: " + activeItems.get(activeIndex).getName());
    }

    public ActiveItem getEquippedActive() {
        if (activeIndex >= 0 && activeIndex < activeItems.size())
            return activeItems.get(activeIndex);
        return null;
    }

    private void recomputeStatsFromPassives() {
        float speedMul = 1f;
        float cdMul = 1f;
        int hpBonus = 0;
        for (PassiveItem p : passiveItems) {
            speedMul *= p.getSpeedMultiplier();
            cdMul *= p.getShootCooldownMultiplier();
            hpBonus += p.getMaxHpBonus();
        }

        this.currentSpeed = baseSpeed * speedMul;
        this.cooldownMultiplier = cdMul;
        int prevMax = this.maxHealth;
        this.maxHealth = baseMaxHealth + hpBonus;
        int delta = this.maxHealth - prevMax;
        if (delta > 0) {
            this.currentHealth = Math.min(this.currentHealth + delta, this.maxHealth);
        }

        ActiveItem equipped = getEquippedActive();
        if (equipped instanceof Weapon) {
            ((Weapon) equipped).updateCooldownMultiplier(cooldownMultiplier);
        }
    }
}
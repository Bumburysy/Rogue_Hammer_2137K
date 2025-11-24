package project.roguelike.items;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public abstract class Item {
    public enum ItemType {
        PASSIVE, ACTIVE, CONSUMABLE, WEAPON, CURRENCY
    }

    private final String id;
    private final String name;
    private final ItemType type;
    private Vector2 position;

    protected Texture texture;
    protected float width;
    protected float height;

    protected Item(String id, String name, ItemType type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public void setPosition(Vector2 position) {
        this.position = new Vector2(position);
    }

    public void setTexture(Texture texture, float baseSize) {
        if (texture == null) {
            return;
        }

        this.texture = texture;
        float aspect = (float) texture.getHeight() / texture.getWidth();
        this.width = texture.getWidth() + baseSize;
        this.height = this.width * aspect;
    }

    public void update(float delta) {
    }

    public void render(SpriteBatch batch) {
        if (!canRender()) {
            return;
        }

        batch.draw(texture,
                position.x - width / 2f,
                position.y - height / 2f,
                width, height);
    }

    public void render(SpriteBatch batch, Vector2 renderPosition) {
        if (texture == null || renderPosition == null) {
            return;
        }

        batch.draw(texture,
                renderPosition.x - width / 2f,
                renderPosition.y - height / 2f,
                width, height);
    }

    public void dispose() {
        if (texture != null) {
            texture.dispose();
            texture = null;
        }
    }

    private boolean canRender() {
        return texture != null && position != null;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ItemType getType() {
        return type;
    }

    public Vector2 getPosition() {
        return position;
    }
}

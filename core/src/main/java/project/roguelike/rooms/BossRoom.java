package project.roguelike.rooms;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;
import project.roguelike.core.RoomContentGenerator;
import project.roguelike.core.RoomContentPlan;
import project.roguelike.core.GameConfig;

public class BossRoom extends Room {
    private static final Vector2 BOSS_SPAWN_POSITION = new Vector2(10, 4);

    private static final int FIRE_FRAME_WIDTH = 16;
    private static final int FIRE_FRAME_HEIGHT = 16;
    private static final float FIRE_FRAME_DURATION = 0.15f;

    private static final Texture FIRE_TEXTURE = new Texture("textures/candlestick.png");
    private static final Animation<TextureRegion> FIRE_ANIMATION;
    static {
        TextureRegion[][] tmp = TextureRegion.split(FIRE_TEXTURE, FIRE_FRAME_WIDTH, FIRE_FRAME_HEIGHT);
        FIRE_ANIMATION = new Animation<>(FIRE_FRAME_DURATION, tmp[0]);
    }

    private float fireStateTime = 0f;

    public BossRoom(Vector2 position, RoomShape shape) {
        super(position, shape);
    }

    @Override
    public void generateContentIfNeeded() {
        if (isContentGenerated()) {
            return;
        }

        RoomContentPlan plan = new RoomContentPlan();
        plan.enemies.add(new RoomContentPlan.EnemySpawn("boss", BOSS_SPAWN_POSITION));
        RoomContentGenerator.generate(this, plan);
        setContentGenerated(true);
    }

    @Override
    public void update(float delta, project.roguelike.entities.Player player) {
        super.update(delta, player);
        fireStateTime += delta;
    }

    @Override
    public void render(com.badlogic.gdx.graphics.g2d.SpriteBatch batch) {
        super.render(batch);

        float fireSize = GameConfig.TILE_SIZE;
        TextureRegion fireFrame = FIRE_ANIMATION.getKeyFrame(fireStateTime, true);

        float x0 = getPosition().x;
        float y0 = getPosition().y;
        float x1 = getPosition().x + GameConfig.ROOM_WIDTH - fireSize;
        float y1 = getPosition().y + GameConfig.ROOM_HEIGHT - fireSize;

        batch.draw(fireFrame, x0, y0, fireSize, fireSize);
        batch.draw(fireFrame, x1, y0, fireSize, fireSize);
        batch.draw(fireFrame, x0, y1, fireSize, fireSize);
        batch.draw(fireFrame, x1, y1, fireSize, fireSize);
    }
}

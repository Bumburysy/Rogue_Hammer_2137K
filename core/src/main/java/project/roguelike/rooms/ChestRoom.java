package project.roguelike.rooms;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;
import project.roguelike.core.GameConfig;
import project.roguelike.core.RoomContentPlan;
import project.roguelike.core.RoomContentGenerator;
import java.util.Random;

public class ChestRoom extends Room {
    private static final int FIRE_FRAME_WIDTH = 16;
    private static final int FIRE_FRAME_HEIGHT = 16;
    private static final float FIRE_FRAME_DURATION = 0.15f;
    private static final Random random = new Random();
    private static final int MIN_ENEMIES = 2;
    private static final int MAX_ENEMIES = 6;

    private static final Texture FIRE_TEXTURE = new Texture("textures/candlestick.png");
    private static final Animation<TextureRegion> FIRE_ANIMATION;
    static {
        TextureRegion[][] tmp = TextureRegion.split(FIRE_TEXTURE, FIRE_FRAME_WIDTH, FIRE_FRAME_HEIGHT);
        FIRE_ANIMATION = new Animation<>(FIRE_FRAME_DURATION, tmp[0]);
    }

    private float fireStateTime = 0f;

    public ChestRoom(Vector2 position, RoomShape shape) {
        super(position, shape);
    }

    @Override
    public void generateContentIfNeeded() {
        if (isContentGenerated())
            return;

        int gridWidth = getGrid().length;
        int gridHeight = getGrid()[0].length;
        int centerX = gridWidth / 2;
        int centerY = gridHeight / 2;

        RoomContentPlan plan = new RoomContentPlan();
        plan.chests.add(new RoomContentPlan.ChestSpawn(new Vector2(centerX - 1, centerY)));
        plan.chests.add(new RoomContentPlan.ChestSpawn(new Vector2(centerX + 1, centerY)));

        {
            plan.minEnemies = MIN_ENEMIES;
            plan.maxEnemies = MAX_ENEMIES;

            int enemyCount = random.nextInt(MAX_ENEMIES - MIN_ENEMIES + 1) + MIN_ENEMIES;
            for (int i = 0; i < enemyCount; i++) {
                plan.enemies.add(new RoomContentPlan.EnemySpawn("orc", null));
            }
        }

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

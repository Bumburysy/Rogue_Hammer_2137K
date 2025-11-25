package project.roguelike.rooms;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import project.roguelike.entities.GoblinEnemy;
import project.roguelike.entities.Player;
import project.roguelike.entities.Trap;
import project.roguelike.core.RoomContentPlan;
import project.roguelike.core.GameConfig;

public class TrapRoom extends Room {
    private static final Random random = new Random();
    private static final int MIN_ENEMIES = 2;
    private static final int MAX_ENEMIES = 5;
    private static final int MIN_TRAPS = 4;
    private static final int MAX_TRAPS = 8;

    private final List<Trap> traps = new ArrayList<>();
    private RoomContentPlan contentPlan;

    public TrapRoom(Vector2 position, RoomShape shape) {
        super(position, shape);
    }

    @Override
    public void generateContentIfNeeded() {
        if (isContentGenerated())
            return;

        contentPlan = createContentPlan();
        addRandomEnemies(contentPlan);
        addRandomTraps(contentPlan);

        for (RoomContentPlan.EnemySpawn enemy : contentPlan.enemies) {
            getEnemies().add(new GoblinEnemy(enemy.position));
        }

        for (RoomContentPlan.TrapSpawn trapSpawn : contentPlan.traps) {
            float trapSize = GameConfig.TILE_SIZE;
            traps.add(new Trap(trapSpawn.position, trapSize, 1));
        }

        setContentGenerated(true);
    }

    private RoomContentPlan createContentPlan() {
        return new RoomContentPlan();
    }

    private void addRandomEnemies(RoomContentPlan plan) {
        int enemyCount = random.nextInt(MAX_ENEMIES - MIN_ENEMIES + 1) + MIN_ENEMIES;
        for (int i = 0; i < enemyCount; i++) {
            Vector2 pos = getRandomFreePosition();
            plan.enemies.add(new RoomContentPlan.EnemySpawn("goblin", pos));
        }
    }

    private void addRandomTraps(RoomContentPlan plan) {
        int trapCount = random.nextInt(MAX_TRAPS - MIN_TRAPS + 1) + MIN_TRAPS;
        for (int i = 0; i < trapCount; i++) {
            Vector2 pos = getRandomFreePosition();
            plan.traps.add(new RoomContentPlan.TrapSpawn(pos));
        }
    }

    @Override
    public void update(float delta, Player player) {
        super.update(delta, player);
        for (Trap trap : traps) {
            trap.update(delta, player);
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        super.render(batch);
        for (Trap trap : traps) {
            trap.render(batch);
        }
    }

    private Vector2 getRandomFreePosition() {
        float margin = GameConfig.TILE_SIZE * 1.5f;
        float x = getPosition().x + margin + (float) Math.random() * (GameConfig.ROOM_WIDTH - 2 * margin);
        float y = getPosition().y + margin + (float) Math.random() * (GameConfig.ROOM_HEIGHT - 2 * margin);
        return new Vector2(x, y);
    }
}

package project.roguelike.rooms;

import com.badlogic.gdx.math.Vector2;
import java.util.Random;

import project.roguelike.core.RoomContentGenerator;
import project.roguelike.core.RoomContentPlan;

public class TrapRoom extends Room {
    private static final Random random = new Random();
    private static final int MIN_ENEMIES = 2;
    private static final int MAX_ENEMIES = 5;
    private static final int MIN_TRAPS = 4;
    private static final int MAX_TRAPS = 8;

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

        RoomContentGenerator.generate(this, contentPlan);

        setContentGenerated(true);
    }

    private RoomContentPlan createContentPlan() {
        return new RoomContentPlan();
    }

    private void addRandomEnemies(RoomContentPlan plan) {
        int enemyCount = random.nextInt(MAX_ENEMIES - MIN_ENEMIES + 1) + MIN_ENEMIES;
        for (int i = 0; i < enemyCount; i++) {
            plan.enemies.add(new RoomContentPlan.EnemySpawn("goblin", null));
        }
    }

    private void addRandomTraps(RoomContentPlan plan) {
        int trapCount = random.nextInt(MAX_TRAPS - MIN_TRAPS + 1) + MIN_TRAPS;
        for (int i = 0; i < trapCount; i++) {
            plan.traps.add(new RoomContentPlan.TrapSpawn(null, 1));
        }
    }
}

package project.roguelike.rooms;

import com.badlogic.gdx.math.Vector2;
import project.roguelike.core.RoomContentGenerator;
import project.roguelike.core.RoomContentPlan;
import java.util.Random;

public class NormalRoom extends Room {
    private static final Random random = new Random();
    private static final int MIN_ENEMIES = 0;
    private static final int MAX_ENEMIES = 0;

    public NormalRoom(Vector2 position, RoomShape shape) {
        super(position, shape);
    }

    @Override
    public void generateContentIfNeeded() {
        if (isContentGenerated()) {
            return;
        }

        RoomContentPlan plan = createContentPlan();
        RoomContentGenerator.generate(this, plan);
        setContentGenerated(true);
    }

    private RoomContentPlan createContentPlan() {
        RoomContentPlan plan = new RoomContentPlan();
        addRandomEnemies(plan);
        return plan;
    }

    private void addRandomEnemies(RoomContentPlan plan) {
        plan.minEnemies = MIN_ENEMIES;
        plan.maxEnemies = MAX_ENEMIES;

        int enemyCount = random.nextInt(MAX_ENEMIES - MIN_ENEMIES + 1) + MIN_ENEMIES;
        for (int i = 0; i < enemyCount; i++) {
            String type = random.nextBoolean() ? "goblin" : "orc";
            plan.enemies.add(new RoomContentPlan.EnemySpawn(type, null));
        }
    }
}

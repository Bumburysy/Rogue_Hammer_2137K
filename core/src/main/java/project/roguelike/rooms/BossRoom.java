package project.roguelike.rooms;

import com.badlogic.gdx.math.Vector2;
import project.roguelike.core.RoomContentGenerator;
import project.roguelike.core.RoomContentPlan;

public class BossRoom extends Room {
    private static final Vector2 BOSS_SPAWN_POSITION = new Vector2(10, 4);

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
}

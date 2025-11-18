package project.roguelike.core;

import java.util.List;
import java.util.ArrayList;
import com.badlogic.gdx.math.Vector2;

public class RoomContentPlan {
    public int minEnemies = 0;
    public int maxEnemies = 0;

    public List<EnemySpawn> enemies = new ArrayList<>();
    public List<ItemSpawn> items = new ArrayList<>();

    public static class EnemySpawn {
        public String type;
        public Vector2 position;

        public EnemySpawn(String type, Vector2 position) {
            this.type = type;
            this.position = position;
        }
    }

    public static class ItemSpawn {
        public String id;
        public Vector2 position;

        public ItemSpawn(String id, Vector2 position) {
            this.id = id;
            this.position = position;
        }
    }
}

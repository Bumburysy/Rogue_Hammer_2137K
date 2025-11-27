package project.roguelike.core;

import java.util.List;
import java.util.ArrayList;
import com.badlogic.gdx.math.Vector2;

public class RoomContentPlan {
    public int minEnemies = 0;
    public int maxEnemies = 0;

    public List<EnemySpawn> enemies = new ArrayList<>();
    public List<ItemSpawn> items = new ArrayList<>();
    public List<TrapSpawn> traps = new ArrayList<>();
    public List<ChestSpawn> chests = new ArrayList<>();

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

    public static class TrapSpawn {
        public Vector2 position;
        public int damage;

        public TrapSpawn(Vector2 position, int damage) {
            this.position = position;
            this.damage = damage;
        }
    }

    public static class ChestSpawn {
        public Vector2 position;

        public ChestSpawn(Vector2 position) {
            this.position = position;
        }
    }
}

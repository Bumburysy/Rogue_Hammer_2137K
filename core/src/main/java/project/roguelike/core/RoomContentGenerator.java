package project.roguelike.core;

import project.roguelike.rooms.Room;
import project.roguelike.entities.*;
import project.roguelike.items.Item;
import project.roguelike.items.weapons.*;
import project.roguelike.items.activeItems.*;
import project.roguelike.items.consumableItems.LargeHealthPotion;
import project.roguelike.items.consumableItems.SmallHealthPotion;
import project.roguelike.items.currencyItems.Coin;
import project.roguelike.items.currencyItems.Key;
import project.roguelike.items.passiveItems.BulletSpeedBoost;
import project.roguelike.items.passiveItems.DamageBoost;
import project.roguelike.items.passiveItems.FireRateBoost;
import project.roguelike.items.passiveItems.MagazineSizeBoost;
import project.roguelike.items.passiveItems.MaxHpBoost;
import project.roguelike.items.passiveItems.MovementSpeedBoost;
import project.roguelike.items.passiveItems.ReloadSpeedBoost;

import com.badlogic.gdx.math.Vector2;
import java.util.Random;

public class RoomContentGenerator {
    private static final Random random = new Random();
    private static final int MAX_SPAWN_ATTEMPTS = 1000;
    private static final int DOOR_SAFETY_RADIUS = 2;

    public static void generate(Room room, RoomContentPlan plan) {
        Room.CellType[][] grid = room.getGrid();

        spawnEnemies(room, grid, plan.enemies);
        spawnItems(room, grid, plan.items);
    }

    private static void spawnEnemies(Room room, Room.CellType[][] grid,
            java.util.List<RoomContentPlan.EnemySpawn> enemies) {
        for (RoomContentPlan.EnemySpawn enemySpawn : enemies) {
            Vector2 position = enemySpawn.position != null
                    ? cellToWorldPosition(room, enemySpawn.position)
                    : getRandomFreePosition(room, grid);

            if (position != null) {
                createEnemy(room, enemySpawn.type, position);
            }
        }
    }

    private static void spawnItems(Room room, Room.CellType[][] grid, java.util.List<RoomContentPlan.ItemSpawn> items) {
        for (RoomContentPlan.ItemSpawn itemSpawn : items) {
            Vector2 position = itemSpawn.position != null
                    ? cellToWorldPosition(room, itemSpawn.position)
                    : getRandomFreePosition(room, grid);

            if (position != null) {
                createItem(room, itemSpawn.id, position);
            }
        }
    }

    private static void createEnemy(Room room, String type, Vector2 position) {
        Enemy enemy = instantiateEnemy(type, position);
        if (enemy != null) {
            enemy.setParentRoom(room);
            room.getEnemies().add(enemy);
        }
    }

    private static Enemy instantiateEnemy(String type, Vector2 position) {
        switch (type.toLowerCase()) {
            case "orc":
                return new OrcEnemy(position);
            case "goblin":
                return new GoblinEnemy(position);
            case "boss":
                return new BossEnemy(position);
            default:
                return null;
        }
    }

    private static void createItem(Room room, String type, Vector2 position) {
        Item item = instantiateItem(type);
        if (item != null) {
            item.setPosition(position);
            room.getItems().add(item);
        }
    }

    private static Item instantiateItem(String type) {
        switch (type.toLowerCase()) {
            case "pistol":
                return new Pistol();
            case "rifle":
                return new Rifle();
            case "shotgun":
                return new Shotgun();
            case "sniper":
                return new Sniper();
            case "smg":
                return new Smg();

            case "medkit":
                return new MedKit();
            case "ammo_box":
                return new AmmoBox();

            case "small_health_potion":
                return new SmallHealthPotion();
            case "large_health_potion":
                return new LargeHealthPotion();

            case "speed_boost":
                return new MovementSpeedBoost();
            case "max_hp_boost":
                return new MaxHpBoost();
            case "damage_boost":
                return new DamageBoost();
            case "bullet_speed_boost":
                return new BulletSpeedBoost();
            case "fire_rate_boost":
                return new FireRateBoost();
            case "magazine_size_boost":
                return new MagazineSizeBoost();
            case "reload_speed_boost":
                return new ReloadSpeedBoost();

            case "key":
                return new Key();
            case "coin":
                return new Coin();

            default:
                return null;
        }
    }

    private static Vector2 cellToWorldPosition(Room room, Vector2 cellPosition) {
        if (cellPosition == null) {
            return null;
        }

        int cellX = (int) cellPosition.x;
        int cellY = (int) cellPosition.y;
        float halfTile = GameConfig.TILE_SIZE / 2f;

        float worldX = room.getPosition().x + cellX * GameConfig.TILE_SIZE + halfTile;
        float worldY = room.getPosition().y + cellY * GameConfig.TILE_SIZE + halfTile;

        return new Vector2(worldX, worldY);
    }

    private static Vector2 getRandomFreePosition(Room room, Room.CellType[][] grid) {
        int width = grid.length;
        int height = grid[0].length;

        for (int attempt = 0; attempt < MAX_SPAWN_ATTEMPTS; attempt++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);

            if (isValidSpawnLocation(room, grid, x, y)) {
                grid[x][y] = Room.CellType.ENEMY;
                return cellToWorldPosition(room, new Vector2(x, y));
            }
        }

        return null;
    }

    private static boolean isValidSpawnLocation(Room room, Room.CellType[][] grid, int x, int y) {
        if (!isWithinBounds(grid, x, y)) {
            return false;
        }

        if (grid[x][y] != Room.CellType.EMPTY) {
            return false;
        }

        return !isNearDoor(room, x, y);
    }

    private static boolean isWithinBounds(Room.CellType[][] grid, int x, int y) {
        int width = grid.length;
        int height = grid[0].length;
        return x >= 1 && y >= 1 && x < width - 1 && y < height - 1;
    }

    private static boolean isNearDoor(Room room, int gridX, int gridY) {
        for (Room.DoorDirection door : room.getDoors()) {
            Vector2 doorPosition = getDoorGridPosition(room, door);
            float distance = Math.max(
                    Math.abs(doorPosition.x - gridX),
                    Math.abs(doorPosition.y - gridY));

            if (distance <= DOOR_SAFETY_RADIUS) {
                return true;
            }
        }
        return false;
    }

    private static Vector2 getDoorGridPosition(Room room, Room.DoorDirection door) {
        int gridWidth = room.getGrid().length;
        int gridHeight = room.getGrid()[0].length;
        int centerX = gridWidth / 2;
        int centerY = gridHeight / 2;

        switch (door) {
            case UP:
                return new Vector2(centerX, gridHeight - 1);
            case DOWN:
                return new Vector2(centerX, 0);
            case LEFT:
                return new Vector2(0, centerY);
            case RIGHT:
                return new Vector2(gridWidth - 1, centerY);
            default:
                return new Vector2(centerX, centerY);
        }
    }
}

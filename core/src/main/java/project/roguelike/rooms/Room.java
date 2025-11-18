package project.roguelike.rooms;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import project.roguelike.core.GameConfig;
import project.roguelike.entities.Bullet;
import project.roguelike.entities.Enemy;
import project.roguelike.entities.Player;
import project.roguelike.items.Item;
import project.roguelike.core.GameStatistics;

public abstract class Room {
    private static final int TEXTURE_BASE_SIZE = 16;
    private static final float DOOR_INTERACTION_PADDING = GameConfig.TILE_SIZE * 0.3f;
    private static final float DOOR_WIDTH_MULTIPLIER = 2f;
    private static final float DOOR_PULSE_DURATION = 2000f;
    private static final float DOOR_BASE_ALPHA = 0.5f;
    private static final float DOOR_PULSE_ALPHA = 0.5f;

    private final Texture floor, wallTop, wallRight, doorUp, doorRight;
    private final TextureRegion floorRegion, wallTopRegion, wallBottomRegion;
    private final TextureRegion wallLeftRegion, wallRightRegion;
    private final TextureRegion doorUpRegion, doorDownRegion, doorLeftRegion, doorRightRegion;

    private final Vector2 position;
    private final RoomShape shape;
    private final List<DoorDirection> doors;
    private final CellType[][] grid;
    private final List<Enemy> enemies;
    private final List<Item> items;

    private final float wallThickness = GameConfig.WALL_THICKNESS;
    private final float roomWidth = GameConfig.ROOM_WIDTH;
    private final float roomHeight = GameConfig.ROOM_HEIGHT;
    private final float doorWidth = wallThickness * DOOR_WIDTH_MULTIPLIER;
    private final float tileSize = GameConfig.TILE_SIZE;
    private final int innerGridWidth = (int) (roomWidth / tileSize);
    private final int innerGridHeight = (int) (roomHeight / tileSize);
    private final float pickupRadius = GameConfig.TILE_SIZE;

    private int gridRow = -1;
    private int gridCol = -1;
    private boolean contentGenerated = false;
    private DoorDirection activeDoor = null;
    private GameStatistics statistics;
    private boolean roomClearedReported = false;
    private boolean active = false;
    private int initialEnemyCount = 0;

    public enum CellType {
        EMPTY, ENEMY, CHEST, ITEM, OBSTACLE, WALL
    }

    public enum RoomShape {
        O_SHAPE,
        I_SHAPE_N, I_SHAPE_E,
        T_SHAPE_N, T_SHAPE_E, T_SHAPE_S, T_SHAPE_W,
        L_SHAPE_N, L_SHAPE_E, L_SHAPE_S, L_SHAPE_W,
        D_SHAPE_N, D_SHAPE_E, D_SHAPE_S, D_SHAPE_W
    }

    public enum DoorDirection {
        UP, DOWN, LEFT, RIGHT
    }

    public Room(Vector2 position, RoomShape shape) {
        this.position = position;
        this.shape = shape;

        this.floor = loadTexture("textures/floor.png");
        this.wallTop = loadTexture("textures/wall_top.png");
        this.wallRight = loadTexture("textures/wall_right.png");
        this.doorUp = loadTexture("textures/door_up.png");
        this.doorRight = loadTexture("textures/door_right.png");

        this.floorRegion = new TextureRegion(floor);
        this.wallTopRegion = new TextureRegion(wallTop);
        this.wallBottomRegion = new TextureRegion(wallTop);
        this.wallLeftRegion = new TextureRegion(wallRight);
        this.wallRightRegion = new TextureRegion(wallRight);
        this.doorUpRegion = new TextureRegion(doorUp);
        this.doorDownRegion = new TextureRegion(doorUp);
        this.doorLeftRegion = new TextureRegion(doorRight);
        this.doorRightRegion = new TextureRegion(doorRight);

        this.doors = initializeDoors(shape);
        initializeRegions();
        flipRegions();

        this.enemies = new ArrayList<>();
        this.items = new ArrayList<>();
        this.grid = new CellType[innerGridWidth][innerGridHeight];
        initializeGrid();
    }

    public void render(SpriteBatch batch) {
        renderFloor(batch);
        renderWalls(batch);
        renderDoors(batch);
        renderEnemies(batch);
        renderItems(batch);
    }

    public void update(float delta, Player player) {
        updateEnemies(delta, player);
        checkRoomCleared();
    }

    public void activate() {
        active = true;

        if (initialEnemyCount == 0 && !enemies.isEmpty()) {
            initialEnemyCount = enemies.size();
        }

        for (Enemy enemy : enemies) {
            enemy.activate(this);
        }
    }

    public void deactivate() {
        active = false;
        for (Enemy enemy : enemies) {
            enemy.deactivate();
        }
    }

    public void dispose() {
        floor.dispose();
        wallTop.dispose();
        wallRight.dispose();
        doorUp.dispose();
        doorRight.dispose();
    }

    public abstract void generateContentIfNeeded();

    public void setActiveDoor(DoorDirection door, boolean canUse) {
        this.activeDoor = canUse ? door : null;
    }

    public Rectangle getDoorBounds(DoorDirection direction) {
        switch (direction) {
            case UP:
                return createDoorBounds(
                        position.x + roomWidth / 2f - doorWidth / 2f,
                        position.y + roomHeight - wallThickness,
                        doorWidth, wallThickness);
            case DOWN:
                return createDoorBounds(
                        position.x + roomWidth / 2f - doorWidth / 2f,
                        position.y - wallThickness / 2f,
                        doorWidth, wallThickness);
            case LEFT:
                return createDoorBounds(
                        position.x - wallThickness / 2f,
                        position.y + roomHeight / 2f - doorWidth / 2f,
                        wallThickness, doorWidth);
            case RIGHT:
                return createDoorBounds(
                        position.x + roomWidth - wallThickness / 2f,
                        position.y + roomHeight / 2f - doorWidth / 2f,
                        wallThickness, doorWidth);
        }
        return null;
    }

    public Item getNearbyItem(Player player) {
        for (Item item : items) {
            if (item.getPosition() == null) {
                continue;
            }

            if (player.getPosition().dst(item.getPosition()) <= pickupRadius) {
                return item;
            }
        }
        return null;
    }

    public void removeItem(Item item) {
        items.remove(item);
    }

    public void onEnemyDeath(Enemy enemy) {
        if (enemy != null) {
            enemy.dispose();
        }
    }

    private Texture loadTexture(String path) {
        Texture texture = new Texture(path);
        texture.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
        return texture;
    }

    private List<DoorDirection> initializeDoors(RoomShape shape) {
        List<DoorDirection> doorList = new ArrayList<>();

        switch (shape) {
            case O_SHAPE:
                doorList.add(DoorDirection.UP);
                doorList.add(DoorDirection.DOWN);
                doorList.add(DoorDirection.LEFT);
                doorList.add(DoorDirection.RIGHT);
                break;
            case I_SHAPE_E:
                doorList.add(DoorDirection.LEFT);
                doorList.add(DoorDirection.RIGHT);
                break;
            case I_SHAPE_N:
                doorList.add(DoorDirection.UP);
                doorList.add(DoorDirection.DOWN);
                break;
            case T_SHAPE_N:
                doorList.add(DoorDirection.UP);
                doorList.add(DoorDirection.LEFT);
                doorList.add(DoorDirection.RIGHT);
                break;
            case T_SHAPE_E:
                doorList.add(DoorDirection.UP);
                doorList.add(DoorDirection.RIGHT);
                doorList.add(DoorDirection.DOWN);
                break;
            case T_SHAPE_S:
                doorList.add(DoorDirection.LEFT);
                doorList.add(DoorDirection.RIGHT);
                doorList.add(DoorDirection.DOWN);
                break;
            case T_SHAPE_W:
                doorList.add(DoorDirection.UP);
                doorList.add(DoorDirection.LEFT);
                doorList.add(DoorDirection.DOWN);
                break;
            case L_SHAPE_N:
                doorList.add(DoorDirection.UP);
                doorList.add(DoorDirection.RIGHT);
                break;
            case L_SHAPE_E:
                doorList.add(DoorDirection.RIGHT);
                doorList.add(DoorDirection.DOWN);
                break;
            case L_SHAPE_S:
                doorList.add(DoorDirection.LEFT);
                doorList.add(DoorDirection.DOWN);
                break;
            case L_SHAPE_W:
                doorList.add(DoorDirection.UP);
                doorList.add(DoorDirection.LEFT);
                break;
            case D_SHAPE_N:
                doorList.add(DoorDirection.UP);
                break;
            case D_SHAPE_E:
                doorList.add(DoorDirection.RIGHT);
                break;
            case D_SHAPE_S:
                doorList.add(DoorDirection.DOWN);
                break;
            case D_SHAPE_W:
                doorList.add(DoorDirection.LEFT);
                break;
        }

        return doorList;
    }

    private void initializeRegions() {
        float floorScaleX = roomWidth / tileSize;
        float floorScaleY = roomHeight / tileSize;
        float wallScaleX = roomWidth / tileSize;
        float wallScaleY = wallThickness / tileSize;
        float doorScaleX = doorWidth / tileSize;
        float doorScaleY = wallThickness / tileSize;

        floorRegion.setRegion(0, 0, (int) (TEXTURE_BASE_SIZE * floorScaleX), (int) (TEXTURE_BASE_SIZE * floorScaleY));
        wallTopRegion.setRegion(0, 0, (int) (TEXTURE_BASE_SIZE * wallScaleX), (int) (TEXTURE_BASE_SIZE * wallScaleY));
        wallBottomRegion.setRegion(0, 0, (int) (TEXTURE_BASE_SIZE * wallScaleX),
                (int) (TEXTURE_BASE_SIZE * wallScaleY));
        wallLeftRegion.setRegion(0, 0, (int) (TEXTURE_BASE_SIZE * wallScaleY), (int) (TEXTURE_BASE_SIZE * floorScaleY));
        wallRightRegion.setRegion(0, 0, (int) (TEXTURE_BASE_SIZE * wallScaleY),
                (int) (TEXTURE_BASE_SIZE * floorScaleY));
        doorUpRegion.setRegion(0, 0, (int) (TEXTURE_BASE_SIZE * doorScaleX), (int) (TEXTURE_BASE_SIZE * doorScaleY));
        doorDownRegion.setRegion(0, 0, (int) (TEXTURE_BASE_SIZE * doorScaleX), (int) (TEXTURE_BASE_SIZE * doorScaleY));
        doorLeftRegion.setRegion(0, 0, (int) (TEXTURE_BASE_SIZE * doorScaleY), (int) (TEXTURE_BASE_SIZE * doorScaleX));
        doorRightRegion.setRegion(0, 0, (int) (TEXTURE_BASE_SIZE * doorScaleY), (int) (TEXTURE_BASE_SIZE * doorScaleX));
    }

    private void flipRegions() {
        wallBottomRegion.flip(true, true);
        wallLeftRegion.flip(true, true);
        doorDownRegion.flip(true, true);
        doorLeftRegion.flip(true, true);
    }

    private void initializeGrid() {
        for (int x = 0; x < innerGridWidth; x++) {
            for (int y = 0; y < innerGridHeight; y++) {
                grid[x][y] = CellType.EMPTY;
            }
        }
    }

    private void renderFloor(SpriteBatch batch) {
        batch.draw(floorRegion, position.x, position.y, roomWidth, roomHeight);
    }

    private void renderWalls(SpriteBatch batch) {
        batch.draw(wallTopRegion, position.x, position.y + roomHeight, roomWidth, wallThickness);
        batch.draw(wallBottomRegion, position.x, position.y - wallThickness, roomWidth, wallThickness);
        batch.draw(wallLeftRegion, position.x - wallThickness / 2f, position.y - wallThickness,
                wallThickness / 2f, roomHeight + wallThickness * 2f);
        batch.draw(wallRightRegion, position.x + roomWidth, position.y - wallThickness,
                wallThickness / 2f, roomHeight + wallThickness * 2f);
    }

    private void renderDoors(SpriteBatch batch) {
        for (DoorDirection door : doors) {
            renderDoor(batch, door);
        }
    }

    private void renderDoor(SpriteBatch batch, DoorDirection direction) {
        boolean isActive = (activeDoor == direction);
        TextureRegion region;
        float x, y, width, height;

        switch (direction) {
            case UP:
                region = doorUpRegion;
                x = position.x + roomWidth / 2f - doorWidth / 2f;
                y = position.y + roomHeight;
                width = doorWidth;
                height = wallThickness;
                break;
            case DOWN:
                region = doorDownRegion;
                x = position.x + roomWidth / 2f - doorWidth / 2f;
                y = position.y - wallThickness;
                width = doorWidth;
                height = wallThickness;
                break;
            case LEFT:
                region = doorLeftRegion;
                x = position.x - wallThickness / 2f;
                y = position.y + roomHeight / 2f - doorWidth / 2f;
                width = wallThickness / 2f;
                height = doorWidth;
                break;
            case RIGHT:
                region = doorRightRegion;
                x = position.x + roomWidth;
                y = position.y + roomHeight / 2f - doorWidth / 2f;
                width = wallThickness / 2f;
                height = doorWidth;
                break;
            default:
                return;
        }

        batch.draw(region, x, y, width, height);

        if (isActive) {
            drawDoorOverlay(batch, region, x, y, width, height);
        }
    }

    private void drawDoorOverlay(SpriteBatch batch, TextureRegion doorRegion, float x, float y, float width,
            float height) {
        float time = (System.currentTimeMillis() % (long) DOOR_PULSE_DURATION) / DOOR_PULSE_DURATION;
        float pulseAlpha = DOOR_BASE_ALPHA + DOOR_PULSE_ALPHA * (float) Math.sin(time * Math.PI * 2);

        batch.setColor(0f, 1f, 0f, pulseAlpha);
        batch.draw(doorRegion, x, y, width, height);
        batch.setColor(1f, 1f, 1f, 1f);
    }

    private void renderEnemies(SpriteBatch batch) {
        for (Enemy enemy : enemies) {
            if (enemy.isActive() && !enemy.isDead()) {
                enemy.render(batch);
            }
        }
    }

    private void renderItems(SpriteBatch batch) {
        for (Item item : items) {
            item.render(batch);
        }
    }

    private Rectangle createDoorBounds(float x, float y, float width, float height) {
        return new Rectangle(
                x - DOOR_INTERACTION_PADDING / 2f,
                y - DOOR_INTERACTION_PADDING / 2f,
                width + DOOR_INTERACTION_PADDING,
                height + DOOR_INTERACTION_PADDING);
    }

    private void updateEnemies(float delta, Player player) {
        Iterator<Enemy> iter = enemies.iterator();
        while (iter.hasNext()) {
            Enemy enemy = iter.next();
            if (!enemy.isActive()) {
                continue;
            }

            enemy.update(delta, player);
            checkBulletCollisions(enemy, player);

            if (enemy.isDead()) {
                onEnemyDeath(enemy);
                iter.remove();
            }
        }
    }

    private void checkBulletCollisions(Enemy enemy, Player player) {
        for (Bullet bullet : player.getBullets()) {
            if (bullet.isActive() && bullet.getBounds().overlaps(enemy.getBounds())) {
                bullet.hitEnemy(enemy);
            }
        }
    }

    private void checkRoomCleared() {
        if (!roomClearedReported && initialEnemyCount > 0 && enemies.isEmpty()) {
            roomClearedReported = true;
            if (statistics != null) {
                statistics.onRoomCleared();
            }
        }
    }

    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getCenter() {
        return new Vector2(position.x + roomWidth / 2f, position.y + roomHeight / 2f);
    }

    public void setGridPosition(int row, int col) {
        this.gridRow = row;
        this.gridCol = col;
    }

    public int getGridRow() {
        return gridRow;
    }

    public int getGridCol() {
        return gridCol;
    }

    public List<DoorDirection> getDoors() {
        return doors;
    }

    public RoomShape getShape() {
        return shape;
    }

    public void setContentGenerated(boolean generated) {
        this.contentGenerated = generated;
    }

    public boolean isContentGenerated() {
        return contentGenerated;
    }

    public boolean isActive() {
        return active;
    }

    public CellType[][] getGrid() {
        return grid;
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public List<Item> getItems() {
        return items;
    }

    public boolean hasAliveEnemies() {
        return enemies.stream().anyMatch(e -> e.isActive() && !e.isDead());
    }

    public void setStatistics(GameStatistics statistics) {
        this.statistics = statistics;
    }

    public GameStatistics getStatistics() {
        return statistics;
    }
}

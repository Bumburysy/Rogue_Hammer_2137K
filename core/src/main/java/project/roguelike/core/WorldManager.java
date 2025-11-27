package project.roguelike.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.graphics.Cursor.SystemCursor;
import project.roguelike.entities.Player;
import project.roguelike.levels.LevelGenerator;
import project.roguelike.levels.RoomData;
import project.roguelike.rooms.*;
import project.roguelike.scenes.GameOverScene;
import project.roguelike.ui.GameUI;
import java.util.*;

public class WorldManager {
    private static final float DEATH_TRANSITION_DELAY = 2.0f;
    private static final float CAMERA_LERP_FACTOR = 0.25f;
    private static final float DOOR_SPAWN_OFFSET_DIVISOR = 12f;

    private InputManager inputManager;
    private final SceneManager sceneManager;
    private final GameStatistics statistics;
    private final GameUI gameUI;

    private final RoomData[][] layout;
    private List<Room> rooms;
    private Room currentRoom;
    private int currentRow;
    private int currentCol;

    private Player player;

    private OrthographicCamera camera;
    private FitViewport viewport;
    private Texture crosshair;
    private final Vector2 worldMouse = new Vector2();

    private float deathTransitionTimer = 0f;
    private boolean playerDeathTriggered = false;
    private boolean bossMusicPlaying = false;

    public WorldManager(RoomData[][] layout, SceneManager sceneManager) {
        this.layout = layout;
        this.sceneManager = sceneManager;
        this.statistics = new GameStatistics();
        this.gameUI = new GameUI();
    }

    public WorldManager(RoomData[][] layout, SceneManager sceneManager, Player player) {
        this.layout = layout;
        this.sceneManager = sceneManager;
        this.statistics = new GameStatistics();
        this.gameUI = new GameUI();
        this.player = player;
    }

    public void create() {
        initializeInput();
        initializeLevel();
        initializeCamera();
        initializePlayer();
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void update(float delta) {
        statistics.update(delta);
        inputManager.update();
        updateMousePosition();

        if (currentRoom instanceof BossRoom) {
            if (!bossMusicPlaying && currentRoom.hasAliveEnemies()) {
                SoundManager.playMusic(SoundManager.musicBoss, true);
                bossMusicPlaying = true;
            }
            if (bossMusicPlaying && !currentRoom.hasAliveEnemies()) {
                SoundManager.playMusic(SoundManager.musicMenu, true);
                bossMusicPlaying = false;
            }
        } else {
            if (bossMusicPlaying) {
                SoundManager.playMusic(SoundManager.musicMenu, true);
                bossMusicPlaying = false;
            }
        }

        if (playerDeathTriggered) {
            handleDeathTransition(delta);
            return;
        }

        updatePlayer(delta);
        updateCurrentRoom(delta);

        if (checkPlayerDeath()) {
            return;
        }

        updateDoorInteraction();
        updateCameraToCurrentRoom(false);
    }

    public void render(SpriteBatch batch) {
        if (camera == null) {
            return;
        }

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        currentRoom.render(batch);
        player.render(batch);

        batch.end();

        gameUI.render(viewport, player, layout, currentRow, currentCol);

        batch.begin();
        renderCrosshair(batch);
    }

    public void resize(int width, int height) {
        if (viewport != null) {
            viewport.update(width, height, true);
            updateCameraToCurrentRoom(true);
        }
    }

    public void dispose() {
        disposePlayer();
        disposeRooms();
        disposeCrosshair();
        restoreSystemCursor();
        gameUI.dispose();
    }

    public void handleScroll(int amount) {
        if (amount != 0) {
            player.switchWeapons(-amount);
        }
    }

    private void initializeInput() {
        inputManager = new InputManager();
        inputManager.setScrollCallback(this::handleScroll);
        Gdx.input.setInputProcessor(inputManager);
        crosshair = new Texture("textures/crosshair.png");
        Gdx.graphics.setSystemCursor(SystemCursor.None);
    }

    private void initializeLevel() {
        LevelGenerator generator = new LevelGenerator();
        rooms = generator.generateLevel(layout);

        for (Room room : rooms) {
            room.setStatistics(statistics);
        }

        Room startRoom = findStartRoom();
        if (startRoom == null) {
            throw new IllegalStateException("No START room found in layout!");
        }

        currentRoom = startRoom;
        currentRoom.generateContentIfNeeded();
        currentRoom.activate();
    }

    private Room findStartRoom() {
        for (int row = 0; row < layout.length; row++) {
            for (int col = 0; col < layout[row].length; col++) {
                RoomData data = layout[row][col];
                if (data != null && data.type == RoomData.RoomType.START) {
                    Room room = getRoomAt(row, col);
                    if (room != null) {
                        currentRow = row;
                        currentCol = col;
                        return room;
                    }
                }
            }
        }
        return null;
    }

    private void initializeCamera() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT, camera);
        viewport.apply(true);
    }

    private void initializePlayer() {
        if (player == null) {
            Vector2 spawnPoint = getStartingSpawnPoint();
            player = new Player(spawnPoint.x, spawnPoint.y, GameConfig.PLAYER_SIZE, GameConfig.PLAYER_SIZE);
            player.setStatistics(statistics);
        } else {
            Vector2 spawnPoint = getStartingSpawnPoint();
            player.getPosition().set(spawnPoint);
            player.getBounds().setPosition(
                    spawnPoint.x - player.getBounds().width / 2f,
                    spawnPoint.y - player.getBounds().height / 2f);
            player.setStatistics(statistics);
        }
        updateCameraToCurrentRoom(true);
    }

    private Vector2 getStartingSpawnPoint() {
        if (currentRoom instanceof StartRoom) {
            return ((StartRoom) currentRoom).getSpawnPoint();
        }
        return currentRoom.getCenter();
    }

    private void handleDeathTransition(float delta) {
        deathTransitionTimer += delta;
        player.update(delta, currentRoom, viewport, worldMouse, inputManager);
        updateCurrentRoom(delta);

        if (deathTransitionTimer >= DEATH_TRANSITION_DELAY) {
            transitionToGameOver();
        }
    }

    private void updateMousePosition() {
        worldMouse.set(Gdx.input.getX(), Gdx.input.getY());
        viewport.unproject(worldMouse);
    }

    private void updatePlayer(float delta) {
        player.update(delta, currentRoom, viewport, worldMouse, inputManager);
    }

    private void updateCurrentRoom(float delta) {
        currentRoom.update(delta, player);
    }

    private boolean checkPlayerDeath() {
        if (player.hasJustDied()) {
            playerDeathTriggered = true;
            deathTransitionTimer = 0f;
            return true;
        }
        return false;
    }

    private void transitionToGameOver() {
        restoreSystemCursor();
        sceneManager.setScene(new GameOverScene(sceneManager, statistics));
    }

    private void updateDoorInteraction() {
        Room.DoorDirection nearDoor = checkDoorProximity();
        boolean canUseDoor = canUseDoor();

        if (nearDoor != null) {
            handleNearDoor(nearDoor, canUseDoor);
        } else {
            clearDoorState();
        }
    }

    private void handleNearDoor(Room.DoorDirection nearDoor, boolean canUseDoor) {
        currentRoom.setActiveDoor(nearDoor, canUseDoor);
        if (canUseDoor && inputManager.isActionJustPressed(InputAction.USE)) {
            useDoor(nearDoor);
        }
    }

    private void clearDoorState() {
        currentRoom.setActiveDoor(null, false);
    }

    private Room.DoorDirection checkDoorProximity() {
        if (currentRoom == null || player == null) {
            return null;
        }

        Rectangle playerBounds = player.getBounds();
        for (Room.DoorDirection dir : currentRoom.getDoors()) {
            Rectangle doorBounds = currentRoom.getDoorBounds(dir);
            if (doorBounds != null && playerBounds.overlaps(doorBounds)) {
                return dir;
            }
        }
        return null;
    }

    private boolean canUseDoor() {
        return currentRoom != null && !currentRoom.hasAliveEnemies();
    }

    private void useDoor(Room.DoorDirection direction) {
        if (!canUseDoor()) {
            return;
        }

        int[] newCoords = calculateNewRoomCoordinates(direction);
        Room nextRoom = getRoomAt(newCoords[0], newCoords[1]);

        if (nextRoom == null) {
            return;
        }

        transitionToRoom(nextRoom, newCoords[0], newCoords[1], direction);
    }

    private int[] calculateNewRoomCoordinates(Room.DoorDirection direction) {
        int newRow = currentRow;
        int newCol = currentCol;

        switch (direction) {
            case UP:
                newRow--;
                break;
            case DOWN:
                newRow++;
                break;
            case LEFT:
                newCol--;
                break;
            case RIGHT:
                newCol++;
                break;
        }

        return new int[] { newRow, newCol };
    }

    private void transitionToRoom(Room nextRoom, int newRow, int newCol, Room.DoorDirection enteredFrom) {
        currentRoom.setActiveDoor(null, false);
        currentRoom.deactivate();

        currentRoom = nextRoom;
        currentRow = newRow;
        currentCol = newCol;
        currentRoom.generateContentIfNeeded();
        currentRoom.activate();

        repositionPlayer(enteredFrom, nextRoom);
    }

    private void repositionPlayer(Room.DoorDirection enteredFrom, Room nextRoom) {
        Vector2 spawnPos = getPlayerSpawnPosition(enteredFrom, nextRoom);
        player.getPosition().set(spawnPos);
        player.getBounds().setPosition(
                spawnPos.x - player.getBounds().width / 2f,
                spawnPos.y - player.getBounds().height / 2f);
    }

    private Room.DoorDirection getOppositeDirection(Room.DoorDirection dir) {
        switch (dir) {
            case UP:
                return Room.DoorDirection.DOWN;
            case DOWN:
                return Room.DoorDirection.UP;
            case LEFT:
                return Room.DoorDirection.RIGHT;
            case RIGHT:
                return Room.DoorDirection.LEFT;
            default:
                return dir;
        }
    }

    private Vector2 getPlayerSpawnPosition(Room.DoorDirection enteredFrom, Room nextRoom) {
        float offset = GameConfig.ROOM_HEIGHT / DOOR_SPAWN_OFFSET_DIVISOR;
        Room.DoorDirection spawnAt = getOppositeDirection(enteredFrom);
        Vector2 roomPos = nextRoom.getPosition();
        float centerX = roomPos.x + GameConfig.ROOM_WIDTH / 2f;
        float centerY = roomPos.y + GameConfig.ROOM_HEIGHT / 2f;

        switch (spawnAt) {
            case UP:
                return new Vector2(centerX, roomPos.y + GameConfig.ROOM_HEIGHT - offset);
            case DOWN:
                return new Vector2(centerX, roomPos.y + offset);
            case LEFT:
                return new Vector2(roomPos.x + offset, centerY);
            case RIGHT:
                return new Vector2(roomPos.x + GameConfig.ROOM_WIDTH - offset, centerY);
            default:
                return nextRoom.getCenter();
        }
    }

    private Room getRoomAt(int row, int col) {
        if (!isValidRoomCoordinate(row, col)) {
            return null;
        }

        RoomData data = layout[row][col];
        if (data == null) {
            return null;
        }

        for (Room room : rooms) {
            if (room.getGridRow() == row && room.getGridCol() == col) {
                return room;
            }
        }
        return null;
    }

    private boolean isValidRoomCoordinate(int row, int col) {
        return row >= 0 && col >= 0 && row < layout.length && col < layout[row].length;
    }

    private void renderCrosshair(SpriteBatch batch) {
        float size = GameConfig.BULLET_SIZE * 1.5f;
        batch.draw(crosshair, worldMouse.x - size / 2f, worldMouse.y - size / 2f, size, size);
    }

    private void updateCameraToCurrentRoom(boolean immediate) {
        if (camera == null || currentRoom == null) {
            return;
        }

        Vector2 center = currentRoom.getCenter();
        float yOffset = GameConfig.TOP_MARGIN / 2f;
        float targetX = center.x;
        float targetY = center.y + yOffset;

        if (immediate) {
            camera.position.set(targetX, targetY, 0f);
        } else {
            camera.position.x += (targetX - camera.position.x) * CAMERA_LERP_FACTOR;
            camera.position.y += (targetY - camera.position.y) * CAMERA_LERP_FACTOR;
        }

        camera.update();
    }

    private void disposePlayer() {
        if (player != null) {
            player.dispose();
        }
    }

    private void disposeRooms() {
        if (rooms != null) {
            for (Room room : rooms) {
                room.dispose();
            }
        }
    }

    private void disposeCrosshair() {
        if (crosshair != null) {
            crosshair.dispose();
        }
    }

    private void restoreSystemCursor() {
        try {
            Gdx.graphics.setSystemCursor(SystemCursor.Arrow);
        } catch (Exception ignored) {
        }
    }

    public GameStatistics getStatistics() {
        return statistics;
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public Player getPlayer() {
        return player;
    }
}

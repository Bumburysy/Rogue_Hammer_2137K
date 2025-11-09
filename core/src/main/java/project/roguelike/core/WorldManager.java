package project.roguelike.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.graphics.Cursor.SystemCursor;
import project.roguelike.entities.Player;
import project.roguelike.items.Pistol;
import project.roguelike.items.Rifle;
import project.roguelike.items.Shotgun;
import project.roguelike.items.Smg;
import project.roguelike.items.Sniper;
import project.roguelike.levels.LevelGenerator;
import project.roguelike.levels.RoomData;
import java.util.*;
import project.roguelike.rooms.*;

public class WorldManager {
    private InputManager inputManager;
    private RoomData[][] layout;
    private List<Room> rooms;
    private Player player;
    private Room currentRoom;
    private int currentRow;
    private int currentCol;
    private boolean isNearDoor = false;
    private OrthographicCamera camera;
    private FitViewport viewport;
    private Texture crosshair;

    public WorldManager(RoomData[][] layout) {
        this.layout = layout;
    }

    public void create() {
        inputManager = new InputManager(this);
        Gdx.input.setInputProcessor(inputManager);
        LevelGenerator generator = new LevelGenerator();
        rooms = generator.generateLevel(layout);
        camera = new OrthographicCamera();
        viewport = new FitViewport(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT, camera);
        viewport.apply(true);
        crosshair = new Texture("textures/crosshair.png");
        Gdx.graphics.setSystemCursor(SystemCursor.None);
        Room startRoom = null;
        int startRow = -1, startCol = -1;
        outer: for (int row = 0; row < layout.length; row++) {
            for (int col = 0; col < layout[row].length; col++) {
                RoomData data = layout[row][col];
                if (data != null && data.type == RoomData.RoomType.START) {
                    startRoom = getRoomAt(row, col);
                    startRow = row;
                    startCol = col;
                    break outer;
                }
            }
        }

        if (startRoom == null) {
            if (rooms == null || rooms.isEmpty()) {
                throw new IllegalStateException("No rooms generated");
            }
            startRoom = rooms.get(0);
            startRow = 0;
            startCol = 0;
            System.out.println("No START room found!");
        }

        currentRoom = startRoom;
        currentRow = startRow;
        currentCol = startCol;

        float playerSize = GameConfig.PLAYER_SIZE;
        Vector2 spawn = currentRoom.getCenter();
        player = new Player(spawn.x, spawn.y, playerSize, playerSize);
        updateCameraToCurrentRoom(true);
        player.addActiveItem(new Pistol());
        player.addActiveItem(new Smg());
        player.addActiveItem(new Shotgun());
        player.addActiveItem(new Rifle());
        player.addActiveItem(new Sniper());
    }

    public void handleScroll(int amount) {
        player.switchActive(-amount);
    }

    public void update(float delta) {
        player.update(delta, currentRoom, viewport);

        Room.DoorDirection nearDoor = checkDoorProximity();
        if (nearDoor != null) {
            isNearDoor = true;
            if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                useDoor(nearDoor);
            }
        } else {
            isNearDoor = false;
        }
        updateCameraToCurrentRoom(false);
    }

    public void dispose() {
        player.dispose();
        for (Room r : rooms) {
            r.dispose();
        }
        if (crosshair != null) {
            crosshair.dispose();
        }
        try {
            Gdx.graphics.setSystemCursor(SystemCursor.Arrow);
        } catch (Exception ignored) {
        }
    }

    private Room getRoomAt(int row, int col) {
        if (row < 0 || col < 0 || row >= layout.length || col >= layout[row].length) {
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

    private Room.DoorDirection checkDoorProximity() {
        for (Room.DoorDirection dir : currentRoom.getDoors()) {
            if (player.getBounds().overlaps(currentRoom.getDoorBounds(dir))) {
                return dir;
            }
        }
        return null;
    }

    private void useDoor(Room.DoorDirection direction) {
        if (!canUseDoor()) {
            System.out.println("Cannot use door");
            return;
        }

        int newRow = currentRow;
        int newCol = currentCol;

        switch (direction) {
            case UP: {
                newRow--;
                break;
            }
            case DOWN: {
                newRow++;
                break;
            }
            case LEFT: {
                newCol--;
                break;
            }
            case RIGHT: {
                newCol++;
                break;
            }
        }

        Room nextRoom = getRoomAt(newRow, newCol);
        if (nextRoom == null) {
            return;
        }

        currentRoom = nextRoom;
        currentRow = newRow;
        currentCol = newCol;

        Vector2 newPos = getPlayerSpawnPosition(direction, nextRoom);
        player.getPosition().set(newPos);
        player.getBounds().setPosition(newPos.x - player.getBounds().width / 2f,
                newPos.y - player.getBounds().height / 2f);
    }

    private boolean canUseDoor() {
        return true;
    }

    private Room.DoorDirection getOppositeDirection(Room.DoorDirection dir) {
        switch (dir) {
            case UP: {
                return Room.DoorDirection.DOWN;
            }
            case DOWN: {
                return Room.DoorDirection.UP;
            }
            case LEFT: {
                return Room.DoorDirection.RIGHT;
            }
            case RIGHT: {
                return Room.DoorDirection.LEFT;
            }
        }
        return dir;
    }

    private Vector2 getPlayerSpawnPosition(Room.DoorDirection enteredFrom, Room nextRoom) {
        float offset = GameConfig.ROOM_HEIGHT / 12f;
        Room.DoorDirection spawnAt = getOppositeDirection(enteredFrom);

        switch (spawnAt) {
            case UP: {
                return new Vector2(
                        nextRoom.getPosition().x + GameConfig.ROOM_WIDTH / 2f,
                        nextRoom.getPosition().y + GameConfig.ROOM_HEIGHT - offset);
            }
            case DOWN: {
                return new Vector2(
                        nextRoom.getPosition().x + GameConfig.ROOM_WIDTH / 2f,
                        nextRoom.getPosition().y + offset);
            }
            case LEFT: {
                return new Vector2(
                        nextRoom.getPosition().x + offset,
                        nextRoom.getPosition().y + GameConfig.ROOM_HEIGHT / 2f);
            }
            case RIGHT: {
                return new Vector2(
                        nextRoom.getPosition().x + GameConfig.ROOM_WIDTH - offset,
                        nextRoom.getPosition().y + GameConfig.ROOM_HEIGHT / 2f);
            }
        }
        return nextRoom.getCenter();
    }

    public void render(SpriteBatch batch) {
        if (camera != null) {
            camera.update();
        }

        batch.setProjectionMatrix(camera.combined);
        currentRoom.render(batch);
        player.render(batch);
        Vector2 mouse = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        viewport.unproject(mouse);
        float size = GameConfig.BULLET_SIZE * 1.5f;
        batch.draw(crosshair, mouse.x - size / 2f, mouse.y - size / 2f, size, size);

        if (isNearDoor) {
        }
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
            float lerp = 0.25f;
            camera.position.x += (targetX - camera.position.x) * lerp;
            camera.position.y += (targetY - camera.position.y) * lerp;
        }
        camera.update();
    }

    public void resize(int width, int height) {
        if (viewport != null) {
            viewport.update(width, height, true);
            updateCameraToCurrentRoom(true);
        }
    }
}

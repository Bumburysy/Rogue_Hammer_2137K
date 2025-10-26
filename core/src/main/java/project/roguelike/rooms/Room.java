package project.roguelike.rooms;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Room {
    private Texture floor, wallTop, wallBottom, wallLeft, wallRight;
    private Texture doorUp, doorDown, doorLeft, doorRight;
    private Vector2 position;

    private RoomType type;
    private RoomShape shape;
    private List<DoorDirection> doors;

    public enum RoomType {
        NORMAL, START, END, BOSS, SHOP, TRAP, CHEST
    }

    public enum RoomShape {
        O_SHAPE, I_SHAPE_H, I_SHAPE_V,
    }

    public enum DoorDirection {
        UP, DOWN, LEFT, RIGHT
    }

    public Room(Vector2 position, RoomType type, RoomShape shape) {
        this.position = position;
        this.type = type;
        this.shape = shape;

        this.floor = new Texture("textures/floor.png");
        this.wallTop = new Texture("textures/wall_top.png");
        this.wallBottom = new Texture("textures/wall_bottom.png");
        this.wallLeft = new Texture("textures/wall_left.png");
        this.wallRight = new Texture("textures/wall_right.png");

        this.doorUp = new Texture("textures/door_up.png");
        this.doorDown = new Texture("textures/door_down.png");
        this.doorLeft = new Texture("textures/door_left.png");
        this.doorRight = new Texture("textures/door_right.png");

        addDoors();
    }

    private void addDoors() {
        doors = new ArrayList<>();
        switch (shape) {
            case O_SHAPE: {
                doors.add(DoorDirection.UP);
                doors.add(DoorDirection.DOWN);
                doors.add(DoorDirection.LEFT);
                doors.add(DoorDirection.RIGHT);
            }
            case I_SHAPE_H: {
                doors.add(DoorDirection.LEFT);
                doors.add(DoorDirection.RIGHT);
            }
            case I_SHAPE_V: {
                doors.add(DoorDirection.UP);
                doors.add(DoorDirection.DOWN);
            }
        }
    }

    public void render(SpriteBatch batch) {
        float scale = 0.3f;

        batch.draw(floor, position.x, position.y, floor.getWidth() * scale, floor.getHeight() * scale);
        batch.draw(wallTop, position.x, position.y + floor.getHeight() * scale, wallTop.getWidth() * scale,
                wallTop.getHeight() * scale);
        batch.draw(wallBottom, position.x, position.y - floor.getHeight() / 2 * scale, wallBottom.getWidth() * scale,
                wallBottom.getHeight() * scale);
        batch.draw(wallRight, position.x + floor.getWidth() * scale, position.y, wallRight.getWidth() * scale,
                wallRight.getHeight() * scale);
        batch.draw(wallLeft, position.x - wallLeft.getWidth() * scale, position.y, wallLeft.getWidth() * scale,
                wallLeft.getHeight() * scale);

        for (DoorDirection door : doors) {
            switch (door) {
                case UP:
                    batch.draw(doorUp, position.x + floor.getWidth() * scale / 2 - doorUp.getWidth() * scale / 2,
                            position.y + floor.getHeight() * scale, doorUp.getWidth() * scale,
                            doorUp.getHeight() * scale);
                case DOWN:
                    batch.draw(doorDown,
                            position.x + floor.getWidth() * scale / 2 - doorDown.getWidth() * scale / 2,
                            position.y - doorDown.getHeight() * scale, doorDown.getWidth() * scale,
                            doorDown.getHeight() * scale);
                case LEFT:
                    batch.draw(doorLeft, position.x - doorLeft.getWidth() * scale,
                            position.y + floor.getHeight() * scale / 2 - doorLeft.getHeight() * scale / 2,
                            doorLeft.getWidth() * scale, doorLeft.getHeight() * scale);
                case RIGHT:
                    batch.draw(doorRight, position.x + floor.getWidth() * scale,
                            position.y + floor.getHeight() * scale / 2 - doorRight.getHeight() * scale / 2,
                            doorRight.getWidth() * scale, doorRight.getHeight() * scale);
            }
        }
    }

    public void dispose() {
        floor.dispose();
        wallTop.dispose();
        wallBottom.dispose();
        wallLeft.dispose();
        wallRight.dispose();
    }
}

package project.roguelike.levels;

import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.List;
import project.roguelike.core.GameConfig;
import project.roguelike.rooms.NormalRoom;
import project.roguelike.rooms.Room;
import project.roguelike.rooms.StartRoom;

public class LevelGenerator {
    private final float roomWidth;
    private final float roomHeight;
    private final float spacing;

    public LevelGenerator() {
        this.roomWidth = GameConfig.ROOM_WIDTH;
        this.roomHeight = GameConfig.ROOM_HEIGHT;
        this.spacing = GameConfig.ROOM_SPACING;
    }

    public List<Room> generateLevel(RoomData[][] layout) {
        List<Room> rooms = new ArrayList<>();
        int rows = layout.length;
        int cols = layout[0].length;
        float totalWidth = cols * roomWidth + (cols - 1) * spacing;
        float offsetX = (GameConfig.WORLD_WIDTH - totalWidth) / 2f;
        float offsetY = 0f;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                RoomData data = layout[row][col];
                if (data == null) {
                    continue;
                }
                Vector2 position = new Vector2(
                        offsetX + col * (roomWidth + spacing),
                        offsetY + (rows - 1 - row) * (roomHeight + spacing));
                Room.RoomShape shape = data.shape != null
                        ? data.shape
                        : detectShape(layout, row, col);
                Room room;
                switch (data.type) {
                    case START: {
                        room = new StartRoom(position, shape);
                        break;
                    }
                    default: {
                        room = new NormalRoom(position, shape);
                        break;
                    }
                }
                room.setGridPosition(row, col);
                rooms.add(room);
            }
        }
        return rooms;
    }

    private Room.RoomShape detectShape(RoomData[][] layout, int row, int col) {
        boolean up = row > 0 && layout[row - 1][col] != null;
        boolean down = row < layout.length - 1 && layout[row + 1][col] != null;
        boolean left = col > 0 && layout[row][col - 1] != null;
        boolean right = col < layout[row].length - 1 && layout[row][col + 1] != null;
        if (up && down && left && right) {
            return Room.RoomShape.O_SHAPE;
        }
        if (up && left && right) {
            return Room.RoomShape.T_SHAPE_N;
        }
        if (down && left && right) {
            return Room.RoomShape.T_SHAPE_S;
        }
        if (up && down && left) {
            return Room.RoomShape.T_SHAPE_W;
        }
        if (up && down && right) {
            return Room.RoomShape.T_SHAPE_E;
        }
        if (up && down) {
            return Room.RoomShape.I_SHAPE_N;
        }
        if (left && right) {
            return Room.RoomShape.I_SHAPE_E;
        }
        if (up && right) {
            return Room.RoomShape.L_SHAPE_N;
        }
        if (right && down) {
            return Room.RoomShape.L_SHAPE_E;
        }
        if (down && left) {
            return Room.RoomShape.L_SHAPE_S;
        }
        if (left && up) {
            return Room.RoomShape.L_SHAPE_W;
        }
        if (up) {
            return Room.RoomShape.D_SHAPE_N;
        }
        if (down) {
            return Room.RoomShape.D_SHAPE_S;
        }
        if (left) {
            return Room.RoomShape.D_SHAPE_W;
        }
        if (right) {
            return Room.RoomShape.D_SHAPE_E;
        }
        return Room.RoomShape.D_SHAPE_S;
    }
}

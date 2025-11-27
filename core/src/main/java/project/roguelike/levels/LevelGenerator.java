package project.roguelike.levels;

import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.List;
import project.roguelike.core.GameConfig;
import project.roguelike.rooms.BossRoom;
import project.roguelike.rooms.ChestRoom;
import project.roguelike.rooms.EndRoom;
import project.roguelike.rooms.NormalRoom;
import project.roguelike.rooms.Room;
import project.roguelike.rooms.ShopRoom;
import project.roguelike.rooms.StartRoom;
import project.roguelike.rooms.TrapRoom;

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

                Vector2 position = calculateRoomPosition(offsetX, offsetY, row, col, rows);
                Room.RoomShape shape = determineRoomShape(layout, data, row, col);
                Room room = createRoom(data.type, position, shape);

                room.setGridPosition(row, col);
                rooms.add(room);
            }
        }

        return rooms;
    }

    private Vector2 calculateRoomPosition(float offsetX, float offsetY, int row, int col, int totalRows) {
        return new Vector2(
                offsetX + col * (roomWidth + spacing),
                offsetY + (totalRows - 1 - row) * (roomHeight + spacing));
    }

    private Room.RoomShape determineRoomShape(RoomData[][] layout, RoomData data, int row, int col) {
        return data.shape != null ? data.shape : detectShape(layout, row, col);
    }

    private Room createRoom(RoomData.RoomType type, Vector2 position, Room.RoomShape shape) {
        switch (type) {
            case START:
                return new StartRoom(position, shape);
            case BOSS:
                return new BossRoom(position, shape);
            case TRAP:
                return new TrapRoom(position, shape);
            case CHEST:
                return new ChestRoom(position, shape);
            case SHOP:
                return new ShopRoom(position, shape);
            case END:
                return new EndRoom(position, shape);
            case NORMAL:
                return new NormalRoom(position, shape);
            default:
                return new NormalRoom(position, shape);
        }
    }

    private Room.RoomShape detectShape(RoomData[][] layout, int row, int col) {
        ConnectionData connections = analyzeConnections(layout, row, col);

        if (connections.countConnections() == 4) {
            return Room.RoomShape.O_SHAPE;
        }

        if (connections.countConnections() == 3) {
            return detectTShape(connections);
        }

        if (connections.countConnections() == 2) {
            return detectTwoConnectionShape(connections);
        }

        return detectSingleConnectionShape(connections);
    }

    private ConnectionData analyzeConnections(RoomData[][] layout, int row, int col) {
        boolean up = isRoomAt(layout, row - 1, col);
        boolean down = isRoomAt(layout, row + 1, col);
        boolean left = isRoomAt(layout, row, col - 1);
        boolean right = isRoomAt(layout, row, col + 1);

        return new ConnectionData(up, down, left, right);
    }

    private boolean isRoomAt(RoomData[][] layout, int row, int col) {
        return row >= 0 && row < layout.length &&
                col >= 0 && col < layout[row].length &&
                layout[row][col] != null;
    }

    private Room.RoomShape detectTShape(ConnectionData conn) {
        if (conn.up && conn.left && conn.right)
            return Room.RoomShape.T_SHAPE_N;
        if (conn.down && conn.left && conn.right)
            return Room.RoomShape.T_SHAPE_S;
        if (conn.up && conn.down && conn.left)
            return Room.RoomShape.T_SHAPE_W;
        if (conn.up && conn.down && conn.right)
            return Room.RoomShape.T_SHAPE_E;
        return Room.RoomShape.O_SHAPE;
    }

    private Room.RoomShape detectTwoConnectionShape(ConnectionData conn) {
        if (conn.up && conn.down)
            return Room.RoomShape.I_SHAPE_N;
        if (conn.left && conn.right)
            return Room.RoomShape.I_SHAPE_E;
        if (conn.up && conn.right)
            return Room.RoomShape.L_SHAPE_N;
        if (conn.right && conn.down)
            return Room.RoomShape.L_SHAPE_E;
        if (conn.down && conn.left)
            return Room.RoomShape.L_SHAPE_S;
        if (conn.left && conn.up)
            return Room.RoomShape.L_SHAPE_W;
        return Room.RoomShape.D_SHAPE_S;
    }

    private Room.RoomShape detectSingleConnectionShape(ConnectionData conn) {
        if (conn.up)
            return Room.RoomShape.D_SHAPE_N;
        if (conn.down)
            return Room.RoomShape.D_SHAPE_S;
        if (conn.left)
            return Room.RoomShape.D_SHAPE_W;
        if (conn.right)
            return Room.RoomShape.D_SHAPE_E;
        return Room.RoomShape.D_SHAPE_S;
    }

    private static class ConnectionData {
        final boolean up, down, left, right;

        ConnectionData(boolean up, boolean down, boolean left, boolean right) {
            this.up = up;
            this.down = down;
            this.left = left;
            this.right = right;
        }

        int countConnections() {
            int count = 0;
            if (up)
                count++;
            if (down)
                count++;
            if (left)
                count++;
            if (right)
                count++;
            return count;
        }
    }
}

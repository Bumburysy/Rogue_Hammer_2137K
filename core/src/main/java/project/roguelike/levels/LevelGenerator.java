package project.roguelike.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.List;
import project.roguelike.rooms.Room;
import project.roguelike.rooms.RoomData;

public class LevelGenerator {

    private final int roomWidth;
    private final int roomHeight;

    public LevelGenerator(int roomWidth, int roomHeight) {
        this.roomWidth = roomWidth;
        this.roomHeight = roomHeight;
    }

    public List<Room> generateLevel(RoomData[][] layout) {
        List<Room> rooms = new ArrayList<>();

        int rows = layout.length;
        int cols = layout[0].length;

        float totalWidth = cols * roomWidth;
        float totalHeight = rows * roomHeight;

        float offsetX = Gdx.graphics.getWidth() / 2f - totalWidth / 2f;
        float offsetY = Gdx.graphics.getHeight() / 2f - totalHeight / 2f;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                RoomData data = layout[row][col];
                if (data == null)
                    continue;

                Vector2 position = new Vector2(
                        col * roomWidth + offsetX,
                        (rows - 1 - row) * roomHeight + offsetY);

                Room room = new Room(position, data.type, data.shape);
                rooms.add(room);
            }
        }

        return rooms;
    }

}

package project.roguelike.rooms;

import com.badlogic.gdx.math.Vector2;
import project.roguelike.core.GameConfig;

public class StartRoom extends Room {
    public StartRoom(Vector2 position, RoomShape shape) {
        super(position, shape);
    }

    public Vector2 getSpawnPoint() {
        return new Vector2(getPosition().x + GameConfig.ROOM_WIDTH / 2f, getPosition().y + GameConfig.ROOM_HEIGHT / 2f);
    }
}

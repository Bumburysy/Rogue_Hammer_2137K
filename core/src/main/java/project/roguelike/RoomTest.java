package project.roguelike;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.List;
import project.roguelike.levels.Layout1;
import project.roguelike.levels.LevelGenerator;
import project.roguelike.rooms.Room;
import project.roguelike.rooms.RoomData;

public class RoomTest implements Scene {

    private SpriteBatch batch;
    private List<Room> rooms;

    @Override
    public void create() {
        batch = new SpriteBatch();
        RoomData[][] layout = Layout1.layout;
        LevelGenerator generator = new LevelGenerator(380, 300);
        rooms = generator.generateLevel(layout);
    }

    @Override
    public void render(SpriteBatch batch) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        for (Room r : rooms) {
            r.render(batch);
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        for (Room r : rooms) {
            r.dispose();
        }
    }
}

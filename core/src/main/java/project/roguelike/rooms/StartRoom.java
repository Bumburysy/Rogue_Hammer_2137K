package project.roguelike.rooms;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import project.roguelike.core.GameConfig;
import project.roguelike.core.RoomContentGenerator;
import project.roguelike.core.RoomContentPlan;

public class StartRoom extends Room {
    private final Texture ladderTexture;
    private final Vector2 ladderPosition;
    private final float ladderWidth;
    private final float ladderHeight;

    public StartRoom(Vector2 position, RoomShape shape) {
        super(position, shape);

        this.ladderTexture = new Texture("textures/ladder.png");
        this.ladderHeight = GameConfig.TILE_SIZE;
        this.ladderWidth = calculateLadderWidth();
        this.ladderPosition = calculateLadderPosition();
    }

    @Override
    public void render(SpriteBatch batch) {
        super.render(batch);
        renderLadder(batch);
    }

    @Override
    public void generateContentIfNeeded() {
        if (isContentGenerated()) {
            return;
        }

        RoomContentPlan plan = createStartingItems();
        RoomContentGenerator.generate(this, plan);
        setContentGenerated(true);
    }

    @Override
    public void dispose() {
        super.dispose();
        if (ladderTexture != null) {
            ladderTexture.dispose();
        }
    }

    public Vector2 getSpawnPoint() {
        Vector2 pos = getPosition();
        return new Vector2(
                pos.x + GameConfig.ROOM_WIDTH / 2f,
                pos.y + GameConfig.ROOM_HEIGHT / 2f);
    }

    private float calculateLadderWidth() {
        float aspectRatio = (float) ladderTexture.getWidth() / ladderTexture.getHeight();
        return ladderHeight * aspectRatio;
    }

    private Vector2 calculateLadderPosition() {
        Vector2 pos = getPosition();
        return new Vector2(
                pos.x + GameConfig.ROOM_WIDTH / 2f - ladderWidth / 2f,
                pos.y + GameConfig.ROOM_HEIGHT / 2f - ladderHeight / 2f);
    }

    private void renderLadder(SpriteBatch batch) {
        batch.draw(ladderTexture,
                ladderPosition.x,
                ladderPosition.y,
                ladderWidth,
                ladderHeight);
    }

    private RoomContentPlan createStartingItems() {
        RoomContentPlan plan = new RoomContentPlan();
        Vector2 pistolPos = new Vector2(10f, 4f);
        plan.items.add(new RoomContentPlan.ItemSpawn("pistol", pistolPos));
        return plan;
    }
}

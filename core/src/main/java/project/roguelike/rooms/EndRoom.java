package project.roguelike.rooms;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import project.roguelike.core.GameConfig;
import project.roguelike.core.RoomContentPlan;
import project.roguelike.core.RoomContentGenerator;
import project.roguelike.entities.Player;

public class EndRoom extends Room {
    private static final Texture HATCH_TEXTURE = new Texture("textures/hatch.png");
    private boolean onHatch = false;

    private final float hatchSize;
    private final float hatchX;
    private final float hatchY;

    public EndRoom(Vector2 position, RoomShape shape) {
        super(position, shape);
        this.hatchSize = GameConfig.TILE_SIZE;
        this.hatchX = getPosition().x + GameConfig.ROOM_WIDTH / 2f - hatchSize / 2f;
        this.hatchY = getPosition().y + GameConfig.ROOM_HEIGHT / 2f - hatchSize / 2f;
    }

    @Override
    public void generateContentIfNeeded() {
        if (isContentGenerated())
            return;

        Vector2 chestPos = new Vector2(12, 5);
        Vector2 itemPos = new Vector2(8, 5);

        RoomContentPlan plan = new RoomContentPlan();
        plan.chests.add(new RoomContentPlan.ChestSpawn(chestPos));
        plan.items.add(new RoomContentPlan.ItemSpawn("random", itemPos));
        RoomContentGenerator.generate(this, plan);

        setContentGenerated(true);
    }

    @Override
    public void update(float delta, Player player) {
        super.update(delta, player);
        onHatch = isPlayerOnHatch(player, hatchX, hatchY, hatchSize);
    }

    public boolean isPlayerOnHatch(Player player, float hatchX, float hatchY, float hatchSize) {
        return player.getBounds().overlaps(
                new com.badlogic.gdx.math.Rectangle(hatchX, hatchY, hatchSize, hatchSize));
    }

    public boolean isOnHatch() {
        return onHatch;
    }

    @Override
    public void render(SpriteBatch batch) {
        super.render(batch);
        batch.draw(HATCH_TEXTURE, hatchX, hatchY, hatchSize, hatchSize);
    }
}

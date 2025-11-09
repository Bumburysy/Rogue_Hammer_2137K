package project.roguelike.core;

public class GameConfig {
    public static final float TILE_SIZE = 64f;
    public static final float PLAYER_SIZE = TILE_SIZE;
    public static final float BULLET_SIZE = PLAYER_SIZE / 4f;
    public static final float BULLET_SPEED = 512f;

    public static final float WALL_THICKNESS = 64f;
    public static final float TOP_MARGIN = 128f;

    public static final float ROOM_WIDTH = 1280f;
    public static final float ROOM_HEIGHT = ROOM_WIDTH / 2f;

    public static final float ROOM_TOTAL_WIDTH = ROOM_WIDTH + WALL_THICKNESS;
    public static final float ROOM_TOTAL_HEIGHT = ROOM_HEIGHT + WALL_THICKNESS * 2;

    public static final float ROOM_SPACING = WALL_THICKNESS * 2f;

    public static final float WORLD_HEIGHT = ROOM_TOTAL_HEIGHT + TOP_MARGIN;
    public static final float WORLD_WIDTH = WORLD_HEIGHT * 16f / 9f;

    public static final float WEAPON_WIDTH_RATIO = 0.4f;
    public static final float OFFSET_X_RATIO = 0.33f;
    public static final float OFFSET_Y_RATIO = -0.33f;
}

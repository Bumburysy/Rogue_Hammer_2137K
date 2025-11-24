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

    public static final float UI_TITLE_HEIGHT = 64f;
    public static final float UI_TITLE_MARGIN_TOP = 128f;
    public static final float UI_TITLE_MARGIN_BOTTOM = 128f;

    public static final float UI_ELEMENT_SPACING = 128f;
    public static final float UI_ELEMENT_SPACING_COMPACT = 48f;
    public static final float UI_ELEMENT_HEIGHT = 32f;

    public static final float UI_TEXT_SCALE = 2f;
    public static final float UI_TEXT_SCALE_SMALL = 1.5f;
    public static final float UI_VALUE_TEXT_SCALE = 1.5f;

    public static final float UI_VALUE_Y_OFFSET = 32f;

    public static final float UI_CONTROLS_LABEL_X_OFFSET = 250f;
    public static final float UI_CONTROLS_BUTTON_X_OFFSET = 100f;
    public static final float UI_CONTROLS_KEY_X_OFFSET = 128f;

    public static final float UI_MARGIN_BOTTOM = 50f;
    public static final float UI_MARGIN_SIDE = 150f;

    public static final float UI_HOVER_SCALE = 1.1f;
    public static final float UI_INACTIVE_TINT = 0.8f;

    public static final float UI_OVERLAY_ALPHA = 0.7f;
    public static final float UI_FADE_SPEED = 2f;

    public static final float UI_LOADING_SPEED = 1.5f;
    public static final float UI_PROGRESS_BAR_WIDTH = 400f;
    public static final float UI_PROGRESS_BAR_HEIGHT = 30f;

    public static final int UI_VOLUME_STEP = 10;
    public static final int UI_MAX_KEYCODE = 256;
}

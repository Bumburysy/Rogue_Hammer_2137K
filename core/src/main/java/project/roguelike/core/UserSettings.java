package project.roguelike.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class UserSettings {
    private static final String PREFERENCES_NAME = "roguelike_settings";
    private static final Preferences prefs = Gdx.app.getPreferences(PREFERENCES_NAME);

    private static final String KEY_VOLUME = "volume";
    private static final String KEY_MUSIC_VOLUME = "music_volume";
    private static final String KEY_UI_VOLUME = "ui_volume";
    private static final String KEY_SFX_VOLUME = "sfx_volume";
    private static final String KEY_FULLSCREEN = "fullscreen";
    private static final String KEY_WIDTH = "width";
    private static final String KEY_HEIGHT = "height";

    private static final float DEFAULT_VOLUME = 0.5f;
    private static final float DEFAULT_MUSIC_VOLUME = 0.5f;
    private static final float DEFAULT_UI_VOLUME = 0.5f;
    private static final float DEFAULT_SFX_VOLUME = 0.5f;
    private static final boolean DEFAULT_FULLSCREEN = false;
    private static final int DEFAULT_WIDTH = 1280;
    private static final int DEFAULT_HEIGHT = 720;

    public static float masterVolume = DEFAULT_VOLUME;
    public static float musicVolume = DEFAULT_MUSIC_VOLUME;
    public static float uiVolume = DEFAULT_UI_VOLUME;
    public static float sfxVolume = DEFAULT_SFX_VOLUME;
    public static boolean fullscreen = DEFAULT_FULLSCREEN;
    public static int screenWidth = DEFAULT_WIDTH;
    public static int screenHeight = DEFAULT_HEIGHT;

    public static void load() {
        masterVolume = prefs.getFloat(KEY_VOLUME, DEFAULT_VOLUME);
        musicVolume = prefs.getFloat(KEY_MUSIC_VOLUME, DEFAULT_MUSIC_VOLUME);
        uiVolume = prefs.getFloat(KEY_UI_VOLUME, DEFAULT_UI_VOLUME);
        sfxVolume = prefs.getFloat(KEY_SFX_VOLUME, DEFAULT_SFX_VOLUME);
        fullscreen = prefs.getBoolean(KEY_FULLSCREEN, DEFAULT_FULLSCREEN);
        screenWidth = prefs.getInteger(KEY_WIDTH, DEFAULT_WIDTH);
        screenHeight = prefs.getInteger(KEY_HEIGHT, DEFAULT_HEIGHT);
    }

    public static void save() {
        prefs.putFloat(KEY_VOLUME, masterVolume);
        prefs.putFloat(KEY_MUSIC_VOLUME, musicVolume);
        prefs.putFloat(KEY_UI_VOLUME, uiVolume);
        prefs.putFloat(KEY_SFX_VOLUME, sfxVolume);
        prefs.putBoolean(KEY_FULLSCREEN, fullscreen);
        prefs.putInteger(KEY_WIDTH, screenWidth);
        prefs.putInteger(KEY_HEIGHT, screenHeight);
        prefs.flush();
    }

    public static void setVolume(float volume) {
        masterVolume = clamp(volume, 0f, 1f);
        save();
    }

    public static void setMusicVolume(float value) {
        musicVolume = clamp(value, 0f, 1f);
        save();
    }

    public static void setUiVolume(float value) {
        uiVolume = clamp(value, 0f, 1f);
        save();
    }

    public static void setSfxVolume(float value) {
        sfxVolume = clamp(value, 0f, 1f);
        save();
    }

    public static void setResolution(int width, int height) {
        screenWidth = width;
        screenHeight = height;

        if (!fullscreen) {
            Gdx.graphics.setWindowedMode(width, height);
        }

        save();
    }

    public static void toggleFullscreen() {
        fullscreen = !fullscreen;

        if (fullscreen) {
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        } else {
            Gdx.graphics.setWindowedMode(screenWidth, screenHeight);
        }

        save();
    }

    public static String getResolutionString() {
        return screenWidth + "x" + screenHeight;
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
}

package project.roguelike.core;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import java.util.HashMap;
import java.util.Map;

public class KeyBindings {
    private static final String PREFERENCES_NAME = "roguelike_keybindings";
    private static KeyBindings instance;

    private final Preferences prefs;
    private final Map<InputAction, Integer> keyMap = new HashMap<>();

    private KeyBindings() {
        this.prefs = Gdx.app.getPreferences(PREFERENCES_NAME);
        setDefaultBindings();
        load();
    }

    public static KeyBindings getInstance() {
        if (instance == null) {
            instance = new KeyBindings();
        }
        return instance;
    }

    public void reload() {
        keyMap.clear();
        setDefaultBindings();
        load();
    }

    private void setDefaultBindings() {
        keyMap.put(InputAction.MOVE_UP, Input.Keys.W);
        keyMap.put(InputAction.MOVE_DOWN, Input.Keys.S);
        keyMap.put(InputAction.MOVE_LEFT, Input.Keys.A);
        keyMap.put(InputAction.MOVE_RIGHT, Input.Keys.D);
        keyMap.put(InputAction.RELOAD, Input.Keys.R);
        keyMap.put(InputAction.USE, Input.Keys.E);
        keyMap.put(InputAction.USE_ACTIVE_ITEM, Input.Keys.Q);
        keyMap.put(InputAction.SELECT_ACTIVE_ITEM_PREV, Input.Keys.NUM_1);
        keyMap.put(InputAction.SELECT_ACTIVE_ITEM_NEXT, Input.Keys.NUM_2);
        keyMap.put(InputAction.PAUSE, Input.Keys.ESCAPE);
    }

    public int getKey(InputAction action) {
        return keyMap.getOrDefault(action, -1);
    }

    public void setKey(InputAction action, int keycode) {
        keyMap.put(action, keycode);
        save();
    }

    public void resetToDefaults() {
        keyMap.clear();
        setDefaultBindings();
        save();
    }

    public void load() {
        for (InputAction action : InputAction.values()) {
            String key = "key_" + action.name();
            if (prefs.contains(key)) {
                int keycode = prefs.getInteger(key);
                keyMap.put(action, keycode);
            }
        }
    }

    public void save() {
        for (Map.Entry<InputAction, Integer> entry : keyMap.entrySet()) {
            String key = "key_" + entry.getKey().name();
            prefs.putInteger(key, entry.getValue());
        }
        prefs.flush();
    }

    public String getKeyName(InputAction action) {
        int key = getKey(action);
        return key != -1 ? Input.Keys.toString(key) : "None";
    }

    public String getActionDisplayName(InputAction action) {
        switch (action) {
            case MOVE_UP:
                return "Move Up";
            case MOVE_DOWN:
                return "Move Down";
            case MOVE_LEFT:
                return "Move Left";
            case MOVE_RIGHT:
                return "Move Right";
            case RELOAD:
                return "Reload";
            case USE:
                return "Use/Interact";
            case USE_ACTIVE_ITEM:
                return "Use Active Item";
            case SELECT_ACTIVE_ITEM_PREV:
                return "Previous Active Item";
            case SELECT_ACTIVE_ITEM_NEXT:
                return "Next Active Item";
            case PAUSE:
                return "Pause Game";
            default:
                return action.name();
        }
    }
}
package project.roguelike.ui;

import java.util.LinkedHashMap;
import java.util.Map;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;
import project.roguelike.core.GameConfig;
import project.roguelike.entities.Player;
import project.roguelike.items.activeItems.ActiveItem;
import project.roguelike.items.passiveItems.PassiveItem;
import project.roguelike.items.weapons.Weapon;
import project.roguelike.levels.RoomData;
import project.roguelike.levels.RoomData.RoomType;

public class GameUI {
    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch batch;
    private final Texture backgroundTexture;
    private final TextureRegion backgroundRegion;
    private final BitmapFont font;
    private Texture heartIcon;
    private Texture coinIcon;
    private Texture keyIcon;
    private Texture startRoomTexture;
    private Texture bossRoomTexture;
    private Texture normalRoomTexture;
    private TextureRegion startRoomIcon;
    private TextureRegion bossRoomIcon;
    private TextureRegion normalRoomIcon;

    private static final float UI_HEIGHT = 128f;
    private static final Color UI_BORDER = new Color(0.5f, 0.5f, 0.5f, 1f);
    private static final int TEXTURE_BASE_SIZE = 16;

    private static final Color HP_BAR_BG = new Color(0.2f, 0.2f, 0.2f, 1f);
    private static final Color HP_BAR_FILL = new Color(0.8f, 0.1f, 0.1f, 1f);
    private static final Color HP_BAR_BORDER = new Color(1f, 1f, 1f, 1f);
    private static final float HP_BAR_WIDTH = 256f;
    private static final float HP_BAR_HEIGHT = 32f;
    private static final float HEART_ICON_SIZE = HP_BAR_HEIGHT;
    private static final float HP_BAR_PADDING_LEFT = 24f;
    private static final float HP_BAR_PADDING_TOP = 24f;
    private static final float ICON_PADDING = HP_BAR_HEIGHT / 2f;

    private static final float ICON_ROW_SIZE = HEART_ICON_SIZE * 1.5f;
    private static final float ICON_ROW_SPACING = 0.55f;
    private static final float ICON_ROW_PADDING_LEFT = HP_BAR_PADDING_LEFT * 0.75f;
    private static final float ICON_ROW_PADDING_TOP = HP_BAR_PADDING_TOP + HP_BAR_HEIGHT * 1.25f;
    private static final float ICON_TEXT_SCALE = 0.33f;
    private static final float ICON_TEXT_GAP = 44f;

    private static final float CENTER_SLOT_WIDTH = 128f;
    private static final float CENTER_SLOT_HEIGHT = 72f;
    private static final float CENTER_SLOT_SPACING = 24f;
    private static final float CENTER_SLOT_BAR_HEIGHT = 12f;
    private static final float CENTER_SLOT_BAR_MARGIN = 8f;
    private static final float CENTER_SLOT_ICON_SIZE = 64f;
    private static final float CENTER_SLOT_ICON_SHIFT_Y = 8f;

    private static final Color SLOT_BORDER_READY = Color.GREEN;
    private static final Color SLOT_BORDER_BUSY = Color.RED;
    private static final Color SLOT_BORDER_EMPTY = Color.WHITE;
    private static final Color SLOT_BAR_BG = new Color(0.2f, 0.2f, 0.2f, 1f);
    private static final Color SLOT_BAR_FILL = new Color(1f, 0.8f, 0f, 1f);

    private static final float MINIMAP_PADDING = 12f;
    private static final float MINIMAP_ROOM_SIZE = GameConfig.TILE_SIZE;
    private static final float MINIMAP_ROOM_SPACING = 4f;
    private static final float MINIMAP_ICON_SIZE_RATIO = 0.5f;

    public GameUI() {
        this.shapeRenderer = new ShapeRenderer();
        this.batch = new SpriteBatch();
        this.backgroundTexture = new Texture("ui/background.png");
        this.backgroundTexture.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);

        int tileSize = (int) GameConfig.TILE_SIZE;
        this.backgroundRegion = new TextureRegion(
                backgroundTexture,
                0, 0,
                tileSize, tileSize);

        this.font = new BitmapFont();
        this.font.getData().setScale(1.2f);
        this.font.setColor(Color.WHITE);

        this.heartIcon = new Texture("ui/heart_icon.png");
        this.coinIcon = new Texture("ui/coin_icon.png");
        this.keyIcon = new Texture("ui/key_icon.png");

        this.startRoomTexture = new Texture("ui/minimap_start.png");
        this.bossRoomTexture = new Texture("ui/minimap_boss.png");
        this.normalRoomTexture = new Texture("ui/minimap_normal.png");

        this.startRoomIcon = new TextureRegion(startRoomTexture);
        this.bossRoomIcon = new TextureRegion(bossRoomTexture);
        this.normalRoomIcon = new TextureRegion(normalRoomTexture);
    }

    public void render(Viewport viewport, Player player, RoomData[][] layout, int playerRoomRow, int playerRoomCol) {
        OrthographicCamera camera = (OrthographicCamera) viewport.getCamera();

        float viewportWidth = viewport.getWorldWidth();
        float viewportHeight = viewport.getWorldHeight();

        float cameraX = camera.position.x;
        float cameraY = camera.position.y;

        float uiX = cameraX - viewportWidth / 2f;
        float uiY = cameraY + viewportHeight / 2f - UI_HEIGHT;
        float uiWidth = viewportWidth;

        renderBackground(uiX, uiY, uiWidth, camera);
        renderHealthBar(uiX, uiY, player, camera);
        renderResourceAndPassiveRow(uiX, uiY, player, camera);
        renderCenterSlots(uiX, uiY, uiWidth, player, camera);
        renderMinimap(uiX, uiY, uiWidth, UI_HEIGHT, layout, camera, playerRoomRow, playerRoomCol);

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(UI_BORDER);
        shapeRenderer.line(uiX, uiY, uiX + uiWidth, uiY);
        shapeRenderer.end();
    }

    private void renderBackground(float uiX, float uiY, float uiWidth, OrthographicCamera camera) {
        float tileSize = GameConfig.TILE_SIZE;
        float scaleX = uiWidth / tileSize;
        float scaleY = UI_HEIGHT / tileSize;

        backgroundRegion.setRegion(
                0, 0,
                (int) (TEXTURE_BASE_SIZE * scaleX),
                (int) (TEXTURE_BASE_SIZE * scaleY));

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(
                backgroundRegion,
                uiX, uiY,
                uiWidth, UI_HEIGHT);
        batch.end();
    }

    private void renderHealthBar(float uiX, float uiY, Player player, OrthographicCamera camera) {
        float hpBarX = uiX + HP_BAR_PADDING_LEFT + HEART_ICON_SIZE + ICON_PADDING;
        float hpBarY = uiY + UI_HEIGHT - HP_BAR_PADDING_TOP - HP_BAR_HEIGHT;

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(HP_BAR_BG);
        shapeRenderer.rect(hpBarX, hpBarY, HP_BAR_WIDTH, HP_BAR_HEIGHT);

        float hpPercent = (float) player.getHealth() / player.getMaxHealth();
        float hpFillWidth = HP_BAR_WIDTH * hpPercent;
        shapeRenderer.setColor(HP_BAR_FILL);
        shapeRenderer.rect(hpBarX, hpBarY, hpFillWidth, HP_BAR_HEIGHT);

        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(HP_BAR_BORDER);
        shapeRenderer.rect(hpBarX, hpBarY, HP_BAR_WIDTH, HP_BAR_HEIGHT);
        shapeRenderer.end();

        float heartX = uiX + HP_BAR_PADDING_LEFT;
        float heartY = hpBarY + (HP_BAR_HEIGHT - HEART_ICON_SIZE);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(heartIcon, heartX, heartY, HEART_ICON_SIZE, HEART_ICON_SIZE);

        String hpText = player.getHealth() + " / " + player.getMaxHealth();
        GlyphLayout layout = new GlyphLayout(font, hpText);

        float textX = hpBarX + (HP_BAR_WIDTH - layout.width) / 2f;
        float textY = hpBarY + (HP_BAR_HEIGHT + layout.height) / 2f;

        font.draw(batch, hpText, textX, textY);

        batch.end();
    }

    private void renderResourceAndPassiveRow(float uiX, float uiY, Player player, OrthographicCamera camera) {
        float rowY = uiY + UI_HEIGHT - ICON_ROW_PADDING_TOP - ICON_ROW_SIZE;
        float rowX = uiX + ICON_ROW_PADDING_LEFT;

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        class IconData {
            Texture icon;
            Color color;
            String text;
            float textWidth;

            IconData(Texture icon, Color color, String text, float textWidth) {
                this.icon = icon;
                this.color = color;
                this.text = text;
                this.textWidth = textWidth;
            }
        }
        java.util.List<IconData> icons = new java.util.ArrayList<>();

        String coinText = "x" + player.getCoins();
        font.getData().setScale(1.1f);
        float coinTextWidth = font.getRegion().getRegionWidth();
        coinTextWidth = new GlyphLayout(font, coinText).width;
        icons.add(new IconData(coinIcon, Color.GOLD, coinText, coinTextWidth));

        String keyText = "x" + player.getKeys();
        float keyTextWidth = new GlyphLayout(font, keyText).width;
        icons.add(new IconData(keyIcon, Color.LIGHT_GRAY, keyText, keyTextWidth));

        Map<String, Integer> counts = new LinkedHashMap<>();
        Map<String, PassiveItem> firstOfType = new LinkedHashMap<>();
        for (PassiveItem item : player.getPassiveItems()) {
            String id = item.getId();
            counts.put(id, counts.getOrDefault(id, 0) + 1);
            if (!firstOfType.containsKey(id)) {
                firstOfType.put(id, item);
            }
        }
        for (String id : counts.keySet()) {
            PassiveItem item = firstOfType.get(id);
            Texture texture = item.getTexture();
            String countText = "x" + counts.get(id);
            float countTextWidth = new GlyphLayout(font, countText).width;
            icons.add(new IconData(texture, Color.WHITE, countText, countTextWidth));
        }

        float textScale = ICON_ROW_SIZE * ICON_TEXT_SCALE / font.getCapHeight();
        font.getData().setScale(textScale);

        for (IconData data : icons) {
            if (data.icon != null) {
                if (data.icon != coinIcon && data.icon != keyIcon) {
                    batch.draw(data.icon, rowX, rowY, ICON_ROW_SIZE, ICON_ROW_SIZE, 0, 0, 16, 16, false, false);
                } else {
                    batch.draw(data.icon, rowX, rowY, ICON_ROW_SIZE, ICON_ROW_SIZE);
                }
            }
            font.setColor(data.color);

            GlyphLayout layout = new GlyphLayout(font, data.text);
            float textX = rowX + ICON_TEXT_GAP;
            float textY = rowY + (ICON_ROW_SIZE + layout.height) / 2f;

            font.draw(batch, data.text, textX, textY);

            rowX += ICON_ROW_SPACING * (data.textWidth + ICON_TEXT_GAP + ICON_ROW_SIZE);
        }
        font.getData().setScale(1.2f);
        font.setColor(Color.WHITE);

        batch.end();
    }

    private void renderCenterSlots(float uiX, float uiY, float uiWidth, Player player, OrthographicCamera camera) {
        float slotY = uiY + UI_HEIGHT - HP_BAR_PADDING_TOP - CENTER_SLOT_HEIGHT;

        float slot1X = uiX + uiWidth / 2f - CENTER_SLOT_WIDTH - CENTER_SLOT_SPACING / 2f;
        float slot2X = uiX + uiWidth / 2f + CENTER_SLOT_SPACING / 2f;

        Weapon weapon = player.getCurrentWeapon();
        ActiveItem activeItem = player.getActiveItem();

        String weaponText;
        float weaponTextScale;
        Color weaponTextColor;
        if (weapon != null && player.getCurrentWeaponTexture() != null) {
            if (weapon.isReloading()) {
                weaponText = "Reloading";
                weaponTextScale = 1.0f;
                weaponTextColor = Color.WHITE;
            } else {
                weaponText = weapon.getCurrentAmmo() + " / " + weapon.getMagazineSize();
                weaponTextScale = 1.1f;
                weaponTextColor = Color.WHITE;
            }
        } else {
            weaponText = "No Weapon";
            weaponTextScale = 1.0f;
            weaponTextColor = Color.LIGHT_GRAY;
        }

        String itemText;
        float itemTextScale;
        Color itemTextColor;
        if (activeItem != null && activeItem.getTexture() != null) {
            itemText = String.format("%.1fs", Math.max(0f, activeItem.getCurrentCooldown()));
            itemTextScale = 1.1f;
            itemTextColor = Color.WHITE;
        } else {
            itemText = "No Item";
            itemTextScale = 1.0f;
            itemTextColor = Color.LIGHT_GRAY;
        }

        Color borderColor1 = SLOT_BORDER_EMPTY;
        if (weapon != null) {
            boolean weaponReady = !weapon.isReloading() && weapon.getCurrentAmmo() > 0;
            borderColor1 = weaponReady ? SLOT_BORDER_READY : SLOT_BORDER_BUSY;
        }
        Color borderColor2 = SLOT_BORDER_EMPTY;
        if (activeItem != null) {
            boolean itemReady = activeItem.getCurrentCooldown() <= 0f;
            borderColor2 = itemReady ? SLOT_BORDER_READY : SLOT_BORDER_BUSY;
        }

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0.1f, 0.1f, 0.1f, 0.8f));
        shapeRenderer.rect(slot1X, slotY, CENTER_SLOT_WIDTH, CENTER_SLOT_HEIGHT);
        shapeRenderer.rect(slot2X, slotY, CENTER_SLOT_WIDTH, CENTER_SLOT_HEIGHT);
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(borderColor1);
        shapeRenderer.rect(slot1X, slotY, CENTER_SLOT_WIDTH, CENTER_SLOT_HEIGHT);
        shapeRenderer.setColor(borderColor2);
        shapeRenderer.rect(slot2X, slotY, CENTER_SLOT_WIDTH, CENTER_SLOT_HEIGHT);
        shapeRenderer.end();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        if (weapon != null && player.getCurrentWeaponTexture() != null) {
            Texture weaponTexture = player.getCurrentWeaponTexture();
            int texW = weaponTexture.getWidth();
            int texH = weaponTexture.getHeight();
            float ratio = Math.min(CENTER_SLOT_ICON_SIZE / texW, CENTER_SLOT_ICON_SIZE / texH);
            float drawW = texW * ratio;
            float drawH = texH * ratio;
            float iconX = slot1X + (CENTER_SLOT_WIDTH - drawW) / 2f;
            float iconY = slotY + (CENTER_SLOT_HEIGHT - drawH) / 2f + CENTER_SLOT_ICON_SHIFT_Y;
            batch.draw(weaponTexture, iconX, iconY, drawW, drawH);
        }

        font.getData().setScale(weaponTextScale);
        font.setColor(weaponTextColor);
        GlyphLayout weaponLayout = new GlyphLayout(font, weaponText);
        float weaponTextX = slot1X + (CENTER_SLOT_WIDTH - weaponLayout.width) / 2f;
        float weaponTextY = slotY + 18f;
        font.draw(batch, weaponText, weaponTextX, weaponTextY);

        if (activeItem != null && activeItem.getTexture() != null) {
            Texture itemTexture = activeItem.getTexture();
            int texW = itemTexture.getWidth();
            int texH = itemTexture.getHeight();
            float ratio = Math.min(CENTER_SLOT_ICON_SIZE / texW, CENTER_SLOT_ICON_SIZE / texH);
            float drawW = texW * ratio;
            float drawH = texH * ratio;
            float iconX = slot2X + (CENTER_SLOT_WIDTH - drawW) / 2f;
            float iconY = slotY + (CENTER_SLOT_HEIGHT - drawH) / 2f + CENTER_SLOT_ICON_SHIFT_Y;
            batch.draw(itemTexture, iconX, iconY, drawW, drawH);
        }

        font.getData().setScale(itemTextScale);
        font.setColor(itemTextColor);
        GlyphLayout itemLayout = new GlyphLayout(font, itemText);
        float itemTextX = slot2X + (CENTER_SLOT_WIDTH - itemLayout.width) / 2f;
        float itemTextY = slotY + 18f;
        font.draw(batch, itemText, itemTextX, itemTextY);

        font.getData().setScale(1.2f);
        font.setColor(Color.WHITE);
        batch.end();

        if (weapon != null && weapon.isReloading()) {
            float barX = slot1X;
            float barY = slotY - CENTER_SLOT_BAR_MARGIN - CENTER_SLOT_BAR_HEIGHT;
            float percent = weapon.getRawReloadProgress() / weapon.getReloadTime();
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(SLOT_BAR_BG);
            shapeRenderer.rect(barX, barY, CENTER_SLOT_WIDTH, CENTER_SLOT_BAR_HEIGHT);
            shapeRenderer.setColor(SLOT_BAR_FILL);
            shapeRenderer.rect(barX, barY, CENTER_SLOT_WIDTH * percent, CENTER_SLOT_BAR_HEIGHT);
            shapeRenderer.end();
        }

        if (activeItem != null && activeItem.getCurrentCooldown() > 0f) {
            float barX = slot2X;
            float barY = slotY - CENTER_SLOT_BAR_MARGIN - CENTER_SLOT_BAR_HEIGHT;
            float percent = 1f - (activeItem.getCurrentCooldown() / activeItem.getCooldown());
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(SLOT_BAR_BG);
            shapeRenderer.rect(barX, barY, CENTER_SLOT_WIDTH, CENTER_SLOT_BAR_HEIGHT);
            shapeRenderer.setColor(SLOT_BAR_FILL);
            shapeRenderer.rect(barX, barY, CENTER_SLOT_WIDTH * percent, CENTER_SLOT_BAR_HEIGHT);
            shapeRenderer.end();
        }
    }

    public void renderMinimap(float uiX, float uiY, float uiWidth, float uiHeight, RoomData[][] layout,
            OrthographicCamera camera, int playerRoomRow, int playerRoomCol) {
        if (layout == null)
            return;

        int rows = layout.length;
        int cols = layout[0].length;

        float maxMapWidth = 104f;
        float maxMapHeight = 104f;
        float roomSizeX = (maxMapWidth - (cols - 1) * MINIMAP_ROOM_SPACING) / cols;
        float roomSizeY = (maxMapHeight - (rows - 1) * MINIMAP_ROOM_SPACING) / rows;
        float roomSize = Math.min(MINIMAP_ROOM_SIZE, Math.min(roomSizeX, roomSizeY));

        float mapWidth = cols * roomSize + (cols - 1) * MINIMAP_ROOM_SPACING;
        float mapHeight = rows * roomSize + (rows - 1) * MINIMAP_ROOM_SPACING;
        float minimapX = uiX + uiWidth - MINIMAP_PADDING - mapWidth;
        float minimapY = uiY + uiHeight - MINIMAP_PADDING - mapHeight;

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                RoomData room = layout[row][col];
                if (room == null)
                    continue;
                float x = minimapX + col * (roomSize + MINIMAP_ROOM_SPACING);
                float y = minimapY + (rows - 1 - row) * (roomSize + MINIMAP_ROOM_SPACING);

                if (row == playerRoomRow && col == playerRoomCol) {
                    shapeRenderer.setColor(Color.valueOf("a020f0"));
                    shapeRenderer.rect(x, y, roomSize, roomSize);
                }
            }
        }
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                RoomData room = layout[row][col];
                if (room == null)
                    continue;
                float x = minimapX + col * (roomSize + MINIMAP_ROOM_SPACING);
                float y = minimapY + (rows - 1 - row) * (roomSize + MINIMAP_ROOM_SPACING);
                shapeRenderer.setColor(Color.WHITE);
                shapeRenderer.rect(x, y, roomSize, roomSize);
            }
        }
        shapeRenderer.end();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                RoomData room = layout[row][col];
                if (room == null)
                    continue;
                float x = minimapX + col * (roomSize + MINIMAP_ROOM_SPACING);
                float y = minimapY + (rows - 1 - row) * (roomSize + MINIMAP_ROOM_SPACING);
                TextureRegion icon = null;
                if (room.type == RoomType.START)
                    icon = startRoomIcon;
                else if (room.type == RoomType.BOSS)
                    icon = bossRoomIcon;
                else
                    icon = normalRoomIcon;
                float iconSize = roomSize * MINIMAP_ICON_SIZE_RATIO;
                if (icon != null) {
                    batch.draw(icon, x + (roomSize - iconSize) / 2f, y + (roomSize - iconSize) / 2f, iconSize,
                            iconSize);
                }
            }
        }
        batch.end();
    }

    public void dispose() {
        shapeRenderer.dispose();
        batch.dispose();
        backgroundTexture.dispose();
        heartIcon.dispose();
        font.dispose();
        coinIcon.dispose();
        keyIcon.dispose();
        startRoomTexture.dispose();
        bossRoomTexture.dispose();
        normalRoomTexture.dispose();
    }
}
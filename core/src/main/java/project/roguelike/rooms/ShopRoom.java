package project.roguelike.rooms;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import project.roguelike.core.GameConfig;
import project.roguelike.entities.Player;
import project.roguelike.items.Item;
import project.roguelike.items.ItemFactory;
import project.roguelike.items.passiveItems.PassiveItem;
import project.roguelike.items.consumableItems.ConsumableItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ShopRoom extends Room {
    private static final int SHOP_ITEM_COUNT = 3;
    private static final int[] POSSIBLE_PRICES = { 5, 7, 10, 12, 15, 20, 25, 30 };
    private static final float ITEM_SPACING = GameConfig.TILE_SIZE * 2f;

    private final List<Item> shopItems = new ArrayList<>();
    public final List<Integer> itemPrices = new ArrayList<>();
    private final List<Float> itemAnimTimes = new ArrayList<>();
    private final BitmapFont font = new BitmapFont();

    private static final int CANDLE_FRAME_WIDTH = 16;
    private static final int CANDLE_FRAME_HEIGHT = 16;
    private static final float CANDLE_FRAME_DURATION = 0.15f;
    private static final Texture CANDLE_TEXTURE = new Texture("textures/candlestick.png");
    private static final Animation<TextureRegion> CANDLE_ANIMATION;
    static {
        TextureRegion[][] tmp = TextureRegion.split(CANDLE_TEXTURE, CANDLE_FRAME_WIDTH, CANDLE_FRAME_HEIGHT);
        CANDLE_ANIMATION = new Animation<>(CANDLE_FRAME_DURATION, tmp[0]);
    }
    private float candleStateTime = 0f;

    private static final Texture CARPET_TEXTURE = new Texture("textures/shop_carpet.png");

    public ShopRoom(Vector2 position, RoomShape shape) {
        super(position, shape);
    }

    @Override
    public void generateContentIfNeeded() {
        if (shopItems.isEmpty()) {
            float centerX = getPosition().x + GameConfig.ROOM_WIDTH / 2f;
            float centerY = getPosition().y + GameConfig.ROOM_HEIGHT / 2f;

            Random rand = new Random();
            int generated = 0;
            List<String> weaponIds = new ArrayList<>();
            List<String> activeIds = new ArrayList<>();
            java.util.Map<String, Integer> priceMap = new java.util.HashMap<>();

            while (generated < SHOP_ITEM_COUNT) {
                float offset = (generated - 1) * ITEM_SPACING;
                Vector2 pos = new Vector2(centerX + offset, centerY);

                Item item;
                do {
                    item = ItemFactory.createRandomItem(pos);
                } while (item == null ||
                        item.getType() == Item.ItemType.CURRENCY ||
                        (item.getType() == Item.ItemType.WEAPON && weaponIds.contains(item.getId())) ||
                        (item.getType() == Item.ItemType.ACTIVE && activeIds.contains(item.getId())));

                if (item.getType() == Item.ItemType.WEAPON) {
                    weaponIds.add(item.getId());
                } else if (item.getType() == Item.ItemType.ACTIVE) {
                    activeIds.add(item.getId());
                }

                shopItems.add(item);

                String id = item.getId();
                int price;
                if (priceMap.containsKey(id)) {
                    price = priceMap.get(id);
                } else {
                    price = POSSIBLE_PRICES[rand.nextInt(POSSIBLE_PRICES.length)];
                    priceMap.put(id, price);
                }
                itemPrices.add(price);

                itemAnimTimes.add(0f);
                generated++;
            }
        }
    }

    public void update(float delta, Player player) {
        super.update(delta, player);

        candleStateTime += delta;

        for (int i = 0; i < itemAnimTimes.size(); i++) {
            Item item = shopItems.get(i);
            if (item != null) {
                itemAnimTimes.set(i, itemAnimTimes.get(i) + delta);
                if (item instanceof PassiveItem) {
                    ((PassiveItem) item).update(delta);
                } else if (item instanceof ConsumableItem) {
                    ((ConsumableItem) item).update(delta);
                }
            }
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        super.render(batch);

        float candleSize = GameConfig.TILE_SIZE;
        TextureRegion candleFrame = CANDLE_ANIMATION.getKeyFrame(candleStateTime, true);

        float x0 = getPosition().x;
        float y0 = getPosition().y;
        float x1 = getPosition().x + GameConfig.ROOM_WIDTH - candleSize;
        float y1 = getPosition().y + GameConfig.ROOM_HEIGHT - candleSize;

        batch.draw(candleFrame, x0, y0, candleSize, candleSize);
        batch.draw(candleFrame, x1, y0, candleSize, candleSize);
        batch.draw(candleFrame, x0, y1, candleSize, candleSize);
        batch.draw(candleFrame, x1, y1, candleSize, candleSize);

        for (int i = 0; i < shopItems.size(); i++) {
            Item item = shopItems.get(i);
            if (item == null)
                continue;

            float shelfWidth = GameConfig.TILE_SIZE;
            float shelfHeight = GameConfig.TILE_SIZE;
            float shelfYOffset = -GameConfig.TILE_SIZE * 0.45f;

            float shelfX = item.getPosition().x - shelfWidth / 2f;
            float shelfY = item.getPosition().y + shelfYOffset;

            CARPET_TEXTURE.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
            TextureRegion shelfRegion = new TextureRegion(CARPET_TEXTURE);
            float u2 = shelfWidth / GameConfig.TILE_SIZE;
            float v2 = shelfHeight / GameConfig.TILE_SIZE;
            shelfRegion.setRegion(0, 0, u2, v2);

            batch.draw(shelfRegion, shelfX, shelfY, shelfWidth, shelfHeight);
        }

        float labelY = getPosition().y + GameConfig.ROOM_HEIGHT / 2f + GameConfig.TILE_SIZE / 2f;
        for (int i = 0; i < shopItems.size(); i++) {
            Item item = shopItems.get(i);
            if (item == null)
                continue;

            if (item instanceof PassiveItem) {
                ((PassiveItem) item).render(batch, item.getPosition());
            } else if (item instanceof ConsumableItem) {
                ((ConsumableItem) item).render(batch, item.getPosition());
            } else {
                item.render(batch);
            }

            String priceText = itemPrices.get(i) + "C";
            float textWidth = font.getCache().setText(priceText, 0, 0).width;
            float textX = item.getPosition().x - (GameConfig.TILE_SIZE - textWidth) / 4f;
            font.draw(batch, priceText, textX, labelY);
        }
    }

    public List<Item> getShopItems() {
        return shopItems;
    }

    @Override
    public Item getNearbyItem(Player player) {
        for (Item item : shopItems) {
            if (item != null && player.getPosition().dst(item.getPosition()) <= GameConfig.TILE_SIZE) {
                return item;
            }
        }
        return super.getNearbyItem(player);
    }
}

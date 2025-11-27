package project.roguelike.items;

import com.badlogic.gdx.math.Vector2;
import java.util.Random;
import project.roguelike.items.passiveItems.*;
import project.roguelike.items.activeItems.*;
import project.roguelike.items.consumableItems.*;
import project.roguelike.items.weapons.*;
import project.roguelike.items.currencyItems.*;

public class ItemFactory {
    private static final Random rand = new Random();

    public static Item createRandomItem(Vector2 position) {
        int roll = rand.nextInt(17);
        switch (roll) {
            case 0:
                return new DamageBoost(position);
            case 1:
                return new FireRateBoost(position);
            case 2:
                return new MagazineSizeBoost(position);
            case 3:
                return new MaxHpBoost(position);
            case 4:
                return new MovementSpeedBoost(position);
            case 5:
                return new ReloadSpeedBoost(position);
            case 6:
                return new BulletSpeedBoost(position);
            case 7:
                return new MedKit(position);
            case 8:
                return new AmmoBox(position);
            case 9:
                return new SmallHealthPotion(position);
            case 10:
                return new LargeHealthPotion(position);
            case 11:
                return new Rifle(position);
            case 12:
                return new Shotgun(position);
            case 13:
                return new Smg(position);
            case 14:
                return new Sniper(position);
            case 15:
                return new Coin(position, 1);
            case 16:
                return new Key(position, 1);
        }
        return new Coin(position, 1);
    }
}
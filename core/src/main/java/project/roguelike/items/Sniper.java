package project.roguelike.items;

import com.badlogic.gdx.graphics.Texture;

public class Sniper extends Weapon {
    public Sniper() {
        super(
                "Sniper ",
                "Sniper Rifle",
                .8f,
                600f,
                5f,
                4f,
                2f,
                false);
        this.texture = new Texture("textures/sniper.png");
    }
}

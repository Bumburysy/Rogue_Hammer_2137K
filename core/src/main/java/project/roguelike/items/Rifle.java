package project.roguelike.items;

import com.badlogic.gdx.graphics.Texture;

public class Rifle extends Weapon {
    public Rifle() {
        super(
                "rifle",
                "Rifle",
                0.2f,
                400f,
                2f,
                30f,
                1.8f,
                true);
        this.texture = new Texture("textures/rifle.png");
    }
}

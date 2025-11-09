package project.roguelike.items;

import com.badlogic.gdx.graphics.Texture;

public class Smg extends Weapon {
    public Smg() {
        super(
                "smg",
                "SMG",
                0.1f,
                300f,
                1f,
                30f,
                1.4f,
                true);
        this.texture = new Texture("textures/smg.png");
    }
}
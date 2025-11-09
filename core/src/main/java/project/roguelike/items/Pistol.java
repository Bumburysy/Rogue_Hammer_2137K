package project.roguelike.items;

import com.badlogic.gdx.graphics.Texture;

public class Pistol extends Weapon {
    public Pistol() {
        super(
                "pistol",
                "Pistol",
                0.2f,
                300f,
                1f,
                7f,
                1f,
                false);
        this.texture = new Texture("textures/pistol.png");
    }
}

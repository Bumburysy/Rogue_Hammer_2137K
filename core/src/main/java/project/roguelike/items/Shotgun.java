package project.roguelike.items;

import com.badlogic.gdx.graphics.Texture;

public class Shotgun extends Weapon {
    public Shotgun() {
        super(
                "shotgun",
                "Shotgun",
                0.4f,
                200f,
                4f,
                8f,
                2f,
                false);
        this.texture = new Texture("textures/shotgun.png");
    }
}
package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.audio.Sound;

public class HealthPickup implements Pickup {
    private Rectangle bounds;
    private Texture texture;
    private boolean active = true;
    private Sound healthSound;

    public HealthPickup(Texture tex, float x, float y, Sound healthSound) {
        this.texture = tex;
        this.bounds = new Rectangle(x, y, 32, 32);
        this.healthSound = healthSound;
    }

    @Override
    public void update(float dt) {
        bounds.y -= 150 * dt;
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (active)
            batch.draw(texture, bounds.x, bounds.y, bounds.width, bounds.height);
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void applyEffect(Tarro player) {
        if (!active) return;
        player.recuperarSalud(3);
        active = false;
        healthSound.play(1f);
    }

    @Override
    public Rectangle getBounds() {
        return bounds;
    }
}

package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class SlowPickup implements Pickup {
    private Rectangle bounds;
    private Texture texture;
    private boolean active = true;
    private AttackManager attackMgr;

    public SlowPickup(Texture tex, float x, float y) {
        this.texture = tex;
        this.attackMgr = AttackManager.getInstance();
        this.bounds = new Rectangle(x, y, 16, 38);
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
        active = false;
        attackMgr.slowAll(0.7f, 5f); // menos velocidad
    }

    @Override
    public Rectangle getBounds() {
        return bounds;
    }
}

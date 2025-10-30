package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public abstract class Attack {
    protected final Texture texture;
    protected final Rectangle bounds;
    protected float vx, vy;
    protected int damage;
    protected boolean active = true;
    protected float speedFactor = 1f;

    public Attack(Texture tex, float x, float y, float vx, float vy, int damage) {
        this.texture = tex;
        this.texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        this.bounds = new Rectangle(x, y, 32, 32);
        this.vx = vx;
        this.vy = vy;
        this.damage = damage;
    }

    // Para mover los ataques frame por frame
    public abstract void update(float dt, Rectangle arena);

    // Mostrar los sprites
    public void draw(SpriteBatch batch) {
        if (!active) return;
        batch.draw(texture, bounds.x, bounds.y, bounds.width, bounds.height);
    }

    // Colisiones
    public boolean collidesWith(Rectangle r) {
        return active && bounds.overlaps(r);
    }

    public void deactivate() { active = false; }

    // Getters
    public boolean isActive() { return active; }
    public int getDamage()   { return damage; }
    public void setSpeedFactor(float mult) {
        this.speedFactor = mult;
    }
}
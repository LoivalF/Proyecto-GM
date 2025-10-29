package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public abstract class Attack {
    protected final Texture texture;
    protected final Rectangle bounds; // hitbox (y tamaño dibujado)
    protected float vx, vy;           // velocidad
    protected int damage;             // daño típico = 3
    protected boolean active = true;

    public Attack(Texture tex, float x, float y, float vx, float vy, int damage) {
        this.texture = tex;
        this.texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        this.bounds = new Rectangle(x, y, 32, 32); // tamaño por defecto; ajústalo en la subclase
        this.vx = vx;
        this.vy = vy;
        this.damage = damage;
    }

    /** Lógica particular del patrón (mover, curvar, etc.). */
    public abstract void update(float dt, Rectangle arena);

    /** Dibujo genérico (puedes override si necesitas efectos). */
    public void draw(SpriteBatch batch) {
        if (!active) return;
        batch.draw(texture, bounds.x, bounds.y, bounds.width, bounds.height);
    }

    /** Colisión genérica con el jugador. */
    public boolean collidesWith(Rectangle r) {
        return active && bounds.overlaps(r);
    }

    public void deactivate() { active = false; }

    // Getters encapsulados
    public boolean isActive() { return active; }
    public int getDamage()   { return damage; }
    public Rectangle getBounds() { return bounds; }

    /** Opcional: si alguna subclase creó su propia textura, que la libere ahí. */
    public void dispose() {
        // si las Textures son compartidas y las gestiona otro lado, NO las dispongas aquí
    }
}
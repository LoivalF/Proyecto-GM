package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class PencilAttack extends Attack {

    public PencilAttack(Texture tex, float x, float y, float vx, float vy) {
        super(tex, x, y, vx, vy, 3); // daño = 3
        // más angosto que el papel
        bounds.width = 24;
        bounds.height = 48;
    }

    @Override
    public void update(float dt, Rectangle arena) {
        // Movimiento lineal recto (sin zig-zag)
        bounds.x += vx * dt;
        bounds.y += vy * dt;

        // Si sale de la arena, se desactiva
        if (!arena.overlaps(bounds)) {
            deactivate();
        }
    }
}
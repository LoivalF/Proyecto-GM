package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class PencilAttack extends Attack {

    public PencilAttack(Texture tex, float x, float y, float vx, float vy) {
        super(tex, x, y, vx, vy, 3);

        bounds.width = 24;
        bounds.height = 48;
    }

    @Override
    public void update(float dt, Rectangle arena) {
        // Movimiento recto
        bounds.x += vx * dt * speedFactor;
        bounds.y += vy * dt * speedFactor;

        // Ajusta hitbox según orientacion
        if (Math.abs(vx) > Math.abs(vy)) {
            // Movimiento más horizontal
            bounds.width = 64;
            bounds.height = 16;
        } else {
            // Movimiento más vertical
            bounds.width = 16;
            bounds.height = 64;
        }
        // Si sale de la arena, se desactiva
        if (!arena.overlaps(bounds)) {
            deactivate();
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (!active) return;

        // Calcula el ángulo de rotación según su velocidad
        float angle = MathUtils.atan2(vy, vx) * MathUtils.radiansToDegrees - 90f;

        // dimensiones visuales constantes
        float drawWidth = 20f;
        float drawHeight = 64f;

        // dibujo centrado a su hitbox
        float drawX = bounds.x + bounds.width / 2f - drawWidth / 2f;
        float drawY = bounds.y + bounds.height / 2f - drawHeight / 2f;

        batch.draw(texture,
                drawX, drawY,
                drawWidth / 2f, drawHeight / 2f,
                drawWidth, drawHeight,
                1f, 1f,
                angle,
                0, 0,
                texture.getWidth(), texture.getHeight(),
                false, false);
    }
}
package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class PaperAttack extends Attack {

    private float time; // para generar movimiento senoidal (zig-zag)

    public PaperAttack(Texture tex, float x, float y, float vx, float vy) {
        super(tex, x, y, vx, vy, 3); // daño = 3
        // ajustamos tamaño (más chico que la textura)
        bounds.width = 32;
        bounds.height = 32;
        time = 0;
    }

    @Override
    public void update(float dt, Rectangle arena) {
        time += dt;

        // Movimiento principal
        bounds.x += vx * dt;
        bounds.y += vy * dt;

        // Zig-zag horizontal usando seno (efecto papel flotando)
        bounds.x += MathUtils.sin(time * 6f) * 60f * dt;

        // Desactivar si sale del área de batalla
        if (!arena.overlaps(bounds)) {
            deactivate();
        }
    }
}
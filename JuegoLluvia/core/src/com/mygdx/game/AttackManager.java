package com.mygdx.game;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class AttackManager {
    private final Array<Attack> attacks = new Array<>();

    public void spawn(Attack a) { attacks.add(a); }

    public void update(float dt, Rectangle arena) {
        for (Attack a : attacks) if (a.isActive()) a.update(dt, arena);
    }

    public void draw(SpriteBatch batch) {
        for (Attack a : attacks) if (a.isActive()) a.draw(batch);
    }

    public void checkHit(Tarro player) {
        // Daño solo si no está herido (invulnerable) para evitar “multi-hit”
        if (player.estaHerido()) return;
        for (Attack a : attacks) {
            if (a.isActive() && a.collidesWith(player.getArea())) {
                player.recibirDanio(a.getDamage()); // ← nuevo método abajo
                a.deactivate();
                break; // un hit por frame
            }
        }
    }

    public void clearInactive() {
        for (int i = attacks.size - 1; i >= 0; --i)
            if (!attacks.get(i).isActive()) attacks.removeIndex(i);
    }

    public void clearAll() { attacks.clear(); }
}
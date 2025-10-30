package com.mygdx.game;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class AttackManager {
    private final Array<Attack> attacks = new Array<>();
    private float slowTimer = 0f;
    private float speedFactor = 1f;

    public void spawn(Attack a) { attacks.add(a); }

    public void update(float dt, Rectangle arena) {
        if (slowTimer > 0f) {
            slowTimer -= dt;
            if (slowTimer <= 0f) {
                speedFactor = 1f;
                for (Attack a : attacks) {
                    a.setSpeedFactor(1f);
                }
            }
        }

        for (Attack a : attacks) if (a.isActive()) a.update(dt, arena);
    }

    public void draw(SpriteBatch batch) {
        for (Attack a : attacks) if (a.isActive()) a.draw(batch);
    }

    public void checkHit(Tarro player) {
        // Daño solo si no está herido para evitar multihit
        if (player.estaHerido()) return;
        for (Attack a : attacks) {
            if (a.isActive() && a.collidesWith(player.getArea())) {
                player.recibirDanio(a.getDamage());
                a.deactivate();
                break; // un hit por frame
            }
        }
    }

    public void clearInactive() {
        for (int i = attacks.size - 1; i >= 0; --i)
            if (!attacks.get(i).isActive()) attacks.removeIndex(i);
    }
    public void slowAll(float factor, float duration) {
        slowTimer = duration;
        speedFactor = factor;

        for (Attack atk : attacks) {
            atk.vx *= factor;
            atk.vy *= factor;
        }
    }

    public float getSpeedFactor() {
        return speedFactor;
    }
}
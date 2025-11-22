package com.mygdx.game;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class AttackManager {
    //NUESTRO SINGLETON
    private static AttackManager instance;
    private AttackManager() { }
    public static AttackManager getInstance() {
        if (instance == null) {
            instance = new AttackManager();
        }
        return instance;
    }

    private final Array<AttackStrategy> attacks = new Array<>();
    private float slowTimer = 0f;
    private float speedFactor = 1f;

    public void spawn(AttackStrategy a) { attacks.add(a); }
    public void update(float dt, Rectangle arena) {
        if (slowTimer > 0f) {
            slowTimer -= dt;
            if (slowTimer <= 0f) {
                speedFactor = 1f;
                for (AttackStrategy a : attacks) {
                    a.setSpeedFactor(1f);
                }
            }
        }

        for (AttackStrategy a : attacks) if (a.isActive()) a.update(dt, arena);
    }
    public void draw(SpriteBatch batch) {
        for (AttackStrategy a : attacks) if (a.isActive()) a.draw(batch);
    }
    public void checkHit(Tarro player) {
        // Daño solo si no está herido para evitar multihit
        if (player.estaHerido()) return;
        for (AttackStrategy a : attacks) {
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

        for (AttackStrategy a : attacks) {
            a.setSpeedFactor(factor);
        }
    }
    public float getSpeedFactor() {
        return speedFactor;
    }
}
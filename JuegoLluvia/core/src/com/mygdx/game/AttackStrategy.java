package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public interface AttackStrategy {
    void update(float dt, Rectangle arena);
    void draw(SpriteBatch batch);
    boolean isActive();
    boolean collidesWith(Rectangle r);
    int getDamage();
    void deactivate();
    void setSpeedFactor(float m);

}

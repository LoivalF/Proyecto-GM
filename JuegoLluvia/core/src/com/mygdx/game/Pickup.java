package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public interface Pickup {
    void update(float dt);
    void draw(SpriteBatch batch);
    boolean isActive();
    void applyEffect(Tarro player);
    Rectangle getBounds();
}

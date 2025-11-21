package com.mygdx.game;

public abstract class BossTemplate {

    protected float timer = 0;
    protected int phase = 1;

    // METODO TEMPLATE
    public final void update(float dt) {
        timer += dt;

        seleccionarFase(timer);
        ejecutarFase(dt);
    }

    // METODOS BOSS
    protected abstract void seleccionarFase(float time);
    protected abstract void ejecutarFase(float dt);
}

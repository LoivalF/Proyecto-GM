package com.mygdx.game;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.MathUtils;

public class Boss {
    private float timer;
    private int phase = 1;
    private float spawnCooldown = 0f;
    private AttackManager manager;
    private Texture texPaper, texPencil;
    private Sound paperSound, pencilSound, beamSound;
    private Rectangle zona;

    public Boss(AttackManager mgr, Rectangle zona,
                Texture texPaper, Texture texPencil,
                Sound paperSound, Sound pencilSound, Sound beamSound) {
        this.manager = mgr;
        this.zona = zona;
        this.texPaper = texPaper;
        this.texPencil = texPencil;
        this.paperSound = paperSound;
        this.pencilSound = pencilSound;
        this.beamSound = beamSound;
    }

    private void spawnPattern(float pencilChance, float diagonalChance, float sideChance) {
        float x = MathUtils.random(zona.x + 32, zona.x + zona.width - 32);
        float y = zona.y + zona.height - 32;

        if (Math.random() < (1 - pencilChance)) {
            PaperAttack p = new PaperAttack(texPaper, x, y, 0, -250);
            p.setSpeedFactor(manager.getSpeedFactor());
            manager.spawn(p);
            paperSound.play(0.1f);
        } else {
            spawnLapices(x, y, diagonalChance, sideChance);
        }
    }

    private void spawnLapices(float x, float y, float diagonalChance, float sideChance) {
        float vx = 0, vy = -400;

        if (MathUtils.randomBoolean(diagonalChance)) {
            vx = MathUtils.randomSign() * 200;
            vy = -250;
        }

        if (MathUtils.randomBoolean(sideChance)) {
            boolean desdeIzq = MathUtils.randomBoolean();
            float ladoX = desdeIzq ? zona.x - 10f : zona.x + zona.width - 10f;
            float ladoY = MathUtils.random(zona.y + 40f, zona.y + zona.height - 40f);
            vx = desdeIzq ? 300f : -300f;
            vy = 0;
            PencilAttack p = new PencilAttack(texPencil, ladoX, ladoY, vx, vy);
            p.setSpeedFactor(manager.getSpeedFactor());
            manager.spawn(p);
        } else {
            PencilAttack p = new PencilAttack(texPencil, x, y, vx, vy);
            p.setSpeedFactor(manager.getSpeedFactor());
            manager.spawn(p);
        }

        pencilSound.play(0.1f);
    }

    private void phase1(float x, float y) {
        // Papeles
        PaperAttack p = new PaperAttack(texPaper, x, y, 0, -250);
        p.setSpeedFactor(manager.getSpeedFactor());
        manager.spawn(p);
        paperSound.play(0.1f);
    }

    private void phase2(float x, float y) {
        // Lapices y papeles
        spawnPattern(0.5f, 0.5f, 0.5f);
    }

    private void phase3(float x, float y) {
        // Lapices gigantes
        if (MathUtils.randomBoolean()) {
            PencilBeamAttack p = new PencilBeamAttack(texPencil, zona, x, 56f, 0.6f, 0.6f, 3);
            p.setSpeedFactor(manager.getSpeedFactor());
            manager.spawn(p);
            beamSound.play(0.1f);
        }

        // Lapices y papeles
        spawnPattern(0.65f, 0.66f, 0.33f);
    }

    public void update(float dt) {
        // Dificultad
        timer += dt;
        spawnCooldown -= dt*2.5f;

        // Actualizar fase según tiempo
        if (timer > 100) {
            phase = 0;
        } else if (timer > 70) {
            phase = 3;
        }  else if (timer > 65) {
            phase = 0;
        } else if (timer > 35) {
            phase = 2;
        } else if (timer > 30) {
          phase = 0;
        } else {
            phase = 1;
        }

        // Controlar cooldown
        if (spawnCooldown <= 0) {
            if (phase == 3) spawnCooldown = 0.7f;
            else if (phase == 2) spawnCooldown = 0.4f;
            else spawnCooldown = 0.5f;

            float x = MathUtils.random(zona.x + 32, zona.x + zona.width - 32);
            float y = zona.y + zona.height - 32;

            if (phase == 1) {
                phase1(x, y);
            } else if (phase == 2) {
                phase2(x, y);
            } else if (phase == 3) {
                phase3(x, y);
            }
        }
    }

    public boolean finished() {
        return timer >= 100;
    }
}


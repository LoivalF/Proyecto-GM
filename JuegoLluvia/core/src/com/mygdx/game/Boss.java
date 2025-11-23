package com.mygdx.game;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Timer;

public class Boss extends BossTemplate {

    private float spawnCooldown = 0f;
    private Texture texPaper, texPencil, texGoma;
    private Sound paperSound, pencilSound, beamSound;
    private Rectangle zona;

    private AttackManager manager = AttackManager.getInstance();
    private AttackSetFactory factory ;

    public Boss(Rectangle zona,
                Texture texPaper, Texture texPencil,  Texture texGoma,
                Sound paperSound, Sound pencilSound, Sound beamSound) {
        this.zona = zona;
        this.texPaper = texPaper;
        this.texPencil = texPencil;
        this.texGoma = texGoma;
        this.paperSound = paperSound;
        this.pencilSound = pencilSound;
        this.beamSound = beamSound;

        factory = new Fase1AttackFactory(texPaper, texGoma, texPencil, zona) ;
    }

    private void spawnPattern(float gomaChance, float diagonalChance, float sideChance) {
        float x = MathUtils.random(zona.x + 32, zona.x + zona.width - 32);
        float y = zona.y + zona.height - 32;

        if (Math.random() > (gomaChance)) {
            Attack p = factory.createPaperAttack(x, y, 0, -250);
            p.setSpeedFactor(manager.getSpeedFactor());
            manager.spawn(p);
            paperSound.play(0.1f);
        } else {
            spawnEraser(x, y, diagonalChance, sideChance);
        }
    }

    private void spawnEraser(float x, float y, float diagonalChance, float sideChance) {
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
            Attack p = factory.createEraserAttack(ladoX, ladoY, vx, vy) ;
            if (p != null){
                p.setSpeedFactor(manager.getSpeedFactor());
                manager.spawn(p);
            }
        } else {
            Attack p = factory.createEraserAttack(x, y, vx, vy);
            if (p != null){
                p.setSpeedFactor(manager.getSpeedFactor());
                manager.spawn(p);
            }
        }
        pencilSound.play(0.1f);
    }

    private void phase1(float x, float y) {
        // Papeles
        Attack p = factory.createPaperAttack(x, y, 0, -250);
        p.setSpeedFactor(manager.getSpeedFactor());
        manager.spawn(p);
        paperSound.play(0.1f);
    }

    private void phase2(float x, float y) {
        // Gomas y papeles
        spawnPattern(0.5f, 0.5f, 0.5f);
    }

    private void phase3(float x, float y) {
        // Lapices gigantes
        if (MathUtils.randomBoolean()) {
            Attack beam = factory.createPencilAttack(x);
            if (beam != null) {
                beam.setSpeedFactor(manager.getSpeedFactor());
                manager.spawn(beam);
            }
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    beamSound.play(0.5f);
                }
            }, 0.6f);
        }

        // Lapices y papeles
        spawnPattern(0.65f, 0.66f, 0.33f);
    }

    @Override
    protected void seleccionarFase(float time) {
        if (time > 100) { phase = 0; }
        else if (time > 70) {
            phase = 3;
            factory = new Fase3AttackFactory(texPaper, texGoma, texPencil, zona) ;
        }
        else if (time > 65) { phase = 0; }
        else if (time > 35) {
            phase = 2;
            factory = new Fase2AttackFactory(texPaper, texGoma, texPencil, zona) ;
        }
        else if (time > 30) { phase = 0; }
        else{
            phase = 1;
            factory = new Fase1AttackFactory(texPaper, texGoma, texPencil, zona) ;
        }
    }

    @Override
    protected void ejecutarFase(float dt) {
        spawnCooldown -= dt * 2.5f;

        if (spawnCooldown <= 0) {

            if (phase == 3) spawnCooldown = 0.7f;
            else if (phase == 2) spawnCooldown = 0.4f;
            else spawnCooldown = 0.5f;

            float x = MathUtils.random(zona.x + 32, zona.x + zona.width - 32);
            float y = zona.y + zona.height - 32;

            switch (phase) {
                case 1: phase1(x, y); break;
                case 2: phase2(x, y); break;
                case 3: phase3(x, y); break;
            }
        }
    }

    public void reset(){
        this.timer = 0f ;
        this. phase = 1 ;
        this.spawnCooldown = 0f ;
        this.factory = new Fase1AttackFactory(texPaper, texGoma, texPencil, zona) ;
    }

    public String getNombreFase(){
        switch(phase){
            case 1: return "Jugando... Fase 1" ;
            case 2: return "Jugando... Fase 2" ;
            case 3: return "Jugando... Fase 3" ;
            default: return "Preparando Fase" ;
        }
    }
}


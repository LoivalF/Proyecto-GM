package com.mygdx.game;


import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.MathUtils;

/**
 * Lápiz tipo "blaster": primero telegraph (warning), luego un rayo vertical que ocupa
 * toda la altura de la arena. El ancho del rayo define el área de daño.
 */
public class PencilBeamAttack extends Attack {

    private enum Phase { TELEGRAPH, BEAM, DONE }
    private Phase phase = Phase.TELEGRAPH;

    private final float telegraphTime;   // segundos (ej: 0.6)
    private final float beamTime;        // segundos (ej: 0.6)
    private float timer = 0f;

    private final float beamWidth;       // ancho de la franja
    private final Rectangle arenaRef;    // referencia para conocer alto/y
    private final Texture warnTex;       // pixel blanco para dibujar avisos

    // Para parpadeo del warning
    private float blinkT = 0f;

    /**
     * pencilTex  textura del lápiz (se estira verticalmente en fase BEAM)
     *  arena      rectángulo de la arena (posición y altura del rayo)
     * centerX    coordenada X central de la franja
     * beamWidth  ancho del rayo
     *  tTele      duración del aviso (s)
     * tBeam      duración del rayo (s)
     *  damage     daño aplicado mientras el rayo está activo
     */
    public PencilBeamAttack(Texture pencilTex,
                            Rectangle arena,
                            float centerX,
                            float beamWidth,
                            float tTele,
                            float tBeam,
                            int damage) {
        // NOTA: vx/vy no se usan; el rayo es estático
        super(pencilTex, centerX - beamWidth / 2f, arena.y, 0f, 0f, damage);

        this.arenaRef = arena;
        this.beamWidth = beamWidth;
        this.telegraphTime = tTele;
        this.beamTime = tBeam;

        // El rectángulo de colisión lo alineamos a la franja; en TELEGRAPH no hace daño
        bounds.width = beamWidth;
        bounds.height = arena.height;

        // Crear un pixel blanco para avisos
        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pm.setColor(1, 1, 1, 1);
        pm.fill();
        this.warnTex = new Texture(pm);
        pm.dispose();
    }

    @Override
    public void update(float dt, Rectangle arenaIgnored) {
        timer += dt;

        switch (phase) {
            case TELEGRAPH:
                blinkT += dt;
                if (timer >= telegraphTime) {
                    // Pasar a BEAM: ya hace daño
                    phase = Phase.BEAM;
                    timer = 0f;
                }
                break;

            case BEAM:
                if (timer >= beamTime) {
                    phase = Phase.DONE;
                    deactivate();
                }
                break;

            case DONE:
                // nada
                break;
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (phase == Phase.DONE) return;

        if (phase == Phase.TELEGRAPH) {
            // Franja de warning semitransparente con parpadeo
            float alpha = 0.35f + 0.25f * (0.5f * (1f + MathUtils.sin(blinkT * 10f)));
            batch.setColor(1f, 0.9f, 0f, alpha); // amarillento
            batch.draw(warnTex, bounds.x, arenaRef.y, beamWidth, arenaRef.height);
            batch.setColor(1f, 1f, 1f, 1f);
        } else if (phase == Phase.BEAM) {
            // Dibuja el "rayo" estirando la textura del lápiz a toda la altura
            batch.draw(texture, bounds.x, arenaRef.y, beamWidth, arenaRef.height);
        }
    }

    /** Durante TELEGRAPH no debe dañar. */
    @Override
    public boolean collidesWith(Rectangle r) {
        if (phase != Phase.BEAM) return false;
        return super.collidesWith(r);
    }

    @Override
    public void dispose() {
        // NO dispose() de 'texture' (la maneja quien la creó/compartió)
        warnTex.dispose();
    }
}
package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.MathUtils;
import com.mygdx.game.AttackManager;
import com.mygdx.game.PaperAttack;
import com.mygdx.game.PencilAttack;
import com.mygdx.game.PencilBeamAttack;



public class JuegoLluvia extends ApplicationAdapter {
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private BitmapFont font;

    private Tarro tarro;
    private Lluvia lluvia;
    private ShapeRenderer shapeRenderer;
    private Rectangle zona;// la “caja” donde se mueve el corazón
    private AttackManager attackMgr;
    private Texture texPaper, texPencil;
    private Sound paperSpawnSnd, pencilSpawnSnd, beamWarnSnd;
    private float spawnTimer;
    private float survived; // tiempo sobrevivido (segundos)
    private int score;

    @Override
    public void create () {
        font = new BitmapFont(); // use libGDX's default Arial font

        // load the images for the droplet and the bucket, 64x64 pixels each
        Sound hurtSound = Gdx.audio.newSound(Gdx.files.internal("damage.ogg"));
        tarro = new Tarro(new Texture(Gdx.files.internal("heartMouse.png")),hurtSound);


        // camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1080);
        batch = new SpriteBatch();
        // creacion del tarro
        shapeRenderer = new ShapeRenderer();
        // x, y, width, height (ajústalo a gusto)
        float zonaWidth = 800;   // ancho de la caja
        float zonaHeight = 400;  // alto de la caja
        float zonaX = (1920 - zonaWidth) / 2f;  // centrado horizontal
        float zonaY = (1080 - zonaHeight) / 2f - 210;
        zona = new Rectangle(zonaX, zonaY, zonaWidth, zonaHeight);
        tarro.crear();
        tarro.setZonaLimite(zona);
        // --- Manager de ataques ---
        attackMgr = new AttackManager();

        // --- Texturas compartidas de ataques ---
        texPaper  = new Texture(Gdx.files.internal("hojaarru.png"));
        texPencil = new Texture(Gdx.files.internal("lapiz.png"));
        texPaper.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        texPencil.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        // --- Sonidos de spawn ---
        paperSpawnSnd  = Gdx.audio.newSound(Gdx.files.internal("papel.ogg"));
        pencilSpawnSnd = Gdx.audio.newSound(Gdx.files.internal("warning.ogg"));
        beamWarnSnd    = Gdx.audio.newSound(Gdx.files.internal("wall.ogg"));

        // estado
        survived = 0f;
        score = 0;
        spawnTimer = 0f;
    }



    @Override
    public void render () {
        // Fondo
        ScreenUtils.clear(0, 0, 0, 1);
        camera.update();

        // --- Dibuja la caja (borde blanco) ---
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(zona.x, zona.y, zona.width, zona.height);
        shapeRenderer.end();

        // --- Lógica de juego ---
        float dt = Gdx.graphics.getDeltaTime();

        // Movimiento del jugador (si no está en “herido”/invulnerable)
        if (!tarro.estaHerido()) {
            tarro.actualizarMovimiento();
        }

        // Tiempo y puntaje
        survived += dt;
        score = (int)(survived * 10); // ejemplo: 10 puntos por segundo

        // --- Spawner simple (puedes reemplazar luego por Boss con fases) ---
        spawnTimer += dt;
        if (spawnTimer >= 0.24f) { // cada ~0.24s
            spawnTimer = 0f;

            float x = MathUtils.random(zona.x + 32, zona.x + zona.width - 32);
            float y = zona.y + zona.height - 32;

            double r = Math.random();
            if (r < 0.15) {
                // BLASTER (warning -> beam)
                attackMgr.spawn(new PencilBeamAttack(texPencil, zona, x, 56f, 0.6f, 0.6f, 3));
                beamWarnSnd.play(0.7f);
            } else if (r < 0.55) {
                // Lápiz recto y rápido
                attackMgr.spawn(new PencilAttack(texPencil, x, y, 0, -420f));
                pencilSpawnSnd.play(0.5f);
            } else {
                // Papel en zig-zag
                attackMgr.spawn(new PaperAttack(texPaper, x, y, 0, -260f));
                paperSpawnSnd.play(0.5f);
            }
        }

        // Actualizar ataques y colisiones
        attackMgr.update(dt, zona);
        attackMgr.checkHit(tarro);   // resta HP si hay choque
        attackMgr.clearInactive();   // limpia los que salieron de la arena

        // --- Dibujo de sprites y HUD ---
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // HUD
        font.draw(batch, "Tiempo: "  + (int)survived + "s", 20, 1060);
        font.draw(batch, "Puntaje: " + score,            20, 1020);
        font.draw(batch, "HP: " + tarro.getVidas() + "/20", 1720, 1060);

        // Jugador y ataques
        tarro.dibujar(batch);
        attackMgr.draw(batch);

        batch.end();

        // --- Estados de fin (opcional, muestra idea) ---
        // boolean victoria = survived >= 60f;
        // boolean derrota  = tarro.getVidas() <= 0;
        // if (victoria || derrota) { /* mostrar mensaje o reiniciar */ }
    }

    @Override
    public void dispose() {
        // jugador
        tarro.destruir();

        // ataques (texturas y sonidos compartidos)
        texPaper.dispose();
        texPencil.dispose();
        paperSpawnSnd.dispose();
        pencilSpawnSnd.dispose();
        beamWarnSnd.dispose();

        // render
        shapeRenderer.dispose();
        batch.dispose();
        font.dispose();
    }
}

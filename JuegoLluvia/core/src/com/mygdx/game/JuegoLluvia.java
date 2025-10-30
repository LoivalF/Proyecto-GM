package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class JuegoLluvia extends ApplicationAdapter {
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private BitmapFont font;

    private Tarro tarro;
    private Array<Pickup> pickups;

    private ShapeRenderer shapeRenderer;
    private Rectangle zona;// la “caja” donde se mueve el corazón
    private AttackManager attackMgr;
    private Texture texPaper, texPencil, texHealthPickup, texSlowPickup;
    private Sound paperSpawnSnd, pencilSpawnSnd, beamWarnSnd, healthSound;
    private Music bgMusic;
    private Boss boss;

    private float survived; // tiempo sobrevivido (segundos)
    private int freezeTimer = 0;
    private float pickupTimer = 0f;
    private boolean freezeActive = false;
    private int score;

    @Override
    public void create () {
        // Fuente personalizada
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/dete.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 32;
        parameter.color = Color.WHITE;
        font = generator.generateFont(parameter);
        generator.dispose();

        // Imagen y sonido corazon
        Sound hurtSound = Gdx.audio.newSound(Gdx.files.internal("sounds/damage.ogg"));
        tarro = new Tarro(new Texture(Gdx.files.internal("images/heartMouse.png")),hurtSound);


        // camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1080);
        batch = new SpriteBatch();
        // creacion del tarro
        shapeRenderer = new ShapeRenderer();
        // x, y, width, height
        float zonaWidth = 800;   // ancho de la caja
        float zonaHeight = 400;  // alto de la caja
        float zonaX = (1920 - zonaWidth) / 2f;  // centrado horizontal
        float zonaY = (1080 - zonaHeight) / 2f - 210;
        zona = new Rectangle(zonaX, zonaY, zonaWidth, zonaHeight);
        tarro.crear();
        tarro.setZonaLimite(zona);
        // Manager de ataques
        attackMgr = new AttackManager();

        // Pickups
        pickups = new Array<Pickup>();

        // Texturas de ataques y pickups
        texPaper  = new Texture(Gdx.files.internal("images/hojaarru.png"));
        texPencil = new Texture(Gdx.files.internal("images/lapiz.png"));
        texHealthPickup = new Texture(Gdx.files.internal("images/pinguinito.png"));
        texSlowPickup = new Texture(Gdx.files.internal("images/monster.png"));
        texPaper.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        texPencil.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        texHealthPickup.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        texSlowPickup.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        // Sonidos de spawn
        paperSpawnSnd  = Gdx.audio.newSound(Gdx.files.internal("sounds/papel.ogg"));
        pencilSpawnSnd = Gdx.audio.newSound(Gdx.files.internal("sounds/warning.ogg"));
        beamWarnSnd = Gdx.audio.newSound(Gdx.files.internal("sounds/wall.ogg"));

        // Pickups
        healthSound = Gdx.audio.newSound(Gdx.files.internal("sounds/heal.ogg"));


        // Musica
        bgMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/asgore.mp3"));
        bgMusic.setLooping(true);
        bgMusic.setVolume(0.5f);
        bgMusic.play();

        // estado
        survived = 0f;
        score = 0;

        boss = new Boss(attackMgr, zona, texPaper, texPencil,
                paperSpawnSnd, pencilSpawnSnd, beamWarnSnd);
    }



    @Override
    public void render () {
        // Fondo
        ScreenUtils.clear(0, 0, 0, 1);
        camera.update();

        // Dibuja la caja "arena"
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(zona.x, zona.y, zona.width, zona.height);
        shapeRenderer.end();

        // Lógica de juego
        float dt = Gdx.graphics.getDeltaTime();

        // Movimiento del jugador
        if (freezeTimer > 0) {
            freezeTimer -= dt;
        }
        else {
            if (!tarro.estaHerido()) {
                tarro.actualizarMovimiento();
            }

            // Tiempo y puntaje
            survived += dt;
            score = (int) (survived * 10); // 10 puntos por segundo

            // Boss y ataques
            boss.update(dt);
            attackMgr.update(dt, zona);
            attackMgr.checkHit(tarro);
            attackMgr.clearInactive();
        }

        // Pickups
        pickupTimer += dt;
        // Uno cada 5 seg
        if (score < 1000) {
            if (pickupTimer >= 5) {
                pickupTimer = 0;

                // Coordenadas random para los pickups
                float x = MathUtils.random(zona.x + 50, zona.x + zona.width - 50);
                float y = zona.y + zona.height - 50;

                // 50% probabilidad de ser pinguinito o monster
                if (MathUtils.randomBoolean()) {
                    pickups.add(new HealthPickup(texHealthPickup, x, y, healthSound));
                } else {
                    pickups.add(new SlowPickup(texSlowPickup, x, y, attackMgr));
                }
            }
        }

        // Pausa al ser herido
        if (tarro.estaHerido() && !freezeActive) {
            freezeTimer = tarro.getTiempoHeridoMax();
            freezeActive = true;
        }
        if (!tarro.estaHerido()) {
            freezeActive = false;
        }

        // Dibujo de sprites y HUD
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // HUD
        font.draw(batch, "HP: " + tarro.getVidas() + "/20", 560, 100);
        font.draw(batch, "Puntaje: " + score, 850, 100);
        font.draw(batch, "Tiempo: " + (int) survived + "s", 1175, 100);

        // Jugador y ataques
        tarro.dibujar(batch);
        attackMgr.draw(batch);

        // Pickups
        for (Pickup  p : pickups) {
            if (freezeTimer <= 0) {
                p.update(dt);
            }
            p.draw(batch);

            if (p.isActive() && freezeTimer <= 0 && p.getBounds().overlaps(tarro.getArea())) {
                p.applyEffect(tarro);
            }
        }

        // Eliminar pickups inactivos
        for (int i = pickups.size - 1; i >= 0; i--) {
            if (!pickups.get(i).isActive()) {
                pickups.removeIndex(i);
            }
        }

        batch.end();
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
        bgMusic.dispose();

        // render
        shapeRenderer.dispose();
        batch.dispose();
        font.dispose();
    }
}

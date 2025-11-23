package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import static com.badlogic.gdx.scenes.scene2d.ui.Table.Debug.table;

public class JuegoLluvia extends ApplicationAdapter {
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private BitmapFont font;

    private Tarro tarro;
    private Array<Pickup> pickups;

    private ShapeRenderer shapeRenderer;
    private Rectangle zona;// la “caja” donde se mueve el corazón
    private AttackManager attackMgr;
    private Texture texPaper, texPencil, texGoma, texHealthPickup, texSlowPickup;
    private Sound paperSpawnSnd, pencilSpawnSnd, beamWarnSnd, healthSound;
    private Music bgMusic;
    private Music bgMusicMenu ; //Musica del Menu solamente
    private Boss boss;

    private float survived; // tiempo sobrevivido (segundos)
    private int freezeTimer = 0;
    private float pickupTimer = 0f;
    private boolean freezeActive = false;
    private int score;

    public enum Estado {MENU, TUTORIAL, JUEGO, GAMEOVER} ;
    public Estado estado = Estado.MENU;
    private Stage stageMenu;
    private Stage stageTutorial;
    private Stage stageJuego ;
    private Stage stageGameOver ;
    private Skin skin;
    private TextButton btnInicio, btnTutorial, btnSalir, btnVolver, btnReintentar, btnMenu;
    private Texture teclasImg ;
    private Texture fondoMenu, fondoTutorial ;

    public static final float UI_WIDTH = 1920;
    public static final float UI_HEIGHT = 1080;


    @Override
    public void create() {
        // Fuente personalizada
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/dete.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 32;
        parameter.color = Color.WHITE;
        font = generator.generateFont(parameter);
        generator.dispose();

        // Imagen y sonido corazon
        Sound hurtSound = Gdx.audio.newSound(Gdx.files.internal("sounds/damage.ogg"));
        tarro = new Tarro(new Texture(Gdx.files.internal("images/heartMouse.png")), hurtSound);

        //fondo del menu
        fondoMenu = new Texture(Gdx.files.internal("images/menuFondo.png"));
        fondoTutorial = new Texture(Gdx.files.internal("images/fondoTutorial.jpeg"));

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
        attackMgr = AttackManager.getInstance();

        // Pickups
        pickups = new Array<Pickup>();

        // Texturas de ataques y pickups
        texPaper = new Texture(Gdx.files.internal("images/hojaarru.png"));
        texPencil = new Texture(Gdx.files.internal("images/lapiz.png"));
        texGoma = new Texture(Gdx.files.internal("images/goma.png")); //textura para segunda fase de ataque
        texHealthPickup = new Texture(Gdx.files.internal("images/pinguinito.png"));
        texSlowPickup = new Texture(Gdx.files.internal("images/monster.png"));
        texPaper.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        texPencil.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        texHealthPickup.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        texSlowPickup.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        // Sonidos de spawn
        paperSpawnSnd = Gdx.audio.newSound(Gdx.files.internal("sounds/papel.ogg"));
        pencilSpawnSnd = Gdx.audio.newSound(Gdx.files.internal("sounds/warning.ogg"));
        beamWarnSnd = Gdx.audio.newSound(Gdx.files.internal("sounds/wall.ogg"));

        // Pickups
        healthSound = Gdx.audio.newSound(Gdx.files.internal("sounds/heal.ogg"));

        //Musica del Menu
        bgMusicMenu = Gdx.audio.newMusic(Gdx.files.internal("sounds/menuMusic.mp3")) ;
        bgMusicMenu.setLooping(true) ;
        bgMusicMenu.setVolume(0.5f) ;
        bgMusicMenu.play() ;

        //Musica del Juego
        bgMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/asgore.mp3"));
        bgMusic.setLooping(true);
        bgMusic.setVolume(0.5f);

        // estado
        survived = 0f;
        score = 0;

        boss = new Boss(zona, texPaper, texPencil, texGoma, paperSpawnSnd, pencilSpawnSnd, beamWarnSnd);

        //Skin botones menu
        skin = new Skin(Gdx.files.internal("uiskin.json")) ;
        stageMenu = new Stage(new FitViewport(UI_WIDTH, UI_HEIGHT));
        Gdx.input.setInputProcessor(stageMenu) ;

        Table table = new Table();
        table.setFillParent(true);
        stageMenu.addActor(table);

        //titulo del juego
        Label titulo = new Label("PAPERTALE", skin) ;
        titulo.setFontScale(4f) ;
        titulo.setColor(Color.WHITE) ;
        table.add(titulo).padBottom(40).row();

        //botones
        btnInicio = new TextButton("Iniciar Juego", skin) ;
        btnInicio.getLabel().setFontScale(2f);
        btnTutorial = new TextButton("Tutorial", skin) ;
        btnTutorial.getLabel().setFontScale(2f);
        btnSalir = new TextButton("Salir", skin) ;
        btnSalir.getLabel().setFontScale(2f);

        table.add(btnInicio).width(400).height(90).pad(15).row();
        table.add(btnTutorial).width(400).height(90).pad(15).row();
        table.add(btnSalir).width(400).height(90).pad(15);

        btnInicio.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                if (bgMusicMenu.isPlaying()) bgMusicMenu.stop();

                estado = Estado.JUEGO;

                bgMusic.setPosition(0);
                bgMusic.play();
            }
        });

        btnTutorial.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                estado = Estado.TUTORIAL;
                Gdx.input.setInputProcessor(stageTutorial);
            }
        });
        btnSalir.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
        //Tutorial
        stageTutorial = new Stage(new FitViewport(UI_WIDTH, UI_HEIGHT));
        Table tutorialTable = new Table() ;
        tutorialTable.setFillParent(true) ;
        stageTutorial.addActor(tutorialTable) ;

        TextButton btnVolverTutorial = new TextButton("Volver", skin);
        btnVolverTutorial.setSize(200, 60); //BOTON EN EL TUTORIAL
        tutorialTable.row().expandY() ;
        tutorialTable.add(btnVolverTutorial).width(200).height(60).bottom().padBottom(50);
        btnVolverTutorial.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                estado = Estado.MENU;
                bgMusicMenu.setPosition(0);
                bgMusicMenu.play();
                Gdx.input.setInputProcessor(stageMenu);
            }
        });

        //BOTON VOLVER PARA CUANDO SE ESTÉ JUGANDO
        stageJuego = new Stage(new FitViewport(UI_WIDTH, UI_HEIGHT));
        btnVolver = new TextButton("Volver", skin) ;
        btnVolver.setSize(200, 60) ;
        btnVolver.setPosition(20, 20);
        btnVolver.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(bgMusic.isPlaying()) bgMusic.stop() ;
                estado = Estado.MENU ;
                bgMusicMenu.setPosition(0);
                bgMusicMenu.play() ;
                Gdx.input.setInputProcessor(stageMenu) ;
            }
        }) ;
        stageJuego.addActor((btnVolver));
        teclasImg = new Texture(Gdx.files.internal("images/teclas.png")) ;

        stageGameOver = new Stage(new FitViewport(UI_WIDTH, UI_HEIGHT));

        // Tabla
        Table tableGO = new Table();
        tableGO.setFillParent(true);
        stageGameOver.addActor(tableGO);

        // Botones
        btnReintentar = new TextButton("Reintentar", skin);
        btnReintentar.getLabel().setFontScale(2f);

        btnMenu = new TextButton("Volver", skin);
        btnMenu.getLabel().setFontScale(2f);

        tableGO.add(btnReintentar).width(400).height(90).pad(20).row();
        tableGO.add(btnMenu).width(400).height(90).pad(20);
        btnMenu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                estado = Estado.MENU;
                bgMusicMenu.setPosition(0);
                bgMusicMenu.play();
                Gdx.input.setInputProcessor(stageMenu);
            }
        }) ;
        btnReintentar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                survived = 0 ;
                score = 0 ;
                tarro.setVidas(20) ;
                pickups.clear();
                attackMgr.clearInactive() ;
                boss.reset();

                estado = Estado.JUEGO;
                bgMusic.setPosition(0);
                bgMusic.play();
                Gdx.input.setInputProcessor(stageJuego);
            }
        }) ;
    }

    private void renderTutorial(){
        ScreenUtils.clear(0, 0, 0, 1);

        batch.setProjectionMatrix(camera.combined) ;
        batch.begin() ;
        batch.draw(fondoTutorial,
                0, 0,
                stageMenu.getViewport().getWorldWidth(),
                stageMenu.getViewport().getWorldHeight()
        );

        font.getData().setScale(2f) ;
        font.draw(batch, "TUTORIAL DE CONTROLES", 1920 / 2f - 370, 950) ;
        font.getData().setScale(1.3f) ;
        font.draw(batch, "Usa las flechas para moverte en el escenario.\n"+"Esquiva los ataques y recoge mejoras.\n"+"Sobrevive el mayor tiempo posible", 1920 / 2f - 450, 800) ;

        float iw = teclasImg.getWidth() ;
        float ih = teclasImg.getHeight() ;
        float scale = 0.8f ;
        float dw = iw * scale ;
        float dh = ih * scale ;
        float x = 1920 /2f - dw / 2f ;
        float y = 200;

        batch.draw(teclasImg, x, y, dw, dh) ;
        batch.end() ;
        stageTutorial.act(Gdx.graphics.getDeltaTime()) ;
        stageTutorial.draw();
    }

    @Override
    public void render () {
        float dt = Gdx.graphics.getDeltaTime();

        switch(estado){
            case MENU:
                ScreenUtils.clear(0, 0, 0, 1) ;

                batch.setProjectionMatrix(camera.combined);
                batch.begin() ;
                batch.draw(fondoMenu,
                        0, 0,
                        Gdx.graphics.getWidth(),
                        Gdx.graphics.getHeight()
                );
                batch.end() ;

                stageMenu.act(dt) ;
                stageMenu.draw() ;
                break ;
            case TUTORIAL:
                renderTutorial();
                break ;
            case JUEGO:
                // Fondo
                ScreenUtils.clear(0, 0, 0, 1);
                camera.update();

                // Dibuja la caja "arena"
                shapeRenderer.setProjectionMatrix(camera.combined);
                shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
                shapeRenderer.setColor(Color.WHITE);
                shapeRenderer.rect(zona.x, zona.y, zona.width, zona.height);
                shapeRenderer.end();

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
                            pickups.add(new SlowPickup(texSlowPickup, x, y));
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
                font.draw(batch, boss.getNombreFase(), 100, 1000) ;

                if (tarro.getVidas() <= 0 || survived >= 120){ //Termina el juego si se acaban las vidas
                    estado = Estado.GAMEOVER;
                    if (bgMusic.isPlaying()) bgMusic.stop();
                    Gdx.input.setInputProcessor(stageGameOver);
                    batch.end() ;
                    return ;
                }

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
                stageJuego.act(dt) ;
                stageJuego.draw() ;
                break ;

            case GAMEOVER:
                ScreenUtils.clear(0, 0, 0, 1);
                batch.begin() ; //linea 435
                font.getData().setScale(4f);
                font.draw(batch, "GAME OVER", 635, 800);

                font.getData().setScale(1f);
                font.draw(batch, "Puntaje: " + score, 860, 400);
                font.draw(batch, "Presiona 'Volver' para regresar al menú", 620, 300);
                batch.end();

                stageGameOver.act(dt);
                stageGameOver.draw();
                break;
        }
    }

    @Override
    public void dispose() {
        // jugador
        tarro.destruir();

        // ataques (texturas y sonidos compartidos)
        texPaper.dispose();
        texPencil.dispose();
        texGoma.dispose() ;
        texHealthPickup.dispose();
        texSlowPickup.dispose();
        paperSpawnSnd.dispose();
        pencilSpawnSnd.dispose();
        beamWarnSnd.dispose();
        bgMusic.dispose();

        // render
        shapeRenderer.dispose();
        batch.dispose();
        font.dispose();

        //Menu
        stageMenu.dispose() ;
        skin.dispose() ;
        teclasImg.dispose() ;
        fondoMenu.dispose() ;
        fondoTutorial.dispose() ;
    }
}

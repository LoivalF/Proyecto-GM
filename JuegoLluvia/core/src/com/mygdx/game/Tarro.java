package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;


public class Tarro {
	   private Rectangle bucket;
	   private Texture bucketImage;
	   private Sound sonidoHerido;
	   private int vidas = 20;
	   private int puntos = 0;
	   private int velx = 400;
	   private boolean herido = false;
	   private int tiempoHeridoMax=50;
	   private int tiempoHerido;
       private Rectangle limite;
    public Tarro(Texture tex, Sound ss) {
		   bucketImage = tex;
           // Cambio que hicimos para suavizar el escalado
           bucketImage.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		   sonidoHerido = ss;
	   }
	   
		public int getVidas() {
			return vidas;
		}

		public int getPuntos() {
			return puntos;
		}
		public Rectangle getArea() {
			return bucket;
		}
		public void sumarPuntos(int pp) {
			puntos+=pp;
		}
        // Setter Zona Rectangular
        public void setZonaLimite(Rectangle zona) { this.limite = zona; }
	
	   public void crear() {
           bucket = new Rectangle();

           // Tamaño visible + colisión del corazón
           bucket.width  = 48;   // ajusta a gusto (32, 48, 64…)
           bucket.height = 48;

           // Centrar en pantalla actual (1920x1080 si tu camera usa eso)
           float worldW = Gdx.graphics.getWidth();
           float worldH = Gdx.graphics.getHeight();
           bucket.x = worldW / 2f - bucket.width / 2f;
           bucket.y = 60; // altura inicial; cámbiala si usas zona (p.ej. zona.y + 20)
	   }
        // agrega esto en Tarro
        public void recibirDanio(int cantidad) {
            vidas = Math.max(0, vidas - cantidad); // 'vidas' = HP total (20)
            herido = true;
            tiempoHerido = tiempoHeridoMax;
            if (sonidoHerido != null) sonidoHerido.play();
        }

        // Mantén el antiguo si quieres compatibilidad:
        public void dañar() { recibirDanio(1); }

    public void dibujar(SpriteBatch batch) {
		 if (!herido)  
		   batch.draw(bucketImage, bucket.x, bucket.y, bucket.width, bucket.height);
		 else {
		
		   batch.draw(bucketImage, bucket.x, bucket.y + MathUtils.random(-5, 5), bucket.width, bucket.height);
		   tiempoHerido--;
		   if (tiempoHerido<=0) herido = false;
		 }
	   } 
	   
	   
	   public void actualizarMovimiento() { 
		   // movimiento desde mouse/touch
		   /*if(Gdx.input.isTouched()) {
			      Vector3 touchPos = new Vector3();
			      touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			      camera.unproject(touchPos);
			      bucket.x = touchPos.x - 64 / 2;
			}*/
		   //movimiento desde teclado
		   if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) bucket.x -= velx * Gdx.graphics.getDeltaTime();
		   if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) bucket.x += velx * Gdx.graphics.getDeltaTime();
           if(Gdx.input.isKeyPressed(Input.Keys.UP)) bucket.y += velx * Gdx.graphics.getDeltaTime();
           if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) bucket.y -= velx * Gdx.graphics.getDeltaTime();
           clamp();
	   }
    private void clamp() {
        if (limite != null) {
            // Limita dentro de la caja Undertale
            float minX = limite.x;
            float minY = limite.y;
            float maxX = limite.x + limite.width  - bucket.width;
            float maxY = limite.y + limite.height - bucket.height;

            if (bucket.x < minX) bucket.x = minX;
            if (bucket.y < minY) bucket.y = minY;
            if (bucket.x > maxX) bucket.x = maxX;
            if (bucket.y > maxY) bucket.y = maxY;
        } else {
            // Limita a los bordes de la “pantalla” del mundo actual
            float worldW = Gdx.graphics.getWidth();
            float worldH = Gdx.graphics.getHeight();

            if (bucket.x < 0) bucket.x = 0;
            if (bucket.y < 0) bucket.y = 0;
            if (bucket.x > worldW - bucket.width)  bucket.x = worldW - bucket.width;
            if (bucket.y > worldH - bucket.height) bucket.y = worldH - bucket.height;
        }
    }


    public void destruir() {
		    bucketImage.dispose();
	   }
	
   public boolean estaHerido() {
	   return herido;
   }
	   
}

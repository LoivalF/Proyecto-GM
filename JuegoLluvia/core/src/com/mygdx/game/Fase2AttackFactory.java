package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import java.util.* ;

public class Fase2AttackFactory extends AbstracAttackFactory{
    public Fase2AttackFactory(Texture texPaper, Texture texGoma, Texture texPencil, Rectangle arena){
        super(texPaper, texGoma, texPencil, arena);
    }
    @Override
    public Attack createPaperAttack(float x, float y, float vx, float vy) {
        return new PaperAttack(texPaper, x, y, vx, vy);
    }

    @Override
    public Attack createEraserAttack(float x, float y, float vx, float vy) {
        return new EraserAttack(texGoma, x, y, vx, vy);
    }

    @Override
    public List<Attack> createAttackSet(float x, float y) {
        List<Attack> list = new ArrayList<>();
        list.add(createPaperAttack(x , y , -250 , 0 ));
        float vy = (float)(Math.random() * 250 -125);
        list.add(createEraserAttack(x, y, -300, vy)) ;
        return list;
    }
}

package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class Fase1AttackFactory extends AbstracAttackFactory{
    public Fase1AttackFactory(Texture texPaper, Texture texGoma, Texture texPencil, Rectangle arena){
        super(texPaper, texGoma, texPencil, arena);
    }
    @Override
    public Attack createPaperAttack(float x, float y, float vx, float vy) {
        return new PaperAttack(texPaper,x , y, vx, vy) ;
    }

    @Override
    public Attack createEraserAttack(float x, float y, float vx, float vy) {
        return null;
    }

    @Override
    public Attack createPencilAttack(float x) {
        return null;
    }

    @Override
    public List<Attack> createAttackSet(float x, float y) {
        List<Attack> list = new ArrayList<>();
        list.add(createPaperAttack(x , y , -250 , 0 ));
        return list ;
    }
}

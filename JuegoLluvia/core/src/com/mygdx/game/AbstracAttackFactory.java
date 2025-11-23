package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

import java.awt.*;

public abstract class AbstracAttackFactory implements AttackSetFactory{
    protected Texture texPaper ;
    protected Texture texPencil ;
    protected Texture texGoma ;
    protected Rectangle arena ;

    public AbstracAttackFactory(Texture texPaper, Texture texGoma, Texture texPencil, Rectangle arena){
        this.texPaper = texPaper ;
        this.texPencil = texPencil ;
        this.texGoma = texGoma ;
        this.arena = arena ;
    }
    @Override
    public Attack createPencilAttack(float x){
        float centerX = x ;
        return new PencilBeamAttack(texPencil, arena, centerX, 40f,1.0f, 1.5f, 15) ;
    }
}

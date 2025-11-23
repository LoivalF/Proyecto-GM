package com.mygdx.game;

import java.util.List;

public interface AttackSetFactory {
    Attack createPaperAttack(float x, float y, float vx, float vy) ;
    Attack createEraserAttack(float x, float y, float  vx, float vy) ; //Goma para la fase 2
    Attack createPencilAttack(float x) ;
    List<Attack> createAttackSet(float x, float y) ;
}

package com.monobogdan.allzombiesarebastards.z2d;

import java.util.ArrayList;

public class DecalSystem {
    static final int MAX_DECALS = 32;

    class Decal {
        public Sprite Sprite;
        public float X, Y;
        public float LifeTime;
    }

    private ArrayList<Decal> decals;
    private int decalCount;

    public DecalSystem() {
        decals = new ArrayList<>(MAX_DECALS);
    }

    public void addDecal(Sprite sprite, float x, float y, float lifeTime) {
        if(sprite != null && decalCount < MAX_DECALS) {
            Decal decal = new Decal();
            decal.Sprite = sprite;
            decal.X = x;
            decal.Y = y;
            decal.LifeTime = lifeTime;

            decals.add(decal);
            decalCount++;
        }
    }

    public void update() {
        for(Decal decal : decals) {
            decal.LifeTime -= 0.1f;
        }
    }

    public void draw() {
        for (Decal decal :
             decals) {
            //Engine.Current.Graphics.drawSprite(decal.Sprite, decal.X, decal.Y, Graphics2D.Color.White);
        }
    }
}

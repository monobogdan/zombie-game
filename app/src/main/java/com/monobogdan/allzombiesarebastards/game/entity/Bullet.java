package com.monobogdan.allzombiesarebastards.game.entity;

import com.monobogdan.allzombiesarebastards.game.Entity;
import com.monobogdan.allzombiesarebastards.game.Game;
import com.monobogdan.allzombiesarebastards.z2d.Engine;
import com.monobogdan.allzombiesarebastards.z2d.Graphics2D;
import com.monobogdan.allzombiesarebastards.z2d.Sprite;

public class Bullet extends Entity {
    public static Sprite Drawable;

    public float Speed;
    public float LifeTime;
    public float Damage;

    private static void precache() {
        if(Drawable == null)
            Drawable = Sprite.load("bullet.png");
    }

    public Bullet() {
        precache();
    }

    @Override
    public void draw() {
        super.draw();

        X += ForwardX * Speed;
        Y += ForwardY * Speed;
        LifeTime -= Engine.Current.DeltaTime;

        if(LifeTime < 0)
            Game.current.World.despawn(this);

        for(Entity ent : Game.current.World.Entities) {
            if(Zombie.class.isAssignableFrom(ent.getClass())) {
                Zombie zombie = (Zombie) ent;

                if(AABBTest(ent, Drawable.Width, Drawable.Height, zombie.Drawable.Width, zombie.Drawable.Height)) {
                    zombie.Health -= Damage;

                    Game.current.World.despawn(this);
                }
            }

        }

        Engine.Current.Graphics.drawSprite(Drawable, X, Y, 0, 0, 0, Rotation, Graphics2D.Color.White);
    }
}

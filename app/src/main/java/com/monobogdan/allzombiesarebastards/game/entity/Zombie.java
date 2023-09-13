package com.monobogdan.allzombiesarebastards.game.entity;

import com.monobogdan.allzombiesarebastards.game.Entity;
import com.monobogdan.allzombiesarebastards.game.Game;
import com.monobogdan.allzombiesarebastards.z2d.Engine;
import com.monobogdan.allzombiesarebastards.z2d.Graphics2D;
import com.monobogdan.allzombiesarebastards.z2d.Sprite;

public abstract class Zombie extends Entity {
    static float REACTION_TIME = 0.4f; // Time to react on player moves

    public Sprite Drawable;
    protected float Damage;
    protected float MaxHealth;

    public float Health;
    private float reactionTime;

    protected void attackPlayer(float dmg) {

    }

    protected void rotateTowardsEntity(Entity ent) {
        if(ent != null) {
            float ptfX = X - ent.X;
            float ptfY = Y - ent.Y;

            Rotation = (float) Math.toDegrees(Math.atan2(-ptfX, ptfY));
        }

        reactionTime -= Engine.Current.DeltaTime;
    }

    protected void moveForward(float speed) {
        X += ForwardX * speed;
        Y += ForwardY * speed;
    }

    @Override
    public void update() {
        super.update();

        if(Health <= 0) {
            Game.current.World.despawn(this);

        }
    }

    @Override
    public void draw() {
        super.draw();

        Engine.Current.Graphics.drawSprite(Drawable, X - 5, Y - 5, Drawable.Width + 10, Drawable.Height + 10, 1.0f, Rotation, Graphics2D.Color.Gray);
        Engine.Current.Graphics.drawSprite(Drawable, X, Y, 0, 0, 0, Rotation, Graphics2D.Color.White);
    }
}

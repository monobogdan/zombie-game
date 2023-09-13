package com.monobogdan.allzombiesarebastards.game.entity;

import com.monobogdan.allzombiesarebastards.game.Game;
import com.monobogdan.allzombiesarebastards.z2d.Engine;
import com.monobogdan.allzombiesarebastards.z2d.Sprite;

public class Crawler extends Zombie {
    static float WALK_SPEED = 65.0f;

    private static com.monobogdan.allzombiesarebastards.z2d.Sprite pSprite;

    private static void precache() {
        if(pSprite == null)
            pSprite = Sprite.load("z_crawler.png");
    }

    public Crawler() {
        precache();

        Drawable = pSprite;

        MaxHealth = 60;
        Health = MaxHealth;
    }

    @Override
    public void update() {
        super.update();

        Player player = Game.current.World.Player;
        rotateTowardsEntity(player);

        if(distanceTo(player.X, player.Y) > 35)
            moveForward(WALK_SPEED * Engine.Current.DeltaTime);
    }
}

package com.monobogdan.allzombiesarebastards.game.entity;

import com.monobogdan.allzombiesarebastards.game.Entity;
import com.monobogdan.allzombiesarebastards.game.Game;
import com.monobogdan.allzombiesarebastards.game.Gun;
import com.monobogdan.allzombiesarebastards.game.ui.HUD;
import com.monobogdan.allzombiesarebastards.game.ui.Joystick;
import com.monobogdan.allzombiesarebastards.z2d.Engine;
import com.monobogdan.allzombiesarebastards.z2d.Graphics2D;
import com.monobogdan.allzombiesarebastards.z2d.Input;
import com.monobogdan.allzombiesarebastards.z2d.Sprite;

import java.util.ArrayList;
import java.util.List;

public class Player extends Entity {
    public static class GunItem {
        public com.monobogdan.allzombiesarebastards.game.Gun Gun;
        public int Ammo;

        public GunItem(Gun gun, int ammo) {
            Gun = gun;
            Ammo = ammo;
        }
    }

    public static final int SPRITE_HANDGUN = 0;
    public static final int SPRITE_AUTO = 1;
    public static final int SPRITE_SHOTGUN = 2;
    public static final int SPRITE_MACHINEGUN = 3;
    public static final int SPRITE_SMG = 4;
    public static final int SPRITE_MAX = 5;

    final int WALK_SPEED = 75;

    private HUD hudRenderer;
    private Sprite[] sprites;

    public List<GunItem> Guns;
    private int EquippedGun;

    private Joystick joyInput;
    private float aimX, aimY;
    private float targetRot;
    private float nextAttack;

    public Player() {
        sprites = new Sprite[SPRITE_MAX];

        hudRenderer = new HUD();

        joyInput = new Joystick();
        Gun.precacheAudio();
        sprites[SPRITE_HANDGUN] = Sprite.load("player_pistol.png");

        Guns = new ArrayList<>();
        equip(Gun.GunDescription[0]);
    }

    public void equip(Gun gun) {
        GunItem item = new GunItem(gun, gun.MaxAmmo);
        Guns.add(item);

        EquippedGun = Guns.indexOf(item);
    }

    float lerp(float a, float b, float f)
    {
        return a * (1.0f - f) + (b * f);
    }

    @Override
    public void update() {
        super.update();

        joyInput.update();

        float inpX = joyInput.VelocityX;
        float inpY = joyInput.VelocityY;

        if(Engine.Current.Input.Gamepad.Buttons[Input.GAMEPAD_DPAD_LEFT]) {
            inpX = -1;
            Rotation = 270;
        }

        if(Engine.Current.Input.Gamepad.Buttons[Input.GAMEPAD_DPAD_RIGHT]) {
            inpX = 1;
            Rotation = 90;
        }

        if(Engine.Current.Input.Gamepad.Buttons[Input.GAMEPAD_DPAD_DOWN]) {
            inpY = 1;
            Rotation = 180;
        }

        if(Engine.Current.Input.Gamepad.Buttons[Input.GAMEPAD_DPAD_UP]) {
            inpY = -1;
            Rotation = 0;
        }

        X += inpX * (WALK_SPEED * Engine.Current.DeltaTime);
        Y += inpY * (WALK_SPEED * Engine.Current.DeltaTime);

        Engine.Current.Graphics.Camera.X = X - (Engine.Current.Graphics.ViewWidth / 2);
        Engine.Current.Graphics.Camera.Y = Y - (Engine.Current.Graphics.ViewHeight / 2);

        int finger = 0;
        if((finger = Engine.Current.Input.isTouchingZone(0, 0, Engine.Current.Graphics.ViewWidth, Engine.Current.Graphics.ViewHeight)) != -1) {
            Input.TouchState state = Engine.Current.Input.Touches[finger];

            aimX = state.X;
            aimY = state.Y;

            // Convert player position from world-space, to screen-space
            // Constant values are kind of hack to make scope on sprite follow real finger position
            float ptfX = (X - Engine.Current.Graphics.Camera.X) - state.X;
            float ptfY = (Y - Engine.Current.Graphics.Camera.Y) - state.Y;

            Rotation = (float)Math.toDegrees(Math.atan2(-ptfX, ptfY));
            recalculateForward();

            if(nextAttack < 0) {
                GunItem currGun = Guns.get(EquippedGun);
                currGun.Gun.FireEffect.createInstance().play();
                nextAttack = currGun.Gun.Speed;

                Bullet bullet = new Bullet();
                bullet.Speed = 15;
                bullet.LifeTime = 3.0f;
                bullet.Rotation = Rotation;
                bullet.Damage = currGun.Gun.Damage;

                float bullX = sprites[currGun.Gun.Sprite].Width / 2;
                float bullY = sprites[currGun.Gun.Sprite].Height / 2;
                float fwXFactor = ForwardX * 19;
                float fwYFactor = ForwardY * 19;

                bullet.X = X + bullX - (Bullet.Drawable.Width / 2) + fwXFactor;
                bullet.Y = Y + bullY - (Bullet.Drawable.Height / 2) + fwYFactor;

                Game.current.World.spawn(bullet);
            }
        }
        nextAttack -= Engine.Current.DeltaTime;
    }

    @Override
    public void draw() {
        super.draw();

        GunItem item = Guns.get(EquippedGun);

        float screenX = X - Engine.Current.Graphics.Camera.X + (sprites[item.Gun.Sprite].Width / 2);
        float screenY = Y - Engine.Current.Graphics.Camera.Y + (sprites[item.Gun.Sprite].Height / 2);

        Engine.Current.Graphics.drawLine(screenX, screenY, screenX + ForwardX * 250, screenY + ForwardY * 250, Graphics2D.Color.Red);

        Engine.Current.Graphics.drawSprite(sprites[item.Gun.Sprite], X - 5, Y - 5, sprites[item.Gun.Sprite].Width + 10, sprites[item.Gun.Sprite].Height + 10, 1.0f, Rotation, Graphics2D.Color.Gray);
        Engine.Current.Graphics.drawSprite(sprites[item.Gun.Sprite], X, Y, 1.0f, Rotation, Graphics2D.Color.White);

        hudRenderer.draw();
        joyInput.draw();
    }
}

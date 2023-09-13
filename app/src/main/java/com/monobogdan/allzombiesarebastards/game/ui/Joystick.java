package com.monobogdan.allzombiesarebastards.game.ui;

import android.text.method.Touch;

import com.monobogdan.allzombiesarebastards.z2d.Engine;
import com.monobogdan.allzombiesarebastards.z2d.GUI;
import com.monobogdan.allzombiesarebastards.z2d.Graphics2D;
import com.monobogdan.allzombiesarebastards.z2d.Input;
import com.monobogdan.allzombiesarebastards.z2d.Sprite;

public class Joystick {
    private Sprite joySprite;

    public float VelocityX;
    public float VelocityY;

    public float OriginX, OriginY;
    private float fingerX, fingerY;
    private int joyFinger;

    public Joystick() {
        joySprite = Sprite.load("ui_button.png");

        OriginX = -999;
        OriginY = -999;
    }

    private float clamp(float a, float min, float max) {
        return a < min ? min : (a > max ? max : a);
    }

    public void update() {
        int finger = 0;

        if((finger = Engine.Current.Input.isTouchingZone(0, 0, Engine.Current.Graphics.ViewWidth, Engine.Current.Graphics.ViewHeight)) != -1) {
            if(OriginX == -999) {
                OriginX = Engine.Current.Input.Touches[finger].X;
                OriginY = Engine.Current.Input.Touches[finger].Y;
            }

            float xdiff = (Engine.Current.Input.Touches[finger].X - OriginX) / Engine.Current.Graphics.ViewWidth;
            float ydiff = (Engine.Current.Input.Touches[finger].Y - OriginY) / Engine.Current.Graphics.ViewHeight;

            VelocityX = clamp(xdiff / 0.2f, -1, 1);
            VelocityY = clamp(ydiff / 0.2f, -1, 1);
        } else {
            OriginX = -999;
            OriginY = -999;
        }
    }

    public void draw() {
        VelocityX = 0;
        VelocityY = 0;
    }
}

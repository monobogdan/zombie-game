package com.monobogdan.allzombiesarebastards.z2d;

public class GUI {
    public static boolean GrabbedFocusInCurrentFrame;

    static void prepare() {
        GrabbedFocusInCurrentFrame = false;
    }

    public static boolean button(float x, float y, Sprite sprite) {
        return false;
    }

    public static boolean repeatButton(float x, float y, Sprite sprite) {
        if(sprite != null) {
            boolean state = false;

            if(Engine.Current.Input.isAnyFingerInZone(x, y, sprite.Width, sprite.Height)) {
                GrabbedFocusInCurrentFrame = true;
                state = true;
            }

            Graphics2D.Color col = state ? Graphics2D.Color.Green : Graphics2D.Color.White;
            //Engine.Current.Graphics.drawSprite(sprite, x, y, col);

            return state;
        }

        return false;
    }

    public static void textField() {

    }
}

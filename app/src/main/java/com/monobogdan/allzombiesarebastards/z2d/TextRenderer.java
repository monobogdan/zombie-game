package com.monobogdan.allzombiesarebastards.z2d;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.fonts.Font;
import android.view.Display;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class TextRenderer {
    // Simple & straightforward monospaced text renderer implementation
    private Sprite[] texture;

    TextRenderer() {
        texture = new Sprite[255];

        loadFont("Courier");
    }

    void loadFont(String fileName) {
        InputStream is = null;
        try {
            is = Engine.Current.MainActivity.getAssets().open("sprites/" + fileName);
            byte[] data = new byte[is.available()];
            is.read(data);
            ByteBuffer buf = ByteBuffer.wrap(data);
            buf.order(ByteOrder.LITTLE_ENDIAN);

            int glyphSize = buf.getInt();
            int pos = buf.position();
            for(int i = 0; i < 255; i++) {
                buf.position(pos);
                Engine.log("Size %d", data.length);
                texture[i] = new Sprite();
                texture[i].upload(buf, glyphSize, glyphSize, Sprite.FORMAT_RGBA);
                pos += glyphSize * glyphSize * 4;
            }

        } catch (IOException e) {
            Engine.log("Failed to load sprite %s", fileName);

            throw new RuntimeException(e);
        }
    }

    void resizeBackBuffer(int w, int h) {
        Engine.log("Allocated %dx%d backbuffer for text rendering", w, h);
    }

    void clear() {

    }

    public void drawString(String str, float x, float y, Graphics2D.Color col) {
        /*for(int i = 0; i < str.length(); i++) {
            Engine.Current.Graphics.drawSprite(texture[(byte)str.charAt(i)], Engine.Current.Graphics.Camera.X + (i * (16 - 4)),
                    Engine.Current.Graphics.Camera.Y + y, 0, Graphics2D.Color.White);
        }*/
    }

    void flip() {

    }
}

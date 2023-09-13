package com.monobogdan.allzombiesarebastards.z2d;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES10;
import android.opengl.GLES10Ext;
import android.opengl.GLES11;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class Sprite {
    static int[] NPOTWrapTable = { 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192, 16384 }; // TODO: Auto resize NPOT textures to their POT counterparts.

    public static final int FORMAT_RGB = 0;
    public static final int FORMAT_RGBA = 1;

    private int[] texBuffer;

    public int TextureId;
    public int Width, Height;

    public Sprite() {
        texBuffer = new int[1];

        Engine.log("Allocating texture");
        GLES10.glGenTextures(1, texBuffer, 0);
        TextureId = texBuffer[0];
    }

    public void upload(ByteBuffer data, int width, int height, int format) {
        if(data != null) {
            int len = data.capacity();

            GLES10.glEnable(GLES10.GL_TEXTURE_2D);
            GLES10.glBindTexture(GLES10.GL_TEXTURE_2D, TextureId);
            GLES10.glTexImage2D(GLES10.GL_TEXTURE_2D, 0, GLES10.GL_RGBA, width, height, 0, GLES10.GL_RGBA, GLES10.GL_UNSIGNED_BYTE, data);
            GLES11.glTexParameteri(GLES10.GL_TEXTURE_2D, GLES10.GL_TEXTURE_MIN_FILTER, GLES10.GL_NEAREST);
            GLES11.glTexParameteri(GLES10.GL_TEXTURE_2D, GLES10.GL_TEXTURE_MAG_FILTER, GLES10.GL_NEAREST);

            Width = width;
            Height = height;
        }
    }

    public static Sprite load(String fileName) {
        InputStream is = null;
        try {
            is = Engine.Current.MainActivity.getAssets().open("sprites/" + fileName);

            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inPreferredConfig = Bitmap.Config.ARGB_8888;

            Bitmap bmp = BitmapFactory.decodeStream(is, null, opts);
            ByteBuffer buf = ByteBuffer.allocateDirect(bmp.getRowBytes() * bmp.getHeight());
            bmp.copyPixelsToBuffer(buf);
            bmp.recycle();
            buf.rewind();

            Sprite ret = new Sprite();
            ret.upload(buf, bmp.getWidth(), bmp.getHeight(), FORMAT_RGBA);

            return ret;
        } catch (IOException e) {
            Engine.log("Failed to load sprite %s", fileName);

            throw new RuntimeException(e);
        }
    }
}

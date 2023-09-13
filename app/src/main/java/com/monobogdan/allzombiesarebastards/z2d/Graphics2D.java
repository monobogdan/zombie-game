package com.monobogdan.allzombiesarebastards.z2d;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.opengl.GLES10;
import android.opengl.GLES11;
import android.opengl.GLES11Ext;
import android.opengl.GLSurfaceView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public final class Graphics2D {

    public static class Color {
        public static final Color Black = new Color(0, 0 , 0, 1);
        public static final Color White = new Color(1, 1, 1, 1);
        public static final Color Red = new Color(1, 0, 0, 1);
        public static final Color Blue = new Color(0, 0, 1, 1);
        public static final Color Green = new Color(0, 1, 0, 1);
        public static final Color Gray = new Color(0.8f, 0.8f, 0.8f, 0.3f);

        public float R, G, B, A;

        public Color(float r, float g, float b, float a) {
            R = r;
            G = g;
            B = b;
            A = a;
        }
    }

    public class Camera {
        class DistanceAdjust {
            public int Width;
            public int Height;
            public float Distance;

            public DistanceAdjust(int w, int h, float dist) {
                Width = w;
                Height = h;
                Distance = dist;
            }
        }

        public float Distance;
        public float X;
        public float Y;

        public Camera() {

        }

        public void autoAdjustDistance(int w, int h) {
            w = w / 240;
            h = h / 320;

            Distance = (w + h) / 2;
        }
    }

    public static class StaticBatch {
        ByteBuffer VertexBuffer;
        ByteBuffer ColorBuffer;
        ByteBuffer UVBuffer;
        Sprite Sprite;

        public int NumVertices;

        public StaticBatch(Sprite sprite, int numSprites) {
            NumVertices = numSprites * 6;
            VertexBuffer = ByteBuffer.allocateDirect((4 * 8) * NumVertices);
            ColorBuffer = ByteBuffer.allocateDirect((4 * 16) * NumVertices);
            UVBuffer = ByteBuffer.allocateDirect((4 * 8) * NumVertices);
            VertexBuffer.order(ByteOrder.LITTLE_ENDIAN);
            ColorBuffer.order(ByteOrder.LITTLE_ENDIAN);
            UVBuffer.order(ByteOrder.LITTLE_ENDIAN);

            Sprite = sprite;
            Engine.log("Allocating static batch for %d sprites", numSprites);
        }

        public void clear() {
            VertexBuffer.clear();
            ColorBuffer.clear();
            UVBuffer.clear();
        }

        private void vertex(float x, float y, float u, float v, Color col) {
            VertexBuffer.putFloat(x);
            VertexBuffer.putFloat(y);
            ColorBuffer.putFloat(col.R);
            ColorBuffer.putFloat(col.G);
            ColorBuffer.putFloat(col.B);
            ColorBuffer.putFloat(col.A);
            UVBuffer.putFloat(u);
            UVBuffer.putFloat(v);
        }

        public void addInstance(float x, float y, Color col) {
            vertex(x, y, 0, 0, Color.White);
            vertex(x + Sprite.Width, y, 1, 0, Color.White);
            vertex(x + Sprite.Width, y + Sprite.Height, 1, 1, Color.White);
            vertex(x, y, 0, 0, Color.White);
            vertex(x, y + Sprite.Height, 0, 1, Color.White);
            vertex(x + Sprite.Width, y + Sprite.Height, 1, 1, Color.White);
        }

        public void prepare() {
            VertexBuffer.rewind();
            ColorBuffer.rewind();
            UVBuffer.rewind();
        }
    }

    public class Statistics {
        public int DrawCalls;
        public int OccludedDraws;
        public int FrameTimeMS;
        public int FPS;
        public int TextureMemorySize;

        private float nextUpdate;

        Statistics() {
            nextUpdate = 3.0f;
        }

        public void update() {
            if(nextUpdate < 0) {
                FPS = (int)(1000 / FrameTimeMS);
                Engine.log("Current FPS measurement: %d (%d ms)", FPS, FrameTimeMS);
                Engine.log("Frame statistics: DC - %d, Occluded - %d", DrawCalls, OccludedDraws);

                nextUpdate = 3.0f;
            }

            nextUpdate -= Engine.Current.DeltaTime;
        }
    }

    public GLSurfaceView GLView;

    private void prepareContext() {
        GLView = new GLSurfaceView(Engine.Current.MainActivity);
        //GLView.setEGLConfigChooser(8, 8, 8, 8, 0, 0);
        GLView.setEGLContextClientVersion(1);
    }

    private void setupRenderState() {
        GLES10.glEnable(GLES10.GL_TEXTURE_2D);
        GLES10.glDisable(GLES10.GL_LIGHTING);


       // GLES10.glEnable(GLES10.GL_DEPTH_TEST);

        GLES10.glEnable(GLES10.GL_BLEND);
        GLES10.glBlendFunc(GLES10.GL_SRC_ALPHA, GLES10.GL_ONE_MINUS_SRC_ALPHA);

        GLES10.glEnableClientState(GLES10.GL_VERTEX_ARRAY);
        GLES10.glEnableClientState(GLES10.GL_COLOR_ARRAY);
        GLES10.glClientActiveTexture(GLES10.GL_TEXTURE0);
        GLES10.glActiveTexture(GLES10.GL_TEXTURE0);
        GLES10.glEnableClientState(GLES10.GL_TEXTURE_COORD_ARRAY);
    }

    private void attachMainLoop() {
        GLView.setRenderer(new GLSurfaceView.Renderer() {
            @Override
            public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
                Engine.log("GL context successfully created");
                Engine.log("Vendor: %s", GLES10.glGetString(GLES10.GL_VENDOR));
                Engine.log("Renderer: %s", GLES10.glGetString(GLES10.GL_RENDERER));

                Text = new TextRenderer();

                setupRenderState();
                Engine.Current.loadResources();
            }

            @Override
            public void onSurfaceChanged(GL10 gl10, int w, int h) {
                DeviceWidth = w;
                DeviceHeight = h;

                Text.resizeBackBuffer(w, h);

                GLES10.glMatrixMode(GLES10.GL_PROJECTION);
                GLES10.glLoadIdentity();
                GLES10.glOrthof(0, 800, 480, 0, 0, 255);

                Camera.autoAdjustDistance(w, h);

                Engine.log("New render target resolution: %dx%d", w, h);
            }

            @Override
            public void onDrawFrame(GL10 gl10) {
                Engine.Current.drawFrame();
            }
        });
        GLView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        Engine.Current.MainActivity.setContentView(GLView);
    }

    public int DeviceWidth;
    public int DeviceHeight;

    public int ViewWidth;
    public int ViewHeight;

    public Camera Camera;
    public Statistics Statistics;
    public TextRenderer Text;

    private ByteBuffer vPosBuf; // Temporary buffer for drawing primitives as-is.
    private ByteBuffer vColBuf; // Temporary buffer for drawing primitives as-is.
    private ByteBuffer vUVBuf; // Temporary buffer for drawing primitives as-is.
    private int[] rectBuf;
    private int vertexSize = 32;

    public Graphics2D() {
        Engine.log("Initializing graphics subsystem");

        ViewWidth = 800;
        ViewHeight = 480;

        prepareContext();
        attachMainLoop();


        Statistics = new Statistics();

        Camera = new Camera();
        Camera.Distance = 5.0f;

        // Vertex format:
        //   vec2 pos; -- 8 bytes
        //   vec4 color; -- 16 bytes
        //   vec2 uv; -- 8 bytes
        //   32 bytes total
        int numVerts = 6;
        vPosBuf = ByteBuffer.allocateDirect((4 * 8) * numVerts);
        vColBuf = ByteBuffer.allocateDirect((4 * 16) * numVerts);
        vUVBuf = ByteBuffer.allocateDirect((4 * 8) * numVerts);
        vPosBuf.order(ByteOrder.LITTLE_ENDIAN);
        vColBuf.order(ByteOrder.LITTLE_ENDIAN);
        vUVBuf.order(ByteOrder.LITTLE_ENDIAN);
        rectBuf = new int[4];
    }

    public void beginScene() {
        GLES10.glClearColor(0, 0, 1, 1);
        GLES10.glClear(GLES10.GL_COLOR_BUFFER_BIT | GLES10.GL_DEPTH_BUFFER_BIT);

        Statistics.OccludedDraws = 0;
        Statistics.DrawCalls = 0;
    }

    private void vertex(float x, float y, float u, float v, Color col) {
        if(col == null)
            col = Color.White;

        vPosBuf.putFloat(x);
        vPosBuf.putFloat(y);
        vColBuf.putFloat(col.R);
        vColBuf.putFloat(col.G);
        vColBuf.putFloat(col.B);
        vColBuf.putFloat(col.A);
        vUVBuf.putFloat(u);
        vUVBuf.putFloat(v);
    }

    public void drawLine(float fX, float fY, float tX, float tY, Color col) {

        GLES10.glLineWidth(5.0f);

        vertex(fX, fY, 0, 0, col);
        vertex(tX, tY, 0, 0, col);
        vPosBuf.rewind();
        vColBuf.rewind();
        vUVBuf.rewind();

        GLES10.glMatrixMode(GLES10.GL_MODELVIEW);
        GLES10.glLoadIdentity();

        GLES10.glDisable(GLES10.GL_TEXTURE_2D);
        GLES10.glBindTexture(GLES10.GL_TEXTURE_2D, 0);
        GLES10.glVertexPointer(2, GLES10.GL_FLOAT, 0, vPosBuf);
        GLES10.glColorPointer(4, GLES10.GL_FLOAT, 0, vColBuf);
        GLES10.glTexCoordPointer(2, GLES10.GL_FLOAT, 0, vUVBuf);

        GLES10.glDrawArrays(GLES10.GL_LINES, 0, 2);
    }

    public void drawSprite(Sprite spr, float x, float y, float z, Color col) {
        drawSprite(spr, x, y, 1.0f, 0, col);
    }

    public void drawSprite(Sprite spr, float x, float y, float z, float rot, Color col) {
        drawSprite(spr, x, y,  0, 0,z, rot, col);
    }

    public void drawSprite(Sprite spr, float x, float y, float width, float height, float z, float rotation, Color col) {
        if(spr != null) {
            if(col == null)
                col = Color.White;

            if(width == 0)
                width = spr.Width;

            if(height == 0)
                height = spr.Height;

            // Convert position from world space to screen space
            x = x - Camera.X;
            y = y - Camera.Y;

            if(x > ViewWidth || y > ViewHeight || x + width < 0 || y + height < 0) {
                Statistics.OccludedDraws++;

                return;
            }

            GLES10.glEnable(GLES10.GL_TEXTURE_2D);
            GLES10.glBindTexture(GLES10.GL_TEXTURE_2D, spr.TextureId);

            GLES10.glMatrixMode(GLES10.GL_MODELVIEW);
            GLES10.glLoadIdentity();
            GLES10.glTranslatef(x + (width / 2), y + (height / 2), 0);
            GLES10.glRotatef(rotation, 0, 0, 1);
            GLES10.glTranslatef(-(width / 2), -(height / 2), 0);
            GLES10.glScalef(width, height, 1.0f);

            vertex(0, 0, 0, 0, col);
            vertex(1, 0, 1, 0, col);
            vertex(1, 1, 1, 1, col);
            vertex(0, 0, 0, 0, col);
            vertex(0, 1, 0, 1, col);
            vertex(1, 1, 1, 1, col);
            vPosBuf.rewind();
            vColBuf.rewind();
            vUVBuf.rewind();

            GLES10.glVertexPointer(2, GLES10.GL_FLOAT, 0, vPosBuf);
            GLES10.glColorPointer(4, GLES10.GL_FLOAT, 0, vColBuf);
            GLES10.glTexCoordPointer(2, GLES10.GL_FLOAT, 0, vUVBuf);

            GLES10.glDrawArrays(GLES10.GL_TRIANGLES, 0, 6);

            Statistics.DrawCalls++;

            /*rectBuf[0] = 0;
            rectBuf[1] = 0;
            rectBuf[2] = spr.Width;
            rectBuf[3] = -spr.Height;
            GLES11.glTexParameteriv(GLES10.GL_TEXTURE_2D, GLES11Ext.GL_TEXTURE_CROP_RECT_OES, rectBuf, 0);

            GLES11Ext.glDrawTexfOES(x, ViewHeight - spr.Height - y, 0, spr.Width, spr.Height);
        */}
    }

    // Static batches are assumed to be rendered in world space in fixed positions - just like their counterparts in 3D
    public void drawBatch(StaticBatch batch) {
        if(batch != null) {
            GLES10.glEnable(GLES10.GL_TEXTURE_2D);
            GLES10.glBindTexture(GLES10.GL_TEXTURE_2D, batch.Sprite.TextureId);

            GLES10.glMatrixMode(GLES10.GL_MODELVIEW);
            GLES10.glLoadIdentity();
            GLES10.glTranslatef(-Camera.X, -Camera.Y, 0);

            GLES10.glVertexPointer(2, GLES10.GL_FLOAT, 0, batch.VertexBuffer);
            GLES10.glColorPointer(4, GLES10.GL_FLOAT, 0, batch.ColorBuffer);
            GLES10.glTexCoordPointer(2, GLES10.GL_FLOAT, 0, batch.UVBuffer);
            GLES10.glDrawArrays(GLES10.GL_TRIANGLES, 0, batch.NumVertices);

            Statistics.DrawCalls++;
        }
    }

    public void endScene() {
        GLES10.glFinish();
    }
}

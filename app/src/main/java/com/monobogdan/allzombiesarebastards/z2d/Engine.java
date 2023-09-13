package com.monobogdan.allzombiesarebastards.z2d;

import android.app.Activity;

import com.monobogdan.allzombiesarebastards.game.Game;
import com.monobogdan.allzombiesarebastards.game.ui.Joystick;
import com.monobogdan.allzombiesarebastards.game.World;
import com.monobogdan.allzombiesarebastards.game.MusicManager;
import com.monobogdan.allzombiesarebastards.game.entity.Player;

public class Engine {
    public static Engine Current;

    public Activity MainActivity;
    public Graphics2D Graphics;
    public Input Input;

    public float DeltaTime;
    private float nextFPSDebug;

    public static void initialize(Activity mainActivity) {
        if(Current == null) {
            Current = new Engine(mainActivity);
            Current.postInit();
        }
    }

    public static void log(String fmt, Object... args)
    {
        String method = Thread.currentThread().getStackTrace()[3].getMethodName();

        android.util.Log.v("ENGINE", String.format("[" + method + "]: " + fmt, args));
    }

    Engine(Activity mainActivity) {
        MainActivity = mainActivity;
    }

    public void postInit() {
        this.Graphics = new Graphics2D();
        this.Input = new Input();
    }

    private Game game;

    void loadResources() {
        game = new Game();
    }

    private long frameBegin;

    void drawFrame() {
        // This will allow us to measure eglSwapBuffers (and VSync timing) too.
        if(frameBegin != 0) {
            long frameTime = (System.nanoTime() - frameBegin) / 1000000;
            DeltaTime = (float)frameTime / 1000.0f;

            Graphics.Statistics.FrameTimeMS = (int)frameTime;
            Graphics.Statistics.update();
        }

        frameBegin = System.nanoTime();

        Engine.Current.Graphics.beginScene();
        GUI.prepare();
        game.update();
        game.draw();
        Engine.Current.Graphics.endScene();
    }
}

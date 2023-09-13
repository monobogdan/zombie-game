package com.monobogdan.allzombiesarebastards.game;

import com.monobogdan.allzombiesarebastards.z2d.Engine;

public class Game {
    public static Game current;

    public interface State {
        void enter();
        void update();
        void draw();
        void exit();
    }

    public World World;

    public Game() {
        current = this;

        beginDM();
    }

    public void beginDM() {
        Engine.log("Starting DM game");

        World = new World("lvl0");
    }

    public void update() {
        if(World != null)
            World.update();
    }

    public void draw() {
        if(World != null)
            World.draw();
    }

}

package com.monobogdan.allzombiesarebastards.game;

public abstract class Entity {

    public float X, Y;
    public float ForwardX, ForwardY; // Forward vector
    public float RightX, RightY;
    public float Rotation;
    public boolean IsVisible;

    public int DrawingOrder;

    public float distanceTo(float x, float y) {
        x = X - x;
        y = Y - y;

        return (float)Math.sqrt((x * x) + (y * y));
    }

    public boolean AABBTest(Entity ent, float myWidth, float myHeight, float width, float height) {
        return X < ent.X + width && Y < ent.Y + height && ent.X < X + myWidth && ent.Y < Y + myHeight;
    }

    public void recalculateForward() {
        ForwardX = (float)Math.sin(Math.toRadians(Rotation));
        ForwardY = -(float)Math.cos(Math.toRadians(Rotation));
    }

    public void update() {
        recalculateForward();
    }

    public void draw() {

    }
}

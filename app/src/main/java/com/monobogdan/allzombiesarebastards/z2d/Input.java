package com.monobogdan.allzombiesarebastards.z2d;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.text.method.Touch;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.monobogdan.allzombiesarebastards.game.Game;

public final class Input {
    public static final int TOUCH_IDLE = 0;
    public static final int TOUCH_PRESSED = 1;
    public static final int TOUCH_RELEASED = 2;

    public interface TextCallback {
        void onEnteredText(String str);
    }

    public static class TouchState {
        public boolean State;
        public int Id;
        public float X, Y;
    }

    public static int GAMEPAD_A = 0;
    public static int GAMEPAD_B = 1;
    public static int GAMEPAD_Y = 2;
    public static int GAMEPAD_X = 3;
    public static int GAMEPAD_LT = 4;
    public static int GAMEPAD_RT = 5;
    public static int GAMEPAD_DPAD_LEFT = 6;
    public static int GAMEPAD_DPAD_RIGHT = 7;
    public static int GAMEPAD_DPAD_UP = 8;
    public static int GAMEPAD_DPAD_DOWN = 9;
    public static int GAMEPAD_BUTTON_COUNT = 10;

    public static class GamepadState {
        public float AnalogX, AnalogY;
        public boolean[] Buttons;

        GamepadState() {
            Buttons = new boolean[GAMEPAD_BUTTON_COUNT];
        }
    }

    class TouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            for(int i = 0; i < motionEvent.getPointerCount(); i++) {
                Touches[i].Id = motionEvent.getPointerId(i);

                // Convert from device-space to view-space.
                float xVal = motionEvent.getX() / Engine.Current.Graphics.DeviceWidth;
                float yVal = motionEvent.getY() / Engine.Current.Graphics.DeviceHeight;
                Touches[i].X = xVal * Engine.Current.Graphics.ViewWidth;
                Touches[i].Y = yVal * Engine.Current.Graphics.ViewHeight;

                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                    Touches[i].State = true;

                if(motionEvent.getAction() == MotionEvent.ACTION_UP)
                    Touches[i].State = false;
            }

            return true;
        }
    }

    public TouchState[] Touches;
    public GamepadState Gamepad;
    // Format - first int is KEYCODE mapped on Android, second is gamepad button
    private final int[] gamePadMapping =
            {
                    KeyEvent.KEYCODE_DPAD_CENTER, GAMEPAD_A,
                    KeyEvent.KEYCODE_BACK, GAMEPAD_B,
                    KeyEvent.KEYCODE_BUTTON_Y, GAMEPAD_Y,
                    KeyEvent.KEYCODE_BUTTON_X, GAMEPAD_X,
                    KeyEvent.KEYCODE_DPAD_UP, GAMEPAD_DPAD_UP,
                    KeyEvent.KEYCODE_DPAD_RIGHT, GAMEPAD_DPAD_RIGHT,
                    KeyEvent.KEYCODE_DPAD_LEFT, GAMEPAD_DPAD_LEFT,
                    KeyEvent.KEYCODE_DPAD_UP, GAMEPAD_DPAD_UP,
                    KeyEvent.KEYCODE_DPAD_DOWN, GAMEPAD_DPAD_DOWN
            };

    public Input() {
        Touches = new TouchState[5];

        for(int i = 0; i < Touches.length; i++)
            Touches[i] = new TouchState();

        Gamepad = new GamepadState();

        Engine.log("Initializing input subsystem...");
        Engine.Current.Graphics.GLView.setOnTouchListener(new TouchListener());
    }

    public int isTouchingZone(float x, float y, float w, float h) {
        boolean touching = false;

        for(int i = 0; i < Touches.length; i++) {
            touching = Touches[i].X > x && Touches[i].Y > y && Touches[i].X < x + w && Touches[i].Y < y + h;

            if(touching && Touches[i].State)
                return i;
        }

        return -1;
    }

    public boolean isAnyFingerInZone(float x, float y, float w, float h) {
        boolean touching = false;

        for(int i = 0; i < Touches.length; i++) {
            touching = Touches[i].X > x && Touches[i].Y > y && Touches[i].X < x + w && Touches[i].Y < y + h;

            if(touching && Touches[i].State)
                return true;
        }

        return false;
    }

    public void requestTextInput(String title, String target, TextCallback callback) {
        AlertDialog.Builder dlgBuilder = new AlertDialog.Builder(Engine.Current.MainActivity);

        TextView text = new TextView(Engine.Current.MainActivity);
        EditText editor = new EditText(Engine.Current.MainActivity);

        text.setText(target + ":");
        editor.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        dlgBuilder.setTitle(title);

        LinearLayout layout = new LinearLayout(Engine.Current.MainActivity);
        layout.addView(text);
        layout.addView(editor);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(5, 5, 5, 5);
        dlgBuilder.setView(layout);
        dlgBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                callback.onEnteredText(editor.getText().toString());
            }
        });

        Engine.Current.MainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dlgBuilder.show();
            }
        });
    }

    void handleKeyInput(boolean state, int keyCode) {
        /*for(int i = 0; i < gamePadMapping.length; i++) {
            int code = gamePadMapping[i * 2];
            int gameKey = gamePadMapping[i * 2 + 1];

            if(keyCode == code) {
                Gamepad.Buttons[gameKey] = state;
                break;
            }
        }*/
    }
}

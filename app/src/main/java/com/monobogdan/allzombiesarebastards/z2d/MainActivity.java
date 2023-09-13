package com.monobogdan.allzombiesarebastards.z2d;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setBackgroundDrawable(null);
        Engine.initialize(this);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        System.exit(0);
    }

    @Override
    protected void onPause() {
        super.onPause();

        Engine.Current.Graphics.GLView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Engine.Current.Graphics.GLView.onResume();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Engine.Current.Input.handleKeyInput(false, event.getKeyCode());

        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Engine.Current.Input.handleKeyInput(true, event.getKeyCode());

        return false;
    }
}

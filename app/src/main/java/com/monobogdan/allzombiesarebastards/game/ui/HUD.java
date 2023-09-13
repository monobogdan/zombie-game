package com.monobogdan.allzombiesarebastards.game.ui;

import com.monobogdan.allzombiesarebastards.game.Game;
import com.monobogdan.allzombiesarebastards.z2d.Engine;
import com.monobogdan.allzombiesarebastards.z2d.Graphics2D;
import com.monobogdan.allzombiesarebastards.z2d.Sprite;

public class HUD {
    private Sprite hpSprite;
    private Sprite moneySpr;

    private ShopDialog shopDlg;

    public HUD() {
        shopDlg = new ShopDialog();
        shopDlg.show();
    }

    public void draw() {
        Engine.Current.Graphics.Text.drawString("Health: ", 0, 0, Graphics2D.Color.Red);
        Engine.Current.Graphics.Text.drawString("Money: ", 0, 14, Graphics2D.Color.Red);
    }
}

package com.monobogdan.allzombiesarebastards.game.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Layout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.monobogdan.allzombiesarebastards.R;
import com.monobogdan.allzombiesarebastards.game.Gun;
import com.monobogdan.allzombiesarebastards.z2d.Engine;

import org.json.JSONObject;

import java.io.IOException;

public class ShopDialog {
    private AlertDialog.Builder builder;

    private void populateStore(LinearLayout layout) {
        for(Gun gun : Gun.GunDescription) {
            LinearLayout item = (LinearLayout)Engine.Current.MainActivity.getLayoutInflater().inflate(R.layout.shop_item, null);

            Bitmap bmp = null;
            try {
                bmp = BitmapFactory.decodeStream(Engine.Current.MainActivity.getAssets().open("sprites/ui/" + gun.ShopSprite));
                ((ImageView)item.findViewById(R.id.item_sprite)).setImageBitmap(bmp);
                ((TextView)item.findViewById(R.id.item_title)).setText(gun.Name);
                ((TextView)item.findViewById(R.id.item_price)).setText(gun.Price + "$");

                layout.addView(item);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public ShopDialog() {
        builder = new AlertDialog.Builder(Engine.Current.MainActivity);
        builder.setNegativeButton("Закрыть", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        builder.setTitle("Магазин");

        ScrollView parentScroll = new ScrollView(Engine.Current.MainActivity);
        LinearLayout layout = new LinearLayout(Engine.Current.MainActivity);
        layout.setOrientation(LinearLayout.VERTICAL);
        populateStore(layout);
        parentScroll.addView(layout);
        builder.setView(parentScroll);
    }

    public void show() {
        Engine.Current.MainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                builder.show();
            }
        });
    }
}

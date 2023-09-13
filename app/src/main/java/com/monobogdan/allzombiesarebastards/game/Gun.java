package com.monobogdan.allzombiesarebastards.game;

import com.monobogdan.allzombiesarebastards.game.entity.Player;
import com.monobogdan.allzombiesarebastards.z2d.AudioStream;

public class Gun {

    public String Name;
    public int Sprite;
    public String ShopSprite;
    public float Damage;
    public float Speed;
    public int ClipSize;
    public int MaxAmmo;

    public String FireSound;
    public AudioStream FireEffect;

    public int Price;

    public static void precacheAudio() {
        for(Gun gun : GunDescription) {
            if(gun.FireEffect == null) {
                gun.FireEffect = AudioStream.load(gun.FireSound);
            }
        }
    }

    public Gun(String name, int sprite, float dmg, float speed, int clipSize, int maxAmmo, String fireSound, String shopSprite, int price) {
        Name = name;
        Sprite = sprite;
        Damage = dmg;
        Speed = speed;
        ClipSize = clipSize;
        MaxAmmo = maxAmmo;

        FireSound = fireSound;
        ShopSprite = shopSprite;

        Price = price;
    }

    public static Gun[] GunDescription = {
            new Gun("Glock-18", Player.SPRITE_HANDGUN, 20.0f, 0.4f, 20, 90, "glock18.wav", "pistol.png", 1500),
            new Gun("UZI", Player.SPRITE_HANDGUN, 20.0f, 0.15f, 20, 90, "uzi.wav", "pistol.png", 1500),
            new Gun("Deagle", Player.SPRITE_HANDGUN, 100.0f, 0.7f, 20, 90, "deagle.wav", "pistol.png", 1500),
            new Gun("TOZ-34", Player.SPRITE_HANDGUN, 100.0f, 1.1f, 20, 90, "shotgun.wav", "pistol.png", 1500),
            new Gun("XM1014", Player.SPRITE_HANDGUN, 90.0f, 0.6f, 20, 90, "shotgun.wav", "pistol.png", 1500),
            new Gun("AK47", Player.SPRITE_HANDGUN, 40.0f, 1.1f, 20, 90, "ak47.wav", "pistol.png", 1500),
            new Gun("M4-A1", Player.SPRITE_HANDGUN, 90.0f, 0.6f, 20, 90, "m4.wav", "pistol.png", 1500),
            new Gun("MiniFGun", Player.SPRITE_HANDGUN, 30.0f, 0.15f, 20, 90, "minigun.wav", "pistol.png", 1500)
    };
}

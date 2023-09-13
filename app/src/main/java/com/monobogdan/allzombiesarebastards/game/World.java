package com.monobogdan.allzombiesarebastards.game;

import android.content.res.AssetManager;
import android.util.Range;

import com.monobogdan.allzombiesarebastards.game.entity.Crawler;
import com.monobogdan.allzombiesarebastards.game.entity.Player;
import com.monobogdan.allzombiesarebastards.game.entity.Zombie;
import com.monobogdan.allzombiesarebastards.z2d.Engine;
import com.monobogdan.allzombiesarebastards.z2d.Graphics2D;
import com.monobogdan.allzombiesarebastards.z2d.Sprite;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public final class World {
    public class MatchInfo {
        public int Kills;
        public int Money;
        public float AliveTime;
        public int Level;
    }

    public class SpawnManager {
        public float NextSpawn;
        private ArrayList<Prop> Spawners;

        public SpawnManager() {
            Spawners = new ArrayList<>();

            for(Prop p : props) {
                if(p.Name.equals("zed_spawner"))
                    Spawners.add(p);
            }
        }

        private int randRange(int min, int max) {
            float rand = new Random().nextFloat();

            return (int)(rand * (max - min) + min);
        }

        public void update() {
            if(NextSpawn < 0) {
                Zombie zombie = new Crawler();
                Prop spawner = Spawners.get(randRange(0, Spawners.size() - 1));
                zombie.X = spawner.X;
                zombie.Y = spawner.Y;

                spawn(zombie);

                NextSpawn = 3.0f;
            }

            NextSpawn -= Engine.Current.DeltaTime;
        }
    }

    static final int MAX_TILESET = 64;
    static final int TILE_SIZE = 32;

    public static class Prop {
        public String Name;
        public Sprite Sprite;
        public float X, Y;
        public boolean Visible;
    }

    public Player Player;

    public MatchInfo Match;
    public SpawnManager Spawner;

    public int AbsoluteWidth, AbsoluteHeight;

    public List<Entity> Entities;
    private List<Entity> entitySpawn;
    private List<Entity> entityDespawn;

    private List<Prop> props;
    private Sprite[] tileSet;

    private int width;
    private int height;
    private byte[] tiles;
    private Sprite mask;
    private HashMap<Sprite, Graphics2D.StaticBatch> batches;

    private void buildBatch() {
        batches = new HashMap<Sprite, Graphics2D.StaticBatch>();

        for(int i = 0; i < width; i++) {
            for(int j = 0; j < height; j++) {
                Sprite tile = tileSet[tiles[j * width + i]];

                if(!batches.containsKey(tile))
                    batches.put(tile, new Graphics2D.StaticBatch(tile, width * height));

                batches.get(tile).addInstance(i * 32, j * 32, Graphics2D.Color.White);
            }
        }

        for(Sprite spr : batches.keySet()) {
            batches.get(spr).prepare();
        }

        Engine.log("Generated %d batches", batches.size());
    }

    private void parseJson(String json) {
        try {
            JSONObject obj = new JSONObject(json);

            width = obj.getInt("width");
            height = obj.getInt("height");

            JSONArray jtileSet = obj.getJSONArray("tilesets").getJSONObject(0).getJSONArray("tiles");
            for(int i = 0; i < jtileSet.length(); i++) {
                JSONObject tile = jtileSet.getJSONObject(i);

                String name = tile.getString("image");
                name = name.substring(name.lastIndexOf("/") + 1);
                tileSet[tile.getInt("id")] = Sprite.load(name);
            }

            JSONArray layers = obj.getJSONArray("layers");

            this.tiles = new byte[width * height];
            Engine.log("Level size %d %d", width, height);

            for(int i = 0; i < layers.length(); i++) {
                JSONObject layer = layers.getJSONObject(i);
                boolean isTileData = layer.has("data");

                if(isTileData) {
                    JSONArray tiles = layer.getJSONArray("data");

                    Engine.log("Loading tile data");
                    for(int j = 0; j < tiles.length(); j++)
                        this.tiles[j] = (byte)(tiles.getInt(j) - 1);
                } else {
                    JSONArray objects = layer.getJSONArray("objects");

                    for(int j = 0; j < objects.length(); j++) {
                        JSONObject jobj = objects.getJSONObject(j);

                        Prop prop = new Prop();
                        prop.Sprite = tileSet[jobj.getInt("gid") - 1];
                        prop.Name = jobj.getString("name");
                        prop.X = (float)jobj.getDouble("x");
                        prop.Y = (float)jobj.getDouble("y");
                        prop.Visible = true;

                        String type = jobj.getString("type");
                        if(type.equals("invisible"))
                            prop.Visible = false;

                        props.add(prop);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace(); // Level loading is unrecoverable error
            throw new RuntimeException(e);
        }
    }

    private void loadLevel(String name) {
        try {
            AssetManager assets = Engine.Current.MainActivity.getAssets();
            InputStream strm = assets.open("maps/" + name + ".json");

            BufferedReader reader = new BufferedReader(new InputStreamReader(strm));
            String json = "";

            while(reader.ready())
                json += reader.readLine();

            parseJson(json);
            buildBatch();

            AbsoluteWidth = width * 32;
            AbsoluteHeight = height * 32;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public World(String name) {
        props = new ArrayList<>();
        tileSet = new Sprite[MAX_TILESET];

        Entities = new ArrayList<>();
        entitySpawn = new ArrayList<>();
        entityDespawn = new ArrayList<>();

        loadLevel(name);

        Match = new MatchInfo();
        Spawner = new SpawnManager();
        Player = new Player();

        Prop spawnPoint = findProp("spawn_point");
        if(spawnPoint != null) {
            Player.X = spawnPoint.X;
            Player.Y = spawnPoint.Y;
        } else {
            Engine.log("No spawn_point found");
        }

        spawn(this.Player);
    }

    public Prop findProp(String name) {
        for(Prop prop : props) {
            if(prop.Name.equals(name))
                return prop;
        }

        return null;
    }

    public void spawn(Entity ent) { entitySpawn.add(ent); }

    public void despawn(Entity ent) {
        entityDespawn.add(ent);
    }

    public void update() {
        Spawner.update();

        for(Entity ent : Entities) {
            ent.update();
        }

        for(Entity ent : entitySpawn)
            Entities.add(ent);
        entitySpawn.clear();

        for(Entity ent : entityDespawn)
            Entities.remove(ent);
        entityDespawn.clear();
    }

    public void draw() {
        // Old and ineffective tile rendering
        /*for(int i = 0; i < width; i++) {
            for(int j = 0; j < height; j++) {
                Sprite tile = tileSet[tiles[j * width + i]];

                Engine.Current.Graphics.drawSprite(tile, i * 32 , j * 32, 0, Graphics2D.Color.White);
            }
        }*/

        // New batched tile renderer
        for(Sprite spr : batches.keySet()) {
            Engine.Current.Graphics.drawBatch(batches.get(spr));
        }

        for(Entity ent : Entities) {
            ent.draw();
        }

        for (Prop prop :
             props) {
            if(prop.Visible)
                Engine.Current.Graphics.drawSprite(prop.Sprite,
                        prop.X, prop.Y - prop.Sprite.Height, 2, Graphics2D.Color.White);
        }
    }
}

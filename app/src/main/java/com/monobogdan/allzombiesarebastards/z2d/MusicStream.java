package com.monobogdan.allzombiesarebastards.z2d;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;

import java.io.IOException;

public class MusicStream {
    private MediaPlayer mediaPlayer;
    private boolean ready;

    public MusicStream(MediaPlayer player) {
        mediaPlayer = player;
    }

    public void forceRelease() {
        if(mediaPlayer.isPlaying())
            mediaPlayer.stop();

        mediaPlayer.release();
    }

    public void play() {
        if(!mediaPlayer.isPlaying())
            mediaPlayer.start();
    }

    public void pause() {
        if(mediaPlayer.isPlaying())
            mediaPlayer.pause();
    }

    public void stop() {
        if(!mediaPlayer.isPlaying())
            mediaPlayer.stop();
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public void setLoop(boolean isLooping) {
        mediaPlayer.setLooping(isLooping);
    }

    public static MusicStream load(String fileName) {
        AssetManager assets = Engine.Current.MainActivity.getAssets();

        try {
            AssetFileDescriptor afd = assets.openFd("music/" + fileName);
            MediaPlayer player = new MediaPlayer();
            player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            player.setVolume(0.3f, 0.3f); // TODO: Move volume settings to Audio
            player.prepare();

            return new MusicStream(player);
        } catch (IOException e) {
            Engine.log("Failed to load audio stream %s", fileName);

            return null;
        }
    }
}

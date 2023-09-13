package com.monobogdan.allzombiesarebastards.game;

import com.monobogdan.allzombiesarebastards.z2d.MusicStream;

public class MusicManager {
    static final String[] TRACKS = { "bgm0.mid", "bgm1.mid", "bgm2.mid" };

    private MusicStream activeStream;
    private int currTrack;
    private boolean playState;

    public MusicManager() {
        currTrack = -1;
        //nextTrack();
    }

    public void nextTrack() {
        /*currTrack++;
        if(currTrack >= TRACKS.length)
            currTrack = 0;

        activeStream = MusicStream.load(TRACKS[currTrack]);
        playState = false;*/
    }

    public void update() {
        /*if(!activeStream.isPlaying()) {
            if(!playState) {
                activeStream.play();
                playState = true;
            }
            else {
                nextTrack();
            }
        }*/

    }
}

package com.monobogdan.allzombiesarebastards.z2d;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.SoundPool;
import android.media.session.PlaybackState;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class AudioStream {
    public static class Instance {
        private AudioStream parent;
        private int id;

        Instance(AudioStream parent) {
            this.parent = parent;
        }

        public void play() {
            id = sharedPool.play(parent.streamId, Audio.MasterAudioLevel, Audio.MasterAudioLevel, 0, 0, 1.0f);
        }

        public void stop() {
            sharedPool.stop(id);
        }
    }

    private static SoundPool sharedPool;
    private int streamId;

    static {
        Engine.log("Allocating SoundPool");
        sharedPool = new SoundPool(255, AudioManager.STREAM_MUSIC, 0);
    }

    public AudioStream(int streamId) {
        this.streamId = streamId;
    }

    @Override
    protected void finalize() throws Throwable {
        sharedPool.unload(streamId);
        super.finalize();
    }

    public static AudioStream load(String fileName) {
        AssetManager assets = Engine.Current.MainActivity.getAssets();

        try {
            AssetFileDescriptor afd = assets.openFd("sounds/" + fileName);
            int streamId = sharedPool.load(afd, 0);

            return new AudioStream(streamId);
        } catch (IOException e) {
            Engine.log("Failed to load audio stream %s", fileName);

            return null;
        }
    }

    public Instance createInstance() {
        return new Instance(this);
    }

    // Old PCM implementation. Very buggy as AudioTrack API is bad :(
    /*private AudioTrack track;

    public boolean IsPlaying;

    public AudioStream(byte[] pcmData, int offset, int freq, boolean isStereo, int wordLen) {
        int channelSize = wordLen == 8 ? AudioFormat.ENCODING_PCM_8BIT : AudioFormat.ENCODING_PCM_16BIT;

        try {
            track = new AudioTrack(AudioManager.STREAM_MUSIC, freq, isStereo ? AudioFormat.CHANNEL_OUT_STEREO : AudioFormat.CHANNEL_OUT_MONO,
                    channelSize, pcmData.length, AudioTrack.MODE_STATIC);

            track.setStereoVolume(1.0f, 1.0f);
            track.write(pcmData, offset, pcmData.length - offset);

            track.setNotificationMarkerPosition(wordLen == 8 ? pcmData.length / 2 : pcmData.length / 2);
            track.setPlaybackPositionUpdateListener(new AudioTrack.OnPlaybackPositionUpdateListener() {
                @Override
                public void onMarkerReached(AudioTrack audioTrack) {

                    Engine.log("Marker!");
                    IsPlaying = false;
                    audioTrack.stop();
                    audioTrack.reloadStaticData();
                    audioTrack.setPlaybackHeadPosition(0);
                }

                @Override
                public void onPeriodicNotification(AudioTrack audioTrack) {

                }
            });
        } catch (Exception e) {
            Engine.log("Failed to create AudioStream with desired format");

            e.printStackTrace();
        }
    }

    public void play() {

        if(track != null && !IsPlaying) {
            track.play();

            IsPlaying = true;
        }
    }

    public void pause() {
        if(track != null)
            track.pause();
    }

    public void stop() {
        if(track != null)
            track.stop();
    }

    public static AudioStream load(String fileName) {
        try {
            InputStream is = Engine.Current.MainActivity.getAssets().open("sounds/" + fileName, AssetManager.ACCESS_RANDOM);
            byte[] data = new byte[is.available()];
            ByteBuffer buf = ByteBuffer.wrap(data);
            buf.order(ByteOrder.LITTLE_ENDIAN);
            is.read(data);

            Engine.log("Loading AudioStream %s", fileName);
            int riff = buf.getInt();
            if(riff == 1179011410) {
                int fSize = buf.getInt();
                int waveHdr = buf.getInt();
                int fmtHdr = buf.getInt();

                int fmtDataLen = buf.getInt();
                short pcmFmt = buf.getShort();
                short channels = buf.getShort();
                int sampleRate = buf.getInt();
                int bitRate = buf.getInt();
                int bitRate2 = buf.getShort();
                int bitsPerSample = buf.getShort();
                int chunkHdr = buf.getInt();
                int chunkSize = buf.getInt();

                return new AudioStream(data, buf.position(), sampleRate, channels == 2, bitsPerSample);
            } else {
                Engine.log("Not a RIFF wave file");

                return null;
            }

        } catch (IOException e) {
            Engine.log("Failed to load AudioStream %s", fileName);

            return null;
        }
    }*/
}

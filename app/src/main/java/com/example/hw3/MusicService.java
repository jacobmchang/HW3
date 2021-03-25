package com.example.hw3;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class MusicService extends Service {

    MusicPlayer musicPlayer;
    private final IBinder iBinder= new MyBinder();

    public static final String COMPLETE_INTENT = "complete intent";
    public static final String MUSICNAME = "music name";

    public static final String PICTURE_INTENT = "complete intent";
    public static final String PICTURENAME = "music name";

    @Override
    public void onCreate() {
        super.onCreate();
        musicPlayer = new MusicPlayer(this);
    }

    public void startMusic(){

        musicPlayer.playMusic();
    }

    public void pauseMusic(){

        musicPlayer.pauseMusic();
    }

    public void resumeMusic(){

        musicPlayer.resumeMusic();
    }

    public void restartMusic() {

        musicPlayer.restartMusic();
    }

    public int getPlayingStatus(){

        return musicPlayer.getMusicStatus();
    }


    public void onUpdateMusicName(String musicname) {
        Intent intent = new Intent(COMPLETE_INTENT);
        intent.putExtra(MUSICNAME, musicname);
        sendBroadcast(intent);
    }

    public void onUpdatePicture(String picture) {
        Intent intent = new Intent(PICTURE_INTENT);
        intent.putExtra(PICTURENAME, picture);
        sendBroadcast(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        //String message = intent.getStringExtra("song");
        //System.out.println(message);
        //musicPlayer.setSong(message);
        return iBinder;
    }


    public class MyBinder extends Binder {
        com.example.hw3.MusicService getService(){
            return com.example.hw3.MusicService.this;
        }
    }

    public MusicPlayer getMusicPlayer() {
        return musicPlayer;
    }
}

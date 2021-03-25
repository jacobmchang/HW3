package com.example.hw3;

import android.media.MediaPlayer;

public class MusicPlayer implements MediaPlayer.OnCompletionListener {

    MediaPlayer player;
    MediaPlayer sounds[] = new MediaPlayer[3];
    int startTimes[] = new int[3];
    boolean running[] = new boolean[3];
    int currentPosition = 0;
    int musicIndex = 2;
    private int musicStatus = 0; //0: before playing, 1 playing, 2 paused
    private MusicService musicService;

    static final int[] MUSICPATH = new int[]{
            R.raw.jump,
            R.raw.sandman,
            R.raw.gotechgo,
            R.raw.clapping,
            R.raw.cheering,
            R.raw.lestgohokies
    };

    static final String[] MUSICNAME = new String[]{
            "Jump Around",
            "Enter Sandman",
            "go Tech go!",
            "Clapping",
            "Cheering",
            "Lets go hokies!"
    };

    public MusicPlayer(MusicService service) {

        this.musicService = service;
    }

    public void setSong(String song) {
        if (song.equals("Go Tech Go!")) {
            musicIndex = 2;
        }
        else if (song.equals("Enter Sandman")) {
            musicIndex = 1;
        } else if (song.equals("Jump Around")) {
            musicIndex = 0;
        }
    }

    public void setSound(String sound, int start, int index) {
        int id = 0;
        if (sound.equals("Clapping"))
            id = 3;
        else if (sound.equals("Cheering"))
            id = 4;
        else if (sound.equals("Go Hokies"))
            id = 5;
        sounds[index] = MediaPlayer.create(this.musicService, MUSICPATH[id]);
        startTimes[index] = start;
    }


    public int getMusicStatus() {

        return musicStatus;
    }

    public String getMusicName() {

        return MUSICNAME[musicIndex];
    }

    public void playMusic() {
        player= MediaPlayer.create(this.musicService, MUSICPATH[musicIndex]);
        player.start();
        player.setOnCompletionListener(this);
        musicService.onUpdateMusicName(getMusicName());
        musicStatus = 1;
    }

    public void pauseMusic() {
        if(player!= null && player.isPlaying()){
            player.pause();
            currentPosition= player.getCurrentPosition();
            musicStatus= 2;
            for (int i = 0; i < 3; i++) {
                MediaPlayer media = sounds[i];
                if (media != null && media.isPlaying())
                    running[i] = true;
                    media.pause();
            }
        }
    }

    public void resumeMusic() {
        if(player!= null){
            player.seekTo(currentPosition);
            player.start();
            musicStatus=1;
            for (int i = 0; i < 3; i++) {
                MediaPlayer media = sounds[i];
                if (media != null && running[i])
                    media.start();
            }
        }
    }

    public void restartMusic() {
        if (player != null) {
            //player.pause();
            player.release();
            player= MediaPlayer.create(this.musicService, MUSICPATH[musicIndex]);
            player.seekTo(0);
            currentPosition = 0;
            player.start();
            musicStatus = 1;
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        //musicIndex = (musicIndex +1) % MUSICNAME.length;
        musicStatus = 2;
        mediaPlayer.release();
        player = null;
        //pauseMusic();
    }

    public MediaPlayer getPlayer() {
        return player;
    }

    public int[] getStartTimes() {
        return startTimes;
    }

    public MediaPlayer[] getSounds() {
        return sounds;
    }

}
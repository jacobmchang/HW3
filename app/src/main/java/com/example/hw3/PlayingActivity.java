package com.example.hw3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class PlayingActivity extends AppCompatActivity implements View.OnClickListener {

    TextView music;
    Button play;
    Button returnButton;
    Button restartButton;
    ImageView image;

    String current;
    String song;
    String sounds[];
    int seeks[];

    MusicService musicService;
    MusicCompletionReceiver musicCompletionReceiver;
    Intent startMusicServiceIntent;
    boolean isBound = false;
    boolean isInitialized = false;

    public static final String INITIALIZE_STATUS = "intialization status";
    public static final String MUSIC_PLAYING = "music playing";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing);

        image = (ImageView) findViewById(R.id.imageView);

        play = (Button) findViewById(R.id.playButton);
        play.setOnClickListener(this);

        restartButton = (Button) findViewById(R.id.restartButton);
        restartButton.setOnClickListener(this);

        returnButton = (Button) findViewById(R.id.returnButton);
        returnButton.setOnClickListener(this);

        music = (TextView) findViewById(R.id.song);

        if(savedInstanceState != null){
            isInitialized = savedInstanceState.getBoolean(INITIALIZE_STATUS);
            music.setText(savedInstanceState.getString(MUSIC_PLAYING));
        }

        startMusicServiceIntent = new Intent(this, MusicService.class);
        Intent intent = getIntent();
        song = intent.getStringExtra("song");
        sounds = new String[3];
        seeks = new int[3];
        sounds[0] = intent.getStringExtra("sound1");
        sounds[1] = intent.getStringExtra("sound2");
        sounds[2] = intent.getStringExtra("sound3");
        seeks[0] = intent.getIntExtra("seek1", 0);
        seeks[1] = intent.getIntExtra("seek2", 0);
        seeks[2] = intent.getIntExtra("seek3", 0);

        if(!isInitialized){
            startService(startMusicServiceIntent);
            isInitialized= true;
        }

        musicCompletionReceiver = new MusicCompletionReceiver(this);
        setImage(song);
        music.setText(song);
        if (savedInstanceState != null) {
            current = savedInstanceState.getString("display");
            setImage(current);
        }
    }

    /**
     * Sets the image of the acitivty
     * @param sound the sounds/song
     */
    private void setImage(String sound) {
        if (sound.equals("Go Tech Go!"))
            image.setImageResource(R.drawable.main);
        else if (sound.equals("Enter Sandman"))
            image.setImageResource(R.drawable.main2);
        else if (sound.equals("Jump Around"))
            image.setImageResource(R.drawable.main3);
        else if (sound.equals("Clapping"))
            image.setImageResource(R.drawable.clapping);
        else if (sound.equals("Cheering"))
            image.setImageResource(R.drawable.cheering);
        else if (sound.equals("Go Hokies"))
            image.setImageResource(R.drawable.gohokies);
    }

    @Override
    public void onClick(View view) {
        MusicPlayer mp = musicService.getMusicPlayer();
        mp.setSong(song);

        mp.setSound(sounds[0], seeks[0], 0);
        mp.setSound(sounds[1], seeks[1], 1);
        mp.setSound(sounds[2], seeks[2], 2);

        Button b = (Button) view;
        if (b.getId() == R.id.returnButton) // Return Button
            finish();
        else if (b.getId() == R.id.restartButton) { // Restart Button
            musicService.restartMusic();
            play.setText("Pause");
        }
        else if (isBound) {
            switch (musicService.getPlayingStatus()) {
                case 0:
                    musicService.startMusic();
                    play.setText("Pause");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            int totalLength = mp.getPlayer().getDuration();
                            int times[] = new int[3];
                            int endTimes[] = new int[3];
                            for (int i = 0; i < 3; i++) {
                                if (mp.getStartTimes()[i] != 0) {
                                    times[i] = (totalLength * mp.getStartTimes()[i]) / 100;
                                    times[i] = times[i] / 1000;
                                    endTimes[i] = times[i] + (mp.getSounds()[i].getDuration() / 1000) ;
                                }
                            }
                            while (musicService.getMusicPlayer() != null) {
                                if (musicService.getPlayingStatus() == 1) {
                                    int currentTime = mp.getPlayer().getCurrentPosition() / 1000;
                                    for (int j = 0; j < 3; j++) {
                                        if (times[j] != 0 && currentTime == times[j]) { // If start
                                            Handler threadHandler = new Handler(Looper.getMainLooper());
                                            String sound = sounds[j];
                                            MediaPlayer soundPlayer = mp.getSounds()[j];
                                            soundPlayer.start();
                                            current = sound;
                                            threadHandler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    setImage(sound);
                                                }
                                            });
                                        } else if (endTimes[j] != 0 && currentTime == endTimes[j]) { // If stop
                                            Handler threadHandler = new Handler(Looper.getMainLooper());
                                            String sound = sounds[j];
                                            threadHandler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    setImage(song);
                                                }
                                            });
                                        }
                                    }
                                }
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).start();
                    break;
                case 1:
                    musicService.pauseMusic();
                    play.setText("Resume");
                    break;
                case 2:
                    musicService.resumeMusic();
                    play.setText("Pause");
                    break;
            }
        }
    }

    public void updateName(String musicName) {
        music.setText(musicName);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(isInitialized && !isBound){
            bindService(startMusicServiceIntent, musicServiceConnection, Context.BIND_AUTO_CREATE);
        }

        registerReceiver(musicCompletionReceiver, new IntentFilter(MusicService.COMPLETE_INTENT));
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(isBound){
            unbindService(musicServiceConnection);
            isBound= false;
        }

        unregisterReceiver(musicCompletionReceiver);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(INITIALIZE_STATUS, isInitialized);
        outState.putString(MUSIC_PLAYING, music.getText().toString());
        outState.putString("display", current);
        super.onSaveInstanceState(outState);
    }

    private ServiceConnection musicServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MusicService.MyBinder binder = (MusicService.MyBinder) iBinder;
            musicService = binder.getService();
            isBound = true;

            switch (musicService.getPlayingStatus()) {
                case 0:
                    play.setText("Start");
                    break;
                case 1:
                    play.setText("Pause");
                    break;
                case 2:
                    play.setText("Resume");
                    break;
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            musicService = null;
            isBound = false;
        }
    };
}
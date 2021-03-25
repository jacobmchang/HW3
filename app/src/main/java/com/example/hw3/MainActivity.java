package com.example.hw3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Spinner background;
    Spinner spinner1;
    Spinner spinner2;
    Spinner spinner3;

    SeekBar seekbar1;
    SeekBar seekbar2;
    SeekBar seekbar3;

    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // First spinner
        background = findViewById(R.id.background);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.background_music, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        background.setAdapter(adapter);

        // Other spinners
        spinner1 = findViewById(R.id.effect1);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.sound_effect, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter1);

        spinner2 = findViewById(R.id.effect2);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.sound_effect, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);

        spinner3 = findViewById(R.id.effect3);
        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this, R.array.sound_effect, android.R.layout.simple_spinner_item);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner3.setAdapter(adapter3);

        button = findViewById(R.id.button);
        button.setOnClickListener(this);

        // Seek bars
        seekbar1 = (SeekBar) findViewById(R.id.seekBar);
        seekbar2 = (SeekBar) findViewById(R.id.seekBar2);
        seekbar3 = (SeekBar) findViewById(R.id.seekBar3);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, PlayingActivity.class);
        intent.putExtra("song", background.getSelectedItem().toString());
        intent.putExtra("sound1", spinner1.getSelectedItem().toString());
        intent.putExtra("seek1", seekbar1.getProgress());
        intent.putExtra("sound2", spinner2.getSelectedItem().toString());
        intent.putExtra("seek2", seekbar2.getProgress());
        intent.putExtra("sound3", spinner3.getSelectedItem().toString());
        intent.putExtra("seek3", seekbar3.getProgress());
        startActivity(intent);
    }
}
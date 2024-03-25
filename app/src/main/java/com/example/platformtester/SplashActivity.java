package com.example.platformtester;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class SplashActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener
{

    private Button playButton,settingsButton,quitButton;
    String text;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        playButton = (Button)findViewById(R.id.play_Button);
        settingsButton = (Button)findViewById(R.id.settings_Button);
        quitButton = (Button)findViewById(R.id.quit_Button);
        setContentView(R.layout.activity_splash);

        Spinner spinner = findViewById(R.id.character_Spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.characters,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);


    }


    public void playActivate(View view)
    {
        Intent playIntent = new Intent(SplashActivity.this, com.example.platformer.MainActivity.class);
        playIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        playIntent.putExtra("character",text);
        startActivity(playIntent);
    }
    public void quitActivate(View view)
    {
        finishAndRemoveTask();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        text = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
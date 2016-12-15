package com.example.nils.rhymetime;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class PlayOverviewActivity extends AppCompatActivity {

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_overview);

        Bundle extras = getIntent().getExtras();
        username = extras.getString("username", "anonymous");
    }

    // this method shows a check-mark next to all completed levels
    public void showCompletedLevels() {
        // get SharedPreferences
        SharedPreferences shared = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        boolean L1Completed = shared.getBoolean("L1Completed", true);
        boolean L2Completed = shared.getBoolean("L2Completed", true);
        boolean L3Completed = shared.getBoolean("L3Completed", true);
        boolean L4Completed = shared.getBoolean("L4Completed", false);
        boolean L5Completed = shared.getBoolean("L5Completed", false);

        Button button;
        if (L1Completed) {
            button = (Button) findViewById(R.id.play1Button);
            button.setTextColor(Color.parseColor("#21FE80"));
            button.setAllCaps(true);
        }
        if (L2Completed) {
            button = (Button) findViewById(R.id.play2Button);
            button.setTextColor(Color.parseColor("#21FE80"));
            button.setAllCaps(true);
        }
        if (L3Completed) {
            button = (Button) findViewById(R.id.play3Button);
            button.setTextColor(Color.parseColor("#21FE80"));
            button.setAllCaps(true);
        }
        if (L4Completed) {
            button = (Button) findViewById(R.id.play4Button);
            button.setTextColor(Color.parseColor("#21FE80"));
            button.setAllCaps(true);
        }
        if (L5Completed) {
            button = (Button) findViewById(R.id.play5Button);
            button.setTextColor(Color.parseColor("#21FE80"));
        }
    }

    // when a user clicks a level button, go to the PlayActivity
    public void goToPlay(View view) {
        Button b = (Button)view;
        String buttonText = b.getText().toString();

        if (isNetworkAvailable()) {
            Intent goToPlay = new Intent(this, PlayActivity.class);
            goToPlay.putExtra("stage", buttonText);
            goToPlay.putExtra("username", username);
            startActivity(goToPlay);
            finish();
        } else {
            Toast.makeText(this, "You do not have an active internet connection. " +
                    "Please connect to the internet to be able to play.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    // this method checks whether there is an active network connection
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}

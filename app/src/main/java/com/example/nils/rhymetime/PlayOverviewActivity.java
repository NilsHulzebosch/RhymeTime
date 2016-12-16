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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_overview);
    }

    // when a user clicks a state button, go to the PlayActivity and pass the correct param
    public void goToPlay(View view) {
        Button b = (Button)view;
        String buttonText = b.getText().toString();

        if (isNetworkAvailable()) {
            Intent goToPlay = new Intent(this, PlayActivity.class);
            goToPlay.putExtra("stage", buttonText); // important
            startActivity(goToPlay);
            finish();
        } else {
            // without internet the API cannot be called, so stop the user here
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

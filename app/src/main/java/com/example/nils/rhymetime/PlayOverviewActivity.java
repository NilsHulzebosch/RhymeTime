package com.example.nils.rhymetime;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PlayOverviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_overview);
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
        }
        if (L2Completed) {
            button = (Button) findViewById(R.id.play2Button);
            button.setTextColor(Color.parseColor("#21FE80"));
        }
        if (L3Completed) {
            button = (Button) findViewById(R.id.play3Button);
            button.setTextColor(Color.parseColor("#21FE80"));
        }
        if (L4Completed) {
            button = (Button) findViewById(R.id.play4Button);
            button.setTextColor(Color.parseColor("#21FE80"));
        }
        if (L5Completed) {
            button = (Button) findViewById(R.id.play5Button);
            button.setTextColor(Color.parseColor("#21FE80"));
        }
    }

    // when a user clicks a level button, go to the PlayActivity
    public void goToPlay(View view) {
        Intent goToPlay = new Intent(this, PlayActivity.class);
        goToPlay.putExtra("niveau", "1");
        startActivity(goToPlay);
        finish();
    }
}

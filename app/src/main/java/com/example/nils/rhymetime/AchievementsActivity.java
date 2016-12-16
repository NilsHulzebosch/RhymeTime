package com.example.nils.rhymetime;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class AchievementsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);
        showAchievementStatus();
    }

    /* Get SharedPreferences for achievements. For every achievements that is still
     * unlocked, set opacity so you cannot read it but see that it's there.
     * The ones that are unlocked have their own color and remain unchanged.
     */
    public void showAchievementStatus() {
        SharedPreferences shared = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        boolean A1Unlocked = shared.getBoolean("A1Unlocked", false);
        boolean A2Unlocked = shared.getBoolean("A2Unlocked", false);
        boolean A3Unlocked = shared.getBoolean("A3Unlocked", false);
        boolean A4Unlocked = shared.getBoolean("A4Unlocked", false);
        boolean A5Unlocked = shared.getBoolean("A5Unlocked", false);

        int alpha = 15;
        TextView title;
        TextView text;

        // if achievement is not unlocked, set opacity to make it semi-invisible
        if (!A1Unlocked) {
            title = ((TextView)findViewById(R.id.A1Title));
            title.setTextColor(Color.argb(alpha, 255, 0, 0));
            text = ((TextView)findViewById(R.id.A1Text));
            text.setTextColor(Color.argb(alpha, 255, 0, 0));
        }
        if (!A2Unlocked) {
            title = ((TextView)findViewById(R.id.A2Title));
            title.setTextColor(Color.argb(alpha, 255, 0, 0));
            text = ((TextView)findViewById(R.id.A2Text));
            text.setTextColor(Color.argb(alpha, 255, 0, 0));
        }
        if (!A3Unlocked) {
            title = ((TextView)findViewById(R.id.A3Title));
            title.setTextColor(Color.argb(alpha, 255, 0, 0));
            text = ((TextView)findViewById(R.id.A3Text));
            text.setTextColor(Color.argb(alpha, 255, 0, 0));
        }
        if (!A4Unlocked) {
            title = ((TextView)findViewById(R.id.A4Title));
            title.setTextColor(Color.argb(alpha, 255, 0, 0));
            text = ((TextView)findViewById(R.id.A4Text));
            text.setTextColor(Color.argb(alpha, 255, 0, 0));
        }
        if (!A5Unlocked) {
            title = ((TextView)findViewById(R.id.A5Title));
            title.setTextColor(Color.argb(alpha, 255, 0, 0));
            text = ((TextView)findViewById(R.id.A5Text));
            text.setTextColor(Color.argb(alpha, 255, 0, 0));
        }
    }
}

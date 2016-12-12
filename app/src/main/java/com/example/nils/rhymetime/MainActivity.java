package com.example.nils.rhymetime;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void goToPlayOverview(View view) {
        Intent goToPlayOverview = new Intent(this, PlayOverviewActivity.class);
        startActivity(goToPlayOverview);
    }

    public void goToScoreOverview(View view) {
        Intent goToScoreOverview = new Intent(this, ScoreOverviewActivity.class);
        startActivity(goToScoreOverview);
    }

    public void goToAchievements(View view) {
        Intent goToAchievements = new Intent(this, AchievementsActivity.class);
        startActivity(goToAchievements);
    }

    public void goToInstructions(View view) {
        Intent goToInstructions = new Intent(this, InstructionsActivity.class);
        startActivity(goToInstructions);
    }
}

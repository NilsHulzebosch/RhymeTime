package com.example.nils.rhymetime;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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

        // if the user plays the game for the first time, show instruction screens
        SharedPreferences shared = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        boolean firstTime = shared.getBoolean("firstTime", true);
        if (firstTime) {
            showWelcomeDialog1();
        }
    }

    // first "screen" for instructions
    public void showWelcomeDialog1() {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                new ContextThemeWrapper(this, R.style.AlertDialogCustom));
        builder.setTitle("Instructions (1/3)");
        builder.setMessage("Welcome! Thanks for downloading. " +
                "In this game you have to find as much rhyme words as possible " +
                "within the specified time. Do you got what it takes to become " +
                "the next rhyme master?");
        builder.setNegativeButton(">", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                showWelcomeDialog2();
            }
        });
        builder.show();
    }

    // second "screen" for instructions
    public void showWelcomeDialog2() {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                new ContextThemeWrapper(this, R.style.AlertDialogCustom));
        builder.setTitle("Instructions (2/3)");
        builder.setMessage("You can choose your difficulty, ranging from Easy to Insane. " +
                "The difficulty influences the words as well as the time to rhyme. " +
                "You can also earn achievements and keep track of your scores, " +
                "these can be found in the Main Menu.");
        builder.setNegativeButton(">", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                showWelcomeDialog3();
            }
        });
        builder.setPositiveButton("<", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                showWelcomeDialog1();
            }
        });
        builder.show();
    }

    // third "screen" for instructions
    public void showWelcomeDialog3() {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                new ContextThemeWrapper(this, R.style.AlertDialogCustom));

        builder.setTitle("Instructions (3/3)");
        builder.setMessage("You can always go to the Instructions from the Main Menu " +
                "when you forget something. Have fun playing!");
        builder.setNegativeButton("OK!", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // save to SharedPreferences
                SharedPreferences.Editor editor = getSharedPreferences(
                        "SharedPreferences", MODE_PRIVATE).edit();
                editor.putBoolean("firstTime", false);
                editor.apply();

            }
        });
        builder.setPositiveButton("<", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                showWelcomeDialog2();
            }
        });
        builder.show();
    }

    /* Intents for the four activities
     * No finish() because the MainActivity (Main Menu) is the fallback place
     * where the user goes to when clicking the back button.
     */
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

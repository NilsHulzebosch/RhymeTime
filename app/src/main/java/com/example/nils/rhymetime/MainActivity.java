package com.example.nils.rhymetime;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private String userID;
    private String username;

    private Button singInOrOutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView welcomeTV = (TextView) findViewById(R.id.welcomeTV);
        singInOrOutButton = (Button) findViewById(R.id.singInOrOutButton);

        Bundle extras = getIntent().getExtras();
        Boolean anonymous = false;
        if (extras != null){
            anonymous = extras.getBoolean("anonymous", false);
        }

        if (anonymous) {
            username = "anonymous";
            welcomeTV.setText("Welcome, anonymous.");
            singInOrOutButton.setText("Sign in");
        } else {
            firebaseAuth = FirebaseAuth.getInstance();
            firebaseUser = firebaseAuth.getCurrentUser();
            if (firebaseUser == null) {
                Intent startSignIn = new Intent(this, SignInActivity.class);
                startActivity(startSignIn);
            } else {
                userID = firebaseUser.getUid();
                System.out.println("USER ID: " + userID);

                username = firebaseUser.getDisplayName();
                String welcomeStr = "Signed in as " + username;
                welcomeTV.setText(welcomeStr);

                singInOrOutButton.setText("Sign out");
            }
        }

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

    /* This method turns the full name of the user into the first name only.
     * This is for UI purposes when showing the scores. Of course it is better to have
     * a username instead of someone's real name to prevent duplicates, but for now
     * his will suffices (also because the people that will play this game will
     * not have the same name, because only a handful of people (I know) will play it).
     */
    public void getFirstName() {

    }

    /* Intents for the four activities
     * No finish() because the MainActivity (Main Menu) is the fallback place
     * where the user goes to when clicking the back button.
     */
    public void goToPlayOverview(View view) {
        Intent goToPlayOverview = new Intent(this, PlayOverviewActivity.class);
        goToPlayOverview.putExtra("username", username);
        startActivity(goToPlayOverview);
    }

    public void goToScoreOverview(View view) {
        Intent goToScoreOverview = new Intent(this, ScoreOverviewActivity.class);
        goToScoreOverview.putExtra("username", username);
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

    public void singInOrOut(View view) {
        // sign out and 'refresh' by going to MainActivity again
        if (singInOrOutButton.getText().equals("Sign out")) {
            FirebaseAuth.getInstance().signOut();
            Intent refresh = new Intent(this, MainActivity.class);
            refresh.putExtra("anonymous", true);
            startActivity(refresh);
            finish();

            // sign in by going to signInActivity
        } else {
            firebaseAuth = FirebaseAuth.getInstance();
            firebaseUser = firebaseAuth.getCurrentUser();
            if (firebaseUser == null) {
                Intent startSignIn = new Intent(this, SignInActivity.class);
                startActivity(startSignIn);
            }
        }
    }
}

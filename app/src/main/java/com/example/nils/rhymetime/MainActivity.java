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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private String username;
    private boolean anonymous;

    private TextView welcomeTV;
    private Button singInOrOutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        welcomeTV = (TextView) findViewById(R.id.welcomeTV);
        singInOrOutButton = (Button) findViewById(R.id.singInOrOutButton);

        // check whether there was an intent
        Bundle extras = getIntent().getExtras();
        anonymous = false;
        if (extras != null){
            anonymous = extras.getBoolean("anonymous", false);
        }

        handleAuthentication(); // log in or continue as 'anonymous'

        // if the user plays the game for the first time, show instruction screens
        SharedPreferences shared = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        boolean firstTime = shared.getBoolean("firstTime", true);
        if (firstTime) {
            showWelcomeDialog1();
        }
    }

    // set correct text and button based on login or not
    public void handleAuthentication() {

        if (anonymous) {
            username = "anonymous";
            String welcomeText = "Welcome, anonymous.";
            welcomeTV.setText(welcomeText);

            String signInText = "Sign in";
            singInOrOutButton.setText(signInText);

        } else {
            firebaseAuth = FirebaseAuth.getInstance();
            firebaseUser = firebaseAuth.getCurrentUser();
            if (firebaseUser == null) {
                Intent startSignIn = new Intent(this, SignInActivity.class);
                startActivity(startSignIn);
            } else {
                String userID = firebaseUser.getUid();
                System.out.println("USER ID: " + userID);

                username = getFirstName(firebaseUser.getDisplayName());
                String welcomeStr = "Signed in as " + username;
                welcomeTV.setText(welcomeStr);

                String signOutText = "Sign out";
                singInOrOutButton.setText(signOutText);
            }

            // create SharedPreferences editor to save
            SharedPreferences.Editor editor = getSharedPreferences(
                    "SharedPreferences", MODE_PRIVATE).edit();
            editor.putString("username", username); // save to SharedPreferences
            editor.apply();
        }
    }

    // first "screen" for instructions
    public void showWelcomeDialog1() {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                new ContextThemeWrapper(this, R.style.AlertDialogCustom));
        builder.setCancelable(false);
        builder.setTitle("Instructions (1/3)");
        builder.setMessage("Welcome! " +
                "In this game you have to find as much rhyme words as possible " +
                "within the specified time. Do you got what it takes to become " +
                "the next rhyme master?");
        builder.setNegativeButton("Yeah!!", new DialogInterface.OnClickListener() {
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
        builder.setCancelable(false);
        builder.setTitle("Instructions (2/3)");
        builder.setMessage("You can choose your difficulty, ranging from Easy to Insane. " +
                "The difficulty influences the words as well as the time to rhyme. " +
                "You can also earn achievements and keep track of your scores " +
                "in the global leaderboard. These can be found in the Main Menu.");
        builder.setPositiveButton("Got it!", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                showWelcomeDialog3();
            }
        });
        builder.setNegativeButton("<", new DialogInterface.OnClickListener() {
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
        builder.setCancelable(false);
        builder.setTitle("Instructions (3/3)");
        builder.setMessage("Important: you will need an active internet connection to be able " +
                "to play the game and check out the leaderboard. " +
                "Also, you can only play in portrait mode. Have fun playing!");
        builder.setPositiveButton("Thanks!", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // save to SharedPreferences
                SharedPreferences.Editor editor = getSharedPreferences(
                        "SharedPreferences", MODE_PRIVATE).edit();
                editor.putBoolean("firstTime", false);
                editor.apply();

            }
        });
        builder.setNegativeButton("<", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                showWelcomeDialog2();
            }
        });
        builder.show();
    }

    /* This method turns the full name of the user into the first name only.
     * This is for UI purposes when showing the scores AND for JSON purposes,
     * because using whitespace in a string can result in errors.
     * (An unique username would be better but for now it's OK).
     */
    public String getFirstName(String fullUsername) {

        int index = fullUsername.indexOf(" "); // find first occurrence of " "

        // if a whitespace is found, set correct username until the whitespace
        if (index != -1) {
            return fullUsername.substring(0, index);
        } else {
            // otherwise, take substring of the name
            if (fullUsername.length() > 9) {
                return fullUsername.substring(0, 9);
            } else {
                return fullUsername.substring(0, fullUsername.length());
            }
        }
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

    // when the button is pressed, check state and choose correct action
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

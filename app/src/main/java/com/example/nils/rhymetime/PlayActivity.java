package com.example.nils.rhymetime;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class PlayActivity extends AppCompatActivity {

    private String theRhymeWord; // the word you have to rhyme with in this level
    private String searchResults; // all rhyme-words from the API as a string

    private String niveau; // easy, medium, etc. (param from PlayOverviewActivity)

    // from searchResults (String) to JSON Object to ArrayList of RhymeWord objects
    private ArrayList<RhymeWord> allRhymeWords;

    // ArrayList of String of words that are already guessed by the user,
    // used to prevent duplicates but also for the UI by the Adapter
    private ArrayList<String> alreadyGuessed;

    private int currentScore; // score for the word the user has currently guessed
    private String currentWord; // word the user has currently guessed
    private int totalScore; // total score in this round
    private int totalRhymeWordsFound; // total rhyme words found in this round

    // TextViews/EditText for the UI
    private TextView wordToRhymeWithTV;
    private TextView timerTV;
    private View lineView;
    private EditText currentWordET;
    private Button checkButton;
    private GridView alreadyGuessedGridView;
    private CustomAdapter toDoListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        initializeParameters();
        showStartingDialog();
        initializeAdapter();

        Bundle extras = getIntent().getExtras();
        niveau = extras.getString("niveau");
        getAllRhymes();
    }

    // initialize all (xml) objects and variables
    public void initializeParameters() {
        theRhymeWord = "sing";
        alreadyGuessed = new ArrayList<>();
        wordToRhymeWithTV = (TextView) findViewById(R.id.wordToRhymeWithTV);
        timerTV = (TextView) findViewById(R.id.timer);
        lineView = findViewById(R.id.lineView);
        currentWordET = (EditText) findViewById(R.id.currentWordET);
        checkButton = (Button) findViewById(R.id.checkButton);
        totalRhymeWordsFound = 0;
        totalScore = 0;
    }

    public void initializeAdapter() {
        alreadyGuessedGridView = (GridView) findViewById(R.id.alreadyGuessedGridView);
        toDoListAdapter = new CustomAdapter(this, alreadyGuessed);
        alreadyGuessedGridView.setAdapter(toDoListAdapter);
    }

    public void updateAdapter() {
        toDoListAdapter.notifyDataSetChanged();
        currentWordET.setFocusableInTouchMode(true);
        currentWordET.requestFocus();
    }

    /* This method shows the dialog when the user enters this activity.
     * The user gets information about the word, the time and other restrictions.
     * The user can press 'start' or go back to the Main Menu.
     * Pressing start makes the UI visible and sets the timer.
     */
    public void showStartingDialog() {
        AlertDialog.Builder startBuilder = new AlertDialog.Builder(
                new ContextThemeWrapper(this, R.style.AlertDialogCustom));
        startBuilder.setTitle("The word you have to rhyme with is \'" + theRhymeWord + "\'.");
        startBuilder.setMessage("You have 60 seconds. Try to find as much rhyme words as possible!");
        startBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // go back to PlayOverview
                Intent goToPlayOverview = new Intent(getApplicationContext(),
                        PlayOverviewActivity.class);
                startActivity(goToPlayOverview);
                finish();
            }
        });
        startBuilder.setPositiveButton("START!", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                makeUIVisible();
                setTimer();
            }
        });
        //AlertDialog startAlert = startBuilder.create();
        startBuilder.show();
    }

    // set all components to visible
    public void makeUIVisible() {
        wordToRhymeWithTV.setVisibility(View.VISIBLE);
        timerTV.setVisibility(View.VISIBLE);
        lineView.setVisibility(View.VISIBLE);
        currentWordET.setVisibility(View.VISIBLE);
        currentWordET.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        checkButton.setVisibility(View.VISIBLE);
    }

    // set all components to visible
    public void makeUIInVisible() {
        wordToRhymeWithTV.setVisibility(View.INVISIBLE);
        timerTV.setVisibility(View.INVISIBLE);
        lineView.setVisibility(View.INVISIBLE);
        currentWordET.setVisibility(View.INVISIBLE);
        InputMethodManager imm = (InputMethodManager)
                getApplicationContext().getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(currentWordET.getWindowToken(), 0);
        currentWordET.clearFocus();
        checkButton.setVisibility(View.INVISIBLE);
    }

    // set a timer corresponding to the level difficulty
    public void setTimer() {

        new CountDownTimer(60000, 1000) {

            // update UI
            public void onTick(long millisUntilFinished) {
                String timerText = "" + millisUntilFinished / 1000 + "";
                //if (millisUntilFinished / 1000 >= 10) {
                //    timerTV.setPadding(50, 50, 50, 50);
                //    timerTV.setGravity(Gravity.CENTER_HORIZONTAL);
                //    timerTV.setGravity(Gravity.CENTER_VERTICAL);
                //} else {
                //    timerTV.setPadding(80, 40, 80, 40);
                //}
                timerTV.setGravity(Gravity.CENTER_HORIZONTAL);
                timerTV.setGravity(Gravity.CENTER_VERTICAL);
                timerTV.setText(timerText);
            }

            // when time's up, hide UI and show dialog
            public void onFinish() {
                timerTV.setText("0");
                makeUIInVisible();
                showEndingDialog();
            }
        }.start();
    }

    public void showEndingDialog() {

        AlertDialog.Builder finishBuilder = new AlertDialog.Builder(
                new ContextThemeWrapper(this, R.style.AlertDialogCustom));

        finishBuilder.setTitle("Time's up!");
        if (totalRhymeWordsFound > 1) {
            finishBuilder.setMessage("You found " + totalRhymeWordsFound +
                        " rhyme words with a total of " + totalScore + " points, good job!");
        } else {
            finishBuilder.setMessage("You found " + totalRhymeWordsFound +
                    " rhyme words. You scored " + totalScore + " points, better luck next time!");
        }
        finishBuilder.setNegativeButton("Main Menu", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // go back to MainActivity
                Intent goToPlayOverview = new Intent(getApplicationContext(),
                        MainActivity.class);
                startActivity(goToPlayOverview);
                finish();
            }
        });
        finishBuilder.setPositiveButton("Challenge friend", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // send a challenge to a friend with the current word,
                // your score and amount of words
            }
        });
        //AlertDialog finishAlert = finishBuilder.create();
        finishBuilder.show();
    }

    /* This method calls the API with the word to be rhymed with and gets a String with
     * all rhyme words (if everything went OK). This is first transformed to a JSONObject,
     * then to a JSONArray, and finally to an ArrayList of RhymeWords.
     * This is used to check whether the user has a correct input.
     */
    public void getAllRhymes() {
        MyAsyncTask movieAsyncTask = new MyAsyncTask(this);
        movieAsyncTask.execute(theRhymeWord);
        String wordExpl = "\'" + theRhymeWord + "\'";
        wordToRhymeWithTV.setText(wordExpl);

        try {
            searchResults = movieAsyncTask.get();
        } catch (InterruptedException | ExecutionException e) {
            searchResults = "";
            e.printStackTrace();
        }

        if (searchResults.length() == 0 || searchResults.equals("[]")) {
            Toast.makeText(this, "No data was found", Toast.LENGTH_SHORT).show();
        } else {
            try {
                allRhymeWords = new ArrayList<>();
                JSONObject respObj = new JSONObject("{rhymes:" + searchResults + "}");
                JSONArray rhymeWordsJSON = respObj.getJSONArray("rhymes");

                for (int i = 0; i < rhymeWordsJSON.length(); i++) {
                    JSONObject currentObject = rhymeWordsJSON.getJSONObject(i);
                    String word;
                    int score = 0;
                    int numSyllables = 0;

                    word = currentObject.getString("word");
                    System.out.println("word" + word);

                    if (currentObject.has("score")) {
                        score = currentObject.optInt("score", 0);
                        System.out.println("score" + score);
                    }

                    if (currentObject.has("numSyllables")) {
                        numSyllables = currentObject.optInt("numSyllables", 0);
                        System.out.println("numSyllables" + numSyllables);
                    }

                    allRhymeWords.add(new RhymeWord(
                            word, score, numSyllables));
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // whenever a user clicks on the button, match input with allRhymeWords
    // based on this give positive or negative feedback and update UI and DB
    public void validateUserInput(View view) {
        // get current user input
        currentWord = currentWordET.getText().toString();
        System.out.print("Current word: " + currentWord);

        if (currentWord.length() != 0) {
            boolean wordMatchBool = false;
            boolean alreadyGuessedBool = false;
            System.out.print("Current word: " + currentWord);
            for (int i = 0; i < allRhymeWords.size(); i++) {
                System.out.print("i=" + i + ": " + allRhymeWords.get(i));
                if (allRhymeWords.get(i).word.equals(currentWord)) {
                    // if word is not in database, add it and increment score
                    // otherwise, notify that they already guessed this one
                    if (!alreadyGuessed.contains(currentWord)) {
                        alreadyGuessed.add(currentWord);
                        currentScore = allRhymeWords.get(i).score;
                        totalRhymeWordsFound += 1;
                        totalScore += currentScore;
                        wordMatchBool = true;
                        updateAdapter();
                    } else {
                        alreadyGuessedBool = true;
                    }
                    break;
                }
            }

            // customized Toast
            LinearLayout layout = new LinearLayout(this);
            TextView tv = new TextView(this);
            tv.setTextColor(Color.WHITE);
            tv.setTextSize(18);
            tv.setPadding(100, 40, 100, 40);
            tv.setWidth(2000);
            tv.setGravity(Gravity.CENTER_HORIZONTAL);
            tv.setGravity(Gravity.CENTER_VERTICAL);
            if (wordMatchBool) {
                // word correctly guessed
                layout.setBackgroundResource(R.color.correctWordToast);
                tv.setText("\'" + currentWord + "\' scored you " + currentScore + " points!");
            } else if (alreadyGuessedBool) {
                // word already guessed
                layout.setBackgroundResource(R.color.neutralWordToast);
                tv.setText("\'" + currentWord + "\' is already guessed.");
            } else {
                // word incorrectly guessed
                layout.setBackgroundResource(R.color.inCorrectWordToast);
                tv.setText("Unfortunately, that doesn't rhyme.");
            }
            layout.addView(tv);

            Toast toast = new Toast(this);
            toast.setView(layout);
            toast.setGravity(Gravity.TOP, 0, 0);
            toast.show();

            currentWordET.setText("");
        } else {
            Toast.makeText(this, "Please enter a word", Toast.LENGTH_SHORT).show();
        }

    }
}

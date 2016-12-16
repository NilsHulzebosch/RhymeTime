package com.example.nils.rhymetime;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class PlayActivity extends AppCompatActivity {

    private String stage; // easy, medium, hard or insane (param from PlayOverviewActivity)

    public List<String> possibleWords; // from here one word is randomly chosen
    private String theRhymeWord; // the chosen word you have to rhyme with

    // from searchResults (String) to JSON Object to ArrayList of RhymeWord objects
    private ArrayList<RhymeWord> allRhymeWords;

    // ArrayList of String of words that are already guessed by the user,
    // used to prevent duplicates but also for the UI by the Adapter
    private ArrayList<String> alreadyGuessed;

    private int currentScore; // score for the word the user has currently guessed
    private int totalScore; // total score in this round
    private int totalRhymeWordsFound; // total rhyme words found in this round
    private int totalTime; // start time of current stage
    private long remainingTime; // remaining time when entering pause dialog

    // all XML objects for the UI
    private TextView wordToRhymeWithTV;
    private TextView timerTV;
    private View lineView;
    private EditText currentWordET;
    private Button checkButton;
    private TextView infoTV;
    private GridView alreadyGuessedGridView;
    private CustomAdapter toDoListAdapter;

    private CountDownTimer countDownTimer; // timer

    private String username; // Google username when logged in, otherwise 'anonymous'


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        Bundle extras = getIntent().getExtras();
        stage = extras.getString("stage");
        username = extras.getString("username", "anonymous");

        setTimeAndPossibleWords();
        initializeParameters();
        showStartingDialog();
        initializeAdapter();
        getAllRhymes();
        setEditTextListener();
    }

    // initialize all (xml) objects and variables
    public void initializeParameters() {
        alreadyGuessed = new ArrayList<>();
        allRhymeWords = new ArrayList<>();

        wordToRhymeWithTV = (TextView) findViewById(R.id.wordToRhymeWithTV);
        timerTV = (TextView) findViewById(R.id.timer);
        lineView = findViewById(R.id.lineView);
        currentWordET = (EditText) findViewById(R.id.currentWordET);
        checkButton = (Button) findViewById(R.id.checkButton);
        infoTV = (TextView) findViewById(R.id.infoTV);
        totalRhymeWordsFound = 0;
        totalScore = 0;

        Random rand = new Random();
        int i = rand.nextInt((possibleWords.size()));
        System.out.println("i is:" + i + "  and possibleWords is " + possibleWords);
        theRhymeWord = possibleWords.get(i);
    }

    /* This method sets the correct time and the possible words list,
     * based on the difficulty of the chosen stage.
     * From this possible words list a random word is chosen.
     */
    public void setTimeAndPossibleWords() {
        possibleWords = new ArrayList<>();
        switch (stage) {
            case "easy":
                totalTime = 70000;
                possibleWords = Arrays.asList("sing", "cat", "you", "pan", "gong", "though");
                break;
            case "medium":
                totalTime = 60000;
                possibleWords = Arrays.asList("sleep", "blue", "cool",
                        "boat", "plant", "brick", "fire", "walk");
                break;
            case "hard":
                totalTime = 50000;
                possibleWords = Arrays.asList("drawer", "rural", "verse", "pork", "arm",
                        "height", "gum", "yellow", "house");
                break;
            case "insane":
                totalTime = 40000;
                possibleWords = Arrays.asList("babe", "against", "lyrics", "sullen",
                        "gravity", "subtle", "colonel", "lettuce", "squirrel",
                        "fruit", "chorus", "tough", "cough", "bought");
                break;
            default: // fallback
                totalTime = 60000;
                possibleWords = Arrays.asList("random", "native", "app", "studio");
                break;
        }
    }

    // initialize GridView and CustomAdapter
    public void initializeAdapter() {
        alreadyGuessedGridView = (GridView) findViewById(R.id.alreadyGuessedGridView);
        toDoListAdapter = new CustomAdapter(this, alreadyGuessed);
        alreadyGuessedGridView.setAdapter(toDoListAdapter);
    }

    // when a word is guessed (and added to ArrayList), update adapter
    public void updateAdapter() {
        toDoListAdapter.notifyDataSetChanged();
        currentWordET.setFocusableInTouchMode(true);
        currentWordET.requestFocus();
    }

    /* This method shows the welcome dialog when the user enters this activity.
     * The user gets information about the word and the time ((and other restrictions)).
     * The user can press 'start' or go back to the Main Menu.
     * Pressing start makes the UI visible and sets the timer (starting automatically).
     */
    public void showStartingDialog() {
        AlertDialog.Builder startBuilder = new AlertDialog.Builder(
                new ContextThemeWrapper(this, R.style.AlertDialogCustom));
        startBuilder.setCancelable(false);
        startBuilder.setTitle("The word you have to rhyme with is \'" + theRhymeWord + "\'.");
        startBuilder.setMessage("You have 60 seconds. Try to find as much rhyme words as possible!");
        startBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // go back to PlayOverview
                Intent goToPlayOverview = new Intent(getApplicationContext(),
                        PlayOverviewActivity.class);
                goToPlayOverview.putExtra("username", username);
                startActivity(goToPlayOverview);
                finish();
            }
        });
        startBuilder.setPositiveButton("START!", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                makeUIVisible();
                setTimer(totalTime);
            }
        });
        startBuilder.show();
    }

    // set all components to visible (during the welcome dialog, these objects are
    // invisible because they can be a distraction in the background)
    public void makeUIVisible() {
        wordToRhymeWithTV.setVisibility(View.VISIBLE);
        timerTV.setVisibility(View.VISIBLE);
        lineView.setVisibility(View.VISIBLE);
        currentWordET.setVisibility(View.VISIBLE);
        currentWordET.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        checkButton.setVisibility(View.VISIBLE);
        infoTV.setVisibility(View.VISIBLE);
    }

    // set all components to invisible (when the time's up, the end dialog shows up
    // and the objects should be invisible again to prevent distractions)
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
        infoTV.setVisibility(View.INVISIBLE);
        alreadyGuessedGridView.setVisibility(View.INVISIBLE);
    }

    // set a timer corresponding to the level's difficulty
    public void setTimer(long totalTime) {

        countDownTimer = new CountDownTimer(totalTime, 1000) {

            // update UI while the timer isn't finished
            public void onTick(long millisUntilFinished) {
                remainingTime = millisUntilFinished;
                String timerText = "" + millisUntilFinished / 1000 + "";
                if (millisUntilFinished / 1000 >= 10) {
                    timerTV.setPadding(48, 0, 0, 0);
                } else {
                    timerTV.setPadding(75, 0, 0, 0);
                }
                timerTV.setGravity(Gravity.CENTER_HORIZONTAL);
                timerTV.setGravity(Gravity.CENTER_VERTICAL);
                timerTV.setText(timerText);
            }

            // when time's up...
            public void onFinish() {
                timerTV.setText("0");
                makeUIInVisible(); // hide UI
                saveScoreToDB(); // save score to FireBase database
                showEndingDialog(); // show how their game went
                checkForAchievements(); // if it applies, show achievements
            }

        }.start();
    }

    /* The dialog shown when time's up. Let the user see their score and amount
     * of words correctly guessed (if they guessed at least one word).
     * Give them the option to return to Main Menu or challenge a fried (currently NOT working).
     * Challenging a friend will probably be replaced by some other button like "see scores".
     */
    public void showEndingDialog() {
        AlertDialog.Builder finishBuilder = new AlertDialog.Builder(
                new ContextThemeWrapper(this, R.style.AlertDialogCustom));
        finishBuilder.setCancelable(false);
        finishBuilder.setTitle("Time's up!");
        if (totalRhymeWordsFound > 1) {
            finishBuilder.setMessage("You found " + totalRhymeWordsFound +
                    " rhyme words with a total of " + totalScore + " points, good job!");
        } else {
            finishBuilder.setMessage("You found " + totalRhymeWordsFound +
                    " rhyme words. You scored " + totalScore + " points, better luck next time!");
        }
        finishBuilder.setPositiveButton("Main Menu", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // go back to MainActivity
                Intent goToPlayOverview = new Intent(getApplicationContext(),
                        MainActivity.class);
                if (username.equals("anonymous")) {
                    goToPlayOverview.putExtra("anonymous", true);

                }
                startActivity(goToPlayOverview);
                finish();
            }
        });
        finishBuilder.setNegativeButton("Leaderboard", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // go back to ScoreOverviewActivity
                Intent goToScoreOverview = new Intent(getApplicationContext(),
                        ScoreOverviewActivity.class);
                if (username.equals("anonymous")) {
                    goToScoreOverview.putExtra("anonymous", true);

                }
                startActivity(goToScoreOverview);
                finish();
            }
        });
        finishBuilder.show();
    }

    /* This method calls the API with the word to be rhymed with and gets a String with
     * all rhyme words (if everything went OK). This is first transformed to a JSONObject,
     * then to a JSONArray, and finally to an ArrayList of RhymeWords.
     * This is used to check whether the user has a correct input during the game and also
     * to assign the correct scores.
     */
    public void getAllRhymes() {
        MyAsyncTask movieAsyncTask = new MyAsyncTask(this);
        movieAsyncTask.execute("rhymeWord", theRhymeWord);
        String wordExpl = "\'" + theRhymeWord + "\'";
        wordToRhymeWithTV.setText(wordExpl);

        String searchResults;
        try {
            searchResults = movieAsyncTask.get();
        } catch (InterruptedException | ExecutionException e) {
            searchResults = "";
            e.printStackTrace();
        }

        // if no results (so API went wrong), go back to PlayOverviewActivity
        if (searchResults.length() == 0 || searchResults.equals("[]")) {
            Toast.makeText(this, "No data was found", Toast.LENGTH_SHORT).show();
            Intent goToPlayOverviewActivity = new Intent(getApplicationContext(),
                    PlayOverviewActivity.class);
            startActivity(goToPlayOverviewActivity);
            finish();
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

                    if (currentObject.has("score")) {
                        score = currentObject.optInt("score", 0);
                        score = score / rhymeWordsJSON.length() * 10; // normalize score
                    }

                    if (currentObject.has("numSyllables")) {
                        numSyllables = currentObject.optInt("numSyllables", 0);
                    }

                    allRhymeWords.add(new RhymeWord(
                            word, score, numSyllables));
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // this method is called whenever the 'Check' button is clicked
    public void callValidateUserInput(View view) {
        validateUserInput();
    }

    /* Besides pressing the 'Check' button after typing in a rhyme word, the user should
     * also be able to just press "Enter" to perform a search action. That's why we add
     * an event listener to the EditText that calls validateUserInput on an Enter-press.
     */
    public void setEditTextListener() {
        currentWordET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction (TextView v,int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    validateUserInput();
                    return true;
                }
                return false;
            }
        });
    }

    /* Whenever a user clicks on the 'check' button, match input with allRhymeWords ArrayList.
     * Based on this give positive or negative feedback and update alreadyGuessed ArrayList
     * (used for the ending of the level for correct score AND UI).
     */
    public void validateUserInput() {
        String currentWord = currentWordET.getText().toString();

        if (currentWord.length() != 0) {
            boolean wordMatchBool = false;
            boolean alreadyGuessedBool = false;

            for (int i = 0; i < allRhymeWords.size(); i++) {
                if (allRhymeWords.get(i).word.equals(currentWord)) {
                    // if word is not in database, add it and increment totalScore
                    // otherwise, notify that they already guessed this one (no cheating!)
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

            // customized LinearLayout/TextView for Toast
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
                tv.setText("Unfortunately, \'" + currentWord + "\' doesn't rhyme.");
            }
            layout.addView(tv);

            // Toast settings
            Toast toast = new Toast(this);
            toast.setView(layout);
            toast.setGravity(Gravity.TOP, 0, 0);
            toast.show();

            currentWordET.setText("");
        } else {
            Toast.makeText(this, "Please enter a word", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    // when the back button is pressed (twice), let user confirm they want to leave
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            countDownTimer.cancel(); // pause timer
            showLeavingDialog(remainingTime); // show 'are you sure you want to leave'-dialog
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /* This dialog is shown when the user wants to leave during the game
     * (by pressing the back button twice). A message shows up, asking whether the user
     * is sure it wants to leave the game. During the dialog the timer is 'paused':
     * the timer is destroyed, remaining time is stored and a new timer is created when
     * the user decides it wants to play further.
     * Otherwise, they will go back to the Main Menu and their game is lost.
     */
    public void showLeavingDialog(final long remainingTime) {
        AlertDialog.Builder leaveBuilder = new AlertDialog.Builder(
                new ContextThemeWrapper(this, R.style.AlertDialogCustom));
        leaveBuilder.setCancelable(false);
        leaveBuilder.setTitle("Psst");
        leaveBuilder.setMessage("Your current progress will not be saved. " +
                " Are you sure you want to leave?");
        leaveBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // new timer
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                setTimer(remainingTime);
            }
        });
        leaveBuilder.setPositiveButton("OK!", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // go back to MainActivity
                Intent goToMainActivity = new Intent(getApplicationContext(),
                        MainActivity.class);
                if (username.equals("anonymous")) {
                    goToMainActivity.putExtra("anonymous", true);

                }
                startActivity(goToMainActivity);
                finish();
            }
        });
        leaveBuilder.show();
    }

    /* This method looks which achievements are already unlocked,
     * and for the ones that are still unlocked, check whether they are unlocked
     * during (i.e. when finishing) this game. If they are unlocked by their score,
     * show customized Toast and add to achievements (SharedPreferences).
     */
    public void checkForAchievements() {
        // get SharedPreferences
        SharedPreferences shared = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        boolean A1Completed = shared.getBoolean("A1Unlocked", false);
        boolean A2Completed = shared.getBoolean("A2Unlocked", false);
        boolean A3Completed = shared.getBoolean("A3Unlocked", false);
        boolean A4Completed = shared.getBoolean("A4Unlocked", false);
        boolean A5Completed = shared.getBoolean("A5Unlocked", false);

        // customized LinearLayout/TextView for Toast
        LinearLayout layout = new LinearLayout(this);
        TextView tv = new TextView(this);
        tv.setTextColor(Color.BLACK);
        tv.setTextSize(20);
        tv.setPadding(100, 100, 100, 100);
        tv.setWidth(2000);
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setGravity(Gravity.CENTER_VERTICAL);

        // create SharedPreferences editor to save
        SharedPreferences.Editor editor = getSharedPreferences(
                "SharedPreferences", MODE_PRIVATE).edit();

        // if achievement 1 still locked...
        if (!A1Completed) {
            // if game is finished, unlock "The Gentlest Experimentalist"
            if (true) {
                editor.putBoolean("A1Unlocked", true); // save to SharedPreferences
                layout.setBackgroundResource(R.color.A2Color); // give Toast
                tv.setText("Congratulations! You unlocked \n" +
                        "\'The Gentlest Experimentalist\' \n" +
                        "You found at least one rhyme word in every stage. " +
                        "Keep up the good work!");
            }
        }

        // if achievement 2 still locked...
        if (!A2Completed) {
            // and if stage = easy & words >= 10, unlock "Easy Complication"
            if (stage.equals("easy") && totalRhymeWordsFound >= 10) {
                editor.putBoolean("A2Unlocked", true); // save to SharedPreferences
                layout.setBackgroundResource(R.color.A1Color); // give Toast
                tv.setText("Congratulations! You unlocked \n" +
                        "\'Easy Complication\' \n " +
                        "You found " + totalRhymeWordsFound + " words in Easy mode. " +
                        "Easy Peasy huh?");
            }
        }

        // if achievement 3 still locked...
        if (!A3Completed) {
            // and if score >= 1001, unlock "1001 Nights of Practice"
            if (totalScore >= 1001) {
                editor.putBoolean("A3Unlocked", true); // save to SharedPreferences
                layout.setBackgroundResource(R.color.A3Color); // give Toast
                tv.setText("Congratulations! You unlocked \n" +
                        "\'1001 Nights of Practice\' \n" +
                        "You scored " + totalScore + "points. Nice!");
            }
        }

        // if achievement 4 still locked...
        if (!A4Completed) {
            // and if stage = hard & words >= 6, unlock "Shaky Spears!"
            if (stage.equals("hard") && totalRhymeWordsFound >= 6) {
                editor.putBoolean("A4Unlocked", true); // save to SharedPreferences
                layout.setBackgroundResource(R.color.A4Color); // give Toast
                tv.setText("Congratulations! You unlocked \n" +
                        "\'Shaky Spears!\' \n" +
                        "You found " + totalRhymeWordsFound +
                        " words in hard mode. Good job!");
            }
        }

        // if achievement 5 still locked...
        if (!A5Completed) {
            // and if stage = insane & words >= 8, unlock "You should be a rapper :-)"
            if (stage.equals("insane") && totalRhymeWordsFound >= 8) {
                editor.putBoolean("A5Unlocked", true); // save to SharedPreferences
                layout.setBackgroundResource(R.color.A5Color); // give Toast
                tv.setText("Congratulations! You unlocked \n" +
                        "\'You should be a rapper :-)\' \n " +
                        "You found " + totalRhymeWordsFound +
                        " words in insane mode. Insane!");
            }
        }
        editor.apply();

        // Toast settings
        layout.addView(tv);
        final Toast toast = new Toast(this);
        toast.setView(layout);
        toast.setGravity(Gravity.TOP, Gravity.CENTER_HORIZONTAL, Gravity.CENTER_VERTICAL);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();

        // Toast.LENGTH_LONG (3.5 sec) + 3 sec = 6.5 sec
        new CountDownTimer(3000, 1000)
        {
            public void onTick(long millisUntilFinished) {toast.show();}
            public void onFinish() {toast.show();}

        }.start();
    }

    // when level is finished, save score to FireBase database
    public void saveScoreToDB() {
        // create new score object with the correct variables
        Score score = new Score(username,
                totalScore,
                totalRhymeWordsFound,
                stage,
                theRhymeWord,
                alreadyGuessed);

        // save score object to database
        Firebase.setAndroidContext(this);
        String FIREBASE_URL = "https://rhymetime-b8195.firebaseio.com/";
        Firebase ref = new Firebase(FIREBASE_URL);
        Firebase newRef = ref.child("Scores").push();
        newRef.setValue(score);
    }
}

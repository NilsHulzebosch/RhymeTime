package com.example.nils.rhymetime;

import android.app.Activity;
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
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class PlayActivity extends AppCompatActivity {

    public List<String> possibleWords; // from here one word is randomly chosen
    private String theRhymeWord; // the chosen word you have to rhyme with
    private String searchResults; // all rhyme-words for this word from the API as a string

    private String stage; // easy, medium, etc. (param from PlayOverviewActivity)

    // from searchResults (String) to JSON Object to ArrayList of RhymeWord objects
    private ArrayList<RhymeWord> allRhymeWords;

    // ArrayList of String of words that are already guessed by the user,
    // used to prevent duplicates but also for the UI by the Adapter
    private ArrayList<String> alreadyGuessed;

    private int currentScore; // score for the word the user has currently guessed
    private String currentWord; // word the user has currently guessed
    private int totalScore; // total score in this round
    private int totalRhymeWordsFound; // total rhyme words found in this round
    private int totalTime; // start time of current stage
    private long remainingTime; // remaining time when pressing 'back'-button
    private int minSyllables; // min. amount of syllables for your rhyme words
    private int maxSyllables; // max. amount of syllables for your rhyme words

    // all XML objects for the UI
    private TextView wordToRhymeWithTV;
    private TextView timerTV;
    private View lineView;
    private EditText currentWordET;
    private Button checkButton;
    private TextView infoTV;
    private GridView alreadyGuessedGridView;
    private CustomAdapter toDoListAdapter;

    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        Bundle extras = getIntent().getExtras();
        stage = extras.getString("stage");

        setTimeAndPossibleWords();
        initializeParameters();
        showStartingDialog();
        initializeAdapter();
        getAllRhymes();
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
        infoTV = (TextView)findViewById(R.id.infoTV);
        totalRhymeWordsFound = 0;
        totalScore = 0;

        Random rand = new Random();
        int i = rand.nextInt((possibleWords.size()) + 1);
        System.out.println("i is:" + i + "  and possibleWords is " + possibleWords);
        theRhymeWord = possibleWords.get(i);
    }

    /* This method sets the correct time and the possible words list,
     * based on the difficulty of the chosen stage. From this possible words list,
     * a random word is chosen. When the "Random" stage is chosen,
     * a totally random word is chosen using an API.
     */
    public void setTimeAndPossibleWords() {
        switch (stage) {
            case "easy":
                totalTime = 70000;
                possibleWords = Arrays.asList("sing", "cool", "boat", "plant",
                        "cat", "walk", "blue", "pan", "brick", "fire");
                minSyllables = 0;
                maxSyllables = 25;
                break;
            case "medium":
                totalTime = 60000;
                possibleWords = Arrays.asList("house", "sleep", "yellow");
                minSyllables = 0;
                maxSyllables = 25;
                break;
            case "hard":
                totalTime = 50000;
                Random rand1 = new Random();
                int randomNum1 = rand1.nextInt((1) + 1);
                if (randomNum1 < 0.5) {
                    possibleWords = Arrays.asList("fruit", "though", "height", "gum");
                    minSyllables = 3;
                    maxSyllables = 3;
                } else {
                    possibleWords = Arrays.asList("drawer", "rural", "verse", "pork");
                    minSyllables = 0;
                    maxSyllables = 25;
                }

                break;
            case "insane":
                totalTime = 40000;
                Random rand2 = new Random();
                int randomNum2 = rand2.nextInt((1) + 1);
                if (randomNum2 < 0.335) {
                    possibleWords = Arrays.asList("fruit", "though");
                    minSyllables = 4;
                    maxSyllables = 4;
                } else if (randomNum2 < 0.67) {
                    possibleWords = Arrays.asList("chorus", "tough", "cough", "bought", "arm");
                    minSyllables = 3;
                    maxSyllables = 25;
                } else {
                    possibleWords = Arrays.asList("babe", "against", "lyrics", "sullen",
                            "gravity", "subtle", "colonel", "lettuce", "squirrel", "heighth");
                    minSyllables = 0;
                    maxSyllables = 25;
                }

                break;
            default:
                totalTime = 60000;
                MyAsyncTask movieAsyncTask = new MyAsyncTask(this);
                movieAsyncTask.execute("randomWord");
                String randomWordResult;
                try {
                    randomWordResult = movieAsyncTask.get();
                } catch (InterruptedException | ExecutionException e) {
                    randomWordResult = "";
                    e.printStackTrace();
                }

                if (randomWordResult.length() == 0) {
                    Toast.makeText(this, "No data was found", Toast.LENGTH_SHORT).show();
                } else {
                    minSyllables = 0;
                    maxSyllables = 25;
                    possibleWords = Arrays.asList(randomWordResult);
                }
                break;
        }
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
                setTimer(totalTime);
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
        infoTV.setVisibility(View.VISIBLE);
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
        infoTV.setVisibility(View.INVISIBLE);
    }

    // set a timer corresponding to the level difficulty
    public void setTimer(long totalTime) {

        countDownTimer = new CountDownTimer(totalTime, 1000) {

            // update UI
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

            // when time's up, hide UI and show dialog
            public void onFinish() {
                timerTV.setText("0");
                makeUIInVisible();
                showEndingDialog();
                checkForAchievements();
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
        movieAsyncTask.execute("rhymeWord", theRhymeWord);
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
            // go back to PlayOverviewActivity
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



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            countDownTimer.cancel(); // pause timer
            long time = remainingTime;
            showLeavingDialog(remainingTime);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void showLeavingDialog(final long remainingTime) {
        AlertDialog.Builder leaveBuilder = new AlertDialog.Builder(
                new ContextThemeWrapper(this, R.style.AlertDialogCustom));
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
                startActivity(goToMainActivity);
                finish();
            }
        });
        //AlertDialog leaveAlert = startBuilder.create();
        leaveBuilder.show();
    }

    /* This method looks which achievements are already unlocked,
     * and for the ones that are still unlocked, check whether they are unlocked
     * during this game. If they are unlocked, show message and add to achievemens.
     */
    public void checkForAchievements() {
        // get SharedPreferences
        SharedPreferences shared = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        boolean A1Completed = shared.getBoolean("A1Unlocked", false);
        boolean A2Completed = shared.getBoolean("A2Unlocked", false);
        boolean A3Completed = shared.getBoolean("A3Unlocked", false);
        boolean A4Completed = shared.getBoolean("A4Unlocked", false);
        boolean A5Completed = shared.getBoolean("A5Unlocked", false);

        // customized Toast
        LinearLayout layout = new LinearLayout(this);
        TextView tv = new TextView(this);
        tv.setTextColor(Color.BLACK);
        tv.setTextSize(20);
        tv.setPadding(100, 100, 100, 100);
        tv.setWidth(2000);
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setGravity(Gravity.CENTER_VERTICAL);

        // save to SharedPreferences
        SharedPreferences.Editor editor = getSharedPreferences(
                "SharedPreferences", MODE_PRIVATE).edit();

        if (!A1Completed) {
            // if stage = easy & words >= 10, unlock "Easy Complication"
            if (stage.equals("easy") && totalRhymeWordsFound >= 10) {
                editor.putBoolean("A1Unlocked", true); // save to SharedPreferences

                // give Toast
                layout.setBackgroundResource(R.color.A1Color);
                tv.setText("Congratulations! You unlocked \n" +
                        "\'Easy Complication\' \n " +
                        "You found " + totalRhymeWordsFound + " words in Easy mode." +
                        "Easy peasy ha?");
            }
        }

        if (!A2Completed) {
            // if found >= 1 rhyme word in every stage, unlock "The Gentlest Experimentalist"
            if (true) {
                editor.putBoolean("A2Unlocked", true); // save to SharedPreferences

                // give Toast
                layout.setBackgroundResource(R.color.A2Color);
                tv.setText("Congratulations! You unlocked \n" +
                        "\'The Gentlest Experimentalist\' \n " +
                        "You found at least one rhyme word in every stage." +
                        "Keep up the good work!");
            }
        }

        if (!A3Completed) {
            // if score >= 1001, unlock "1001 Nights of Practice"
            if (totalScore >= 1001) {
                editor.putBoolean("A3Unlocked", true); // save to SharedPreferences

                // give Toast
                layout.setBackgroundResource(R.color.A3Color);
                tv.setText("Congratulations! You unlocked \n" +
                        "\'1001 Nights of Practice\' \n " +
                        "You scored " + totalScore + "points. Nice!");
            }
        }

        if (!A4Completed) {
            // if stage = hard & words >= 6, unlock "Shaky Spears!"
            if (stage.equals("hard") && totalRhymeWordsFound >= 6) {
                // give Toast
                editor.putBoolean("A4Unlocked", true); // save to SharedPreferences

                // give Toast
                layout.setBackgroundResource(R.color.A4Color);
                tv.setText("Congratulations! You unlocked \n" +
                        "\'Shaky Spears!\' \n " +
                        "You found " + totalRhymeWordsFound +
                        " words in hard mode. Good job!");
            }
        }

        if (!A5Completed) {
            // if stage = insane & words >= 8, unlock "You should be a rapper :-)"
            if (stage.equals("insane") && totalRhymeWordsFound >= 8) {
                editor.putBoolean("A5Unlocked", true); // save to SharedPreferences

                // give Toast
                layout.setBackgroundResource(R.color.A5Color);
                tv.setText("Congratulations! You unlocked \n" +
                        "\'You should be a rapper :-)\' \n " +
                        "You found " + totalRhymeWordsFound +
                        " words in insane mode. Insane!");
            }
        }
        editor.apply();

        // customized toast
        layout.addView(tv);
        final Toast toast = new Toast(this);
        toast.setView(layout);
        toast.setGravity(Gravity.TOP, Gravity.CENTER_HORIZONTAL, Gravity.CENTER_VERTICAL);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();

        new CountDownTimer(3500, 1000)
        {

            public void onTick(long millisUntilFinished) {toast.show();}
            public void onFinish() {toast.show();}

        }.start();
    }

    public void onDestroy() {
        super.onDestroy();
    }

    //public static void hideSoftKeyboard(Activity activity) {
    //    InputMethodManager inputMethodManager =
    //            (InputMethodManager) activity.getSystemService(
    //                    Activity.INPUT_METHOD_SERVICE);
    //    inputMethodManager.hideSoftInputFromWindow(
    //            activity.getCurrentFocus().getWindowToken(), 0);
    //}
}

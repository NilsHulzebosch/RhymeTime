package com.example.nils.rhymetime;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ScoreOverviewActivity extends AppCompatActivity {

    private ArrayList<Score> scoreArrayList;
    private Button refreshButton;
    private ArrayAdapter<Score> scoresList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_overview);

        scoreArrayList = new ArrayList<>();
        refreshButton = (Button) findViewById(R.id.refreshButton);

        setAdapter();
        addListener();

        // get score data from FireBase if there is an active internet connection
        if (isNetworkAvailable()) {
            refreshButton.performClick();
        } else {
            Toast.makeText(this, "You do not have an active internet connection. " +
                            "Please connect to the internet to be able to play.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    // initialize the custom Adapter (with an empty ArrayList)
    public void setAdapter() {
        ListView results = (ListView) findViewById(R.id.scoreListView);
        scoresList = new LeaderboardAdapter(this, scoreArrayList);
        results.setAdapter(scoresList);
    }

    /* Add event listener for the 'Refresh' button. Whenever clicked,
     * call updateScoreList(snapshot), which will get the correct Score data,
     * sort it, and update the Adapter.
     */
    public void addListener() {
        Firebase.setAndroidContext(this); // set context
        String FIREBASE_URL = "https://rhymetime-b8195.firebaseio.com/";
        final Firebase ref = new Firebase(FIREBASE_URL); // get FireBase reference

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        updateScoreList(snapshot);
                        Toast.makeText(getApplicationContext(), "Updated leaderboard",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        Toast.makeText(getApplicationContext(), "Something went wrong.",
                                Toast.LENGTH_SHORT).show();
                        System.out.println("The read failed: " + firebaseError.getMessage());
                    }
                });
            }
        });
    }

    /* Update the ArrayList by getting (Score object) data from FireBase,
     * putting all scores not equal to zero, from highest to lowest, in the ArrayList
     * and then updating the Adapter with the new ArrayList.
     */
    public void updateScoreList(DataSnapshot snapshot) {
        scoreArrayList.clear();

        for (DataSnapshot child : snapshot.getChildren()) {

            for (DataSnapshot scoreChild : child.getChildren()) {

                String scoreObj = scoreChild.getValue().toString();

                try {
                    JSONObject jsonObj = new JSONObject(scoreObj);

                    String username = jsonObj.optString("username", "unknown");
                    int score = jsonObj.optInt("score", 0);
                    int wordsAmount = jsonObj.optInt("wordsAmount", 0);
                    String difficulty = jsonObj.optString("difficulty", "unknown");
                    String rhymeWord = jsonObj.optString("rhymeWord", "unknown");
                    ArrayList<String> listOfRhymedWords = new ArrayList<>();

                    Score scoreObject = new Score(username,
                            score,
                            wordsAmount,
                            difficulty,
                            rhymeWord,
                            listOfRhymedWords);

                    // if score is not zero...
                    if (score != 0) {
                        // add Score object based on score (from highest to lowest)
                        if (scoreArrayList.size() == 0) {
                            scoreArrayList.add(scoreObject);
                        } else {
                            int i = 0;
                            while (i < scoreArrayList.size() &&
                                    scoreObject.score < scoreArrayList.get(i).score) {
                                i++;
                            }
                            if (i == scoreArrayList.size()) {
                                scoreArrayList.add(scoreObject);

                            } else {
                                scoreArrayList.add(i, scoreObject);
                            }
                        }
                    }

                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
        }
        scoresList.notifyDataSetChanged(); // update ArrayAdapter
    }

    // this method checks whether there is an active network connection
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
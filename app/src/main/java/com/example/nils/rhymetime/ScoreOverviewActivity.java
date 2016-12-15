package com.example.nils.rhymetime;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;

import static java.util.logging.Level.parse;

public class ScoreOverviewActivity extends AppCompatActivity {

    private static String FIREBASE_URL = "https://rhymetime-b8195.firebaseio.com/";

    private ArrayList<Score> scoreArrayList;
    private ListView results;
    private ArrayAdapter<Score> scoresList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_overview);

        scoreArrayList = new ArrayList<>();
        setAdapter();
        addTESTListener();
    }

    public void addListener() {
        Firebase.setAndroidContext(this); // set context
        Firebase ref = new Firebase(FIREBASE_URL); // get FireBase reference

        /*
        Button refreshButton = (Button) findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 ref.addListenerForSingleValueEvent(new ValueEventListener() {
                     @Override
                     public void onDataChange(DataSnapshot snapshot) {
                         // do some stuff once
                     }

                     @Override
                     public void onCancelled(FirebaseError firebaseError) {
                         System.out.println("The read failed: " + firebaseError.getMessage());
                     }
                 });
             }
         });
         */

        // value event listener for real-time data update
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                scoreArrayList.clear();
                System.out.println("There are " + snapshot.getChildrenCount() + " children");

                for (DataSnapshot child : snapshot.getChildren()) {

                    System.out.println("There are " + child.getChildrenCount() + " grandchildren");
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

                            //for (int i = 0; i < scoreArrayList.size(); ) {
                            //    if (scoreObject.score >= scoreArrayList.get(i).score) {
                            //        scoreArrayList.add(i, scoreObject);
                            //        break;
                            //    }
                            //}
                            scoreArrayList.add(scoreObject);

                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
                //sortScores();
                scoresList.notifyDataSetChanged(); // update ArrayAdapter
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }

    public void addTESTListener() {
        Firebase.setAndroidContext(this); // set context
        final Firebase ref = new Firebase(FIREBASE_URL); // get FireBase reference

        Button refreshButton = (Button) findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        // do some stuff once
                        scoreArrayList.clear();
                        System.out.println("There are " + snapshot.getChildrenCount() + " children");

                        for (DataSnapshot child : snapshot.getChildren()) {

                            System.out.println("There are " + child.getChildrenCount() + " grandchildren");
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

                                    //for (int i = 0; i < scoreArrayList.size(); ) {
                                    //    if (scoreObject.score >= scoreArrayList.get(i).score) {
                                    //        scoreArrayList.add(i, scoreObject);
                                    //        break;
                                    //    }
                                    //}
                                    scoreArrayList.add(scoreObject);

                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                        //sortScores();
                        scoresList.notifyDataSetChanged(); // update ArrayAdapter
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        System.out.println("The read failed: " + firebaseError.getMessage());
                    }
                });
            }
        });
    }

    // this method sorts all Score objects based on their score from largest to smallest
    public void sortScores() {

    }

    public void setAdapter() {
        System.out.println("Hi there" + scoreArrayList);

        results = (ListView) findViewById(R.id.scoreListView);
        scoresList = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, scoreArrayList);
        results.setAdapter(scoresList);
    }
}
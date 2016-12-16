package com.example.nils.rhymetime;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

public class LeaderboardAdapter extends ArrayAdapter<Score> {


    public LeaderboardAdapter(Context context, ArrayList<Score> items) {
        super(context, R.layout.row_layout, items);
    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.leaderboard_row_layout, parent, false);

        final Score scoreObject = getItem(position);
        String username = "anonymous";
        int score = 0;
        int wordsAmount = 0;
        String difficulty = "unknown";
        String rhymeWord = "unknown";
        ArrayList<String> listOfRhymedWords = new ArrayList<>();

        if (scoreObject != null) {
            username = scoreObject.username;
            score = scoreObject.score;
            wordsAmount = scoreObject.wordsAmount;
            difficulty = scoreObject.difficulty;
            rhymeWord = scoreObject.rhymeWord;
            listOfRhymedWords = scoreObject.listOfRhymedWords;
        }

        TextView scoreTextView = (TextView) view.findViewById(R.id.scoreTextView);
        scoreTextView.setText(String.valueOf(score));

        TextView wordsAmountTextView = (TextView) view.findViewById(R.id.wordsAmountTextView);
        wordsAmountTextView.setText(String.valueOf(wordsAmount));

        TextView difficultyTextView = (TextView) view.findViewById(R.id.difficultyTextView);
        difficultyTextView.setText(difficulty);

        TextView usernameTextView = (TextView) view.findViewById(R.id.usernameTextView);
        usernameTextView.setText(username);

        return view;

    }

}

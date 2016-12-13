package com.example.nils.rhymetime;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;


public class Score {

    public String username;

    public int score;
    public int wordsAmount;
    public String difficulty;
    public String rhymeWord;

    public ArrayList<String> listOfRhymedWords;

    public Score(String username, int score, int wordsAmount, String difficulty,
                  String rhymeWord, ArrayList<String> listOfRhymedWords) {
        this.username = username;
        this.score = score;
        this.wordsAmount = wordsAmount;
        this.difficulty = difficulty;
        this.rhymeWord = rhymeWord;
        this.listOfRhymedWords = listOfRhymedWords;
    }

    @Override
    // TEMPORARY method, this will by replaced by a CustomAdapter
    public String toString() {
        int scoreL = String.valueOf(score).length();
        int wordsAmountL = String.valueOf(wordsAmount).length();
        int difficultyL = difficulty.length();
        int rhymeWordL = rhymeWord.length();

        return "" + score + TextUtils.join("", Collections.nCopies(8 - scoreL, " ")) +
                wordsAmount + TextUtils.join("", Collections.nCopies(8 - wordsAmountL, " ")) +
                rhymeWord + TextUtils.join("", Collections.nCopies(22 - rhymeWordL, " ")) +
                difficulty + "";
    }
}

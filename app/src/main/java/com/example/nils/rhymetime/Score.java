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
    /* This method turns a Score object into a string.
     * It is used at displaying the leaderboard scores. Based on the length of each
     * variable, the correct amount of whitespaces are being added so the
     * UI looks like a smooth leaderboard.
     */
    public String toString() {
        int scoreL = String.valueOf(score).length();
        int wordsAmountL = String.valueOf(wordsAmount).length();
        int difficultyL = difficulty.length();
        int rhymeWordL = rhymeWord.length();

        return "" + score + TextUtils.join("", Collections.nCopies(14 - scoreL*3, " ")) +
                wordsAmount + TextUtils.join("", Collections.nCopies(8 - wordsAmountL*3, " ")) +
                rhymeWord + TextUtils.join("", Collections.nCopies(26 - rhymeWordL*3, " ")) +
                difficulty + TextUtils.join("", Collections.nCopies(18 - difficultyL*2, " ")) +
                username;
    }
}

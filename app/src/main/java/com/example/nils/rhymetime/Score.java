package com.example.nils.rhymetime;

import java.util.ArrayList;

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
}

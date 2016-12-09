package com.example.nils.rhymetime;

public class RhymeWord {

    public String word;
    public int score;
    public int numSyllables;

    // constructor
    public RhymeWord(String word, int score, int numSyllables) {
        this.word = word;
        this.score = score;
        this.numSyllables = numSyllables;
    }
}

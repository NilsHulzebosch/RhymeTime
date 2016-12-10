package com.example.nils.rhymetime;

import java.util.Arrays;
import java.util.List;

public class EasyStage {

    public int time = 70000;
    public List<String> possibleWords = Arrays.asList("sing", "cool", "boat",
            "plant", "cat", "walk", "blue", "pan", "brick", "fire");
    public String rhymeWord;

    public EasyStage() {
        // house
        // sleep
        // yellow
    }

    public String getRandomWord() {
        return possibleWords.get((int)(Math.random() * possibleWords.size()));
    }
}

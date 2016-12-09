package com.example.nils.rhymetime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpRequestHelper {

    // make string for URL
    private static final String startURL = "https://api.datamuse.com/words?rel_rhy=";
    private static String rhymeWord = "";
    //private static final String startingWith = "";

    // method to download from server
    protected static String downloadFromServer(String... params) {

        // declare return String result
        String result = "";

        // get chosen tag from argument
        String chosenTag = params[0]; // this is the user input

        // convert multiple words into words connected with '+' instead of space
        rhymeWord = chosenTag.replaceAll(" ", "+");

        String correctUrl = startURL + rhymeWord;

        // turn string into URL
        URL url = null;
        try {
            url = new URL(correctUrl);
        } catch(MalformedURLException e) {
            e.printStackTrace();
        }


        // make the connection;
        HttpURLConnection connection;
        if (url != null) {
            try {
                // open connection, set request method
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                // get response code
                Integer responseCode = connection.getResponseCode();

                //if response if between 200 and 300, there is a connection,read InputStream
                if (responseCode >= 200 && responseCode < 300) {
                    InputStream is = connection.getInputStream();
                    System.out.println("TEST2");
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    String line;

                    while ((line = br.readLine()) != null) {
                        result = result + line;
                    }

                } else {
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(connection.getErrorStream()) );
                    // communicate correct error
                    System.out.println("ResponseCode was not correct.");

                }

            } catch(IOException e) {
                System.out.println("Connection could not be made.");
            }
        }
        return result;
    }
}

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
    private static final String rhymeWordURL = "https://api.datamuse.com/words?rel_rhy=";
    private static final String randomWordURL = "http://www.setgetgo.com/randomword/get.php";

    // method to download from server
    protected static String downloadFromServer(String... params) {

        // declare return String result
        String result = "";

        String correctUrl;
        if (params[0].equals("rhymeWord")) {
            // the rhyme word API needs to be called
            correctUrl = rhymeWordURL + params[1];
        } else {
            // the random word API needs to be called
            correctUrl = randomWordURL;
        }

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

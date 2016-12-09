package com.example.nils.rhymetime;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.ArrayList;

public class MyAsyncTask extends AsyncTask<String, Integer, String> {

    PlayActivity activity;
    Context context;
    public ArrayList<RhymeWord> searchResults;

    // constructor
    public MyAsyncTask(PlayActivity activity) {
        this.activity = activity;
        this.context = this.activity.getApplicationContext();
        this.searchResults = new ArrayList<>();
    }

    // onPreExecute()
    protected void onPreExecute() {
        Toast.makeText(context, "Getting data from server", Toast.LENGTH_SHORT).show();
    }

    // doInBackground()
    protected String doInBackground(String... params) {
        return HttpRequestHelper.downloadFromServer(params);
    }

    // onProgressUpdate()

    // onPostExecute()
    protected void onPostExecute(String result) {
        super.onPostExecute(result); // call existing class
    }
}
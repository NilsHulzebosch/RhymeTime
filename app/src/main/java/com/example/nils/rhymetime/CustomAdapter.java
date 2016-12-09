package com.example.nils.rhymetime;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class CustomAdapter extends ArrayAdapter<String> {

    public CustomAdapter(Context context, ArrayList<String> items) {
        super(context, R.layout.row_layout, items);
    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.row_layout, parent, false);

        final String guessedWord = getItem(position);

        int rotation;
        if (position % 2 == 0) {
            rotation = (position % 2) * 10 + 3;
        } else {
            rotation = - (position % 2) * 10 + 3;
        }

        TextView textView = (TextView) view.findViewById(R.id.textView1);
        textView.setRotation(rotation);
        textView.setText(guessedWord);

        return view;
    }
}

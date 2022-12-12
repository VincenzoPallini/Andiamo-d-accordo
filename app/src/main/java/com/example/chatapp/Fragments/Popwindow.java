package com.example.chatapp.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;

import com.example.chatapp.R;

public class Popwindow extends Activity {


    @Override
    protected void onCreate(Bundle saveIstanceState) {
        super.onCreate(saveIstanceState);

        setContentView(R.layout.popwindowincontro);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        // calculations added
        double calculatedWidth = width * .7;
        double calculatedHeight = height * .7;

        getWindow().setLayout((int) calculatedWidth, (int) calculatedHeight);

    }


}
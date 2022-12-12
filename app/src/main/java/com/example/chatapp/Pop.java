package com.example.chatapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;

public class Pop extends Activity {


@Override
    protected void onCreate(Bundle saveIstanceState) {
    super.onCreate(saveIstanceState);

    setContentView(R.layout.popwindow);

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

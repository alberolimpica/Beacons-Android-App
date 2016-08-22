package com.example.r00143659.beacondeployment;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by R00143659 on 22/08/2016.
 */
public class BeaconsSquare extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from activity_main.xml
        setContentView(new Rectangle(this));

    }
}

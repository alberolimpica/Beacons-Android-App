package com.example.r00143659.beacondeployment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Main Activity of the app.
 * Here, we will see two buttons to choose between finding a beacon or treasure hunting
 */
public class MainActivity extends AppCompatActivity {
    Button button1, button2, button3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from activity_main.xml
        setContentView(R.layout.activity_main);

        Realm.setDefaultConfiguration(new RealmConfiguration.Builder(this).build());

        Log.e("sss", "onCreate: "+ Realm.getDefaultInstance().where(THProximity.class).findAll() );

        THProximity toSave = new THProximity();
        toSave.setId("Beacons");

        // Locate the button in activity_main.xml
        button1 = (Button) findViewById(R.id.MyButton1);
        button2 = (Button) findViewById(R.id.MyButton2);
        button3 = (Button) findViewById(R.id.CitButton);

        // Capture button clicks
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                // Start NewActivity.class
                Intent myIntent = new Intent(MainActivity.this,
                        BeaconActivity.class);
                startActivity(myIntent);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                // Start NewActivity.class
                Intent myIntent = new Intent(MainActivity.this,
                        TreasureHunting.class);
                startActivity(myIntent);
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                // Start NewActivity.class
                Intent myIntent = new Intent(MainActivity.this,
                        CIT_Services.class);
                startActivity(myIntent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
}
package com.example.r00143659.beacondeployment;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Main Activity of the app.
 * Here, we will see two buttons to choose between finding a beacon or treasure hunting
 */
public class MainActivity extends AppCompatActivity {
    private final static int REQUEST_ENABLE_BT=1;
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    Button beaconActivityButton, treasureHuntingButton, citServicesButton, societiesDayButton;
    private static final String PERMISSION_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final String PERMISSION_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final int BEACONS_REQUEST = 1, SOCIETIES_REQUEST = 2, TREASURE_REQUEST = 3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the view from activity_main.xml
        setContentView(R.layout.activity_main);
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        Realm.setDefaultConfiguration(new RealmConfiguration.Builder(this).build());

        Log.e("sss", "onCreate: "+ Realm.getDefaultInstance().where(THProximity.class).findAll() );

//        THProximity toSave = new THProximity();
//        toSave.setId("RealmBeacon");
//
//        DataManager.save(toSave);
//
//        Log.e("ssss", "onCreate: " + Realm.getDefaultInstance().where(THProximity.class).findAll());
//
//        toSave.setStatus(THProximity.YELLOW);
//        DataManager.save(toSave);
//
//        Log.e("ssss", "onCreate: " + Realm.getDefaultInstance().where(THProximity.class).findAll());
//
//        toSave.setStatus(THProximity.RED);
//        DataManager.save(toSave);
//
//        Log.e("ssss", "onCreate: " + Realm.getDefaultInstance().where(THProximity.class).findAll());

        // Locate the button in activity_main.xml
        beaconActivityButton = (Button) findViewById(R.id.MyButton1);
        treasureHuntingButton = (Button) findViewById(R.id.MyButton2);
        citServicesButton = (Button) findViewById(R.id.CitButton);
        societiesDayButton = (Button) findViewById(R.id.MyButton4);

        // Capture button clicks
        beaconActivityButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                checkAndRequestPermission(BEACONS_REQUEST);
            }
        });

        treasureHuntingButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                checkAndRequestPermission(TREASURE_REQUEST);
            }
        });

        societiesDayButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                checkAndRequestPermission(SOCIETIES_REQUEST);
            }
        });

        citServicesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                // Start NewActivity.class
                Intent myIntent = new Intent(MainActivity.this,
                        CIT_Services.class);
                startActivity(myIntent);
            }
        });

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean("firstrun", true)) {
            // Do first run stuff here then set 'firstrun' as false
            // using the following line to edit/commit prefs
            prefs.edit().putBoolean("firstrun", false).commit();

            saveBeaconsInDB();

        }
    }

    private void saveBeaconsInDB(){

        Realm realm = Realm.getDefaultInstance();

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmBeacon beacon1 = realm.createObject(RealmBeacon.class);
                beacon1.setId("0x30313233343536373839");
                beacon1.setName("Back right");
                beacon1.setLatitude(38.947909);
                beacon1.setLongitude(-0.401673);
                beacon1.setDistance(-1);

                RealmBeacon beacon2 = realm.createObject(RealmBeacon.class);
                beacon2.setId("0x31323334353637383940");
                beacon2.setName("Back");
                beacon2.setLatitude(38.947880);
                beacon2.setLongitude(-0.401673);
                beacon2.setDistance(-1);

                RealmBeacon beacon3 = realm.createObject(RealmBeacon.class);
                beacon3.setId("0x32333435363738394041");
                beacon3.setName("Front");
                beacon3.setLatitude(38.947880);
                beacon3.setLongitude(-0.401723);
                beacon3.setDistance(-1);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Realm.getDefaultInstance().close();
    }

    private boolean hasPermission(){
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)&&(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)) {
            return true;
        }else{
            return false;
        }
    }

    private void checkAndRequestPermission(int activityId){
        if(hasPermission()) {
            launchActivity(activityId);
        } else {
            requestPermission(activityId);
        }
    }

    private void launchActivity(int activityId){
        Class activity;
        switch (activityId) {
            case BEACONS_REQUEST:
                activity = BeaconActivity.class;
                break;
            case SOCIETIES_REQUEST:
                activity = SocietiesDay.class;
                break;
            case TREASURE_REQUEST:
                activity = TreasureHunting.class;
                break;
            default:
                throw new IllegalArgumentException("Why are we here?");
        }

        // Start NewActivity.class
        Intent myIntent = new Intent(MainActivity.this, activity);
        startActivity(myIntent);
    }

    private void requestPermission(int activityId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (shouldShowRequestPermissionRationale(PERMISSION_COARSE_LOCATION) || shouldShowRequestPermissionRationale(PERMISSION_FINE_LOCATION)) {
                Toast.makeText(this, "Permission are required for this activity", Toast.LENGTH_LONG).show();
            }
            requestPermissions(new String[]{PERMISSION_COARSE_LOCATION, PERMISSION_FINE_LOCATION}, activityId);
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode,
                                           @NonNull final String[] permissions,
                                           @NonNull final int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case BEACONS_REQUEST:
                case SOCIETIES_REQUEST:
                case TREASURE_REQUEST:
                    launchActivity(requestCode);
                    break;
                default:
                    return;
            }
        } else {
            checkAndRequestPermission(requestCode);
        }
    }
}
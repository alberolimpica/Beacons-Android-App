package com.example.r00143659.beacondeployment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.text.DecimalFormat;
import java.util.Collection;

import io.realm.Realm;


// (BeaconConsumer, RangeNotifier) These define callback methods when the beacon scanning service is ready and when beacons are discovered in range.
public class conoceLaETSE extends AppCompatActivity implements View.OnClickListener, BeaconConsumer, RangeNotifier {

    ImageButton medicalCentreButton, itServiceButton, cardOfficeButton, libraryButton, bankButton, busStopButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.treasure);

        medicalCentreButton = (ImageButton) findViewById(R.id.medcen);
        itServiceButton = (ImageButton) findViewById(R.id.itser);
        cardOfficeButton = (ImageButton) findViewById(R.id.cardoff);
        libraryButton = (ImageButton) findViewById(R.id.libr);
        bankButton = (ImageButton) findViewById(R.id.bank);
        busStopButton = (ImageButton) findViewById(R.id.busstop);

        // Paint all buttons depending on the DB statuses
        for(THProximity th : DataManager.findAll())
            paintButton(matchId(th.getId()), getColor(th.getId()));

        medicalCentreButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                AlertDialog.Builder alert = new AlertDialog.Builder(conoceLaETSE.this);
                alert.setTitle("Medical Centre");
                alert.setMessage(R.string.medical );
                alert.show();
            }
        });

        itServiceButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent myIntent = new Intent(conoceLaETSE.this,
                        About.class);
                startActivity(myIntent);
            }
        });

        cardOfficeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent myIntent = new Intent(conoceLaETSE.this,
                        About.class);
                startActivity(myIntent);
            }
        });

        libraryButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent myIntent = new Intent(conoceLaETSE.this,
                        About.class);
                startActivity(myIntent);
            }
        });

        bankButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent myIntent = new Intent(conoceLaETSE.this,
                        About.class);
                startActivity(myIntent);
            }
        });

        busStopButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent myIntent = new Intent(conoceLaETSE.this,
                        About.class);
                startActivity(myIntent);
            }
        });
    }
    public void paintButton(int modifierId, int colorRes){
        ImageButton button = null;
        switch(modifierId){
            case R.id.medcen:
                button = medicalCentreButton;
                Log.e("sss", "matchId: pintando");
                break;
            case R.id.itser:
                button = itServiceButton;
                break;
            case R.id.cardoff:
                button = cardOfficeButton;
                break;
            case R.id.libr:
                button = libraryButton;
                break;
            case R.id.bank:
                button = bankButton;
                break;
            case R.id.busstop:
                button = busStopButton;
                break;
        }
        if (button != null) {
            button.setColorFilter(getColorFilter(colorRes));
        }
    }

    private PorterDuffColorFilter getColorFilter(int colorRes){
        int color = ContextCompat.getColor(this, colorRes);
        return new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY);
    }

    @Override
    public void onClick(View view) {
    }

    private BeaconManager mBeaconManager;

    @Override
    public void onResume() {
        //This method will be called when the activity is created/shows to the user.
        //Here we instance BeaconManager and configure it to detect Eddystone frames
        super.onResume();
        mBeaconManager = BeaconManager.getInstanceForApplication(this.getApplicationContext());
        // Detect the main Eddystone-UID frame:
        mBeaconManager.getBeaconParsers().add(new BeaconParser().
                //setBeaconLayout helps to decode an Eddystone frame
                        setBeaconLayout("s:0-1=feaa,m:2-2=00,p:3-3:-41,i:4-13,i:14-19"));
        //To bind the app to the scanning service to start scanning beacons:
        mBeaconManager.bind(this);
    }

    public void onBeaconServiceConnect() {
        //We set a pattern of which beacons are we interested
        Region region = new Region("all-beacons-region", null, null, null);
        try {
            mBeaconManager.startRangingBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        //To get callbacks each time a beacon is found
        mBeaconManager.setRangeNotifier(this);
    }

    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
        //The callback method, this will check all the visible beacons to see if they are Eddystone.
        for (Beacon beacon: beacons) {
            //For a beacon to be eddystone: serviceUuid  0xfeaa and beaconTypeCode x00
            if (beacon.getServiceUuid() == 0xfeaa && beacon.getBeaconTypeCode() == 0x00) {
                // This is a Eddystone-UID frame
                Identifier namespaceId = beacon.getId1();
                Identifier instanceId = beacon.getId2();
                String dist = String.valueOf(beacon.getDistance());
                double distance = Double.parseDouble(dist);
                distance =Double.parseDouble(new DecimalFormat("##.##").format(distance));
                final String Id = String.valueOf(instanceId);
                final String namespace = String.valueOf(namespaceId);
                final double finalDistance = distance;
                Log.e("sss", "didRangeBeaconsInRegion: "+Id );
                Log.e("sss", "didRangeBeaconsInRegion: "+namespace );
                //Only the original thread that created a view hierarchy can touch its views:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int newId = matchId(namespace);
                        updateProximity(namespace, newId, finalDistance);
                    }
                });
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //When the app is stoped we unbind
        mBeaconManager.unbind(this);
    }

    private int matchId(String id){
        int newId = 0;

        if(id.contains("30313233343536373839")){
            newId = R.id.medcen;
            Log.e("sss", "matchId: medcen");
        }
        if(id.contains("31323334353637383940")){
            newId = R.id.itser;
        }
        if(id.contains("32333435363738394041")) {
            newId = R.id.cardoff;
        }
        if( id.contains("89c641c7b59b")) {
            newId = R.id.libr;
        }
        return newId;
    }

    private void updateProximity(String namespace, int id, double distance){
        int status = THProximity.NONE;
        if(distance >= 40 )
            status = THProximity.RED;
        if(40 > distance && distance > 10)
            status = THProximity.YELLOW;
        if(distance <= 10)
            status = THProximity.GREEN;

        THProximity itemDB = new THProximity();
        itemDB.setId(namespace);
        itemDB.setStatus(status);
        DataManager.save(itemDB);
        paintButton(id, getColor(namespace));
    }

    private int getColor(String id){
        THProximity beacon = DataManager.findOne(id);
        int status = beacon != null ? beacon.getStatus() : -1;

        int color = R.color.common_plus_signin_btn_text_light_default;
        switch(status){
            case THProximity.GREEN:
                color = android.R.color.holo_green_dark;
                break;
            case THProximity.YELLOW:
                color = android.R.color.holo_orange_light;
                break;
            case THProximity.RED:
                color = android.R.color.holo_red_dark;
                break;
        }
        return color;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(conoceLaETSE.this);
        builder.setTitle("Simple Dialog");
        builder.setMessage("Some message here");
        return builder.create();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Realm.getDefaultInstance().close();
    }
}
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
import android.widget.TextView;

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

/**
 * Treasure hunting activity. Here the stamps will be shown.
 */

// (BeaconConsumer, RangeNotifier) These define callback methods when the beacon scanning service is ready and when beacons are discovered in range.
public class TreasureHunting extends AppCompatActivity implements View.OnClickListener, BeaconConsumer, RangeNotifier {
    ImageButton button1, button2, button3, button4, button5, button6;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.treasure);

        button1 = (ImageButton) findViewById(R.id.medcen);
        button2 = (ImageButton) findViewById(R.id.itser);
        button3 = (ImageButton) findViewById(R.id.cardoff);
        button4 = (ImageButton) findViewById(R.id.libr);
        button5 = (ImageButton) findViewById(R.id.bank);
        button6 = (ImageButton) findViewById(R.id.busstop);

        // Paint all buttons depending on the DB statuses
        for(THProximity th : DataManager.findAll())
            paintButton(matchId(th.getId()), getColor(th.getId()));


        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                AlertDialog.Builder alert = new AlertDialog.Builder(TreasureHunting.this);
                alert.setTitle("Medical Centre");
                alert.setMessage(R.string.medical );

                alert.show();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                ((TextView)TreasureHunting.this.findViewById(R.id.AboutMessage)).setText("Beacons found:");
                Intent myIntent = new Intent(TreasureHunting.this,
                        About.class);

                startActivity(myIntent);
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent myIntent = new Intent(TreasureHunting.this,
                        About.class);
                startActivity(myIntent);
            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent myIntent = new Intent(TreasureHunting.this,
                        About.class);
                startActivity(myIntent);
            }
        });
        button5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent myIntent = new Intent(TreasureHunting.this,
                        About.class);
                startActivity(myIntent);
            }
        });
        button6.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent myIntent = new Intent(TreasureHunting.this,
                        About.class);
                startActivity(myIntent);
            }
        });
    }
    public void paintButton(int modifierId, int colorRes){
        ImageButton button = null;
        switch(modifierId){
            case R.id.medcen:
                button = button1;
                Log.e("sss", "matchId: pintando");
                break;
            case R.id.itser:
                button = button2;
                break;
            case R.id.cardoff:
                button = button3;
                break;
            case R.id.libr:
                button = button4;
                break;
            case R.id.bank:
                button = button5;
                break;
            case R.id.busstop:
                button = button6;
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

//    public void distance(double distance){
//        if(distance >= 70.00 ){
//            paintButton(R.id.medcen,android.R.color.holo_red_dark );
//        }else if(10.00 <= distance&& distance < 70.00){
//            paintButton(R.id.medcen,android.R.color.holo_orange_light );
//
//        }else if(distance<10){
//            paintButton(R.id.medcen,android.R.color.holo_green_dark );
//        }
//    }


    @Override
    public void onPause() {
        super.onPause();
        //When the app is stoped we unbind
        mBeaconManager.unbind(this);
    }

//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    private int matchId(String id){
        int newId = 0;

        if(id.contains("0x29c7451088e8")){
            newId = R.id.medcen;
            Log.e("sss", "matchId: medcen");
        }
        if(id.contains("badc4d168877")){
            newId = R.id.itser;
        }
        if(id.contains("3380475fa920")) {
            newId = R.id.cardoff;
        }
        if( id.contains("89c641c7b59b")) {
            newId = R.id.libr;
        }
//            case id.contains("3380475fa920"):
//                newId = R.id.bank;
//                break;
//            case id.contains("3380475fa920"):
//                newId = R.id.busstop;
//                break;
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
        AlertDialog.Builder builder = new AlertDialog.Builder(TreasureHunting.this);
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
package com.example.r00143659.beacondeployment;

import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
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

//        button1.setOnClickListener(this);
//        button2.setOnClickListener(this);
//        button3.setOnClickListener(this);
//        button4.setOnClickListener(this);
//        button5.setOnClickListener(this);
//        button6.setOnClickListener(this);



    }
    public void paintButton(int modifierId, int colorRes){
        ImageButton button = null;
        switch(modifierId){
            case R.id.medcen:
                button = button1;
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
                //Only the original thread that created a view hierarchy can touch its views:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateProximity(Id, finalDistance);
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

    private void updateProximity(String id, double distance){
        int status = THProximity.NONE;
        if(distance > 40){
            status = THProximity.RED;
            paintButton(R.id.medcen,android.R.color.holo_red_dark );
        }
        if(40 > distance && distance > 10) {
            status = THProximity.YELLOW;
            paintButton(R.id.medcen, android.R.color.holo_orange_light);
        }
        if(distance < 10) {
            status = THProximity.GREEN;
            paintButton(R.id.medcen, android.R.color.holo_green_dark);
        }

        THProximity itemDB = DataManager.findOne(id);
        if(itemDB == null)
            itemDB = new THProximity();

        itemDB.setStatus(status);
        DataManager.save(itemDB);

    }
}
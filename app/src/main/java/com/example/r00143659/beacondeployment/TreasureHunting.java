package com.example.r00143659.beacondeployment;

import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;

/**
 * Treasure hunting activity. Here the stamps will be shown.
 */
public class TreasureHunting extends AppCompatActivity implements View.OnClickListener, BeaconConsumer, RangeNotifier {
    ImageButton button1, button2, button3, button4, button5, button6;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from activity_main.xml
        setContentView(R.layout.treasure);
        button1 = (ImageButton) findViewById(R.id.medical_centre);
        button2 = (ImageButton) findViewById(R.id.it_service);
        button3 = (ImageButton) findViewById(R.id.card_office);
        button4 = (ImageButton) findViewById(R.id.library);
        button5 = (ImageButton) findViewById(R.id.boi);
        button6 = (ImageButton) findViewById(R.id.bus_stop);

        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        button5.setOnClickListener(this);
        button6.setOnClickListener(this);

    }
    public void colorModifierButton(int modifierId, boolean active){

        switch(modifierId){
            case R.id.medical_centre:
                break;
            case R.id.it_service:
                break;
            case R.id.card_office:
                break;
            case R.id.library:
                break;
            case R.id.boi:
                break;
            case R.id.bus_stop:
                break;
        }
    }

    @Override
    public void onClick(View view) {


    }


    private BeaconManager mBeaconManager;

    @Override
    public void onResume() {
        super.onResume();
        mBeaconManager = BeaconManager.getInstanceForApplication(this.getApplicationContext());
        // Detect the main Eddystone-UID frame:
        mBeaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("s:0-1=feaa,m:2-2=00,p:3-3:-41,i:4-13,i:14-19"));
        mBeaconManager.bind(this);
    }
//http://developer.radiusnetworks.com/2015/07/14/building-apps-with-eddystone.html
    public void onBeaconServiceConnect() {
        Region region = new Region("all-beacons-region", null, null, null);
        try {
            mBeaconManager.startRangingBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        mBeaconManager.setRangeNotifier(this);
    }

    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
        for (Beacon beacon: beacons) {
            if (beacon.getServiceUuid() == 0xfeaa && beacon.getBeaconTypeCode() == 0x00) {
                // This is a Eddystone-UID frame
                Identifier namespaceId = beacon.getId1();
                Identifier instanceId = beacon.getId2();
//                Log.d("RangingActivity", "I see a beacon transmitting namespace id: " + namespaceId +
//                        " and instance id: " + instanceId +
//                        " approximately " + beacon.getDistance() + " meters away.");
//                runOnUiThread(new Runnable() {
//                    public void run() {
//                        ((TextView)TreasureHunting.this.findViewById(R.id.message)).setText("Hello world, and welcome to Eddystone!");
//                    }
//                });
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mBeaconManager.unbind(this);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
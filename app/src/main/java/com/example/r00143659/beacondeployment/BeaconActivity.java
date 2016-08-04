package com.example.r00143659.beacondeployment;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.nearby.messages.MessagesOptions;
import com.google.android.gms.nearby.messages.NearbyPermissions;
import com.google.android.gms.nearby.messages.Strategy;
import com.google.android.gms.nearby.messages.SubscribeCallback;
import com.google.android.gms.nearby.messages.SubscribeOptions;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BeaconActivity extends AppCompatActivity implements BeaconConsumer, RangeNotifier, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener  {

    private BeaconManager mBeaconManager;
    private List<BeaconItem> beacons = new ArrayList<>();
    private GoogleApiClient mGoogleApiClient;
    private MessageListener mMessageListener;
    private static final String TAG = BeaconActivity.class.getSimpleName();

    private static final int REQUEST_RESOLVE_ERROR = 1001;
    private ArrayAdapter<String> mNearbyDevicesArrayAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon);

        //Nearby messages API
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Nearby.MESSAGES_API, new MessagesOptions.Builder()
                            .setPermissions(NearbyPermissions.BLE)
                            .build())
                    .addConnectionCallbacks(this)
                    .enableAutoManage(this, this)
                    .build();
            Log.i(TAG, "API connected");
        }
        mMessageListener = new MessageListener() {

            @Override
            public void onFound(Message message) {
                String messageAsString = new String(message.getContent());
                Log.d(TAG, "Found message: " + messageAsString);
                //When a message is found
                Log.i(TAG, "Message found: " + message.toString());
                Log.i(TAG, "Message string: " + new String(message.getContent()));
                Log.i(TAG, "Message namespaced type: " + message.getNamespace() +
                        "/" + message.getType());
                mNearbyDevicesArrayAdapter.add(DeviceMessage.fromNearbyMessage(message).getMessageBody());

            }

            @Override
            public void onLost(Message message) {
                //When a message is no longer detectable
                String messageAsString = new String(message.getContent());
                Log.d(TAG, "Lost sight of message: " + messageAsString);
                mNearbyDevicesArrayAdapter.remove(DeviceMessage.fromNearbyMessage(message).getMessageBody());
            }
        };
        final List<String> nearbyDevicesArrayList = new ArrayList<>();
        mNearbyDevicesArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, nearbyDevicesArrayList);
        final ListView nearbyDevicesListView = (ListView) findViewById(R.id.nearby_devices_list_view);
        if (nearbyDevicesListView != null){
            nearbyDevicesListView.setAdapter(mNearbyDevicesArrayAdapter);
        }
    }
    private void subscribe() {
        Log.i(TAG, "Subscribing.");
        SubscribeOptions options = new SubscribeOptions.Builder()
                .setStrategy(Strategy.BLE_ONLY)
                .setCallback(new SubscribeCallback() {
                    @Override
                    public void onExpired() {
                        super.onExpired();
                        Log.i(TAG, "No longer subscribing");
                    }
                })
                .build();
        Nearby.Messages.subscribe(mGoogleApiClient, mMessageListener, options)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()){
                            Log.i(TAG, "Subscibed succesfully");
                        }else{
                            Log.i(TAG, "Could not subscribe");
                        }
                    }
                });

    }
    private void unsuscribe(){
        Log.i(TAG, "Unsuscribing");
        Nearby.Messages.unsubscribe(mGoogleApiClient, mMessageListener);
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        Log.i(TAG, "onStart");
    }

    @Override
    public void onConnected(Bundle connectionHint) {

        Log.i(TAG, "GoogleApiClient connected");
        subscribe();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.e(TAG, "GoogleApiClient disconnected with cause: " + cause);
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (result.hasResolution()) {
            try {
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "GoogleApiClient connection failed");
        }
    }

    @Override
    public void onStop() {

        unsuscribe();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_RESOLVE_ERROR) {
            if (resultCode == RESULT_OK) {
                mGoogleApiClient.connect();
            } else {
                Log.e(TAG, "GoogleApiClient connection failed. Unable to resolve.");
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    //This method gets called when the activity appears
    public  void onResume (){
        super.onResume();
        mBeaconManager = BeaconManager.getInstanceForApplication(this.getApplicationContext());
        //setBeaconLayout method will tell the Android Beacon Library how to decode an Eddystone UID frame
        mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("s:0-1=feaa,m:2-2=00,p:3-3:-41,i:4-13,i:14-19"));
        mBeaconManager.bind(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }
    @Override
    public void onBeaconServiceConnect(){
        //To tell the libraryy that we want to see all beacons:
        Region region = new Region("all-beacons-region", null, null, null);
        try{
            //to start looking for beacons that match this region definition
            mBeaconManager.startRangingBeaconsInRegion(region);
        }catch(RemoteException e){
            e.printStackTrace();
        }
        //This class will receive callbacks everytime a beacon is seen
        mBeaconManager.setRangeNotifier(this);
    }
    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {

        for (Beacon beacon: beacons) {
            // You can tell if a beacon is an Eddystone beacon because it will have a serviceUuid of
            // 0xfeaa, and a beaconTypeCode of x00. (For the Eddystone-TLM frame, the beaconTypeCode will be 0x20 and for Eddystone-URL the beaconType code will be 0x10).
            if (beacon.getServiceUuid() == 0xfeaa && beacon.getBeaconTypeCode() == 0x00) {
                // This is a Eddystone-UID frame
                Identifier namespaceId = beacon.getId1();
                Identifier instanceId = beacon.getId2();

//                Log.d("Finding Beacons", "I see a beacon transmitting namespace id: " + namespaceId +
//                        " and instance id: " + instanceId +
//                        " approximately " + beacon.getDistance() + " meters away.");
                final String Id = String.valueOf(instanceId);
                final String namespace = String.valueOf(namespaceId);
                final double distance =  beacon.getDistance();
              //  storeBeacons(new BeaconItem(Id, namespace, distance));// Only the original thread that created a view hierarchy can touch its views.
                runOnUiThread(new Runnable() {
                    public void run() {
                        Log.d("BeaconActivity", "Este beacon");
                        storeBeacons(new BeaconItem(Id, namespace, distance));// Only the original thread that created a view hierarchy can touch its views.


                        ((TextView)BeaconActivity.this.findViewById(R.id.message)).setText("Hello world, and welcome to Eddystone!");
                    }
                });
            }
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        mBeaconManager.unbind(this);
    }

    public void storeBeacons(BeaconItem beacon){
        List<String> beaconInfo = new ArrayList<String>();
//        if(beacons.size() >= 3){
//            return;
//        }

        boolean equal = false;
        for (BeaconItem aux : beacons) {
            if(beacon.getId() == aux.getId()){
                aux.setDistance(beacon.getDistance());
                equal = true;

            }
        }

        if(!equal){
            beacons.add(beacon);
        }

        for (BeaconItem aux : beacons) {
            String name = aux.getNamespace();
            String dist = String.valueOf(aux.getDistance());
            String id = aux.getId();
            beaconInfo.add("I see a beacon transmitting namespace id: " + name +
                    " and instance id: " + id +
                    " approximately " + dist + " meters away.");
        }

        ListView listView = (ListView) findViewById(R.id.listView1);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, beaconInfo);
        listView.setAdapter(adapter);
    }


}





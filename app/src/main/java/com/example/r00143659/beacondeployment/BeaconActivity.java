package com.example.r00143659.beacondeployment;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;

public class BeaconActivity extends AppCompatActivity implements BeaconConsumer, RangeNotifier, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {

    HashMap<String, SimpleBeacon> hmap = new HashMap<>();
    Realm realm = Realm.getDefaultInstance();
    private BeaconManager mBeaconManager;
    private List<SimpleBeacon> beaconsList;
    private GoogleApiClient mGoogleApiClient;
    private MessageListener mMessageListener;
    private static final String TAG = BeaconActivity.class.getSimpleName();
    private GoogleMap mGoogleMap ;


    ArrayAdapter<String> adapter;
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    //This is a new addition, and in onStart the if() is one too
    private static int[] NETWORK_TYPES = {ConnectivityManager.TYPE_WIFI, ConnectivityManager.TYPE_ETHERNET};

    private boolean isConnectedToNetwork() {
        ConnectivityManager connManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        for (int networkType : NETWORK_TYPES) {
            NetworkInfo info = connManager.getNetworkInfo(networkType);
            if (info != null && info.isConnectedOrConnecting()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
                //Nearby messages API
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Nearby.MESSAGES_API, new MessagesOptions.Builder()
                            .setPermissions(NearbyPermissions.BLE)
                            .build())
                    .addConnectionCallbacks(this)
                    .enableAutoManage(this, this)
                    .build();
            Log.i(TAG, "API connected");

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
            }

            @Override
            public void onLost(Message message) {
                //When a message is no longer detectable
                String messageAsString = new String(message.getContent());
                Log.d(TAG, "Lost sight of message: " + messageAsString);
            }
        };
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        final Handler handler = new Handler();
        final int delay = 5000; //milliseconds

        handler.postDelayed(new Runnable(){
            public void run(){
                if(hmap.size()<3){
                    handler.postDelayed(this, delay);
                    return;
                }

                beaconsList = new ArrayList<>(hmap.values());
                Collections.sort(beaconsList, new Comparator<SimpleBeacon>() {
                    @Override
                    public int compare(SimpleBeacon o1, SimpleBeacon o2) {
                        if(o1.getDistance() > o2.getDistance())
                            return 1; //01 mayor que 02
                        else if(o1.getDistance() < o2.getDistance())
                            return -1; // 01 menor que 02
                        else
                            return 0;//Iguales
                    }
                });

                List<SimpleBeacon> triangulationBeacons = beaconsList.subList(0,3);

                List<RealmBeacon> realmBeacons = getAllBeaconsByUid(triangulationBeacons);

                if(realmBeacons == null) {
                    handler.postDelayed(this, delay);
                    return;
                }

                for (RealmBeacon beacon : realmBeacons) {
                    LatLng beaconLatLong = new LatLng(beacon.getLatitude(), beacon.getLongitude());
                    mGoogleMap.addMarker(new MarkerOptions().position(beaconLatLong));
                }

                final LatLng centerLatLon = Position.getCenter(realmBeacons);

                if(centerLatLon.toString().toLowerCase().contains("inf") || centerLatLon.toString().toLowerCase().contains("nan") ||centerLatLon.toString().toLowerCase().contains("90.0,")){
                    handler.postDelayed(this, delay);
                    return;
                }

                findViewById(R.id.go_to_button).setOnClickListener(new View.OnClickListener()
                {
                    public void onClick(View v)
                    {
                        Polyline line = mGoogleMap.addPolyline(new PolylineOptions()
                                .add(centerLatLon, new LatLng(38.947891, -0.401538))
                                .width(5)
                                .color(Color.RED));
                    }
                });
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(centerLatLon));
                mGoogleMap.addMarker(new MarkerOptions().position(centerLatLon).alpha(0.7f).icon(BitmapDescriptorFactory.fromResource(R.mipmap.user_position)));
                mGoogleMap.setMinZoomPreference(20.0f);
                mGoogleMap.setMaxZoomPreference(25.0f);


                handler.postDelayed(this, delay);
            }
        }, delay);

    }

    private List<RealmBeacon> getAllBeaconsByUid(List<SimpleBeacon> triangulationBeacons){
        List<RealmBeacon> beaconsInDB = new ArrayList<>();
        for (SimpleBeacon beacon : triangulationBeacons) {
            String namespaceOfBeacon = beacon.getNamespace();
            RealmBeacon actualBeacon = realm.where(RealmBeacon.class).equalTo("id", namespaceOfBeacon).findFirst();

            if(actualBeacon == null){
                return null;
            }

            String dist = String.valueOf(beacon.getDistance());
            double distance = Double.parseDouble(dist);
            distance =Double.parseDouble(new DecimalFormat("##.##").format(distance));

            actualBeacon.setDistance(distance);
            beaconsInDB.add(actualBeacon);
        }
        return beaconsInDB;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
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

    @Override
    public void onStart() {
        super.onStart();
        if( isConnectedToNetwork()){
            if (!mGoogleApiClient.isConnected()) {
                mGoogleApiClient.connect();
            }
        Log.i(TAG, "onStart");
        }
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

    private void unsubscribe(){
        Log.i(TAG, "Unsusbcribing");
        if (mGoogleApiClient.isConnected()) {
            Nearby.Messages.unsubscribe(mGoogleApiClient, mMessageListener);
        }
    }

    @Override
    public void onStop() {
            unsubscribe();
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
        //To tell the library that we want to see all beaconsList:
        Region region = new Region("all-beaconsList-region", null, null, null);
        try{
            //to start looking for beaconsList that match this region definition
            mBeaconManager.startRangingBeaconsInRegion(region);
        }catch(RemoteException e){
            e.printStackTrace();
        }
        //This class will receive callbacks every time a beacon is seen
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

                Log.d("Finding RealmBeacon", "I see a beacon transmitting namespace id: " + namespaceId +
                        " and instance id: " + instanceId +
                        " approximately " + beacon.getDistance() + " meters away.");
                final String Id = String.valueOf(instanceId);
                final String namespace = String.valueOf(namespaceId);
                final double distance =  beacon.getDistance();
              //  storeBeacons(new SimpleBeacon(Id, namespace, distance));// Only the original thread that created a view hierarchy can touch its views.
                runOnUiThread(new Runnable() {
                    public void run() {
                        Log.d("BeaconActivity", "Este beacon");
                        storeBeacon(new SimpleBeacon(Id, namespace, distance));// Only the original thread that created a view hierarchy can touch its views
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

    public void storeBeacon(SimpleBeacon beacon){
        String beaconNamespace = beacon.getNamespace();
        hmap.put(beaconNamespace, beacon);
    }
}
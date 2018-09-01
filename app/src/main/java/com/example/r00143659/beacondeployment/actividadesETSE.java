package com.example.r00143659.beacondeployment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import org.altbeacon.beacon.utils.UrlBeaconUrlCompressor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by R00143659 on 07/09/2016.
 */
public class actividadesETSE extends AppCompatActivity implements BeaconConsumer, RangeNotifier, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener  {
    String[] beaconsID = new String[2];
    String[] beaconsSocieties =  new String[2];
    String[] beaconsURL =  new String[2];

    private BeaconManager mBeaconManager;
    private List<SimpleBeacon> beacons = new ArrayList<>();
    private GoogleApiClient mGoogleApiClient;
    private MessageListener mMessageListener;
    private static final String TAG = irASitiosETSE.class.getSimpleName();

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
        setContentView(R.layout.actividades_etse);

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
            }

            @Override
            public void onLost(Message message) {
                //When a message is no longer detectable
                String messageAsString = new String(message.getContent());
                Log.d(TAG, "Lost sight of message: " + messageAsString);
            }
        };
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
            mGoogleApiClient.disconnect();
        }
    }
    @Override
    public void onStop() {
        if (mGoogleApiClient != null) {
            unsubscribe();
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
        mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("s:0-1=feaa,m:2-2=10,p:3-3:-41,i:4-20v"));
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
            // for Eddystone-URL the beaconType code will be 0x10).
            if (beacon.getServiceUuid() == 0xfeaa && beacon.getBeaconTypeCode() == 0x10) {
                Identifier namespaceId = beacon.getId1();
                final String namespace = String.valueOf(namespaceId);
                byte[] array = beacon.getId1().toByteArray();
                if(array.length == 0)
                    return;

                final String url = UrlBeaconUrlCompressor.uncompress(array);
                Log.d(TAG, "I see a beacon transmitting a url: " + url +
                        " approximately " + beacon.getDistance() + " meters away.");

//                //  storeBeacons(new SimpleBeacon(Id, namespace, distance));// Only the original thread that created a view hierarchy can touch its views.
                runOnUiThread(new Runnable() {
                    public void run() {
                      Log.d("actividadesETSE", "Este beacon");
//                        storeBeacons(new SimpleBeacon(url));// Only the original thread that created a view hierarchy can touch its views.
                        ((TextView)actividadesETSE.this.findViewById(R.id.message)).setText("Societies found:");
                        matchId(namespace, url);
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

    private void matchId(String id, String url){
        String Society = "";
        Log.i(TAG, "storeBeacons: matchID "+id);


        if(url.contains("http://www.goo.gl/6hiL4P")){
            Society = "Post Grad CIT";
            Log.i(TAG, "storeBeacons: matchID "+id);
        }else if(url.contains("badc4d168877")){
            Society = "   ";
        }else if(url.contains("3380475fa920")) {
            Society = "   ";
        }else if( url.contains("http://www.goo.gl/nL93Yh")) {
            Society = "International Society";
        }

        storeBeacons(id, Society, url);
    }

    public void storeBeacons(String id, String society, String url){
        Log.i(TAG, "storeBeacons:  "+society);
        int g=0;
        boolean found = false;
        for (int i=0; i<beaconsID.length;i++){
            Log.i(TAG, "storeBeacons:  for"+society);
            if(id.equalsIgnoreCase(beaconsID[i])){
                Log.i(TAG, "storeBeacons:  for id? "+society);
                found = true;
            }
            if(beaconsID[i]== null){
                g=i;
                Log.i(TAG, "storeBeacons: break "+g);
                show(found, g,id, society, url);
                break;
            }
        }
    }

    public void show(boolean found, int g, String id, String Society, String URL){
        Log.i(TAG, "show: "+g);
        if(!found && g<3){
            beaconsID[g] = id;
            beaconsSocieties[g] = Society;
            beaconsURL[g] =URL;
        }

        for (int i=0; i<beaconsID.length;i++){
            if(i==0){
                ((TextView)actividadesETSE.this.findViewById(R.id.tv12)).setText(beaconsSocieties[0]);
                ((TextView)actividadesETSE.this.findViewById(R.id.tv13)).setText(beaconsURL[0]);
            }else if(i==1){
                ((TextView)actividadesETSE.this.findViewById(R.id.tv22)).setText(beaconsSocieties[1]);
                ((TextView)actividadesETSE.this.findViewById(R.id.tv23)).setText(beaconsURL[1]);
            }else if(i==2){
                ((TextView)actividadesETSE.this.findViewById(R.id.tv32)).setText(beaconsSocieties[2]);
                ((TextView)actividadesETSE.this.findViewById(R.id.tv33)).setText(beaconsURL[2]);
            }
        }
    }
}
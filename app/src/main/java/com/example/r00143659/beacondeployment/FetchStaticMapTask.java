package com.example.r00143659.beacondeployment;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;

/**
 * Simple AsyncTask to fetch an image from the Static Maps API.
 */
class FetchStaticMapTask extends AsyncTask<String, Void, Bitmap> {
    private static final String TAG = FetchStaticMapTask.class.getSimpleName();

    private final ImageView view;

    FetchStaticMapTask(ImageView view) {
        this.view = view;
    }

    @Override
    protected Bitmap doInBackground(String... urls) {
        String url = urls[0];
        Bitmap image = null;
        try {
            InputStream in = new java.net.URL(url).openStream();
            image = BitmapFactory.decodeStream(in);
        }
        catch (IOException e) {
            Log.e(TAG, "IOException fetching map view", e);
        }
        return image;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (bitmap != null) {
            view.setImageBitmap(bitmap);
        }
    }
}

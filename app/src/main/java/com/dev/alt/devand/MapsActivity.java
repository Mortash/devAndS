

package com.dev.alt.devand;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class MapsActivity /*extends FragmentActivity implements OnMapReadyCallback*/ {
/*
    private GoogleMap mMap;

    private Bitmap testBitMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab2_gallery);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);


    }

    public Bitmap getBitmapFromURL(String src) {
        Toast.makeText(getApplicationContext(), "this is my Toast message!!! =)", Toast.LENGTH_LONG).show();

        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    //    ImageView bmImage;
        Bitmap dest;

        public DownloadImageTask(Bitmap dest) {
    //        this.bmImage = bmImage;
            this.dest=dest;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
        //    bmImage.setImageBitmap(result);
            Toast.makeText(getApplicationContext(), "success",
                    Toast.LENGTH_LONG).show();
            dest=result;
            testBitMap=result;
        }
    }
*/
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

/*
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.moveCamera(CameraUpdateFactory.zoomTo(8));
        // Add a marker in Sydney and move the camera
        LatLng paris = new LatLng(48.8534100, 2.3488000);
        mMap.addMarker(new MarkerOptions().position(paris).title("Marker in Paris"));


        final String TAG = "Test MAP";


        String pathMarker= "/Users/Tom/Desktop/logo.jpg";



        final LatLng pPerso = new LatLng(48.408, 2.699);

        ImageLoader imageLoader = ImageLoader.getInstance();

        String imageUri="http://img15.hostingpics.net/pics/388375Pioupiou.png";

        Bitmap testB = null;
        new DownloadImageTask(testB)
                .execute(imageUri);


        mMap.addMarker(new MarkerOptions().
                            position(pPerso).
                            title("Marker in fontainebleau")
                           // .icon(BitmapDescriptorFactory.fromResource(R.drawable.logo_test)));
                            //.icon(BitmapDescriptorFactory.fromBitmap(testBitMap)));
                           // .icon(BitmapDescriptorFactory.fromResource(dIcon)));

        );



        mMap.moveCamera(CameraUpdateFactory.newLatLng(paris));


    }

    */
}

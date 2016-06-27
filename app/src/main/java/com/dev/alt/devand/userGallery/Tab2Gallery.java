package com.dev.alt.devand.userGallery;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dev.alt.devand.R;

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


public class Tab2Gallery extends Fragment implements OnMapReadyCallback{

    private GoogleMap mMap;

    public Tab2Gallery() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager fm = getChildFragmentManager();

        SupportMapFragment mapFragment =(SupportMapFragment) fm.findFragmentById(R.id.map);

        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map, mapFragment).commit();
        }

        mapFragment.getMapAsync(this);


        //ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        //ImageLoader.getInstance().init(config);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.tab2_gallery, container, false);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.moveCamera(CameraUpdateFactory.zoomTo(8));
        // Add a marker in Sydney and move the camera
        LatLng paris = new LatLng(48.8534100, 2.3488000);
        mMap.addMarker(new MarkerOptions().position(paris).title("Paris la nuit"));


        final String TAG = "Test MAP";


        String pathMarker= "/Users/Tom/Desktop/logo.jpg";



        final LatLng pPerso = new LatLng(48.408, 2.699);

        ImageLoader imageLoader = ImageLoader.getInstance();

        String imageUri="http://img15.hostingpics.net/pics/388375Pioupiou.png";




        mMap.addMarker(new MarkerOptions().
                        position(pPerso).
                        title("Quel beau chateau")
                // .icon(BitmapDescriptorFactory.fromResource(R.drawable.logo_test)));
                //.icon(BitmapDescriptorFactory.fromBitmap(testBitMap)));
                // .icon(BitmapDescriptorFactory.fromResource(dIcon)));

        );


        final LatLng pEvry = new LatLng(48.633333, 2.450000);

        mMap.addMarker(new MarkerOptions().
                        position(pEvry).
                        title("Bienvenu à l'université")

                // .icon(BitmapDescriptorFactory.fromResource(R.drawable.logo_test)));
                //.icon(BitmapDescriptorFactory.fromBitmap(testBitMap)));
                 .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_evry))

        );


        mMap.moveCamera(CameraUpdateFactory.newLatLng(paris));


    }

}
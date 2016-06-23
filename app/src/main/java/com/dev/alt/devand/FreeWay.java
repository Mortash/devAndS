package com.dev.alt.devand;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.location.Criteria;
import android.location.Location;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.dev.alt.devand.connectDB.ConnectPicture;
import com.dev.alt.devand.entities.PersonEntity;
import com.dev.alt.devand.entities.DataBaseRepository;
import com.dev.alt.devand.service.receiverBroadcast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FreeWay extends AppCompatActivity implements SurfaceHolder.Callback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private PersonEntity pe;
    DataBaseRepository pr;
    BroadcastReceiver receiver;

    // photo vars
    private Camera camera;
    private SurfaceView surfaceCamera;
    private Boolean isPreview;
    private FileOutputStream stream;
    protected Uri image=null;
    Intent uploadIntent;

    // GPS var
    LocationRequest locationRequest;
    FusedLocationProviderApi fusedLocationProviderApi;
    GoogleApiClient googleApiClient;
    Location located;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_way);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        pr = new DataBaseRepository(getApplicationContext());
        pe = null;
        located = null;
        Bundle extras = getIntent().getExtras();

        // Check if the user is connected
        if (extras != null) {
            String login = extras.getString("login");

            if (pr.existPerson(login)) {
                pe = pr.getPerson(login);
            } else {
                Intent returnConnection = new Intent(FreeWay.this, Connection.class);
                returnConnection.putExtra("err", "Try to connect first");
                startActivity(returnConnection);
            }
        } else {
            Intent returnConnection = new Intent(FreeWay.this, Connection.class);
            returnConnection.putExtra("err", "Try to connect first");
            startActivity(returnConnection);
        }

        // If user is not connected
        if (pe == null || pe.getLoggedIn() == 0) {
            Intent returnConnection = new Intent(FreeWay.this, Connection.class);
            returnConnection.putExtra("err", "Try to connect first");
            startActivity(returnConnection);
        }

        Handler myHandler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                SavePicture();
            }
        };

        BroadcastReceiver receiver = new receiverBroadcast(myHandler);

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction("android.media.VOLUME_CHANGED_ACTION");
        registerReceiver(receiver, intentFilter);


        // Caméra
        isPreview = false;
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        surfaceCamera = (SurfaceView) findViewById(R.id.surfaceViewCamera);
        //SurfaceView view = new SurfaceView(this);  // Piste pour éviter la preview ?
        InitializeCamera();
        getLocation();

        Button login = (Button) findViewById(R.id.btn_stopFree);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleApiClient.disconnect();
                Intent i = new Intent(FreeWay.this, CheckPicture.class);
                i.putExtra("login", pe.getLogin());
                startActivity(i);
            }
        });
    }

    // Retour sur l'application
    @Override
    public void onResume() {
        super.onResume();
    }

    // Mise en pause de l'application
    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        googleApiClient.disconnect();
        try {
            unregisterReceiver(receiver);
        } catch (Exception e) {
            Log.e("FreeWay","Impossible de supprimer le receiver");
        }
    }

    // méthode pour la caméra
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        // Si le mode preview est lancé alors nous le stoppons
        if (isPreview) {
            camera.stopPreview();
        }

        // Nous récupérons les paramètres de la caméra
        Camera.Parameters parameters = camera.getParameters();

        // Nous changeons la taille
        parameters.setPreviewSize(width, height);

        // Nous appliquons nos nouveaux paramètres
        camera.setParameters(parameters);

        try {
            // Nous attachons notre prévisualisation de la caméra au holder de la surface
            camera.setPreviewDisplay(surfaceCamera.getHolder());
        } catch (IOException e) {
            Log.e("camera",e.getMessage());
        }

        // Nous lançons la preview
        camera.startPreview();
        isPreview = true;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // Nous arrêtons la camera et nous rendons la main
        if (camera != null) {
            camera.stopPreview();
            isPreview = false;
            camera.release();
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // Nous prenons le contrôle de la camera
        if (camera == null)
            camera = Camera.open();
    }

    public void InitializeCamera() {
        // Nous attachons nos retours du holder à notre activité
        surfaceCamera.getHolder().addCallback(this);
        // Nous spécifiions le type du holder en mode SURFACE_TYPE_PUSH_BUFFERS
        surfaceCamera.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

    }

    private void SavePicture() {
        getLocation();

        // Callback pour la prise de photo
        Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {

            public void onPictureTaken(byte[] data, Camera camera) {

                if (data != null) {
                    // Enregistrement de votre image
                    try {
                        if (stream != null) {
                            stream.write(data);
                            stream.flush();
                            stream.close();
                        }
                    } catch (Exception e) {
                        Log.e(this.getClass().getSimpleName(),e.getMessage()+"");
                    }

                    // Nous redémarrons la prévisualisation
                    camera.startPreview();
                }
            }
        };

        // Initialisation
        File file =  null;
        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String fileName = pe.getLogin() + "_" + timeStampFormat.format(new Date());

            String path = Environment.getExternalStorageDirectory().toString();
            file = new File(path, "/DCIM/Camera/" + fileName + ".png");
            stream = new FileOutputStream(file);

            Camera.Parameters params = camera.getParameters();
            params.setJpegQuality(100);
            camera.setParameters(params);

            camera.takePicture(null, null, pictureCallback);
        } catch (Exception e) {
            Log.d("FreeWay", "erreur en prenant la photo :" + e.getMessage());
        }

        // Récupération position GPS

        //données valide
        if(located==null) {
            this.onConnected(null);
        }
        Toast.makeText(this, "location :" + located.getLatitude() + " , " + located.getLongitude(), Toast.LENGTH_SHORT).show();

        // Enregistrement de l'image sur le serveur en décalé pour attendre le bon enregistrement de la photo sur le device
        if(file != null) {
            image = Uri.fromFile(file);
            uploadIntent = new Intent(FreeWay.this, ConnectPicture.class);
            uploadIntent.setData(image);
            uploadIntent.putExtra("login",pe.getLogin());
            uploadIntent.putExtra("latitude",located.getLatitude());
            uploadIntent.putExtra("longitude",located.getLongitude());

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startService(uploadIntent);
                }
            }, 4000);
        } else {
            Log.e("UploadFromFW", "Image null");
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.e("Location", "requested permission");
            return;
        }
        fusedLocationProviderApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void getLocation(){
        if(locationRequest == null) {
            locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(150);
            locationRequest.setFastestInterval(150);
            fusedLocationProviderApi = LocationServices.FusedLocationApi;
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            if (googleApiClient != null) {
                googleApiClient.connect();
            } else {
                Log.e("Location", "erreur création googleApiClient");
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        this.located = location;
    }
}

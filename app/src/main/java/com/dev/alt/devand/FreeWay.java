package com.dev.alt.devand;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.dev.alt.devand.helper.PersonEntity;
import com.dev.alt.devand.helper.PersonRepository;
import com.dev.alt.devand.service.receiverBroadcast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FreeWay extends AppCompatActivity implements SurfaceHolder.Callback {

    //Intent servDaemon;
    private PersonEntity pe;
    PersonRepository pr;
    BroadcastReceiver receiver;

    // photo vars
    private Camera camera;
    private SurfaceView surfaceCamera;
    private Boolean isPreview;
    private FileOutputStream stream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_way);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        pr = new PersonRepository(getApplicationContext());
        pe = null;

        Bundle extras = getIntent().getExtras();
/*
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
        }*/


        // Daemon
        Handler myHandler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                Log.d("handler", "handler lancé");
                SavePicture();
            }
        };

        BroadcastReceiver receiver = new receiverBroadcast(myHandler);

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction("android.media.VOLUME_CHANGED_ACTION");
        registerReceiver(receiver, intentFilter);


        //servDaemon = new Intent(this, Daemon.class);
        //startService(servDaemon);

        Log.d("freeway", "service lancé");
        Button login = (Button) findViewById(R.id.btn_stopFree);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //stopService(servDaemon);
                Log.d("freeway", "service arrété");
                Intent i = new Intent(FreeWay.this, MainMenu.class);
                //i.putExtra("login", pe.getLogin());
                startActivity(i);
            }
        });

        //IntentFilter filter = new IntentFilter();
        //filter.addAction("android.media.EXTRA_VOLUME_STREAM_VALUE");
    }

/*
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)){
            Toast.makeText(getApplicationContext(), "touche appuyé",
                    Toast.LENGTH_SHORT).show();
            Log.d("toast", "titi et gros minet");
            //CameraEntity ce = new CameraEntity();
            new Thread(new Runnable() {
                public void run() {
                    // Nous prenons une photo
                    if (camera != null) {
                        SavePicture();
                    }
                }
            }).start();
        }
        return true;
    }*/

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

        unregisterReceiver(receiver);
        //stopService(servDaemon);
    }

    // méthode pour la caméra
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    }

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
        } catch (Exception e) {
            Log.d("FreeWay", "Erreur à la mise en place du preview" + e.getMessage());
        }

        // Nous lançons la preview
        camera.startPreview();

        isPreview = true;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    public void InitializeCamera() {
        // Nous attachons nos retours du holder à notre activité
        surfaceCamera.getHolder().addCallback(this);
        // Nous spécifions le type du holder en mode SURFACE_TYPE_PUSH_BUFFERS
        surfaceCamera.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    private void SavePicture() {
        // Callback pour la prise de photo
        Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {

            public void onPictureTaken(byte[] data, Camera camera) {
                for(byte b : data) {
                    Log.d("picture", ""+ b);
                }

                if (data != null) {
                    Log.d("picture","data not null");
                    // Enregistrement de votre image
                    try {
                        if (stream != null) {
                            Log.d("picture","stream not null");
                            stream.write(data);
                            stream.flush();
                            stream.close();
                        }
                    } catch (Exception e) {
                    }

                    // Nous redémarrons la prévisualisation
                    camera.startPreview();
                }
            }
        };

        // Initialisation
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        isPreview = false;
        surfaceCamera = (SurfaceView) findViewById(R.id.surfaceViewCamera);
        InitializeCamera();
        camera = Camera.open();

        //try {
            SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss");
            String fileName = "photo_" + timeStampFormat.format(new Date()) + ".jpg";

            // Metadata pour la photo
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, fileName);
            values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
            values.put(MediaStore.Images.Media.DESCRIPTION, "Image prise par FormationCamera");
            values.put(MediaStore.Images.Media.DATE_TAKEN, new Date().getTime());
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

            // Support de stockage
            Uri taken = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

            // Ouverture du flux pour la sauvegarde
        try {
            stream = (FileOutputStream) getContentResolver().openOutputStream(taken);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        camera.takePicture(null, null, pictureCallback);
        /*} catch (Exception e) {
            Log.d("FreeWay", "erreur en prenant la photo :" + e.getMessage());
        }*/

        // Destruction
        camera.stopPreview();
        isPreview = false;
        camera.release();
    }
}

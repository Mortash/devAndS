package com.dev.alt.devand.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Camera;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/*******************
 * Inutilisé
 *******************/

public class Daemon extends Service implements VolumeContentObserverCallback {

    private final BroadcastReceiver receiver = new BroadcastReceiver() {

            boolean b = true;
            String a ="";

            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                    Log.d("daemon", Intent.ACTION_SCREEN_OFF);
                } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                    Log.d("daemon", Intent.ACTION_SCREEN_ON);
                }

                int event = -1;
                if ("android.media.VOLUME_CHANGED_ACTION".equals(intent.getAction())) {
                    event = (int) intent
                            .getExtras().get("android.media.EXTRA_VOLUME_STREAM_TYPE");
                }

                if(event == -1) {
                    return;
                } else {
                    if(b) {
                        Log.e("broadcast", "I got volume key down event");
                        a += "a";
                        Toast.makeText(context, a, Toast.LENGTH_LONG).show();
                    }
                    b = !b;
                }
            }

        };

    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    @Override
    public int  onStartCommand(Intent intent, int flags, int startId) {

        Log.e("service", "service opérationnelle");

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction("android.media.VOLUME_CHANGED_ACTION");
        registerReceiver(receiver, intentFilter);

        return Service.START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        stopSelf();
        //getApplicationContext().getContentResolver().unregisterContentObserver(vco);
    }

    public void update() {
        Log.d("vco", "Winter is coming");
    }
}

package com.dev.alt.devand.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

public class receiverBroadcast extends BroadcastReceiver {

    boolean b = true;
    private Handler handler;

    public receiverBroadcast(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        //TODO possible d'améliorer l'algorithme
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            Log.d("daemon", Intent.ACTION_SCREEN_OFF);
            // lance le Handler pour prendre la photo
            if (handler != null) {
                // lance le Handler pour prendre la photo
                handler.sendEmptyMessage(0);
            }
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            Log.d("daemon", Intent.ACTION_SCREEN_ON);
            // lance le Handler pour prendre la photo
            if (handler != null) {
                // lance le Handler pour prendre la photo
                handler.sendEmptyMessage(0);
            }
        }

        int event = -1;
        if ("android.media.VOLUME_CHANGED_ACTION".equals(intent.getAction())) {
            event = (int) intent
                    .getExtras().get("android.media.EXTRA_VOLUME_STREAM_TYPE");
        }

        if(event != -1) {
            if(b) {
                Log.e("broadcast", "broadcasté !!!");
                if (handler != null) {
                    // lance le Handler pour prendre la photo
                    handler.sendEmptyMessage(0);
                }
            }
            b = !b;
        }
    }
}

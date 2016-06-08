package com.dev.alt.devand.service;

import android.content.Context;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.util.logging.Logger;

public class VolumeContentObserver extends ContentObserver {

    Context context;
    boolean b;
    Handler handler;
    AudioManager audio;
    VolumeContentObserverCallback vcoc;

    public VolumeContentObserver(Context c, Handler handler, VolumeContentObserverCallback vcoc) {
        super(handler);
        this.handler = handler;
        context=c;
        b=true;
        this.vcoc = vcoc;
        Log.e("vco", "AM gSS");
        audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        Log.e("vco", "AM gedSS");
    }

    @Override
    public boolean deliverSelfNotifications() {
        return super.deliverSelfNotifications();
    }

    @Override
    public void onChange(boolean selfChange) {
        handler.post(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(context, "Toast", Toast.LENGTH_SHORT).show();
            }
        });
        Log.e("vco", "AM gSS");
        this.onChange(selfChange, null);
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange);

        Log.e("vco", "AM gSS");
        audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        Log.e("vco", "AM gedSS");

        // intercepte juste le press_down
        if(b) {
            handler.post(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(context, "Toast", Toast.LENGTH_SHORT).show();
                }
            });

            if(vcoc != null) {
                vcoc.update();
            }
        }
        b = !b;
    }
}

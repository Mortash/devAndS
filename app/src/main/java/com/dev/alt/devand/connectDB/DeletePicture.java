package com.dev.alt.devand.connectDB;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.dev.alt.devand.JSONParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DeletePicture extends IntentService {
    private static final String TAG = "UpdatePictureService";
    private static final String TAG_SUCCESSFUL = "success";
    private static final String TAG_PICTURE = "numPictures";
    private static final String TAG_WALK = "numWalks";
    private static final String TAG_SUCCESS = "numSucces";
    private static final String TAG_FRIEND ="numFriends";

    public DeletePicture() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Bundle extras = intent.getExtras();

        int idPict = extras.getInt("id",-1);

        Log.d("updatePicture", "id: " + idPict);
        if(idPict != -1) {
            // Creating JSON Parser object
            JSONParser jParser = new JSONParser();

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id", idPict+""));

            // getting JSON string from URL
            String url_connection = "http://alt.moments.free.fr/requests/delPicture.php";
            JSONObject json = jParser.makeHttpRequest(url_connection, "POST", params);

            // Check your log cat for JSON response
            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESSFUL);

                if (success == 1) {
                    // Maj réussi
                } else {
                    Log.d(this.getClass().getSimpleName(), "Échec lors de la récupération des informations.");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

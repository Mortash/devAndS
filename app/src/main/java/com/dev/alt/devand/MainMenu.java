package com.dev.alt.devand;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.dev.alt.devand.entities.PersonEntity;
import com.dev.alt.devand.entities.DataBaseRepository;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainMenu extends Activity {

    private PersonEntity pe;
    DataBaseRepository pr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);

        pr = new DataBaseRepository(getApplicationContext());
        pe = null;
        Bundle extras = getIntent().getExtras();

        // Check if the user is connected
        if (extras != null) {
            String login = extras.getString("login");

            if (pr.existPerson(login)) {
                pe = pr.getPerson(login);
            } else {
                Intent returnConnection = new Intent(MainMenu.this, Connection.class);
                returnConnection.putExtra("err", "Try to connect first");
                startActivity(returnConnection);
            }
        } else {
            Intent returnConnection = new Intent(MainMenu.this, Connection.class);
            returnConnection.putExtra("err", "Try to connect first");
            startActivity(returnConnection);
        }

        // If user is not connected
        if (pe == null || pe.getLoggedIn() == 0) {
            Intent returnConnection = new Intent(MainMenu.this, Connection.class);
            returnConnection.putExtra("err", "Try to connect first");
            startActivity(returnConnection);
        }

        // Update the username
        TextView name = (TextView) findViewById(R.id.tv_name);
        name.setText(pe.getLogin());

        //On met à jour les infos de l'utilisateur
        GetUserInfos infos = new GetUserInfos(pe.getLogin());
        infos.execute();

        // Bind components
        TextView login = (TextView) findViewById(R.id.tv_disconnect);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pe.setLoggedIn(0);
                //pr.updatePerson(pe);

                Intent logAction = new Intent(MainMenu.this, Connection.class);
                startActivity(logAction);
            }
        });

        ImageButton startMoment = (ImageButton) findViewById(R.id.ib_start_moment);
        startMoment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Intent logAction = new Intent(MainMenu.this, .class);
                //startService(logAction);
            }
        });

        ImageButton startFree = (ImageButton) findViewById(R.id.ib_start_free);
        startFree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent freeIntent = new Intent(MainMenu.this, FreeWay.class);
                freeIntent.putExtra("login", pe.getLogin());
                startActivity(freeIntent);
            }
        });

        ImageButton friendsList = (ImageButton) findViewById(R.id.ib_friend);
        friendsList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent friendIntent = new Intent(MainMenu.this, FriendList.class);
                friendIntent.putExtra("login", pe.getLogin());
                startActivity(friendIntent);
            }
        });

        ImageButton picList = (ImageButton) findViewById(R.id.ib_pics);
        picList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent friendIntent = new Intent(MainMenu.this, UserGallery.class);
                friendIntent.putExtra("login", pe.getLogin());
                startActivity(friendIntent);
            }
        });
    }

    class GetUserInfos extends AsyncTask<String, String, String> {
        private String login;
        private String numPictures = "0";
        private String numWalks = "0";
        private String numSuccess ="0";
        private String numFriends = "0";
        private static final String TAG_SUCCESSFUL = "success";
        private static final String TAG_PICTURE = "numPictures";
        private static final String TAG_WALK = "numWalks";
        private static final String TAG_SUCCESS = "numSucces";
        private static final String TAG_FRIEND ="numFriends";

        // Creating JSON Parser object
        JSONParser jParser = new JSONParser();

        public GetUserInfos(String l){
            this.login = l;
        }

        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("login", login));

            // getting JSON string from URL
            String url_connection = "http://alt.moments.free.fr/requests/getUserInfos.php";
            JSONObject json = jParser.makeHttpRequest(url_connection, "POST", params);

            try {
                Log.e("récup info photos", json.getString("message") + " ");
            } catch (Exception e) {
                Log.e(this.getClass().getSimpleName(), e.getMessage());
            }
            // Check your log cat for JSON response
            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESSFUL);

                if (success == 1) {
                    //On récupère le résultat du retour de la requête php
                    numPictures = Integer.toString(json.getInt(TAG_PICTURE));
                    numWalks = Integer.toString(json.getInt(TAG_WALK));
                    numSuccess = Integer.toString(json.getInt(TAG_SUCCESS));
                    numFriends = Integer.toString(json.getInt(TAG_FRIEND));
                } else {
                    Log.d(this.getClass().getSimpleName(), "Échec lors de la récupération des informations.");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String file_url) {
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    //Affichage des données
                    TextView displayNbPicture = (TextView) findViewById(R.id.tv_photos);
                    displayNbPicture.setText(numPictures);
                    TextView displayNbWalk = (TextView) findViewById(R.id.tv_moments);
                    displayNbWalk.setText(numWalks);
                    TextView displayNbSuccess = (TextView) findViewById(R.id.tv_success);
                    displayNbSuccess.setText(numSuccess);
                    TextView displayNbFriend = (TextView) findViewById(R.id.tv_friend);
                    displayNbFriend.setText(numFriends);
                }
            });
        }
    }
}

package com.dev.alt.devand;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dev.alt.devand.entities.DataBaseRepository;
import com.dev.alt.devand.entities.PersonEntity;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FriendList extends AppCompatActivity {

    private PersonEntity pe;
    DataBaseRepository pr;
    String[] separatedFriends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends);

        pr = new DataBaseRepository(getApplicationContext());
        pe = null;
        Bundle extras = getIntent().getExtras();

        //Check if the user is connected
        if(extras != null) {
            String login = extras.getString("login");

            if (pr.existPerson(login)) {
                pe = pr.getPerson(login);
            } else {
                Intent returnConnection = new Intent(FriendList.this, Connection.class);
                returnConnection.putExtra("err", "Try to connect first");
                startActivity(returnConnection);
            }
        } else {
            Intent returnConnection = new Intent(FriendList.this, Connection.class);
            returnConnection.putExtra("err", "Try to connect first");
            startActivity(returnConnection);
        }

        //If user is not connected
        if(pe == null || pe.getLoggedIn() == 0) {
            Intent returnConnection = new Intent(FriendList.this, Connection.class);
            returnConnection.putExtra("err", "Try to connect first");
            startActivity(returnConnection);
        }

        // Update the username
        TextView name = (TextView) findViewById(R.id.tv_name);
        name.setText(pe.getLogin());

        // Bind components
        TextView login = (TextView) findViewById(R.id.tv_disconnect);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pe.setLoggedIn(0);
                //pr.updatePerson(pe);

                Intent logAction = new Intent(FriendList.this, Connection.class);
                startActivity(logAction);
            }
        });

        //On récupère la liste des amis
        GetFriendsList friendLv = new GetFriendsList(pe.getLogin());
        friendLv.execute();

        //Liste des amis
        /*final ListView showListFriends = (ListView) findViewById(R.id.lv_friendsList);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, separatedFriends);
        showListFriends.setAdapter(adapter);
        showListFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                AlertDialog.Builder adb = new AlertDialog.Builder(FriendList.this);
                adb.setTitle("Delete?");
                adb.setMessage("Are you sure you want to delete " + position);
                final int positionToRemove = position;
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String friendToDel = adapter.getItem(positionToRemove);
                        adapter.remove(friendToDel);
                        adapter.notifyDataSetChanged();
                    }});
                adb.show();
            }
        });*/


        //Ajout d'un ami
        final EditText tv_SearchFriend = (EditText) findViewById(R.id.tv_searchFriend);
        Button addFriend = (Button) findViewById(R.id.btn_addFriend);
        addFriend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String friendRequest = tv_SearchFriend.getText().toString();

                //On ajoute l'ami uniquement s'il n'est pas déjà présent dans la liste
                boolean isAlreadyExist = false;
                if(separatedFriends.length > 0){
                    for(int i = 0;i < separatedFriends.length;i++){
                        if(separatedFriends[i].equals(friendRequest)){
                            isAlreadyExist = true;
                        }
                    }
                }
                if(isAlreadyExist == false){
                    AddNewFriend createdFriend = new AddNewFriend(pe.getLogin(),friendRequest);
                    createdFriend.execute();
                    //On remet le champ de saisi à vide
                    tv_SearchFriend.setText("");

                    //On appelle à nouveau la récupération de la liste
                    GetFriendsList friendLv = new GetFriendsList(pe.getLogin());
                    friendLv.execute();
                } else {
                    Toast.makeText(FriendList.this, R.string.alreadyFriend, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /******************************/
    /****Remplissage liste ami*****/
    /******************************/

    class GetFriendsList extends AsyncTask<String, String, String> {
        private String login;
        private String listFriends;
        private static final String TAG_SUCCESSFUL = "success";
        private static final String TAG_MESSAGE = "message";
        private static final String TAG_FRIEND = "listFriends";

        // Creating JSON Parser object
        JSONParser jParser = new JSONParser();

        public GetFriendsList(String l){
            this.login = l;
        }

        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("login", login));

            // getting JSON string from URL
            String url_connection = "http://alt.moments.free.fr/requests/getFriendsList.php";
            JSONObject json = jParser.makeHttpRequest(url_connection, "POST", params);

            try {
                Log.e("Récupération liste ami:", json.getString("message") + " ");
            } catch (Exception e) {
                Log.e(this.getClass().getSimpleName(), e.getMessage());
            }
            // Check your log cat for JSON response
            try {
                // Checking for TAG
                int success = json.getInt(TAG_SUCCESSFUL);
                String message = json.getString(TAG_MESSAGE);

                if (success == 1) {
                    //On récupère le résultat du retour de la requête php
                    listFriends = json.getString(TAG_FRIEND);
                } else {
                    if(message.equals("NoFriend")){
                        listFriends = "Aucun ami";
                    } else {
                        Log.d(this.getClass().getSimpleName(), "Échec lors de la récupération des informations.");
                    }
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
                    ListView displayListFriends = (ListView) findViewById(R.id.lv_friendsList);

                    if(listFriends != "Aucun ami"){
                        //On retire les {}
                        listFriends = listFriends.substring(1,listFriends.length()-1);
                    }
                    //On sépare chaque élément de la chaine pour les mettre dans un tableau
                    String delims = ",";
                    separatedFriends = listFriends.split(delims);

                    //On trie le tableau
                    Arrays.sort(separatedFriends);

                    //On ajoute le tableau à la listView
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(FriendList.this, android.R.layout.simple_list_item_1, separatedFriends);
                    displayListFriends.setAdapter(adapter);
                }
            });
        }
    }

    /******************************/
    /*******Ajout nouvel  ami******/
    /******************************/

    class AddNewFriend extends AsyncTask<String, String, String> {
        private String login;
        private String friend;
        private static final String TAG_SUCCESSFUL = "success";
        private static final String TAG_MESSAGE = "message";
        private boolean isCreated = false;

        // Creating JSON Parser object
        JSONParser jParser = new JSONParser();

        public AddNewFriend(String l, String f){
            this.login = l;
            this.friend = f;
        }

        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("login", login));
            params.add(new BasicNameValuePair("friend", friend));

            // getting JSON string from URL
            String url_connection = "http://alt.moments.free.fr/requests/addFriend.php";
            JSONObject json = jParser.makeHttpRequest(url_connection, "POST", params);

            try {
                Log.e("Insertion ami:", json.getString("message") + " ");
            } catch (Exception e) {
                Log.e(this.getClass().getSimpleName(), e.getMessage());
            }
            // Check your log cat for JSON response
            try {
                // Checking for TAG
                int success = json.getInt(TAG_SUCCESSFUL);
                String message = json.getString(TAG_MESSAGE);

                if(success == 1) {
                    //Si l'insertion est réussie
                    isCreated = true;
                } else {
                    if(message.equals("NoUser")){
                        Log.e("User doesnt exist", json.getString("message") + " ");
                    } else {
                        Log.d(this.getClass().getSimpleName(), "Insertion failed.");
                    }
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
                    if(isCreated == true){ //Ajout de l'ami
                        Toast.makeText(FriendList.this, R.string.friendAdded, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(FriendList.this, R.string.noUser, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}

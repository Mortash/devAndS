package com.dev.alt.devand;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dev.alt.devand.helper.PersonEntity;
import com.dev.alt.devand.helper.PersonRepository;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Connection extends AppCompatActivity {

    private PersonEntity pe;
    PersonRepository pr;

    // Progress Dialog
    private ProgressDialog pDialog;

    // url to get all products list
    private static String url_connection = "http://alt.moments.free.fr/requests/identify_user.php";

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "MESSAGE";
    private static final String TAG_LOGIN = "login";
    private static final String TAG_MAIL = "mail";
    private static final String TAG_SOCIALKEY = "socialKey";
    private static final String SALAGE = "alt";

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pr = new PersonRepository(getApplicationContext());
        pe = null;

        //TODO: récupérer la dernière personne connecté, et si elle a toujouts pe.getLoggedIn()==1 alors lancer directement le MainMenu avec lui.

        Bundle extras = getIntent().getExtras();

        setContentView(R.layout.login);

        Button login = (Button) findViewById(R.id.btn_tryLogin);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectUser cu = new ConnectUser(((EditText) findViewById(R.id.et_login)).getText().toString(), ((EditText) findViewById(R.id.et_password)).getText().toString());
                cu.execute();
            }
        });

        TextView registration = (TextView) findViewById(R.id.btn_SignUpViaForm);
        registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Connection.this, Registration.class);
                startActivity(i);
            }
        });
    }

    class ConnectUser extends AsyncTask<String, String, String> {
        private String login;
        private String password;
        protected ConnectUser(String l, String p) {
            this.login = l;
            this.password = p;
        }

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Connection.this);
            pDialog.setMessage("Loading... Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }


        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("login", login));
            params.add(new BasicNameValuePair("pass", get_SHA_512_SecurePassword(password)));

            // Permets de bypass le login par internet
            Intent in = new Intent(getApplicationContext(), MainMenu.class);
            in.putExtra("login", "ate");
            startActivity(in);
            return null;

            /*
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_connection, "POST", params);

            // Check your log cat for JSON response
            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    PersonEntity pe = new PersonEntity(login,
                            json.getString(TAG_MAIL),
                            json.getString(TAG_SOCIALKEY),
                            1);

                    PersonRepository pr = new PersonRepository(getApplicationContext());

                    if(pr.existPerson(login)) {
                        pr.updatePerson(pe);
                    } else {
                        pr.addPerson(pe);
                    }

                    Intent i = new Intent(getApplicationContext(), MainMenu.class);
                    i.putExtra("login", login);
                    startActivity(i);
                } else {
                    //TODO ajouter des messages d'erreur suivant le TAG_MESSAGE
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;*/
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after response
            pDialog.dismiss();
            // updating UI from Background Thread
            //runOnUiThread(new Runnable() {
            //    public void run() {
            //   }
            //});
        }

        public String get_SHA_512_SecurePassword(String passwordToHash){
            String generatedPassword = null;
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-512");
                md.update(SALAGE.getBytes("UTF-8"));
                byte[] bytes = md.digest(passwordToHash.getBytes("UTF-8"));
                StringBuilder sb = new StringBuilder();
                for(int i=0; i< bytes.length ;i++){
                    sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
                }
                generatedPassword = sb.toString();
            }
            catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            catch (NoSuchAlgorithmException e){
                e.printStackTrace();
            }
            return generatedPassword;
        }
    }
}

package com.dev.alt.devand;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.dev.alt.devand.entities.PersonEntity;
import com.dev.alt.devand.entities.DataBaseRepository;
import com.dev.alt.devand.helper.Crypto;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Connection extends AppCompatActivity {

    private PersonEntity pe;
    DataBaseRepository pr;

    // Progress Dialog
    private ProgressDialog pDialog;

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MAIL = "mail";
    private static final String TAG_SOCIALKEY = "socialKey";

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    //Facebook variables
    //private String String_APP_ID = getString(R.string.facebook_app_id);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Init DB
        pr = new DataBaseRepository(getApplicationContext());
        pe = null;

        //TODO: récupérer la dernière personne connecté, et si elle a toujours pe.getLoggedIn()==1 alors lancer directement le MainMenu avec lui.

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

        ImageButton loginFB = (ImageButton) findViewById(R.id.imgBtn_tryLoginFb);
        loginFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //loginToFacebook();
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
            params.add(new BasicNameValuePair("pass", Crypto.get_SHA_512_SecurePassword(password)));

            // getting JSON string from URL
            String url_connection = "http://alt.moments.free.fr/requests/identify_user.php";
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

                    DataBaseRepository pr = new DataBaseRepository(getApplicationContext());

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
                    Toast.makeText(Connection.this, "erreur", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after response
            pDialog.dismiss();
            // updating UI from Background Thread
            //runOnUiThread(new Runnable() {
            //    public void run() {
            //   }
            //});
        }
    }
}

package com.dev.alt.devand;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.dev.alt.devand.helper.PersonEntity;
import com.dev.alt.devand.helper.PersonRepository;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class Registration extends Activity {

    // Progress Dialog
    private ProgressDialog pDialog;

    // url to get all products list
    private static String url_connection = "http://alt.moments.free.fr/requests/create_user.php";

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_MAIL = "mail";
    private static final String SALAGE = "alt";
    private static final String TAG_LOGIN = "login";
    private static final String TAG_SOCIALKEY = "socialKey";

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.registration);

        final EditText pass = (EditText) findViewById(R.id.et_password);
        pass.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    if(event.getRawX() >= (pass.getRight() - 10 - pass.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        pass.setTransformationMethod(null);

                        return true;
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (pass.getRight() - 10 - pass.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        pass.setTransformationMethod(new PasswordTransformationMethod());

                        return true;
                    }
                }
                return false;
            }
        });

        final EditText Cpass = (EditText) findViewById(R.id.et_confirmPassword);
        Cpass.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    if(event.getRawX() >= (Cpass.getRight() - 10 - Cpass.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        Cpass.setTransformationMethod(null);

                        return true;
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (Cpass.getRight() - 10 - Cpass.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        Cpass.setTransformationMethod(new PasswordTransformationMethod());

                        return true;
                    }
                }
                return false;
            }
        });

        Button canc = (Button) findViewById(R.id.btn_cancel);
        canc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent logAction = new Intent(Registration.this, Connection.class);
                startActivity(logAction);
            }
        });


        Button save = (Button) findViewById(R.id.btn_tryRegister);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String l = ((EditText) findViewById(R.id.et_login)).getText().toString();
                String p = ((EditText) findViewById(R.id.et_password)).getText().toString();
                String cp = ((EditText) findViewById(R.id.et_confirmPassword)).getText().toString();
                String m = ((EditText) findViewById(R.id.et_mail)).getText().toString();
                if(p.equals(cp)) {
                    SaveUser cu = new SaveUser(l,p,m);
                    cu.execute();
                }
            }
        });

    }

    class SaveUser extends AsyncTask<String, String, String> {
        private String login;
        private String password;
        private String mail;

        protected SaveUser(String l, String p, String m) {
            this.login = l;
            this.password = p;
            this.mail = m;
        }

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Registration.this);
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
            params.add(new BasicNameValuePair("mail", mail));

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
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after response
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                }
            });
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

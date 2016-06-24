package com.dev.alt.devand;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.dev.alt.devand.entities.DataBaseRepository;
import com.dev.alt.devand.entities.PersonEntity;
import com.dev.alt.devand.userGallery.Tab1Gallery;
import com.dev.alt.devand.userGallery.Tab2Gallery;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class UserGallery extends AppCompatActivity {

    private ProgressDialog pDialog;
    JSONParser jParser = new JSONParser();
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_URI = "uri";
    String uriPict;

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private PersonEntity pe;
    DataBaseRepository pr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_gallery);

        pr = new DataBaseRepository(getApplicationContext());
        pe = null;
        Bundle extras = getIntent().getExtras();

        // Check if the user is connected
        if (extras != null) {
            String login = extras.getString("login");

            if (pr.existPerson(login)) {
                pe = pr.getPerson(login);
            } else {
                Intent returnConnection = new Intent(UserGallery.this, Connection.class);
                returnConnection.putExtra("err", "Try to connect first");
                startActivity(returnConnection);
            }
        } else {
            Intent returnConnection = new Intent(UserGallery.this, Connection.class);
            returnConnection.putExtra("err", "Try to connect first");
            startActivity(returnConnection);
        }

        // If user is not connected
        if (pe == null || pe.getLoggedIn() == 0) {
            Intent returnConnection = new Intent(UserGallery.this, Connection.class);
            returnConnection.putExtra("err", "Try to connect first");
            startActivity(returnConnection);
        }

        GetPictUser gpu = new GetPictUser(pe.getLogin());
        gpu.execute();

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading... Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        Bundle bundle=new Bundle();
        bundle.putString("pict_url", ""+uriPict);
        Tab1Gallery tg = new Tab1Gallery();
        tg.setArguments(bundle);

        adapter.addFragment(tg, "Gallerie");
        adapter.addFragment(new Tab2Gallery(), "Map");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    class GetPictUser extends AsyncTask<String, String, String> {
        private String login;

        protected GetPictUser(String l) {
            this.login = l;
        }

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("login", login));

            // getting JSON string from URL
            String url_connection = "http://alt.moments.free.fr/requests/getListPictUser.php";
            JSONObject json = jParser.makeHttpRequest(url_connection, "POST", params);

            // Check your log cat for JSON response
            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    uriPict = json.getString(TAG_URI);
                    Log.e("UserGalleryResult", "GetPictUser : " + uriPict);

                    String[] separated = uriPict.split(",");

                    for(int i=0; i< separated.length;i++) {
                        Log.d("UserGalleryResult",separated[i]);

                        String add = "http://alt.moments.free.fr/requests/downloadPictures.php?uri=" + separated[i];
                        URL url = null;
                        Bitmap image = null;
                        try {
                            url = new URL(add);
                            image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        Log.d("UserGalleryResult","image récupéré");

                        final File dir = new File(getBaseContext().getFilesDir() + "/DCIM/Camera/");
                        dir.mkdirs(); //create folders where write files
                        final File file = new File(dir, separated[i].split("/")[1]);

                        FileOutputStream out = null;
                        try {
                            out = new FileOutputStream(Environment.getExternalStorageDirectory().toString() + "/DCIM/Camera/" + separated[i].split("/")[1]);
                            image.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
                            // PNG is a lossless format, the compression factor (100) is ignored
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if (out != null) {
                                    out.flush();
                                    out.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        Log.d("UserGalleryResult","image enregistré");
                    }
                    //
                } else {
                    //TODO ajouter des messages d'erreur suivant le TAG_MESSAGE
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
            pDialog.dismiss();


            viewPager = (ViewPager) findViewById(R.id.viewpager);
            setupViewPager(viewPager);

            tabLayout = (TabLayout) findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(viewPager);
            // updating UI from Background Thread
            //runOnUiThread(new Runnable() {
            //    public void run() {
            //   }
            //});
        }
    }
}
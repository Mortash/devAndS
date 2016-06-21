package com.dev.alt.devand;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.dev.alt.devand.connectDB.DeletePicture;
import com.dev.alt.devand.connectDB.UpdatePicture;
import com.dev.alt.devand.entities.PersonEntity;
import com.dev.alt.devand.entities.DataBaseRepository;
import com.dev.alt.devand.entities.PictureEntity;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CheckPicture extends Activity {

    private PersonEntity pe;
    private DataBaseRepository pr;
    private List<PictureEntity> lpe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.checkpicture);
        pr = new DataBaseRepository(getApplicationContext());
        pe = null;

        Bundle extras = getIntent().getExtras();

        // Check if the user is connected
        if (extras != null) {
            String login = extras.getString("login");

            if (pr.existPerson(login)) {
                pe = pr.getPerson(login);
            } else {
                Intent returnConnection = new Intent(CheckPicture.this, Connection.class);
                returnConnection.putExtra("err", "Try to connect first");
                startActivity(returnConnection);
            }
        } else {
            Intent returnConnection = new Intent(CheckPicture.this, Connection.class);
            returnConnection.putExtra("err", "Try to connect first");
            startActivity(returnConnection);
        }


        // If user is not connected
        if (pe == null || pe.getLoggedIn() == 0) {
            Intent returnConnection = new Intent(CheckPicture.this, Connection.class);
            returnConnection.putExtra("err", "Try to connect first");
            startActivity(returnConnection);
        }

        lpe = pr.getAllPicture();
        for(PictureEntity pep : lpe) {
            Log.e("checkpicture", "idpicture : " + pep.getIdPicture()+" ; ");
        }

        ImageView image_preview = (ImageView) findViewById(R.id.iv_imagePreview);
        String path = Environment.getExternalStorageDirectory().toString() + "/DCIM/Camera/" + (lpe.get(0).getPathPicture().split("/"))[1];
        Bitmap bMap = BitmapFactory.decodeFile(path);
        image_preview.setImageBitmap(bMap);

        Log.e("checkpicture", path);

        // Update the username
        TextView name = (TextView) findViewById(R.id.tv_name);
        name.setText(pe.getLogin());


        ImageButton check = (ImageButton) findViewById(R.id.ib_check);
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO vérifier si le champ de commentaire a été rempli (si oui envoyer une requete de maj)
                // Mise à jour du commentaire
                EditText comm = (EditText) findViewById(R.id.et_commentaire);
                if(!comm.getText().toString().equals("")) {
                    Log.e("checkpicture", "commentaire il y a !!!");
                    Intent servMaj = new Intent(CheckPicture.this, UpdatePicture.class);
                    servMaj.putExtra("id", lpe.get(0).getIdPicture());
                    servMaj.putExtra("comm", comm.getText().toString());
                    startService(servMaj);
                }

                // Suppression de l'entité traité de la liste
                pr.deletePicture(lpe.get(0));
                lpe.remove(0);

                actionCheck();
            }
        });


        ImageButton uncheck = (ImageButton) findViewById(R.id.ib_uncheck);
        uncheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO : envoyer une requete pour supprimer la photo du serveur (?) + bdd

                Intent servMaj = new Intent(CheckPicture.this, DeletePicture.class);
                servMaj.putExtra("id", lpe.get(0).getIdPicture());
                startService(servMaj);

                // Suppression de l'entité traité de la liste
                pr.deletePicture(lpe.get(0));
                lpe.remove(0);

                actionCheck();
            }
        });


        Button delay = (Button) findViewById(R.id.b_delay);
        delay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dela = new Intent(CheckPicture.this, MainMenu.class);
                dela.putExtra("login", pe.getLogin());
                startActivity(dela);
            }
        });
    }

    public void actionCheck() {
        if(lpe.size() > 0) {

            EditText comm = (EditText) findViewById(R.id.et_commentaire);
            comm.setText("");
            // Affichage de la prochaine photo à traiter par l'utilisateur
            ImageView image_preview = (ImageView) findViewById(R.id.iv_imagePreview);
            String path = Environment.getExternalStorageDirectory().toString() + "/DCIM/Camera/" + (lpe.get(0).getPathPicture().split("/"))[1];
            Bitmap bMap = BitmapFactory.decodeFile(path);
            image_preview.setImageBitmap(bMap);
        } else {
            Intent i = new Intent(CheckPicture.this, MainMenu.class);
            i.putExtra("login",pe.getLogin());
            startActivity(i);
        }
    }
}

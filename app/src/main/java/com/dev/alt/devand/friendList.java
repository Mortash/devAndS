package com.dev.alt.devand;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.dev.alt.devand.entities.DataBaseRepository;
import com.dev.alt.devand.entities.PersonEntity;

public class FriendList extends AppCompatActivity {

    private PersonEntity pe;
    DataBaseRepository pr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends);
        pr = new DataBaseRepository(getApplicationContext());

        //On vérifie la bonne connexion de l'utilisateur
        pe = null;
        Bundle extras = getIntent().getExtras();

        // Check if the user is connected
        if (extras != null) {
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

        // If user is not connected
        if (pe == null || pe.getLoggedIn() == 0) {
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
    }
}

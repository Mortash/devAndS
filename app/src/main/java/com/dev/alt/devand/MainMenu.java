package com.dev.alt.devand;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.dev.alt.devand.entities.PersonEntity;
import com.dev.alt.devand.entities.PersonRepository;

public class MainMenu extends Activity {

    private PersonEntity pe;
    PersonRepository pr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);

        // Permets de bypass le login
        pr = new PersonRepository(getApplicationContext());
        pe = null;
        Bundle extras = getIntent().getExtras();

        // Check if the user is connected
        if (extras != null) {
            String login = extras.getString("login");

            if(pr.existPerson(login)) {
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
        if(pe== null || pe.getLoggedIn() == 0) {
            Intent returnConnection = new Intent(MainMenu.this, Connection.class);
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
    }
}

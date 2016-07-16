package com.example.user.music.activity.facebook;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import com.example.user.music.R;


public class FetchContactDetails extends Activity {

    public void getContacts() {
        EditText contactsView = (EditText) findViewById(R.id.show_contacts);
        contactsView.setText("Utkarsh!");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch_contact_details);

        final Button launchContacts = (Button) findViewById(R.id.launch_contacts);
        launchContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContacts();
            }
        });
    }
}

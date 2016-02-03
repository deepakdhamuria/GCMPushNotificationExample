package com.prgguru.example;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;


public class ResponseFromHRActivity extends Activity {

    String eMailId, ticketID;
    TextView standardMessage, employeeLoggedIn;
    EditText responseRecieved;
    Context applicationContext;
    ProgressDialog prgDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_response_from_hr);

        SharedPreferences prefs = getSharedPreferences("UserDetails",
                Context.MODE_PRIVATE);
        eMailId = prefs.getString("eMailId", "");
        prgDialog = new ProgressDialog(this);
        // Set Progress Dialog Text
        prgDialog.setMessage("Please wait...");
        // Set Cancelable as False
        prgDialog.setCancelable(false);
        applicationContext = getApplicationContext();
        employeeLoggedIn = (TextView) findViewById(R.id.loggedInEmployee);
        standardMessage = (TextView) findViewById(R.id.standardMessage);
        responseRecieved = (EditText) findViewById(R.id.responseRecieved);
        employeeLoggedIn.setText("Hello "+ eMailId);
        if (getIntent().getStringExtra("msg") != null) {
            String messageTextArray[] = getIntent().getStringExtra("msg").split("#");

            standardMessage.setText("Please find response for your ticket #"+messageTextArray[0]);

            responseRecieved.setText(messageTextArray[1]);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_response_from_hr, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

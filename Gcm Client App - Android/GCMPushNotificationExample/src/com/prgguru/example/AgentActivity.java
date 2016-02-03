package com.prgguru.example;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;


public class AgentActivity extends Activity {

    String eMailId, ticketID;
    ProgressDialog prgDialog;
    RequestParams params = new RequestParams();
    Context applicationContext;
    TextView ticketRaiseBy, employeeLoggedIn;
    EditText queryET, responseText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent);

        SharedPreferences prefs = getSharedPreferences("UserDetails",
                Context.MODE_PRIVATE);
        eMailId = prefs.getString("eMailId", "");
        prgDialog = new ProgressDialog(this);
        // Set Progress Dialog Text
        prgDialog.setMessage("Please wait...");
        // Set Cancelable as False
        prgDialog.setCancelable(false);
        applicationContext = getApplicationContext();
        employeeLoggedIn = (TextView) findViewById(R.id.employeeLoggedIn);
        ticketRaiseBy = (TextView) findViewById(R.id.ticketRaisedBytext);
        queryET = (EditText) findViewById(R.id.eeQueryStatement);
        employeeLoggedIn.setText("Hello "+ eMailId);
        if (getIntent().getStringExtra("msg") != null) {
            String messageTextArray[] = getIntent().getStringExtra("msg").split("#");
            ticketID = messageTextArray[1];
            ticketRaiseBy.setText(messageTextArray[0] + " raised a ticket in system with ticket ID "+messageTextArray[1]);

            queryET.setText(messageTextArray[2]);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_agent, menu);
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

    public void sendResponse(View view) {

        // Get Email ID from Shared preferences

        responseText = (EditText) findViewById(R.id.agentResponseInput);
        String responseToBeSent = responseText.getText().toString();

        prgDialog.show();
        params.put("ticketID", ticketID);
        params.put("responseText",responseToBeSent);
        // Make RESTful webservice call using AsyncHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(ApplicationConstants.SEND_TICKET_RESPONSE_URL, params,
                new AsyncHttpResponseHandler() {
                    // When the response returned by REST has Http
                    // response code '200'
                    @Override
                    public void onSuccess(String response) {
                        // Hide Progress Dialog
                        prgDialog.hide();
                        if (prgDialog != null) {
                            prgDialog.dismiss();
                        }
                        Toast.makeText(applicationContext,
                                "Response successfully submitted to system ",
                                Toast.LENGTH_LONG).show();
                        /*Intent i = new Intent(applicationContext,
                                HomeActivity.class);
                        i.putExtra("emplID", eMailId);
                        startActivity(i);*/
//                        finish();
                    }

                    // When the response returned by REST has Http
                    // response code other than '200' such as '404',
                    // '500' or '403' etc
                    @Override
                    public void onFailure(int statusCode, Throwable error,
                                          String content) {
                        // Hide Progress Dialog
                        prgDialog.hide();
                        if (prgDialog != null) {
                            prgDialog.dismiss();
                        }
                        // When Http response code is '404'
                        if (statusCode == 404) {
                            Toast.makeText(applicationContext,
                                    "Requested resource not found",
                                    Toast.LENGTH_LONG).show();
                        }
                        // When Http response code is '500'
                        else if (statusCode == 500) {
                            Toast.makeText(applicationContext,
                                    "Something went wrong at server end",
                                    Toast.LENGTH_LONG).show();
                        }
                        // When Http response code other than 404, 500
                        else {
                            Toast.makeText(
                                    applicationContext,
                                    "Unexpected Error occcured! [Most common Error: Device might "
                                            + "not be connected to Internet or remote server is not up and running], check for other errors as well",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }
}

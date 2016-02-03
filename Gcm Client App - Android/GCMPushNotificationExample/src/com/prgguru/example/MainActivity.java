package com.prgguru.example;

import java.io.IOException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class MainActivity extends Activity {
	ProgressDialog prgDialog;
	RequestParams params = new RequestParams();
	GoogleCloudMessaging gcmObj;
	Context applicationContext;
	String regId = "";
	String isHRLoginBoolean = "false";

	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	AsyncTask<Void, Void, String> createRegIdTask;

	public static final String REG_ID = "regId";
	public static final String EMAIL_ID = "eMailId";
	public static final String USER_TYPE_HR = "userTypeHR";
	EditText emailET;
	CheckBox isHRLogin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		applicationContext = getApplicationContext();
		emailET = (EditText) findViewById(R.id.email);
		isHRLogin = (CheckBox) findViewById(R.id.isHRLogin);
		prgDialog = new ProgressDialog(this);
		// Set Progress Dialog Text
		prgDialog.setMessage("Please wait...");
		// Set Cancelable as False
		prgDialog.setCancelable(false);

		SharedPreferences prefs = getSharedPreferences("UserDetails",
				Context.MODE_PRIVATE);
		String registrationId = prefs.getString(REG_ID, "");
		String hrLoggedIn = prefs.getString(USER_TYPE_HR, "");
		if (!TextUtils.isEmpty(registrationId) && hrLoggedIn == "false") {
//			Intent i = new Intent(applicationContext, HomeActivity.class);
			Intent i = new Intent(applicationContext, PostQuery.class);

			i.putExtra("regId", registrationId);
			startActivity(i);
			finish();
		} else if (!TextUtils.isEmpty(registrationId) && hrLoggedIn == "true"){
			Intent i = new Intent(applicationContext, AgentActivity.class);

			i.putExtra("regId", registrationId);
			startActivity(i);
			finish();
		}
	}

	public void RegisterUser(View view) {
		String emailID = emailET.getText().toString();

		if (!TextUtils.isEmpty(emailID) && Utility.validate(emailID)) {
			if (checkPlayServices()) {
				registerInBackground(emailID);
			}
		}
		// When Email is invalid
		else {
			Toast.makeText(applicationContext, "Please enter valid email",
					Toast.LENGTH_LONG).show();
		}

	}

	private void registerInBackground(final String emailID) {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					if (gcmObj == null) {
						gcmObj = GoogleCloudMessaging
								.getInstance(applicationContext);
					}
					regId = gcmObj
							.register(ApplicationConstants.GOOGLE_PROJ_ID);
					msg = "Registration ID :" + regId;

				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
				}
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {

				if(isHRLogin.isChecked()){
					isHRLoginBoolean = "true";
				}
				if (!TextUtils.isEmpty(regId)) {
					storeRegIdinSharedPref(applicationContext, regId, emailID,isHRLoginBoolean);
					Toast.makeText(
							applicationContext,
							"Registered with GCM Server successfully.\n\n"
									+ msg, Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(
							applicationContext,
							"Reg ID Creation Failed.\n\nEither you haven't enabled Internet or GCM server is busy right now. Make sure you enabled Internet and try registering again after some time."
									+ msg, Toast.LENGTH_LONG).show();
				}
			}
		}.execute(null, null, null);
	}

	private void storeRegIdinSharedPref(Context context, String regId,
			String emailID, String userTypeHR) {
		SharedPreferences prefs = getSharedPreferences("UserDetails",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(REG_ID, regId);
		editor.putString(EMAIL_ID, emailID);
		editor.putString(USER_TYPE_HR, userTypeHR);
		editor.commit();
		storeRegIdinServer();

	}

	private void storeRegIdinServer() {

		String emplID = emailET.getText().toString();


		prgDialog.show();
		params.put("regId", regId);
		params.put("emplID",emplID);
		params.put("isHRLogin", isHRLoginBoolean);
		// Make RESTful webservice call using AsyncHttpClient object
		AsyncHttpClient client = new AsyncHttpClient();
		client.post(ApplicationConstants.APP_SERVER_URL, params,
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
								"Reg Id shared successfully with Web App ",
								Toast.LENGTH_LONG).show();
						/*Intent i = new Intent(applicationContext,
								HomeActivity.class);*/
						if (isHRLoginBoolean == "false") {
							Intent i = new Intent(applicationContext,
                                    PostQuery.class);
//						i.putExtra("regId", regId);
							startActivity(i);
						} else {
							Intent i = new Intent(applicationContext,
									AgentActivity.class);
//						i.putExtra("regId", regId);
							startActivity(i);
						}
						finish();
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

	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Toast.makeText(
						applicationContext,
						"This device doesn't support Play services, App will not work normally",
						Toast.LENGTH_LONG).show();
				finish();
			}
			return false;
		} else {
			Toast.makeText(
					applicationContext,
					"This device supports Play services, App will work normally",
					Toast.LENGTH_LONG).show();
		}
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		checkPlayServices();
	}
}

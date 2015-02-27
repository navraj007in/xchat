package com.kss.xchat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.google.android.gcm.demo.app.GCMActivity;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.util.Log;

public class xChatApplication extends Application{
	String TAG ="xChat"; 
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    Context context;
    String regid;
    String SENDER_ID = "62717634560";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    SharedPreferences prefs ;
	@Override
	    public void onCreate() {
	        super.onCreate();
	        Log.i(TAG, "Starting Application");
	        gcm = GoogleCloudMessaging.getInstance(this);
	        regid = getRegistrationId(getApplicationContext());
	        if (regid.isEmpty()) {
	            registerInBackground();
	        }
          //  registerInBackground();

//	        Log.i(TAG,"regid"+ regid);

	        // Do something here.
	    }
    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGcmPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(GCMActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    private String getRegistrationId(Context context) {
        prefs= getGcmPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
//        Log.i("regid", registrationId);
        return registrationId;
    }

    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;
//                    Log.i(TAG, msg);
                    Editor editor = prefs.edit();
                    editor.putString(PROPERTY_REG_ID, regid);
                    editor.commit();
                    
                    List<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>(1);
        			nameValuePairs.add(new BasicNameValuePair("UserID", Utils.getLoggedUserId()));
        			nameValuePairs.add(new BasicNameValuePair("DeviceID", regid));

        			nameValuePairs.add(new BasicNameValuePair("apikey", getString(R.string.apikey)));
        			String response=Utils.postData("RegisterDevice", nameValuePairs);

        			/*String response=Utils.RestCall(Utils.WebServiceURL+
        					"RegisterDevice/?UserID="+Utils.getLoggedUserId()
        					+"&DeviceID="+ regid);*/
//        			Log.i(TAG, "URL Response-"+response);

                    // You should send the registration ID to your server over HTTP, so it
                    // can use GCM/HTTP or CCS to send messages to your app.
                    //sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the device will send
                    // upstream messages to a server that echo back the message using the
                    // 'from' address in the message.

                    // Persist the regID - no need to register again.
                    ///storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
               // mDisplay.append(msg + "\n");
            	if(Integer.parseInt(Utils.getLoggedUserId())>0)
            	sendRegistrationIdToServer(regid);
            }
        }.execute(null, null, null);

    }

    public void sendRegistrationIdToServer(String regid){
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
            	return "";
            }

            @Override
            protected void onPostExecute(String msg) {
               // mDisplay.append(msg + "\n");

            }
        }.execute(null, null, null);

    }
    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

}

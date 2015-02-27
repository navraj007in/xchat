package com.kss.xchat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.PlusClient.OnAccessRevokedListener;
import com.google.android.gms.plus.model.people.Person.Image;
import com.kss.xchat.apiresponse.Authenticate;
import com.kss.xchat.apiresponse.BuddyRequest;
import com.kss.xchat.apiresponse.Roster;
import com.kss.xchat.data.Blocked;
import com.kss.xchat.data.Profiles;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Toast;

public class LoginActivity extends Activity implements
								ConnectionCallbacks, 
								OnConnectionFailedListener, 
								OnClickListener,
								OnAccessRevokedListener, 
								com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks {

private static final String TAG = "LoginActivity";
AuthenticateTask mAuthTask;
private static final int OUR_REQUEST_CODE = 49404;
public static final String PROPERTY_REG_ID = "registration_id";
public static PlusClient mPlusClient;
public static boolean mResolveOnFail;
private ConnectionResult mConnectionResult;
private ProgressDialog mConnectionProgressDialog;
public static Image profileImage;
String isLogged;
public static String base64Image;
private InterstitialAd interstitial;
public void displayInterstitial() {
    if (interstitial.isLoaded()) {
      interstitial.show();
    }
  }
@Override
protected void onCreate(Bundle savedInstanceState) {
requestWindowFeature(Window.FEATURE_NO_TITLE);
super.onCreate(savedInstanceState);
//leaveRoom();
//loadAd();

if(Utils.DEV_MODE==Utils.DEV_MODE_EMULATOR)
{
	Intent intent=new Intent(LoginActivity.this,ConversationsActivity.class);
	startActivity(intent);
	finish();
}
	try
	{
		isLogged=Utils.ReadPreference(getApplicationContext(), "isLogged");
		if(isLogged.equalsIgnoreCase("YES")) 
		{
			Intent intent=new Intent(LoginActivity.this,
					ConversationsActivity.class);
			startActivity(intent);
			finish();
			
		}
	
	}
	catch(Exception ex)
	{
		ex.printStackTrace();
	}

setContentView(R.layout.activity_login);

try
{
	mPlusClient = new PlusClient.Builder(this,this,this)
	.build();
	mResolveOnFail = false;

			findViewById(R.id.btn_sign_in).setOnClickListener(this);
			findViewById(R.id.btn_sign_out).setOnClickListener(this);
			findViewById(R.id.btn_revoke_access).setOnClickListener(this);
			findViewById(R.id.btn_sign_out).setVisibility(View.INVISIBLE);
			findViewById(R.id.btn_sign_in).setVisibility(View.VISIBLE);

			mConnectionProgressDialog = new ProgressDialog(this);
			mConnectionProgressDialog.setMessage("Signing in...");
}
catch(Exception ex)
{
	ex.printStackTrace();
}

}

	@Override
	protected void onStart() {
		super.onStart();
		Log.v(TAG, "Start");
	//	mPlusClient.connect();
	}

@Override
protected void onStop() {
super.onStop();
Log.v(TAG, "Stop");
//mPlusClient.disconnect();
}

@Override
public void onConnectionFailed(ConnectionResult result) {
Log.v(TAG, "ConnectionFailed");
if (result.hasResolution()) {
	mConnectionResult = result;
	if (mResolveOnFail) {
		startResolution();
	}
}
}

@Override
public void onConnected(Bundle bundle) {
try
{
	String dob="";
	Log.v(TAG, "Connected. Yay!");
	String accountName = mPlusClient.getAccountName();
	Utils.WritePreference(getApplicationContext(), "Email", accountName);
	String FullName=accountName;
	if(mPlusClient.getCurrentPerson()!=null)
		FullName=mPlusClient.getCurrentPerson().toString();
	Log.i(TAG, FullName);
	if(mPlusClient.getCurrentPerson().getBirthday()!=null)
	{
		dob=Utils.convertDate(mPlusClient.getCurrentPerson().getBirthday(), "yyyy-MM-dd", "MM-dd-yyyy");
	}
	Utils.WritePreference(getApplicationContext(), "FullName", FullName);
	Utils.WritePreference(getApplicationContext(), "DOB", dob);
	Utils.WritePreference(getApplicationContext(), "Gender", String.valueOf(mPlusClient.getCurrentPerson().getGender()));
	//Log.i(TAG,"Places-"+ mPlusClient.getCurrentPerson().getPlacesLived().toString());
	Log.i(TAG, Utils.ReadPreference(getApplicationContext(), "FullName"));
	RegisterActivity.FullName=mPlusClient.getCurrentPerson().getName().getGivenName();
	
	RegisterActivity.DOB=dob;
	Toast.makeText(this, accountName + " is connected.", Toast.LENGTH_LONG)
	        .show();
	profileImage = mPlusClient.getCurrentPerson().getImage();
	Log.i(TAG, profileImage.getUrl());
	Log.i(TAG, "Name-"+RegisterActivity.FullName+"-"+RegisterActivity.DOB);
	Log.i(TAG, String.valueOf(mPlusClient.getCurrentPerson().getGender()));

	Log.i(TAG, mPlusClient.getCurrentPerson().getDisplayName().toString());
	mConnectionProgressDialog.dismiss();
	mAuthTask = new AuthenticateTask();
	int corePoolSize = 80;
	int maximumPoolSize = 100;
	int keepAliveTime = 20;

	BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(maximumPoolSize);
	Executor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);
	mAuthTask.executeOnExecutor(threadPoolExecutor);
	mResolveOnFail = false;
	
}
catch(Exception ex)
{
	ex.printStackTrace();
}

}

public void onDisconnected() {
// Bye!
Log.v(TAG, "Disconnected. Bye!");
}

protected void onActivityResult(int requestCode, int responseCode,
	Intent intent) {
	try
	{
		Log.v(TAG, "ActivityResult: " + requestCode);
		if (requestCode == OUR_REQUEST_CODE && responseCode == RESULT_OK) {
			mResolveOnFail = true;
			mPlusClient.connect();
		} else if (requestCode == OUR_REQUEST_CODE && responseCode != RESULT_OK) {
			mConnectionProgressDialog.dismiss();
		}
		
	}
	catch(Exception ex)
	{
		ex.printStackTrace();
	}
}
@Override
public void onDestroy()
{
	super.onDestroy();
	try
	{
		if(mPlusClient.isConnected())
		{
		mPlusClient.clearDefaultAccount();
		mPlusClient.disconnect();
		}
		
	}
	catch(Exception ex)
	{
		ex.printStackTrace();
	}
}

@Override
public void onClick(View view) {
	try
	{
		switch (view.getId()) {
		case R.id.btn_sign_in:
			if(Utils.isNetworkAvailable(getApplicationContext())){
			Log.v(TAG, "Tapped sign in");
			if (!mPlusClient.isConnected()) {
				try{
				mConnectionProgressDialog.show();
				}
				catch(Exception e){
					e.printStackTrace();
				}
				mResolveOnFail = true;
				if (mConnectionResult != null) {  
					startResolution();
				} else {
					mPlusClient.connect();
				}
			}
			}
			else{
				Toast.makeText(getApplicationContext(), "Internet is not Connected.", Toast.LENGTH_SHORT).show();
			}

			break;
		case R.id.btn_sign_out:
			Log.v(TAG, "Tapped sign out");
			if (mPlusClient.isConnected()) {
				mPlusClient.clearDefaultAccount();
				mPlusClient.disconnect();
				mPlusClient.connect();
				findViewById(R.id.btn_sign_in).setVisibility(View.VISIBLE);
				findViewById(R.id.btn_sign_out)
						.setVisibility(View.INVISIBLE);
				findViewById(R.id.btn_revoke_access).setVisibility(
						View.INVISIBLE);
			}
			break;
		case R.id.btn_revoke_access:
			Log.v(TAG, "Tapped disconnect");
			if (mPlusClient.isConnected()) {
				mPlusClient.clearDefaultAccount();
				mPlusClient.revokeAccessAndDisconnect(this);
			}
		default:
			// Unknown id.
		}
		
	}
	catch(Exception ex)
	{
		ex.printStackTrace();
	}
}

public void onAccessRevoked(ConnectionResult status) {
mPlusClient.connect();
findViewById(R.id.btn_sign_in).setVisibility(View.VISIBLE);
findViewById(R.id.btn_sign_out).setVisibility(View.INVISIBLE);
findViewById(R.id.btn_revoke_access).setVisibility(View.INVISIBLE);
}

/**
* A helper method to flip the mResolveOnFail flag and start the resolution
* of the ConnenctionResult from the failed connect() call.
*/
private void startResolution() {
try {
	mResolveOnFail = false;
	mConnectionResult.startResolutionForResult(this, OUR_REQUEST_CODE);
} catch (SendIntentException e) {
	mPlusClient.connect();
	e.printStackTrace();
}
}


public void onConnectionSuspended(int arg0) {
	// TODO Auto-generated method stub
	
}

public class AuthenticateTask extends AsyncTask<Void, Void, Boolean> {
	private ProgressDialog pd;
	@Override
    protected void onPreExecute(){
       super.onPreExecute();
       try
       {
    	   	pd=new ProgressDialog(LoginActivity.this);

    		pd.setTitle("Authenticating with xChat Server...");
    		pd.setMessage("Please wait.Logging you in....");
    		pd.setCancelable(false);
    		pd.setIndeterminate(true);
    		pd.show();
    	   
       }
       catch(Exception ex)
       {
    	   ex.printStackTrace();
       }
	}
	@Override
	protected void onPostExecute(final Boolean success) {
		mAuthTask = null;
		if(pd!=null) pd.dismiss();
	}
	@Override
	protected void onCancelled() {
		mAuthTask = null;
	}
	@Override
	protected Boolean doInBackground(Void... params) {


			try {
				List<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>(1);
				nameValuePairs.add(new BasicNameValuePair("Appid", "123"));
				nameValuePairs.add(new BasicNameValuePair("apikey", getString(R.string.apikey)));
				nameValuePairs.add(new BasicNameValuePair("os", "Android"));
				nameValuePairs.add(new BasicNameValuePair("email", mPlusClient.getAccountName()));
				nameValuePairs.add(new BasicNameValuePair("deviceid", Utils.ReadPreference(getApplicationContext(),
						PROPERTY_REG_ID)));
				//nameValuePairs.add(new BasicNameValuePair("apikey", getString(R.string.apikey)));
				
				String response=Utils.postData("Authenticate", nameValuePairs);
				
				/*String response=Utils.RestCall(Utils.WebServiceURL+
						"Authenticate/?Appid=123&email="
						+mPlusClient.getAccountName()+"&os=Android&deviceid="+
						Utils.ReadPreference(getApplicationContext(), PROPERTY_REG_ID)
						+"&ApiKey="+getString(R.string.apikey));*/
				Log.i(TAG, "Login Response-"+response);
				 ObjectMapper mapper = new ObjectMapper();

				Authenticate json = mapper.readValue(response, 
						new TypeReference<Authenticate>() {
				});
				if(json.userExists.equalsIgnoreCase("yes"))
				{
					Utils.WritePreference(getApplicationContext(), "AccessToken", json.Token);
					Utils.WritePreference(getApplicationContext(), "NickName", json.NickName);
					Utils.WritePreference(getApplicationContext(), "Email", json.Email);
					Utils.WritePreference(getApplicationContext(), "Name", json.Name);
					Utils.WritePreference(getApplicationContext(), "Gender", json.Gender);
					Utils.WritePreference(getApplicationContext(), "DOB", json.DOB);
					Utils.WritePreference(getApplicationContext(), "City", json.City);
					Utils.WritePreference(getApplicationContext(), "State", json.State);
					Utils.WritePreference(getApplicationContext(), "Country", json.Country);
					Utils.WritePreference(getApplicationContext(), "isLogged", "YES");
					Utils.WritePreference(getApplicationContext(), "ProfileImage", json.ProfileImage);
					Utils.WritePreference(getApplicationContext(), "secureModePassword", json.NickName);
					base64Image=json.ProfileImage;
					Utils.WritePreference(getApplicationContext(), "ProfileImage", json.ProfileImage);
					Utils.WritePreference(getApplicationContext(), "keyStatus", json.Status);
					Utils.WritePreference(getApplicationContext(), "keyAutoReplyText","Do not Disturb");
					Log.i(TAG, Utils.ReadPreference(getApplicationContext(), "ProfileImage"));
					
					Intent intent=new Intent(LoginActivity.this,
							ConversationsActivity.class);
					startActivity(intent);
					new ReadRoster().execute((Void) null);
					new GetRequestsTask().execute((Void) null);
					new GetBlockedTask().execute((Void) null);
					
					finish();
				}
				else
				{
					Intent intent=new Intent(LoginActivity.this,
							RegisterActivity.class);
					RegisterActivity.Email=mPlusClient.getAccountName();
					startActivity(intent);
					finish();
				}
				Log.i(TAG,json.Email+"-"+json.NickName+" Logged in.");
			} catch (JsonParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

		// TODO: register the new account here.
		return true;
	}
}
public class GetBlockedTask extends AsyncTask<Void, Void, Boolean> {
	@Override
    protected void onPreExecute(){
       super.onPreExecute();
	}
	ArrayList<BuddyRequest> blocklist;
	@Override
	protected Boolean doInBackground(Void... params) {
		// TODO: attempt authentication against a network service.
		List<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>(1);
		nameValuePairs.add(new BasicNameValuePair("token", Utils.ReadPreference(getApplicationContext(), "AccessToken")));
		nameValuePairs.add(new BasicNameValuePair("apikey", getString(R.string.apikey)));
		
		Log.i(TAG, "Starting Block list");
		String response=Utils.postData("getBlockedList", nameValuePairs);
		Log.i("Response", response);
			 ObjectMapper mapper = new ObjectMapper();

				try {
					blocklist= mapper.readValue(response, 
							new TypeReference<ArrayList<BuddyRequest>>() {
					});
					Blocked uBlock=new Blocked(getApplicationContext());
					uBlock.clearRecords();
					for(int i=0;i<blocklist.size();i++)
					{
						Blocked iBlock=new Blocked(getApplicationContext());
						iBlock.user=Utils.getLoggedNickName(getApplicationContext());
						iBlock.nickname=blocklist.get(i).NickName;
						iBlock.BlockUser();
						
						com.kss.xchat.data.Roster iProfile =new com.kss.xchat.data.Roster(getApplicationContext());
						iProfile.City=blocklist.get(i).City;
						iProfile.Country=blocklist.get(i).Country;
						iProfile.Gender=blocklist.get(i).Gender;
						iProfile.Name=blocklist.get(i).Name;
						iProfile.NickName=blocklist.get(i).NickName;
						iProfile.ProfileImage=blocklist.get(i).ProfileImage;
						iProfile.addRecord();
						
						
					}
					Log.i(TAG, blocklist.size()+ " Blocked Users");

				} catch (JsonParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JsonMappingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch(Exception e)
				{
					e.printStackTrace();
				}
				
		// TODO: register the new account here.
		return true;
	}


	@Override
	protected void onCancelled() {
	}
}

class ReadRoster extends AsyncTask<Void, Void, Boolean>{

	@Override
	protected Boolean doInBackground(Void... arg0) {
		// TODO Auto-generated method stub
		/*String response=Utils.RestCall(Utils.WebServiceURL+"getRoster/?token="+Utils.ReadPreference(getApplicationContext(), "AccessToken")
				+"&apikey="+getString(R.string.apikey));*/
		
		List<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>(1);
		nameValuePairs.add(new BasicNameValuePair("token", Utils.ReadPreference(getApplicationContext(), "AccessToken")));
		nameValuePairs.add(new BasicNameValuePair("apikey", getString(R.string.apikey)));
		String response=Utils.postData("getRoster", nameValuePairs);
		
		ArrayList<Roster> roster=new ArrayList<Roster>();
		Log.i(TAG,response);
		if(roster!=null) roster.clear();
		
		 ObjectMapper mapper = new ObjectMapper();

			try {
				roster= mapper.readValue(response, 
						new TypeReference<ArrayList<Roster>>() {
				});
				Log.i(TAG,"Roster Size- " +roster.size());
			//	Toast.makeText(getApplicationContext(), "Roster Size- " +roster.size(), Toast.LENGTH_SHORT).show();
				com.kss.xchat.data.Profiles uProfile=new com.kss.xchat.data.Profiles(getApplicationContext());
				uProfile.clearRecords();
				
				for(int i=0;i<roster.size();i++)
				{
					Roster iRoster=roster.get(i);
					Profiles iProfile=new Profiles(getApplicationContext());
					iProfile.NickName=iRoster.NickName;
					iProfile.Name=iRoster.Name;
					iProfile.City=iRoster.City;
					iProfile.State=iRoster.State;
					iProfile.Country=iRoster.Country;
					iProfile.Zip=iRoster.Zip;
					iProfile.ProfileImage=iRoster.ProfileImage;
					iProfile.Status=iRoster.Status;
					iProfile.addRecord();
					Log.i(TAG,iRoster.NickName+"-"+iRoster.ProfileImage);
					
				}
				Log.i(TAG, uProfile.getCount()+" Profiles Loaded");
			//	Toast.makeText(getApplicationContext(), uProfile.getCount()+" Profiles Loaded", Toast.LENGTH_SHORT).show();

			} catch (JsonParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 Log.i(TAG, response);

		return true;
	}
	
}
public class GetRequestsTask extends AsyncTask<Void, Void, Boolean> {
	ArrayList<BuddyRequest> requests;
	String response;

	@Override
    protected void onPreExecute(){
       super.onPreExecute();
       
       if(requests !=null && requests.size()>0) requests.clear();
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		// TODO: attempt authentication against a network service.
		

				try {

					List<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>(1);
					nameValuePairs.add(new BasicNameValuePair("token", Utils.ReadPreference(getApplicationContext(), "AccessToken")));
					nameValuePairs.add(new BasicNameValuePair("apikey", getString(R.string.apikey)));
					
					response=Utils.postData("getRequests", nameValuePairs);
/*					response = Utils.RestCall(Utils.WebServiceURL+"getRequests/?token="+
							Utils.ReadPreference(getApplicationContext(), "AccessToken")+"&apikey="+getString(R.string.apikey));
*/					 Log.i("Response","Requests List -"+ response);
					 ObjectMapper mapper = new ObjectMapper();
					 requests = mapper.readValue(response, 
							new TypeReference<ArrayList<BuddyRequest>>() {
					});
					Log.i(TAG, requests.size()+ " Requests");
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}

		// TODO: register the new account here.
		return true;
	}

	@Override
	protected void onPostExecute(final Boolean success) {
	
		if(requests==null) requests=new ArrayList<BuddyRequest>();
		com.kss.xchat.data.BuddyRequest brc=new com.kss.xchat.data.BuddyRequest(getApplicationContext());
		brc.clearRecords();
		for(int i=0;i<requests.size();i++)
		{
			com.kss.xchat.data.BuddyRequest br=new com.kss.xchat.data.BuddyRequest(getApplicationContext());
			br.fromUser=requests.get(i).NickName;
			br.timeStamp=Utils.getCurrentTimeStamp();
			br.Message=requests.get(i).Message;
			
			br.addRecord();
		}
		ConversationsActivity.requests=requests;
		

	}

	@Override
	protected void onCancelled() {

	}
}
private static final String LOG_TAG = "InterstitialSample";

/** Your ad unit id. Replace with your actual ad unit id. */
private static final String AD_UNIT_ID = "ca-app-pub-9436114210349385/9644962954";

/** The interstitial ad. */
private InterstitialAd interstitialAd;
void loadAd()
{
	 interstitialAd = new InterstitialAd(this);
	    interstitialAd.setAdUnitId(AD_UNIT_ID);

	    // Set the AdListener.
	    interstitialAd.setAdListener(new AdListener() {
	      @Override
	      public void onAdLoaded() {
	        Log.d(LOG_TAG, "onAdLoaded");
	        Toast.makeText(LoginActivity.this, "onAdLoaded", Toast.LENGTH_SHORT).show();
	        if (interstitialAd.isLoaded()) {
	            interstitialAd.show();
	          } else {
	            Log.d(LOG_TAG, "Interstitial ad was not ready to be shown.");
	          }

	        // Change the button text and enable the button.
	      }

	      @Override
	      public void onAdFailedToLoad(int errorCode) {
	        String message = String.format("onAdFailedToLoad (%s)", getErrorReason(errorCode));
	        Log.d(LOG_TAG, message);
	        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();

	      }
	    });

	    AdRequest adRequest = new AdRequest.Builder()
        .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
        .addTestDevice("INSERT_YOUR_HASHED_DEVICE_ID_HERE")
        .build();

    // Load the interstitial ad.
    interstitialAd.loadAd(adRequest);
    if (interstitialAd.isLoaded()) {
        interstitialAd.show();
      } else {
        Log.d(LOG_TAG, "Interstitial ad was not ready to be shown.");
      }
}
private String getErrorReason(int errorCode) {
    String errorReason = "";
    switch(errorCode) {
      case AdRequest.ERROR_CODE_INTERNAL_ERROR:
        errorReason = "Internal error";
        break;
      case AdRequest.ERROR_CODE_INVALID_REQUEST:
        errorReason = "Invalid request";
        break;
      case AdRequest.ERROR_CODE_NETWORK_ERROR:
        errorReason = "Network Error";
        break;
      case AdRequest.ERROR_CODE_NO_FILL:
        errorReason = "No fill";
        break;
    }
    return errorReason;
  }
}
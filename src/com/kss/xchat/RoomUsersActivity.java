package com.kss.xchat;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.kss.xchat.apiresponse.RoomUsers;
import com.kss.xchat.viewadapters.RoomUsersAdapter;
import android.app.ActionBar;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class RoomUsersActivity extends RootActivity implements OnItemClickListener{
	ListView lstUsers;
	String rId;
	GetUsersTask mAuthTask;
	RoomUsersAdapter rAdap;
	APIResponse aresponse ;
	private AdView adView;
	ArrayList<RoomUsers> roomUsers;
	private InterstitialAd interstitial;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_room_users);
		lstUsers=(ListView)findViewById(R.id.lstUsers);
		TextView lblHeading=(TextView)findViewById(R.id.lblHeading);
		loadAd();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {

		ActionBar actionBar=getActionBar();
		actionBar.setTitle(ChatRoom.RoomName);
		}
		lblHeading.setText("Users in this room");
		lstUsers.setOnItemClickListener(this);
		rId=getIntent().getStringExtra("rId");
		mAuthTask = new GetUsersTask();
		mAuthTask.execute((Void) null);
		interstitial = new InterstitialAd(this);
	    interstitial.setAdUnitId(Utils.INTERSTITIALID);
	    // Create ad request.
	   
	    AdRequest adRequest = new AdRequest.Builder()
        .build();
	    // Begin loading your interstitial.
	    interstitial.loadAd(adRequest);
	    displayInterstitial();
	}
	public void displayInterstitial() {
	    if (interstitial.isLoaded()) {
	      interstitial.show();
	    }
	  }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.room_users, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.action_refresh:
			mAuthTask = new GetUsersTask();
			mAuthTask.execute((Void) null);

	    	return true;
	    default:
            return super.onOptionsItemSelected(item);
	    }
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		// TODO Auto-generated method stub
		Intent intent =new Intent(RoomUsersActivity.this,PMActivity.class);
		RoomUsers user = aresponse.roomUsers.get(position);
		rId=getIntent().getStringExtra("rId");
		intent.putExtra("nickname", user.NickName);
		intent.putExtra("name", user.Name);
		intent.putExtra("id", user.ID);
		intent.putExtra("user", user.User);
		startActivity(intent);
		
	}
	private void loadAd()
	{
		adView = new AdView(this);
		 adView.setAdSize(AdSize.SMART_BANNER);
	    adView.setAdUnitId(Utils.ADMOBID);

	    // Add the AdView to the view hierarchy. The view will have no size
	    // until the ad is loaded.
	    LinearLayout layout = (LinearLayout) findViewById(R.id.adLayout);
	    layout.addView(adView);

	    // Create an ad request. Check logcat output for the hashed device ID to
	    // get test ads on a physical device.
	    AdRequest adRequest = new AdRequest.Builder()
	        .build();

	    // Start loading the ad in the background.
	    adView.loadAd(adRequest);

	}
	public class GetUsersTask extends AsyncTask<Void, Void, Boolean> {
		@Override
        protected void onPreExecute(){
           super.onPreExecute();
		}
	
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.
			List<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("RoomID", rId));
			nameValuePairs.add(new BasicNameValuePair("apikey", getString(R.string.apikey)));
			String response=Utils.postData("GetRoomUsers", nameValuePairs);

/*			String response;

				response = Utils.RestCall(Utils.WebServiceURL+"GetRoomUsers/?RoomID="+
						rId+"&apikey="+getString(R.string.apikey));
*/				 Log.i("Response", response);
				 ObjectMapper mapper = new ObjectMapper();

					try {
						APIResponse json = mapper.readValue(response, 
								new TypeReference<APIResponse>() {
						});
						aresponse = json;

					}
					catch(Exception ex)
					{
						
					}

			// TODO: register the new account here.
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
		//	bindChat();
			loadInterstitialAd();

			if(aresponse!=null)
			{
				roomUsers=aresponse.roomUsers;
				if(roomUsers!=null){
					RoomUsers xChatAdmin=new RoomUsers();
					xChatAdmin.Name="xChat Admin";
					xChatAdmin.NickName="xchatadmin";
					xChatAdmin.ID="8";
					roomUsers.add(xChatAdmin);
				}
			rAdap=new RoomUsersAdapter(getApplicationContext(), R.layout.countryrow, 
					R.id.lblCountryName, roomUsers);

			lstUsers.setAdapter(rAdap);
			}

		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
		}
	}
	@Override
	public void onMPResume() {
		// TODO Auto-generated method stub
		
	}
	private static final String LOG_TAG = "InterstitialSample";

	/** Your ad unit id. Replace with your actual ad unit id. */
	private static final String AD_UNIT_ID = "ca-app-pub-9436114210349385/9644962954";

	/** The interstitial ad. */

	private InterstitialAd interstitialAd;
	void loadInterstitialAd()
	{
		 interstitialAd = new InterstitialAd(this);
		    interstitialAd.setAdUnitId(AD_UNIT_ID);

		    // Set the AdListener.
		    interstitialAd.setAdListener(new AdListener() {
		      @Override
		      public void onAdLoaded() {
		        Log.d(LOG_TAG, "onAdLoaded");
		      //  Toast.makeText(RoomUsersActivity.this, "onAdLoaded", Toast.LENGTH_SHORT).show();
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
		        Toast.makeText(RoomUsersActivity.this, message, Toast.LENGTH_SHORT).show();

		      }
		    });

		    AdRequest adRequest = new AdRequest.Builder()
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

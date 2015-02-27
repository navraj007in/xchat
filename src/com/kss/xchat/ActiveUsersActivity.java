package com.kss.xchat;

import java.util.ArrayList;

import com.costum.android.widget.LoadMoreListView;
import com.costum.android.widget.LoadMoreListView.OnLoadMoreListener;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.kss.xchat.RecentlyJoinedActivity.GetUsersTask;
import com.kss.xchat.apiresponse.RecentlyJoined;
import com.kss.xchat.viewadapters.RecentlyJoinedAdapter;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ActiveUsersActivity extends RootActivity implements OnItemClickListener,OnLoadMoreListener{
	LoadMoreListView lstUsers;
	String rId;
	GetUsersTask mAuthTask;
	RecentlyJoinedAdapter rAdap;
	ArrayList<RecentlyJoined> aresponse,recentList ;
	private AdView adView;
	public int currentPage=0;
	int pageSize=10;
	private InterstitialAd interstitial;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recentlyjoined);
		lstUsers=(LoadMoreListView)findViewById(R.id.lstUsers);
		recentList=new ArrayList<RecentlyJoined>();
		mAuthTask = new GetUsersTask();
		mAuthTask.execute((Void) null);

		lstUsers.setOnLoadMoreListener(new OnLoadMoreListener() {
			@Override
			public void onLoadMore() {
				// TODO Auto-generated method stub
				mAuthTask = new GetUsersTask();
				mAuthTask.execute((Void) null);
			}
		});
		loadAd();
		currentPage=0;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {

		ActionBar actionBar=getActionBar();
		actionBar.setTitle("Currently Active");
		}
		lstUsers.setOnItemClickListener(this);
		rId=getIntent().getStringExtra("rId");
		interstitial = new InterstitialAd(this);
	    interstitial.setAdUnitId(Utils.INTERSTITIALID);
	    // Create ad request.
		loadAd();
	   
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
//	    	if(recentList!=null) recentList.clear();
	    	currentPage=0;
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
		Intent intent =new Intent(ActiveUsersActivity.this,PMActivity.class);
		RecentlyJoined user = recentList.get(position);

		intent.putExtra("nickname", user.NickName);
		intent.putExtra("name", user.Name);
		intent.putExtra("id", user.ID);
//		intent.putExtra("user", user.User);
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
		ProgressDialog pd;
		private void showDialog()
		{
	           try{
	        	   pd=new ProgressDialog(ActiveUsersActivity.this);

	        	   pd.setTitle("Loading Users...");
	        	   pd.setMessage("Please wait.");
	        	   pd.setCancelable(false);
	        	   pd.setIndeterminate(true);
	        	   pd.show();
	           }
	           catch(Exception e){
	        	   e.printStackTrace();
	           }

		}
		@Override
        protected void onPreExecute(){
           super.onPreExecute();
           if(currentPage==0)
           showDialog();
        }

	
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.
			
			String response=Utils.makeRestCallHTTPS("https://www.navraj.net/xchat/api/getactiveusers?page="+
			String.valueOf(currentPage++)+"&pagesize="+String.valueOf(pageSize));
				 Log.i("Response", response);
				 ObjectMapper mapper = new ObjectMapper();

					try {
						ArrayList<RecentlyJoined> json = mapper.readValue(response, 
								new TypeReference<ArrayList<RecentlyJoined>>() {
						});
						aresponse = json;
						
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
			mAuthTask = null;
			/*try{
				pd.dismiss();
			}
			catch(Exception e){
				
			}*/
			if(pd!=null){
				if(pd.isShowing()){
					try{
					pd.dismiss();
					}
					catch(Exception e){
						
					}
				}
			}
			
			if(aresponse!=null)
			{
				if(currentPage==1) recentList.clear();
				if(recentList.size()==0){
				recentList=aresponse;
				rAdap=new RecentlyJoinedAdapter(getApplicationContext(), R.layout.recentrow, 
						R.id.lblCountryName, recentList);
				lstUsers.setAdapter(rAdap);
				}
				else{
					for(int i=0;i<aresponse.size();i++){
						rAdap.add(aresponse.get(i));
						
					}
					rAdap.notifyDataSetChanged();
				}
				currentPage++;
			}
			lstUsers.onLoadMoreComplete();

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
		        Toast.makeText(ActiveUsersActivity.this, message, Toast.LENGTH_SHORT).show();

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
	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		lstUsers.onLoadMore();
	}
}

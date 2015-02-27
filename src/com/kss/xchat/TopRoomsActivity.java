package com.kss.xchat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.costum.android.widget.LoadMoreListView;
import com.costum.android.widget.LoadMoreListView.OnLoadMoreListener;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.kss.xchat.apiresponse.ActiveRooms;
import com.kss.xchat.apiresponse.TopRooms;
import com.kss.xchat.viewadapters.ActiveRoomsAdapter;
import com.kss.xchat.viewadapters.TopRoomsAdapter;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class TopRoomsActivity extends RootActivity implements OnItemClickListener,OnLoadMoreListener{
	LoadMoreListView lstUsers;
	String rId;
	GetTopRoomsTask mActiveRoomsTask;
	TopRoomsAdapter rAdap;
	ArrayList<TopRooms> aresponse;
	ArrayList<TopRooms> recentList ;
	private AdView adView;
	public int currentPage=0;
	int pageSize=10;
	private InterstitialAd interstitial;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_top_rooms);
		lstUsers=(LoadMoreListView)findViewById(R.id.lstUsers);
		recentList=new ArrayList<TopRooms>();
		mActiveRoomsTask = new GetTopRoomsTask();
		mActiveRoomsTask.execute((Void) null);

		loadAd();
		currentPage=0;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {

		ActionBar actionBar=getActionBar();
		actionBar.setTitle("Featured Chat Rooms");
		}
		lstUsers.setOnItemClickListener(this);
		rId=getIntent().getStringExtra("rId");
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
		getMenuInflater().inflate(R.menu.top_rooms, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.action_full_list:
	    	Intent intent=new Intent(TopRoomsActivity.this,ChatRoomsListActivity.class);
	    	startActivity(intent);
	    	return true;
	    default:
            return super.onOptionsItemSelected(item);
	    }
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		// TODO Auto-generated method stub
		Intent i=new Intent(TopRoomsActivity.this,ChatRoom.class);
	      //  i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		
			TopRooms room = recentList.get(position);
			i.putExtra("id", String.valueOf(room.RoomID));
			i.putExtra("name", room.RoomName);
	        i.putExtra("Source", "Join");

//		    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

			i.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
			TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
			// Adds the back stack for the Intent (but not the Intent itself)
			stackBuilder.addParentStack(ChatRoom.class);
			// Adds the Intent that starts the Activity to the top of the stack
			stackBuilder.addNextIntent(i);

			startActivity(i);
			finish();
			finish();
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
	public class GetTopRoomsTask extends AsyncTask<Void, Void, Boolean> {
		ProgressDialog pd;
		private void showDialog()
		{
	           try{
	        	   pd=new ProgressDialog(TopRoomsActivity.this);

	        	   pd.setTitle("Loading Rooms...");
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
			
			String response=Utils.makeRestCallHTTPS(
					"https://www.navraj.net/xchat/api/gettoprooms?apikey=123");
				 Log.i("Response", response);
				 ObjectMapper mapper = new ObjectMapper();

					try {
						ArrayList<TopRooms> json = mapper.readValue(response, 
								new TypeReference<ArrayList<TopRooms>>() {
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
			mActiveRoomsTask = null;
			/*try{
				pd.dismiss();
			}
			catch(Exception e){
				
			}*/
			if(pd!=null){
				if(pd.isShowing()){
					pd.dismiss();
				}
			}
			
			if(aresponse!=null)
			{
				if(currentPage==1) recentList.clear();
				if(recentList.size()==0){
				recentList=aresponse;
				for(int i=0;i<recentList.size();i++)
					Log.i(LOG_TAG,"Users Count-"+ recentList.get(i).UsersCount);
				
				Collections.sort(aresponse, new Comparator<TopRooms>() {
				    public int compare(TopRooms newsItem1, 
				    		TopRooms newsItem2) {
				    	return (newsItem1.RoomName.compareToIgnoreCase(newsItem2.RoomName)) ;
				    }
				});
				rAdap=new TopRoomsAdapter(getApplicationContext(), R.layout.recentrow, 
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
			mActiveRoomsTask = null;
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
		        Toast.makeText(TopRoomsActivity.this, message, Toast.LENGTH_SHORT).show();

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

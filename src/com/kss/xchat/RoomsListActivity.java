package com.kss.xchat;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.gson.Gson;
import com.kss.xchat.apiresponse.Countries;
import com.kss.xchat.apiresponse.Room;
import com.kss.xchat.apiresponse.States;
import com.kss.xchat.viewadapters.RoomsAdapter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.View;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;

public class RoomsListActivity extends RootActivity implements
		OnItemClickListener {
	ListView lstCities;
	Spinner spinCategories;
	Room[] data ;
	States[] statedata;
	String[] name;
	ArrayList<Room> rList=new ArrayList<Room>();
	int[] id;
	String rid;
	int roomMode=0;
	private GetCitiesTask mAuthTask = null;
	private GetRoomsTask mRoomsTask = null;
	
	ArrayList<Countries> cList=new ArrayList<Countries>();
	ArrayList<States> sList=new ArrayList<States>();
	int position;
	public static String TAG="RoomsList";
	private InterstitialAd interstitial;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rooms_list);
		loadAd();
		Intent intent=getIntent();
		int mode=intent.getIntExtra("mode", 0);
		String roomname=intent.getStringExtra("name");
		rid=intent.getStringExtra("id");
//		Log.i("Room", roomname +"-"+mode+"-"+rid);
		lstCities =(ListView)findViewById(R.id.lstCountries);
		lstCities.setOnItemClickListener(this);
		if(mode==0)
		{
		mAuthTask = new GetCitiesTask();
		mAuthTask.execute((Void) null);
		}
		else
		{
			mRoomsTask = new GetRoomsTask();
			mRoomsTask.execute((Void) null);
			
		}
		 interstitial = new InterstitialAd(this);
		    interstitial.setAdUnitId(Utils.INTERSTITIALID);

		    // Create ad request.
		    AdRequest adRequest = new AdRequest.Builder().build();

		    // Begin loading your interstitial.
		    interstitial.loadAd(adRequest);
		    displayInterstitial();
	}

	 private AdView adView;
	 public void displayInterstitial() {
		    if (interstitial.isLoaded()) {
		      interstitial.show();
		    }
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







	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		// TODO Auto-generated method stub
		Log.i(TAG, "Joining Chat room");
		Intent i=new Intent(RoomsListActivity.this,ChatRoom.class);
      //  i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	
		Room selRoom=rList.get(position);
		i.putExtra("id", String.valueOf(selRoom.ID));
		i.putExtra("name", selRoom.RoomName);
        i.putExtra("Source", "Join");

//	    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		i.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(ChatRoom.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(i);

		startActivity(i);
		finish();
		
	}
	public class GetCitiesTask extends AsyncTask<Void, Void, Boolean> {
		private ProgressDialog pd;
		@Override
        protected void onPreExecute(){
           super.onPreExecute();
           try{
       	pd=new ProgressDialog(RoomsListActivity.this);

    	pd.setTitle("Loading Rooms List...");
    	pd.setMessage("Please wait.");
    	pd.setCancelable(false);
    	pd.setIndeterminate(true);
    	pd.show();
           }
           catch(Exception e)
           {
        	   e.printStackTrace();
           }
        }
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.
			try
			{
				List<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>(1);
				nameValuePairs.add(new BasicNameValuePair("statecode", rid));
				nameValuePairs.add(new BasicNameValuePair("apikey", getString(R.string.apikey)));
				String iresponse=Utils.postData("getCities", nameValuePairs);
				
//				String response=Utils.RestCall(Utils.WebServiceURL+"getCities/?statecode=" + rid+"&apikey="+getString(R.string.apikey));

				 Gson gson = new Gson();
				// Log.i(TAG, iresponse);
				 data= gson.fromJson(iresponse, Room[].class);
				 
				 for(int i=0;i<data.length;i++)
				 {
					 Room iRoom=new Room();
					 iRoom.ID=data[i].ID;
					 iRoom.RoomName=data[i].RoomName;
					 iRoom.UsersCount=data[i].UsersCount;
					 rList.add(iRoom);
				 }
				
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

			// TODO: register the new account here.
			return true;
		}
		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			try{
			RoomsAdapter cAdap=new RoomsAdapter(getApplicationContext(),
					R.layout.countryrow,
					R.id.lblCountryName,
					rList);
			lstCities.setAdapter(cAdap);
			if(pd!=null) pd.dismiss();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
		//	showProgress(false);
		}
	}

	public class GetRoomsTask extends AsyncTask<Void, Void, Boolean> {
		private ProgressDialog pd;
		@Override
        protected void onPreExecute(){
           super.onPreExecute();
           try{
       	pd=new ProgressDialog(RoomsListActivity.this);

    	pd.setTitle("Loading Rooms List...");
    	pd.setMessage("Please wait.");
    	pd.setCancelable(false);
    	pd.setIndeterminate(true);
    	pd.show();
           }
           catch(Exception e)
           {
        	   e.printStackTrace();
           }
        }
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.
			try
			{
				List<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>(1);
				nameValuePairs.add(new BasicNameValuePair("subcatcode", rid));
				nameValuePairs.add(new BasicNameValuePair("apikey", getString(R.string.apikey)));
				String iresponse=Utils.postData("getRooms", nameValuePairs);
				
//				String response=Utils.RestCall(Utils.WebServiceURL+"getCities/?statecode=" + rid+"&apikey="+getString(R.string.apikey));

				 Gson gson = new Gson();
				 //Log.i(TAG, iresponse);
				 data= gson.fromJson(iresponse, Room[].class);
				 
				 for(int i=0;i<data.length;i++)
				 {
					 Room iRoom=new Room();
					 iRoom.ID=data[i].ID;
					 iRoom.RoomName=data[i].RoomName;
					 iRoom.UsersCount=data[i].UsersCount;
					 rList.add(iRoom);
				 }
				
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

			// TODO: register the new account here.
			return true;
		}
		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			try{
			RoomsAdapter cAdap=new RoomsAdapter(getApplicationContext(),
					R.layout.countryrow,
					R.id.lblCountryName,
					rList);
			lstCities.setAdapter(cAdap);
			if(pd!=null) pd.dismiss();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
		//	showProgress(false);
		}
	}

	@Override
	public void onMPResume() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onResume()
	{
		super.onResume();

	}
}

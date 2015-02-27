package com.kss.xchat;


import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.kss.xchat.apiresponse.BuddyRequest;
import com.kss.xchat.viewadapters.BuddyRequestAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class BuddyRequestsActivity extends RootActivity {
	private static String TAG="BuddyRequests";
	ListView lstRequests;
	ArrayList<BuddyRequest> requestList;
	 private AdView adView;
	 ArrayList<com.kss.xchat.data.BuddyRequest> brList; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try
		{
			setContentView(R.layout.activity_buddy_requests);
			lstRequests=(ListView)findViewById(R.id.lstRequests);
			new GetRequestsTask().execute((Void) null);
			LoadRequests();
			loadAd();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.buddy_requests, menu);
		return true;
	}
	public void loadAd()
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
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.action_refresh:
	    	new GetRequestsTask().execute((Void) null);
			return true;
	    default:
            return super.onOptionsItemSelected(item);
	    }
	}
	public void LoadRequests()
	{

		try
		{
			com.kss.xchat.data.BuddyRequest br=new com.kss.xchat.data.BuddyRequest(getApplicationContext());
			brList=br.getRequestList();
			Log.i(TAG,"sIZE "+ brList.size());
			if(brList!=null)
			{
				if(ConversationsActivity.requests==null) ConversationsActivity.requests=new ArrayList<BuddyRequest>();
				else ConversationsActivity.requests.clear();
				for(int i=0;i<brList.size();i++)
				{
					BuddyRequest ibr=new BuddyRequest();
					ibr.NickName=brList.get(i).fromUser;
					ibr.Message=brList.get(i).Message;
					ConversationsActivity.requests.add(ibr);
				}
			}

			if(ConversationsActivity.requests.size()>0)
			{
				BuddyRequestAdapter convAdap=new BuddyRequestAdapter(getApplicationContext(), R.layout.requestrow, 
						R.id.txtHeading, ConversationsActivity.requests);
				BuddyRequestAdapter.requestActivity=this;
				if(convAdap!=null)
					lstRequests.setAdapter(convAdap);
			}	
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
		}
			
		
	}
	public class GetRequestsTask extends AsyncTask<Void, Void, Boolean> {
		ArrayList<BuddyRequest> requests;
		@Override
        protected void onPreExecute(){
           super.onPreExecute();
           
           if(requests !=null && requests.size()>0) requests.clear();
		}
	
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.
			

					try {
						String response;
						List<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>(1);
						nameValuePairs.add(new BasicNameValuePair("token", Utils.ReadPreference(getApplicationContext(), "AccessToken")));
						nameValuePairs.add(new BasicNameValuePair("apikey", getString(R.string.apikey)));
						response=Utils.postData("getRequests", nameValuePairs);
						
/*						response = Utils.RestCall(Utils.WebServiceURL+"getRequests/?token="+
								Utils.ReadPreference(getApplicationContext(), "AccessToken")+"&apikey="+getString(R.string.apikey));
*/						 Log.i("Response","Requests List -"+ response);
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
			LoadRequests();

			

		}

		@Override
		protected void onCancelled() {

		}
	}
	@Override
	public void onMPResume() {
		// TODO Auto-generated method stub
		
	}

}

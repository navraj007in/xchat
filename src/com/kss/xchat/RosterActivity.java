package com.kss.xchat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.kss.xchat.apiresponse.BuddyRequest;
import com.kss.xchat.apiresponse.Roster;
import com.kss.xchat.data.Blocked;
import com.kss.xchat.data.Profiles;
import com.kss.xchat.viewadapters.RosterAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

@SuppressWarnings("unused")
public class RosterActivity extends RootActivity implements OnItemClickListener{
	String TAG="RosterActivity";
//	GetRosterAsyncTask mAuthTask;
	ListView lstRoster;
	public static ArrayList<Roster> roster;
	 private AdView adView;
	 ArrayList<com.kss.xchat.data.Profiles> myRoster;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_roster);
		loadAd();
		lstRoster=(ListView)findViewById(R.id.lstRoster);
		com.kss.xchat.data.Profiles uRoster=new com.kss.xchat.data.Profiles(getApplicationContext());
		myRoster=uRoster.getProfiles();
/*		if(Utils.DEV_MODE==Utils.DEV_MODE_DEVICE)
		
*/
		new ReadRoster().execute((Void) null);
		new GetBlockedTask().execute((Void) null);
		new GetRequestsTask().execute((Void) null);
		
		RosterAdapter rAdap=new RosterAdapter(getApplicationContext(), R.layout.rosterrow, R.id.txtName,myRoster);
		lstRoster.setAdapter(rAdap);
		lstRoster.setOnItemClickListener(this);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.action_join_room:
            Intent intent = new Intent(this, ChatRoomsListActivity.class);
            startActivity(intent);
	    	return true;
	        default:
	            return super.onOptionsItemSelected(item);
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
		Profiles friend=myRoster.get(position);
		Intent intent=new Intent(RosterActivity.this,PMActivity.class);
		intent.putExtra("id", friend.id);
		intent.putExtra("name", friend.Name);
		intent.putExtra("nickname", friend.NickName);
		intent.putExtra("user", friend.NickName);
		
		startActivity(intent);
		
	}

	@Override
	public void onMPResume() {
		// TODO Auto-generated method stub
		
	}

	class ReadRoster extends AsyncTask<Void, Void, Boolean>{
		@Override
		protected void onPostExecute(final Boolean success) {
			RosterAdapter rAdap=new RosterAdapter(getApplicationContext(), R.layout.rosterrow, R.id.txtName,myRoster);
			lstRoster.setAdapter(rAdap);
		}

		@Override
		protected Boolean doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			List<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("token", Utils.ReadPreference(getApplicationContext(), "AccessToken")));
			nameValuePairs.add(new BasicNameValuePair("apikey", getString(R.string.apikey)));
			String response=Utils.postData("getRoster", nameValuePairs);
			
		/*	String response=Utils.RestCall(Utils.WebServiceURL+"getRoster/?token="+Utils.ReadPreference(getApplicationContext(), "AccessToken")
					+"&apikey="+getString(R.string.apikey));*/
			ArrayList<Roster> roster=new ArrayList<Roster>();
			//Log.i(TAG,response);
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
						
						/*response = Utils.RestCall(Utils.WebServiceURL+"getRequests/?token="+
								Utils.ReadPreference(getApplicationContext(), "AccessToken")+"&apikey="+getString(R.string.apikey));*/
						// Log.i("Response","Requests List -"+ response);
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

			

		}

		@Override
		protected void onCancelled() {

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

}

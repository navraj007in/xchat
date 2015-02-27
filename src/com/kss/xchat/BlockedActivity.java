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
import com.kss.xchat.data.Blocked;
import com.kss.xchat.data.Roster;
import com.kss.xchat.viewadapters.BlockListAdapter;
import com.kss.xchat.viewadapters.RoomUsersAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ListView;

public class BlockedActivity extends RootActivity {
	ListView lstUsers;
	RoomUsersAdapter rAdap;
	APIResponse aresponse ;
	private AdView adView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_blocked);
		lstUsers=(ListView)findViewById(R.id.lstBlockedUsers);
		new GetBlockedTask().execute((Void)null);

		loadBlockList();
		loadAd();
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.action_refresh:
			new GetBlockedTask().execute((Void)null);
			loadBlockList();
	    	return true;
	    default:
            return super.onOptionsItemSelected(item);
	    }
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.blocked, menu);
		return true;
	}
	ArrayList<Blocked> blockList;
	private void loadBlockList()
	{
		try{
		Blocked iBlock=new Blocked(getApplicationContext());
		blockList = iBlock.getBlockList();
		BlockListAdapter iAdap=new BlockListAdapter(getApplicationContext(), R.layout.requestrow, R.id.txtName, blockList);
		lstUsers.setAdapter(iAdap);
		}
		catch(Exception e){
			e.printStackTrace();
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
	private String TAG="BlockList";
	ArrayList<BuddyRequest> blocklist;
	public class GetBlockedTask extends AsyncTask<Void, Void, Boolean> {
		@Override
        protected void onPreExecute(){
           super.onPreExecute();
		}
	
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.
			try{
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
							
							Roster iProfile =new Roster(getApplicationContext());
							iProfile.City=blocklist.get(i).City;
							iProfile.Country=blocklist.get(i).Country;
							iProfile.Gender=blocklist.get(i).Gender;
							iProfile.Name=blocklist.get(i).Name;
							iProfile.NickName=blocklist.get(i).NickName;
							iProfile.ProfileImage=blocklist.get(i).ProfileImage;
							iProfile.addRecord();
							
							
						}
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
				//	Log.i(TAG, blocklist.size()+ " Blocked Users");
			}
			catch(Exception e){
				e.printStackTrace();
			}
			// TODO: register the new account here.
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
		//	bindChat();
			try{
			loadBlockList();
			}
			catch(Exception e){
				e.printStackTrace();
			}
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

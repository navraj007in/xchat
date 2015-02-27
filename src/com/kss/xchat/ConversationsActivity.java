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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.plus.PlusClient.OnAccessRevokedListener;
import com.kss.xchat.apiresponse.BuddyRequest;
import com.kss.xchat.apiresponse.Roster;
import com.kss.xchat.data.Conversations;
import com.kss.xchat.data.SecureMode;
import com.kss.xchat.viewadapters.ConversationsAdapter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.ShareCompat;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

public class ConversationsActivity extends RootActivity implements OnItemClickListener, OnAccessRevokedListener,OnClickListener{
	String TAG="Conversations";
	ListView lstRoster;
	 private AdView adView;

	  /* Your ad unit id. Replace with your actual ad unit id. */
	  //private static final String AD_UNIT_ID = "ca-app-pub-9436114210349385/8168229758";
	 String addNickName;
	ArrayList<Conversations> Headers ;
	TextView lblRequests;
	int requestCount=0;
	GetRequestsTask mRequestsTask;
	GetRosterTask mRosterTask;
	public static ArrayList<BuddyRequest> requests;
	ShareActionProvider mShareActionProvider ;
	BuddyRequestTask mBuddyTask;
	public static final String MESSAGE_INTENT = "com.kss.xchat.MESSAGE";
	public static final String REQUEST_INTENT = "com.kss.xchat.REQUEST";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_conversations);

		//Log.i(TAG,"Token-"+ Utils.getStrPreference(getApplicationContext(), "AccessToken"));

		initializeRecievers();
		initializeDebugMode();
		loadActionBarPic();
		//createMessageNotification("New xChat Messages","navraj");

		lstRoster=(ListView)findViewById(R.id.lstRoster);
		lblRequests=(TextView)findViewById(R.id.lblRequests);
		lblRequests.setOnClickListener(this);
		lstRoster.setOnItemClickListener(this);

		if(RosterActivity.roster!=null && Utils.DEV_MODE==Utils.DEV_MODE_DEVICE && Utils.isNetworkAvailable(getApplicationContext()))
		{
			mRosterTask=new GetRosterTask();
			mRosterTask.execute((Void) null);
		}
		loadAd();

	}
private void loadAd()
{
	try
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
	catch(Exception e)
	{
		e.printStackTrace();
	}

}
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		 if (adView != null) {
		      adView.destroy();
		    }
			unregisterReceiver(iMessageReciever);
			unregisterReceiver(iRequestReciever);


	}
private void LoadConversations()
	{
	try
	{
		Conversations cAdap=new Conversations(getApplicationContext());
		Headers = cAdap.getHeaders(Utils.getLoggedNickName(getApplicationContext()));
		
		if(Headers.size()>0)
		{
			ConversationsAdapter convAdap=new ConversationsAdapter(getApplicationContext(), R.layout.conversationsrow, 
					R.id.txtHeading, Headers);
			lstRoster.setAdapter(convAdap);
			
		}
		
	}
	catch(Exception e)
	{
		e.printStackTrace();
	}
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.conversations, menu);
		try
		{
	        MenuItem item = menu.findItem(R.id.menu_item_share);
	        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
	        mShareActionProvider = (ShareActionProvider) item.getActionProvider();
	        String playStoreLink = "https://play.google.com/store/apps/details?id=" +
	        getPackageName();
	    String yourShareText = "The Chatrooms are back.. Get them on your Android device here.... " + playStoreLink;
	    Intent shareIntent = ShareCompat.IntentBuilder.from(this)
	        .setType("text/plain").setText(yourShareText).getIntent();
	    shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "https://play.google.com/store/apps/details?id=com.kss.xchat");
	    // Set the share Intent
	    mShareActionProvider.setShareIntent(shareIntent);
	        }
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		try
		{
		    switch (item.getItemId()) {
		    case R.id.action_start_conversation:
		    	AlertDialog.Builder alert = new AlertDialog.Builder(this);

		    	alert.setTitle("Start a Conversation");
		    	alert.setMessage("Enter user id to chat with");
		    	// Set an EditText view to get user input 
		    	final EditText input = new EditText(this);
		    	alert.setView(input);
		    	
		    	alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		    	public void onClick(DialogInterface dialog, int whichButton) {
		    	  String value = input.getText().toString().trim().toLowerCase();

		    	  Intent pmIntent=new Intent(ConversationsActivity.this, PMActivity.class);
		    	  pmIntent.putExtra("nickname", value);
		    	  pmIntent.putExtra("name", value);
		    	  
		    	  startActivity(pmIntent);
		    	  
		    	  }
		    	});

		    	alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		    	  public void onClick(DialogInterface dialog, int whichButton) {
		    	    // Canceled.
		    	  }
		    	});

		    	alert.show();
		    	return true;
		    case R.id.action_join_room:
		    	boolean isNetworkAvailable=Utils.isNetworkAvailable(getApplicationContext());
		    	if(isNetworkAvailable){
		    	Intent intent = new Intent(this, TopRoomsActivity.class);
	            startActivity(intent);
		    	}
		    	else{
		    		Toast.makeText(getApplicationContext(), "Internet is not connected!!", Toast.LENGTH_SHORT).show();
		    	}
		    	return true;
		    case R.id.action_add_contact:
		    	AlertDialog.Builder alertAdd = new AlertDialog.Builder(this);

		    	alertAdd.setTitle("Add a Friend");
		    	alertAdd.setMessage("Enter Nick Name of the user to add");

		    	// Set an EditText view to get user input 
		    	final EditText inputAdd = new EditText(this);
		    	alertAdd.setView(inputAdd);

		    	alertAdd.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		    	public void onClick(DialogInterface dialog, int whichButton) {
		    	  String value = inputAdd.getText().toString().trim();
		    	  // Do something with value!
		    	  addNickName=value;
		    	  mBuddyTask=new BuddyRequestTask();
		    	  
		    	  }
		    	});

		    	alertAdd.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		    	  public void onClick(DialogInterface dialog, int whichButton) {
		    	    // Canceled.
		    	  }
		    	});

		    	alertAdd.show();
				return true;
		    case R.id.action_exit:
		    	if(Utils.isNetworkAvailable(getApplicationContext())){
		    	try{
					LogoutTask mLoginTask = new LogoutTask();
					mLoginTask.execute((Void) null);

		    		
		    	}
		    	catch(Exception e){
		    		e.printStackTrace();
		    	}
		    	}
		    	else{
		    		Toast.makeText(getApplicationContext(), "Internet is not connected.", Toast.LENGTH_SHORT).show();
		    	}
		    	
		    	return true;

		    case R.id.action_view_roster:
	            Intent intentRoster = new Intent(this, RosterActivity.class);
	            startActivity(intentRoster);
	            return true;
		    case R.id.action_favorites:
	            Intent favIntent= new Intent(this, FavoritesActivity.class);
	            startActivity(favIntent);
	            return true;
		    case R.id.action_settings:
	            Intent intentPref= new Intent(this, PrefsActivity.class);
	            startActivity(intentPref);
	            return true;
		   
		    case R.id.action_meet_new_people:
	            Intent intentmeet= new Intent(this, MeetOptionListActivity.class);
	            startActivity(intentmeet);
	            return true;
		    	

		    default:
		            return super.onOptionsItemSelected(item);
		    }
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return true;
		}
	}
	@Override
	public void onPause()
	{
		if (adView != null) {
		      adView.pause();
		    }

		super.onPause();
		
	}
	private void LoadChatWindow(final String nickname)
	{
		final String secureModePassword= Utils.ReadPreference(getApplicationContext(), "secureModePassword");

		SecureMode iSecure=new SecureMode(getApplicationContext());
		iSecure.nickname=nickname;
		iSecure.user=Utils.getLoggedNickName(getApplicationContext());
		if(iSecure.isSecureModeEnabled()) 
		{
	    	AlertDialog.Builder alert = new AlertDialog.Builder(this);

	    	alert.setTitle("Secure Mode Enabled");
	    	alert.setMessage("Enter Password to view this conversation");
	    	// Set an EditText view to get user input 
	    	final EditText input = new EditText(this);
	    	input.setInputType( InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD );
	        input.setTransformationMethod(PasswordTransformationMethod.getInstance());
	        alert.setView(input);

	    	alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	    	public void onClick(DialogInterface dialog, int whichButton) {
	    	  String value = input.getText().toString();
	    	  Log.i(TAG, value);
	    	  // Do something with value!

	    	  Log.i(TAG, value+"-"+secureModePassword);
	    	  if(value.equalsIgnoreCase(secureModePassword))
	    	  {
		    	  Intent pmIntent=new Intent(ConversationsActivity.this, PMActivity.class);
		    	  pmIntent.putExtra("nickname", nickname);
		    	  pmIntent.putExtra("name", nickname);
		    	  
		    	  startActivity(pmIntent);

	    	  }
	    	  else
	    		  Toast.makeText(getApplicationContext(), "Invalid password.Please Try Again.", Toast.LENGTH_SHORT).show();

	    	  }
	    	});

	    	alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	    	  public void onClick(DialogInterface dialog, int whichButton) {
	    	    // Cancelled.
	    		  //finish();
	    	  }
	    	});

	    	alert.show();

		}
		else
		{
	    	  Intent pmIntent=new Intent(ConversationsActivity.this, PMActivity.class);
	    	  pmIntent.putExtra("nickname", nickname);
	    	  pmIntent.putExtra("name", nickname);
	    	  
	    	  startActivity(pmIntent);

		}

	}
	@Override
	public void onResume()
	{
		super.onResume();
		LoadConversations();
		if (adView != null) {
		      adView.resume();
		    }
		mRequestsTask=new GetRequestsTask();
		mRequestsTask.execute((Void) null);

	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		// TODO Auto-generated method stub
		try
		{
			Conversations conv=Headers.get(position);
/*			Intent intent=new Intent(ConversationsActivity.this,PMActivity.class);
			intent.putExtra("id", conv.id);
			intent.putExtra("name", conv.sender);
			intent.putExtra("nickname", conv.sender);
			intent.putExtra("user", conv.sender);
			
			startActivity(intent);
*/
			LoadChatWindow(conv.sender);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

	@Override
	public void onAccessRevoked(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public class GetRequestsTask extends AsyncTask<Void, Void, Boolean> {
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
						 Log.i("Response","Requests List -"+ response);
*/						 ObjectMapper mapper = new ObjectMapper();
						requests= mapper.readValue(response, 
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

			if(requests!=null && requests.size()>0) lblRequests.setVisibility(View.VISIBLE);
			else
				lblRequests.setVisibility(View.GONE);

			

		}

		@Override
		protected void onCancelled() {
			mRequestsTask = null;
		}
	}

	public class LogoutTask extends AsyncTask<Void, Void, Boolean> {
		String response;
		APIResponse aresponse;
		@Override
        protected void onPreExecute(){
           super.onPreExecute();
   		}

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.

			try {
						List<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>(1);
						nameValuePairs.add(new BasicNameValuePair("token", Utils.ReadPreference(getApplicationContext(), "AccessToken")));
						nameValuePairs.add(new BasicNameValuePair("apikey", getString(R.string.apikey)));
						response=Utils.postData("logout", nameValuePairs);
						 ObjectMapper mapper = new ObjectMapper();
						 Log.i(TAG, response);
						 aresponse = mapper.readValue(response, 
									new TypeReference<APIResponse>() {
							});
						 
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
			if(aresponse.HttpResponse.equalsIgnoreCase("success")){
    		if(LoginActivity.mPlusClient.isConnected())
	    	{
	    	LoginActivity.mPlusClient.clearDefaultAccount();
	    	LoginActivity.mPlusClient.disconnect();
	    	}
	    	//Conversations xConv=new Conversations(getApplicationContext());
	    	//xConv.clearRecords();
	    	Utils.WritePreference(getApplicationContext(), "isLogged", "NO");
	    	finish();
	    	NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
	    	notificationManager.cancelAll();
			}
			else{
				Toast.makeText(getApplicationContext(), "Could not Log out. Please try again after some time", Toast.LENGTH_SHORT).show();
			}

		}

		@Override
		protected void onCancelled() {

		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId()==R.id.lblRequests)
		{
			lblRequests.setVisibility(View.GONE);
			Intent intent=new Intent(ConversationsActivity.this,BuddyRequestsActivity.class);
			startActivity(intent);
		}
	}
	private class GetRosterTask extends AsyncTask<Void, Void, Boolean> {

		@Override
        protected void onPreExecute(){
           super.onPreExecute();

        }
		
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.

				try {
					
					/*String response=Utils.RestCall(Utils.WebServiceURL+"getRoster/?token="+Utils.ReadPreference(getApplicationContext(), "AccessToken")
							+"&apikey="+getString(R.string.apikey));*/
					
					List<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>(1);
					nameValuePairs.add(new BasicNameValuePair("token", Utils.ReadPreference(getApplicationContext(), "AccessToken")));
					nameValuePairs.add(new BasicNameValuePair("apikey", getString(R.string.apikey)));
					String response=Utils.postData("getRoster", nameValuePairs);
					
					Log.i(TAG,response);
					if(RosterActivity.roster!=null) RosterActivity.roster.clear();
					
					 ObjectMapper mapper = new ObjectMapper();

					RosterActivity.roster= mapper.readValue(response, 
							new TypeReference<ArrayList<Roster>>() {
					});

					Log.i(TAG,"Roster Size- " +RosterActivity.roster.size());

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
				catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			// TODO: register the new account here.
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mRosterTask = null;

		}

		@Override
		protected void onCancelled() {
			mRosterTask= null;

		}
	}
	public class BuddyRequestTask extends AsyncTask<Void, Void, Boolean> {
		APIResponse aresponse ;
		@Override
        protected void onPreExecute(){
           super.onPreExecute();

        }
		
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.
		//	user.NickName="navraj";
			

			try {
				String Message="Hi,Lets be friends on xChat";
				List<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>(1);
				nameValuePairs.add(new BasicNameValuePair("token", Utils.ReadPreference(getApplicationContext(), "AccessToken")));
				nameValuePairs.add(new BasicNameValuePair("apikey", getString(R.string.apikey)));
				nameValuePairs.add(new BasicNameValuePair("NickName", addNickName));
				nameValuePairs.add(new BasicNameValuePair("Message", Message));
				
				String response=Utils.postData("SendRequest", nameValuePairs);
				
				
				/*String response=Utils.RestCall(Utils.WebServiceURL+"SendRequest/?token="+Utils.ReadPreference(getApplicationContext(), "AccessToken")
						+"&apikey="+getString(R.string.apikey)+"&NickName="+addNickName+"&Message="+Message);*/
				ObjectMapper mapper = new ObjectMapper();
				aresponse = mapper.readValue(response, 
						new TypeReference<APIResponse>() {
				});

				Log.i(TAG,response);
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

		@Override
		protected void onPostExecute(final Boolean success) {
			mBuddyTask = null;
			try
			{
				if(aresponse.HttpResponse.equalsIgnoreCase("SUCCESS"))
				{
					Toast.makeText(getApplicationContext(), "Request Sent", Toast.LENGTH_SHORT).show();
				}
				else
				{
					Toast.makeText(getApplicationContext(), aresponse.HttpResponse, Toast.LENGTH_SHORT).show();
				}
				
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
				
		}

		@Override
		protected void onCancelled() {

		//	showProgress(false);
		}
	}

	MessageBroadcast iMessageReciever = new MessageBroadcast() {

	    @Override
	    public void onReceive(Context context, Intent intent) {

	    	if (intent.getAction().equals(MESSAGE_INTENT)) {
		    	Log.i("Broadcast", "Recieved in chat window");
		    	LoadConversations();
	    		}
	    }
	};
	RequestBroadcast iRequestReciever = new RequestBroadcast() {

	    @Override
	    public void onReceive(Context context, Intent intent) {

	    	if (intent.getAction().equals(REQUEST_INTENT)) {
		    	Log.i("Broadcast", "buddy request Recieved in chat window");
		    	lblRequests.setVisibility(View.VISIBLE);
		    	LoadConversations();
	    		}
	    }
	};
private void loadActionBarPic()
{
	try
	{
		byte[] decodedString = Base64.decode(Utils.ReadPreference(getApplicationContext(), "ProfileImage"), Base64.DEFAULT);
		Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
		Drawable d = new BitmapDrawable(getResources(),decodedByte);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
		getActionBar().setIcon(d);
		}		
	}
	catch(Exception ex)
	{
		ex.printStackTrace();
	}

}
	private void initializeDebugMode()
	{
		if(Utils.DEV_MODE==Utils.DEV_MODE_EMULATOR)
		{
			
			Conversations xConv=new Conversations(getApplicationContext());
			
					xConv.clearRecords();
					xConv.sender="navraj";
					xConv.id=0;
					xConv.Message="Message to myself";
					xConv.fromUser="navraj";
					xConv.timeStamp=Utils.getCurrentTimeStamp();
					xConv.unread=1;
					xConv.addRecord();

					xConv.sender="navraj";
					xConv.id=1;
					xConv.Message="Second Message to myself";
					xConv.fromUser="navraj";
					xConv.timeStamp=Utils.getCurrentTimeStamp();
					xConv.unread=0;
					xConv.addRecord();

					xConv.sender="navraj";
					xConv.id=1;
					xConv.Message="Third Message to myself";
					xConv.fromUser="navraj";
					xConv.timeStamp=Utils.getCurrentTimeStamp();
					xConv.unread=1;
					xConv.addRecord();

					xConv.sender="nvskumar";
					xConv.id=2;
					xConv.Message="hello navraj";
					xConv.fromUser="nvskumar";
					xConv.timeStamp=Utils.getCurrentTimeStamp();
					xConv.unread=0;
					xConv.clearChat("nvskumar");
					xConv.addRecord();
					
					xConv.sender="monil";
					xConv.id=3;
					xConv.Message="monil here";
					xConv.fromUser="monil";
					xConv.timeStamp="2014-05-28 11:33:00";
					xConv.unread=1;
					xConv.clearChat("monil");
					xConv.addRecord();
			
			
		}

	}
	private void initializeRecievers()
	{
		final IntentFilter theFilter = new IntentFilter();
        theFilter.addAction(MESSAGE_INTENT);
		registerReceiver(iMessageReciever , theFilter);

		final IntentFilter reqFilter = new IntentFilter();
        reqFilter.addAction(REQUEST_INTENT);
		registerReceiver(iRequestReciever, reqFilter);

		Utils.WritePreference(getApplicationContext(), "secureModePassword", "1234");
		Log.i(TAG, Utils.ReadPreference(getApplicationContext(), "secureModePassword"));

	}
	@Override
	public void onMPResume() {
		// TODO Auto-generated method stub
		
	}
}

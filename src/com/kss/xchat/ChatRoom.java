package com.kss.xchat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gcm.demo.app.GCMActivity;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.kss.xchat.apiresponse.RoomUsers;
import com.kss.xchat.data.RoomChat;
import com.kss.xchat.data.Smiley;
import com.kss.xchat.viewadapters.ChatRoomAdapter;
import com.kss.xchat.viewadapters.SmileyAdapter;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ChatRoom extends RootActivity implements OnClickListener{
	String rId;
	String roomName;
	String Source;
	String TAG="ChatRoom";
	JoinRoomTask mAuthTask;
	SendMessageTask mSendTask;
	TextView txtRoom;
	APIResponse aresponse;
	ActionBar actionBar;
	Button cmdSend;
	public static boolean isChatRoomStarted=false;
	public static String RoomName;
	public static int roomId;
	public static ArrayList<RoomUsers> roomUsers;
    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    public static boolean isChatRoomOpened=false;
    public static boolean dontLeave=false;
    EditText txtMessage;
    String Message;
    ArrayList<RoomChat> chats;
    String UserName;
    String UserId;
    ImageView imgSmiley;
    LinearLayout layoutSmiley;
	SmileyAdapter sAdapter;
    /**
     * Substitute you own sender ID here. This is the project number you got
     * from the API Console, as described in "Getting Started."
     */
    String SENDER_ID = "62717634560";
    TextView mDisplay;
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    Context context;
    ListView lstChats;
    String regid;
    GridView gridSmiley;
	public static final String MESSAGE_INTENT = "com.kss.xchat.MESSAGE";
	ArrayList<Smiley> smileysList;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat_room);
		try
		{
			dontLeave=false;
			lstChats=(ListView)findViewById(R.id.lstChat);

			UserId=Utils.getLoggedUserId();
			UserName=Utils.ReadPreference(getApplicationContext(), "NickName");
			
			final IntentFilter theFilter = new IntentFilter();
	        theFilter.addAction(MESSAGE_INTENT);
			registerReceiver(iMessageReciever , theFilter);

			Intent intent=getIntent();
			Source=intent.getStringExtra("Source");
			rId=intent.getStringExtra("id");
			roomName=intent.getStringExtra("name");
			txtRoom=(TextView)findViewById(R.id.txtRoom);
			txtMessage=(EditText)findViewById(R.id.txtMessage);
			cmdSend=(Button)findViewById(R.id.cmdSend);
			cmdSend.setOnClickListener(this);
			RoomName=roomName;
			roomId=Integer.parseInt(rId);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {

			actionBar=getActionBar();
			actionBar.setTitle("Joining Room "+roomName );

			}
			bindChat();
//			Log.i(TAG, "Joining Room - "+rId+"-"+roomName);
			layoutSmiley=(LinearLayout)findViewById(R.id.layoutSmiley);
			gridSmiley=(GridView)findViewById(R.id.gridSmileys);
			imgSmiley=(ImageView)findViewById(R.id.imgSmiley);
			imgSmiley.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
//					Log.i(TAG, "Clicked Smiley");
					if(layoutSmiley.getVisibility()==View.GONE) layoutSmiley.setVisibility(View.VISIBLE);
					else
						layoutSmiley.setVisibility(View.GONE);
					
					
				}
			});
			gridSmiley.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int position,
						long arg3) {
					// TODO Auto-generated method stub
					int start = Math.max(txtMessage.getSelectionStart(), 0);
					int end = Math.max(txtMessage.getSelectionEnd(), 0);
					txtMessage.getText().replace(Math.min(start, end), Math.max(start, end),
					        smileysList.get(position).smileyCode, 0, smileysList.get(position).smileyCode.length());
				}
			});
			
	        gcm = GoogleCloudMessaging.getInstance(this);
	        regid = getRegistrationId(getApplicationContext());
	        /*if (regid.isEmpty()) {
	        	Log.i(TAG, "Register in background");
	            registerInBackground();
	        }
	        else
	        {
	        	Log.i(TAG, "Joining Room");
	        	mAuthTask = new JoinRoomTask();
	    		mAuthTask.execute((Void) null);
	        	
	        }*/
	        if(Source.equalsIgnoreCase("Join")){
//	        Log.i(TAG, "Joining Room");
			txtRoom.setText("Joining Room - "+roomName);
	        mAuthTask = new JoinRoomTask();
			mAuthTask.execute((Void) null);
	        }
//	        Log.i(TAG,"regid"+ regid);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public void populateSmileys()
	{
		smileysList=Utils.prepareSmileysList();
//		Log.i(TAG, "Smileys "+ smileysList.size());

		sAdapter=new SmileyAdapter(getApplicationContext(), R.layout.smileyrow,R.id.imgSmiley,smileysList);
		gridSmiley.setAdapter(sAdapter);
	}
	MessageBroadcast iMessageReciever = new MessageBroadcast() {
	    @Override
	    public void onReceive(Context context, Intent intent) {

	    	if (intent.getAction().equals(MESSAGE_INTENT)) {
		    	Log.i("Broadcast", "Recieved");
		    	bindChat();
	    		}
	    }
	};
	@Override
	public void onResume()
	{
		super.onResume();
		isChatRoomOpened=true;
		
	}
	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
		/*AlertDialog.Builder alert = new AlertDialog.Builder(this);

    	alert.setTitle("Leave this Room?");
    	alert.setMessage("Do you want to Leave this chat room?");

    	
    	alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
    	public void onClick(DialogInterface dialog, int whichButton) {
			new LeaveRoomTask().execute((Void) null);
    		finish();
    	  }
    	});

    	alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
    	  public void onClick(DialogInterface dialog, int whichButton) {
    	    // Canceled.
    	  }
    	});

    	alert.show();*/
		
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.chat_room, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		try
		{
		    switch (item.getItemId()) {
		    case R.id.action_room_users:
	            Intent intent = new Intent(this, RoomUsersActivity.class);
	            dontLeave=true;
	            intent.putExtra("rId", String.valueOf(rId));
	            startActivity(intent);
		    	return true;
		    case R.id.action_exit:
		    	new LeaveRoomTask().execute((Void) null);
	    		finish();
		        default:
		            return super.onOptionsItemSelected(item);
		    }
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return true;
	}

    @SuppressWarnings("unused")
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
        		mAuthTask = new JoinRoomTask();
        		mAuthTask.execute((Void) null);

            }
        }.execute(null, null, null);
    }

	public class JoinRoomTask extends AsyncTask<Void, Void, Boolean> {
		private ProgressDialog pd;
		@Override
        protected void onPreExecute(){
           super.onPreExecute();
           try
           {
        	   pd=new ProgressDialog(ChatRoom.this);

           	pd.setTitle("Entering Chat Room...");
           	pd.setMessage("Please wait....");
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
			

				try {
					
					List<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>(1);
					nameValuePairs.add(new BasicNameValuePair("AccessToken", Utils.ReadPreference(getApplicationContext(), "AccessToken")));
					nameValuePairs.add(new BasicNameValuePair("apikey", getString(R.string.apikey)));
					nameValuePairs.add(new BasicNameValuePair("RoomID", rId));
					nameValuePairs.add(new BasicNameValuePair("UserID", UserId));
					nameValuePairs.add(new BasicNameValuePair("DeviceID", regid));
					nameValuePairs.add(new BasicNameValuePair("RoomID", rId));
					
					String response=Utils.postData("JoinRoom", nameValuePairs);
					
				/*	String response=Utils.RestCall(Utils.WebServiceURL+"JoinRoom/?RoomID="+rId
							+"&UserID="+ UserId +"&DeviceID="+ regid+"&AccessToken="+Utils.ReadPreference(getApplicationContext(), "AccessToken")
							+"&apikey="+getString(R.string.apikey));
*/
					 ObjectMapper mapper = new ObjectMapper();
					APIResponse json = mapper.readValue(response, 
							new TypeReference<APIResponse>() {
					});
//					 Log.i(TAG, response);
					aresponse=json;
					roomUsers=json.roomUsers;
//					Log.i(TAG,String.valueOf(json.roomUsers.size()));
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
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			

			// TODO: register the new account here.
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			try
			{
				mAuthTask = null;
				txtRoom.setText(aresponse.HttpResponse);
				if(aresponse.HttpResponse.equalsIgnoreCase("SUCCESS") || aresponse.HttpResponse.equalsIgnoreCase("Room Entered Successfully")){
					populateSmileys();
					Utils.WritePreference(getApplicationContext(), "ChatRoom", roomName);
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {

					actionBar.setTitle(roomName +" Chat Room");
					}
					if(pd!=null) pd.dismiss();
				
				}
				else if(aresponse.HttpResponse.equalsIgnoreCase("full"))
				{
					Toast.makeText(getApplicationContext(), "Room is full", Toast.LENGTH_SHORT).show();
					finish();
				}
				else
				{
					Toast.makeText(getApplicationContext(), "Unable to join the room", Toast.LENGTH_SHORT).show();
					finish();
					
				}
				
			}
			catch(Exception e)
			{
				Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
				e.printStackTrace();
				finish();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
		//	showProgress(false);
		}
	}
	public void leaveRoom1()
	{
		Calendar cal = Calendar.getInstance();
	    cal.add(Calendar.SECOND, 10);
	   
	    Intent intent = new Intent(this, LeaveRoom.class);
	    PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);
	   Toast.makeText(getApplicationContext(), "You will be logged out of chat room in 15 minutes", Toast.LENGTH_SHORT).show();
	    AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
	    //for 30 mint 60*60*1000
	    /*alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
	                 60*60*1000, pintent);*/
	    alarm.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (1000 * 60*30), pintent);
	}
	public class LeaveRoomTask extends AsyncTask<Void, Void, Boolean> {
		@Override
        protected void onPreExecute(){
           super.onPreExecute();
           try
           {
        	  
           }
           catch(Exception e)
           {
        	   e.printStackTrace();
           }
        }

		
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.
			try {
				   List<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>(5);
                   nameValuePairs.add(new BasicNameValuePair("UserID", "1"));
                   nameValuePairs.add(new BasicNameValuePair("apikey", getString(R.string.apikey)));
                   nameValuePairs.add(new BasicNameValuePair("AccessToken",Utils.ReadPreference(getApplicationContext(), "AccessToken")));
                   String response=Utils.postData("LeaveRoom", nameValuePairs);


					
					 ObjectMapper mapper = new ObjectMapper();
					APIResponse json = mapper.readValue(response, 
							new TypeReference<APIResponse>() {
					});
//					 Log.i(TAG, response);
					aresponse=json;
					roomUsers=json.roomUsers;
					Log.i(TAG, "Left Room "+roomName);
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
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			

			// TODO: register the new account here.
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			try
			{
				
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

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGcmPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(GCMActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
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

    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGcmPreferences(context);
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId()==R.id.cmdSend)
		{
			mSendTask= new SendMessageTask();
			mSendTask.execute((Void) null);

		}
	}
	
	private void  bindChat()
	{
		try
		{
			RoomChat xChat=new RoomChat(getApplicationContext());
			chats = xChat.getChatRoomList(roomId);
			ChatRoomAdapter xChatADap=new ChatRoomAdapter(getApplicationContext(), 
					R.layout.chatroomrow, R.id.txtMessage, chats);
			lstChats.setAdapter(xChatADap);
		}
		 catch(Exception e)
         {
      	   e.printStackTrace();
         }
		
	}
	public class SendMessageTask extends AsyncTask<Void, Void, Boolean> {
		@Override
        protected void onPreExecute(){
           super.onPreExecute();
           try
           {
        	   	layoutSmiley.setVisibility(View.GONE);
               	Message=txtMessage.getText().toString();
               	RoomChat xChat=new RoomChat(getApplicationContext());
            	String RoomName=Utils.ReadPreference(getApplicationContext(), "ChatRoom");
                xChat.Message=Message;
                xChat.RoomID=rId;
                xChat.RoomName=RoomName;
                xChat.timeStamp=Utils.getCurrentTimeStamp();
                xChat.sender=Utils.getLoggedNickName(getApplicationContext());
                xChat.addChatRecord();
   				txtMessage.setText("");
   				bindChat();
   				Log.i(TAG, "Sending Message to All Room Users");
        	   
           }
           catch(Exception e)
           {
        	   e.printStackTrace();
           }
		}
	
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.
			String response="";
			try {
				List<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>(1);
				nameValuePairs.add(new BasicNameValuePair("RoomID", rId));
				nameValuePairs.add(new BasicNameValuePair("apikey", getString(R.string.apikey)));
				nameValuePairs.add(new BasicNameValuePair("Message", Message));
				nameValuePairs.add(new BasicNameValuePair("DeviceID", "deviceid"));
				nameValuePairs.add(new BasicNameValuePair("User", UserId));
				nameValuePairs.add(new BasicNameValuePair("NickName", Utils.getLoggedNickName(getApplicationContext())));
				nameValuePairs.add(new BasicNameValuePair("AccessToken", Utils.ReadPreference(getApplicationContext(), "AccessToken")));
				nameValuePairs.add(new BasicNameValuePair("User", UserId));
				nameValuePairs.add(new BasicNameValuePair("User", UserId));
				
				

				//nameValuePairs.add(new BasicNameValuePair("apikey", getString(R.string.apikey)));
				
				response=Utils.postData("SendMessageToRoom", nameValuePairs);

/*				response = Utils.RestCall(Utils.WebServiceURL+"SendMessageToRoom/?RoomID="+
						URLEncoder.encode(rId,"UTF-8")
						+"&Message="+URLEncoder.encode(Message,"UTF-8")+"&DeviceID="+ URLEncoder.encode("deviceid","UTF-8")+
						"&User="+URLEncoder.encode(UserId,"UTF-8")+"&NickName="+URLEncoder.encode(Utils.getLoggedNickName(getApplicationContext()),"UTF-8")
						+"&AccessToken="+
						Utils.ReadPreference(getApplicationContext(), "AccessToken")
						+"&apikey="+getString(R.string.apikey));
*///				 Log.i(TAG, response);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// TODO: register the new account here.
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
		}
	}
	@Override
public void onDestroy()
{
	super.onDestroy();
	unregisterReceiver(iMessageReciever);
}
	@Override
	public void onPause()
	{
		super.onPause();
		RootActivity.mActivityName="RootActivity";
		isChatRoomOpened=false;

		
	}
	@Override
	public void onStop()
	{
		super.onStop();
		ChatRoom.isChatRoomStarted=false;

/*		if(!dontLeave)		
			leaveRoom1();
*/		
	}
	@Override
	public void onStart(){
		super.onStart();
		RootActivity.mActivityName="ChatRoom";
		ChatRoom.isChatRoomStarted=true;
	}
	@Override
	public void onMPResume() {
		// TODO Auto-generated method stub
		
	}
	public void leaveRoom()
	{
		Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, 10);
       
        Intent intent = new Intent(this, LeaveRoom.class);
        PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        //for 30 mint 60*60*1000
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                     60*60*1000, pintent);

	}
}

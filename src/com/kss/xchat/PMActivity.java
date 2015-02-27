package com.kss.xchat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.kss.xchat.apiresponse.RoomUsers;
import com.kss.xchat.data.Blocked;
import com.kss.xchat.data.BuddyRequest;
import com.kss.xchat.data.Conversations;
import com.kss.xchat.data.Favorites;
import com.kss.xchat.data.Flash;
import com.kss.xchat.data.Mute;
import com.kss.xchat.data.Profiles;
import com.kss.xchat.data.Roster;
import com.kss.xchat.data.SecureMode;
import com.kss.xchat.data.Smiley;
import com.kss.xchat.viewadapters.AwesomeAdapter;
import com.kss.xchat.viewadapters.ChatAdapter;
import com.kss.xchat.viewadapters.SmileyAdapter;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Data;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PMActivity extends RootActivity implements OnClickListener{
	public static int ACTION_SEND_GALLERY=0;
	public static int ACTION_SEND_PHOTO=1;
	public static int ACTION_SEND_LOCATION=2;
	public static int ACTION_SEND_CONTACT=3;
	static final int PICK_CONTACT_REQUEST = 1; 
	
	private String displayName;
	private String sendNumber;
	static String TAG="PMActivity";
	TextView lblRequests;
	RoomUsers user=new RoomUsers();
	Button cmdSend;
	TextView lblHeader;
	EditText txtMessage;
	SendMessageTask mAuthTask;
	SendPendingMessageTask mSendPendingMessagesTask;
	BlockUserTask mBlockTask;
	BuddyRequestTask mBuddyTask;
	HotListUserTask mHotListTask;
	BuzzTask mBuzzTask;
	ListView lstChat;
	String Message;
	ImageView imgSmiley;
	ArrayList<Smiley> smileysList;
	GridView gridSmiley;
	RespondRequestsTask mRequestTask;
	String Response;
    public static final String PROPERTY_REG_ID = "registration_id";
	public static final String MESSAGE_INTENT = "com.kss.xchat.MESSAGE";
	public static final String REQUEST_INTENT = "com.kss.xchat.REQUEST";
	public static boolean gIsRunning=false;
	public static String gNickName="";
	String secureModePassword;
	LinearLayout layoutSmiley;
	SmileyAdapter sAdapter;
	APIResponse aresponse;
	RelativeLayout layoutRequest;
	ImageButton imgAccept,imgReject;
	ArrayList<Conversations> xChatList ;
	static boolean pendingTaskProgress=false;
	ChatAdapter xChatADap;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pm);
		initView();
		initializeRecievers();
		bindChat();
		populateSmileys();
		loadActionBarPic();
		checkBuddyRequest();
	}
	@SuppressWarnings("unused")
	private void initView()
	{
		Intent intent=getIntent();
		layoutRequest=(RelativeLayout)findViewById(R.id.layoutRequest);
		
		secureModePassword= Utils.ReadPreference(getApplicationContext(), "secureModePassword");
		user.NickName=intent.getStringExtra("nickname");
		user.Name=intent.getStringExtra("name");
		user.ID=String.valueOf(intent.getIntExtra("id",0));
		user.User=intent.getStringExtra("user");
		lblHeader=(TextView)findViewById(R.id.lblHeader);
		lblHeader.setText("In conversation with "+ user.NickName);
		cmdSend=(Button)findViewById(R.id.cmdSend);
		cmdSend.setOnClickListener(this);
		txtMessage=(EditText)findViewById(R.id.txtMessage);
		layoutSmiley=(LinearLayout)findViewById(R.id.layoutSmiley);
		gridSmiley=(GridView)findViewById(R.id.gridSmileys);
		lblRequests=(TextView)findViewById(R.id.lblRequests);
		lblRequests.setOnClickListener(this);
		String token=Utils.ReadPreference(getApplicationContext(), "AccessToken");
		sendPendingMessages();
		imgAccept=(ImageButton)findViewById(R.id.imgAccept);
		imgReject=(ImageButton)findViewById(R.id.imgReject);
		imgAccept.setOnClickListener(this);
		imgReject.setOnClickListener(this);
		imgSmiley=(ImageView)findViewById(R.id.imgSmiley);
		imgSmiley.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(layoutSmiley.getVisibility()==View.GONE) layoutSmiley.setVisibility(View.VISIBLE);
				else
					layoutSmiley.setVisibility(View.GONE);
				
				
			}
		});
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) 
		{
		getActionBar().setTitle(user.Name);
		}
	//	else
	//	setTitle(user.Name);
		
		lstChat=(ListView)findViewById(R.id.lstChat);
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

	}
	private void checkBuddyRequest()
	{
		BuddyRequest req=new BuddyRequest(getApplicationContext());
		boolean isRequestSent= req.isRequestSent(user.NickName);
		if(isRequestSent)
		{
			RelativeLayout layoutRequest=(RelativeLayout)findViewById(R.id.layoutRequest);
			layoutRequest.setVisibility(View.VISIBLE);
			TextView lblRequest=(TextView)findViewById(R.id.lblPMRequest);
			lblRequest.setText(user.NickName+" has sent a friend request");
			
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
		
	}
	private void loadActionBarPic()
	{
		Profiles iProfile=new Profiles(getApplicationContext());

		int i=iProfile.checkProfile(user.NickName);
		if(i>0) 
		{
			iProfile=iProfile.getProfile(user.NickName);
            try
    		{
    			byte[] decodedString = Base64.decode(iProfile.ProfileImage, Base64.DEFAULT);
    			Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    			Drawable d = new BitmapDrawable(getResources(),decodedByte);
    			//iv.setImageBitmap(decodedByte);
    			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) 
    			getActionBar().setIcon(d);

    			
    		}
    		catch(Exception ex)
    		{
    			ex.printStackTrace();
    		}
		}
		else
		{
			Roster iRoster=new Roster(getApplicationContext());
			int j=iRoster.checkProfile(user.NickName);
			if(j>0)
			{
				iProfile=iRoster.getProfile(user.NickName);
	            try
	    		{
	    			byte[] decodedString = Base64.decode(iProfile.ProfileImage, Base64.DEFAULT);
	    			Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
	    			Drawable d = new BitmapDrawable(getResources(),decodedByte);
	    			//iv.setImageBitmap(decodedByte);
	    			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) 
	    			getActionBar().setIcon(d);

	    			
	    		}
	    		catch(Exception ex)
	    		{
	    			ex.printStackTrace();
	    		}
			}
			else
				new GetProfile().execute((Void) null);
			
		}

	}
	@Override
	public void onResume()
	{
		super.onResume();
		RootActivity.mActivityName="ChatWindow";
		RootActivity.mTalkingto=user.NickName;

		PMActivity.gIsRunning=true;
		gNickName=user.NickName;
	}
	@Override
	public void onPause()
	{
		super.onPause();
		RootActivity.mActivityName="RootActivity";
		RootActivity.mTalkingto="None";
		PMActivity.gIsRunning=false;
	}
	
	private void enableAddButton(Menu menu)
	{
		Profiles iProfile=new Profiles(getApplicationContext());
		iProfile=iProfile.getProfile(user.NickName);
		if(iProfile!=null)
		{
			MenuItem mi = menu.findItem(R.id.action_add_contact);
			mi.setVisible(false);
			
		}

	}
	private void setMute(Menu menu)
	{
		Mute iMute=new Mute(getApplicationContext());
		iMute.nickname=user.NickName;
		iMute.user=Utils.getLoggedNickName(getApplicationContext());
		if(iMute.isMuted()) 
			{
			Log.i(TAG, "User is Muted");
			MenuItem mi = menu.findItem(R.id.action_mute);
			mi.setTitle("Unmute");

			}
		else
		{
			Log.i(TAG, "User is not muted");
			MenuItem mi = menu.findItem(R.id.action_mute);
			mi.setTitle("Mute");

		}
		
	}
	private void setBlocked(Menu menu){
		Blocked iBlock=new Blocked(getApplicationContext());
		iBlock.nickname=user.NickName;
		iBlock.user=Utils.getLoggedNickName(getApplicationContext());
		if(iBlock.isBlocked())
		{
			MenuItem mi = menu.findItem(R.id.action_block);
			mi.setVisible(false);
		}
	
	}
	private void setFavorite(Menu menu){
		Favorites iFav=new Favorites(getApplicationContext());
		iFav.nickname=user.NickName;
		iFav.user=Utils.getLoggedNickName(getApplicationContext());
		if(iFav.isFavorite())
		{
			MenuItem mi = menu.findItem(R.id.action_add_as_favorite);
			mi.setVisible(false);
		}
	
	}
	private void setSecureMode(Menu menu){
		SecureMode iSecure=new SecureMode(getApplicationContext());
		iSecure.nickname=user.NickName;
		iSecure.user=Utils.getLoggedNickName(getApplicationContext());
		if(iSecure.isSecureModeEnabled()) 
			{
			Log.i(TAG, "Secure Mode Enabled");
			MenuItem mi = menu.findItem(R.id.action_secure);
			mi.setTitle("Disable Secure Mode");

			}
		else
		{
			Log.i(TAG, "Secure Mode Disabled");
			MenuItem mi = menu.findItem(R.id.action_secure);
			mi.setTitle("Enable Secure Mode");

		}
	}
	private void setFlashMode(Menu menu){
		Flash iFlash=new Flash(getApplicationContext());
		iFlash.nickname=user.NickName;
		iFlash.user=Utils.getLoggedNickName(getApplicationContext());
		if(iFlash.isFlashOn()) 
			{
			Log.i(TAG, "Flash Mode on");
			MenuItem mi = menu.findItem(R.id.action_flash);
			mi.setTitle("Disable Flash Mode");

			}
		else
		{
			Log.i(TAG, "Flash Mode off");
			MenuItem mi = menu.findItem(R.id.action_flash);
			mi.setTitle("Enable Flash Mode");

		}
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pm, menu);
		enableAddButton(menu);
		setMute(menu);
		setBlocked(menu);
		setSecureMode(menu);
		setFlashMode(menu);
		setFavorite(menu);
		return true;
	}

	MessageBroadcast iMessageReciever = new MessageBroadcast() {

	    @Override
	    public void onReceive(Context context, Intent intent) {

	    	if (intent.getAction().equals(MESSAGE_INTENT)) {
		    //	Log.i("Broadcast", "Recieved in chat window");
		    	bindChat();
	    		}
	    }
	};
	RequestBroadcast iRequestReciever = new RequestBroadcast() {

	    @Override
	    public void onReceive(Context context, Intent intent) {

	    	if (intent.getAction().equals(REQUEST_INTENT)) {
		    //	Log.i("Broadcast", "buddy request Recieved in chat window");
		    	lblRequests.setVisibility(View.VISIBLE);
		    	bindChat();
	    		}
	    }
	};

	private void blockUser(final MenuItem item)
	{
    	AlertDialog.Builder alert = new AlertDialog.Builder(this);

    	alert.setTitle("Block User");
    	alert.setMessage("Do you want to block "+ user.NickName+" ?");

    	
    	alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
    	public void onClick(DialogInterface dialog, int whichButton) {
    		mBlockTask = new BlockUserTask();
    		mBlockTask.execute((Void) null);
    		item.setVisible(false);
    	  }
    	});

    	alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    	  public void onClick(DialogInterface dialog, int whichButton) {
    	    // Canceled.
    	  }
    	});

    	alert.show();


	}
	private void addUser(final MenuItem item)
	{
    	AlertDialog.Builder alert = new AlertDialog.Builder(this);

    	alert.setTitle("Add User");
    	alert.setMessage("Do you want to add "+ user.NickName+" as friend?");

    	
    	alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
    	public void onClick(DialogInterface dialog, int whichButton) {
	    	mBuddyTask= new BuddyRequestTask();
			mBuddyTask.execute((Void) null);
    		item.setVisible(false);
    	  }
    	});

    	alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    	  public void onClick(DialogInterface dialog, int whichButton) {
    	    // Canceled.
    	  }
    	});

    	alert.show();
		
	}
	public void performSendAction(int actionCode){
		switch (actionCode) {
		case 0:
			sendGalleryImage();
			break;
		case 1:
			sendCameraImage();
			break;
		case 2:
			sendLocation();
			break;
		case 3:
			sendContact();
			break;

		default:
			break;
		}
	}
	public void sendGalleryImage(){
		
	}
	public void sendCameraImage(){
		
	}
	public void sendLocation(){
		
	}
	public void sendContact(){
		
	}
	public void showSendDialog(){
		AlertDialog.Builder builderSingle = new AlertDialog.Builder(
                PMActivity.this);
        builderSingle.setIcon(R.drawable.ic_launcher);
        builderSingle.setTitle("Send");
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
        		PMActivity.this,
                android.R.layout.select_dialog_singlechoice);
        arrayAdapter.add("Gallery");
        arrayAdapter.add("Photo");
        arrayAdapter.add("Location");
        arrayAdapter.add("Contact");
        builderSingle.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builderSingle.setAdapter(arrayAdapter,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    	if(which==ACTION_SEND_CONTACT){
                    		pickContact();
                    	}
                    	
                    	/*                        String strName = arrayAdapter.getItem(which);
                        AlertDialog.Builder builderInner = new AlertDialog.Builder(
                                PMActivity.this);
                        builderInner.setMessage(strName);
                        builderInner.setTitle("Your Selected Item is");
                        builderInner.setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(
                                            DialogInterface dialog,
                                            int which) {
                                    	performSendAction(which);
                                        dialog.dismiss();
                                    }
                                });
                        builderInner.show();
*/                    }
                });
        builderSingle.show();
	}
	private void pickContact() {
	    Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
	    pickContactIntent.setType(Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
	    startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    // Check which request it is that we're responding to
	    if (requestCode == PICK_CONTACT_REQUEST) {
	        // Make sure the request was successful
	        if (resultCode == RESULT_OK) {
	            // Get the URI that points to the selected contact
	            Uri contactUri = data.getData();
	            // We only need the NUMBER column, because there will be only one row in the result
	            String[] projection = {Phone.NUMBER,Data._ID,Phone.DISPLAY_NAME
	                    };
	            // Perform the query on the contact to get the NUMBER column
	            // We don't need a selection or sort order (there's only one result for the given URI)
	            // CAUTION: The query() method should be called from a separate thread to avoid blocking
	            // your app's UI thread. (For simplicity of the sample, this code doesn't do that.)
	            // Consider using CursorLoader to perform the query.
	            Cursor cursor = getContentResolver()
	                    .query(contactUri, projection, null, null, null);
	            cursor.moveToFirst();
	            for(int i=0;i<cursor.getColumnCount();i++){
	            	Log.i(TAG, "Column-"+cursor.getColumnName(i)+"-"+ cursor.getString(i));
	            }
	            // Retrieve the phone number from the NUMBER column
	            int column = cursor.getColumnIndex(Phone.NUMBER);
	            String number = cursor.getString(column);
	            column = cursor.getColumnIndex(Phone.DISPLAY_NAME);
	            String name = cursor.getString(column);
//	            new JsonObjectRequest(name, null, null, new JsonObjectRequest());
	            class Contact{
	            public String Name;
	            public String Number;
	            }
	            Contact iContact=new Contact();
	            JSONObject json=new JSONObject();
	            Gson gson=new Gson();
	           // json=gson.toJson(iContact);
	            JsonObjectRequest jsObjRequest = new JsonObjectRequest
	                    (Request.Method.POST, Utils.WebServicePOSTURL+"sendContact", json, new com.android.volley.Response.Listener<JSONObject>() {


					@Override
					public void onResponse(JSONObject arg) {
						// TODO Auto-generated method stub
						
					}
	            }, new com.android.volley.Response.ErrorListener() {

	                @Override
	                public void onErrorResponse(VolleyError error) {
	                    // TODO Auto-generated method stub

	                }
	            });

	            // Access the RequestQueue through your singleton class.
	            XChatSingleton.getInstance(this).addToRequestQueue(jsObjRequest);

	            Toast.makeText(getApplicationContext(), "Selected Contact - "+number +"-"+name , Toast.LENGTH_SHORT).show();
	            // Do something with the phone number...
	        }
	    }
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.action_send_image:
	    	showSendDialog();
	    	return true;
	    case R.id.action_block:
	    	if(Utils.isNetworkAvailable(getApplicationContext()))
	    	blockUser(item);
	    	else
	    	{
	    		Toast.makeText(getApplicationContext(), "Internet is not connected.", Toast.LENGTH_SHORT).show();
	    	}
	    	return true;
	    case R.id.action_hotlist:
	    	if(Utils.isNetworkAvailable(getApplicationContext())){
			mHotListTask = new HotListUserTask();
			mHotListTask.execute((Void) null);
	    	}
	    	else{
	    		Toast.makeText(getApplicationContext(), "Internet is not connected.", Toast.LENGTH_SHORT).show();
	    	}
	    	return true;
	    case R.id.action_clear_chat:
	    	Conversations xConv=new Conversations(getApplicationContext());
	    	xConv.clearChat(user.NickName);
	    	bindChat();
	    	return true;
	    case R.id.action_buzz:
	    	if(Utils.isNetworkAvailable(getApplicationContext())){
	    	mBuzzTask=new BuzzTask();
	    	mBuzzTask.execute((Void) null);
	    	}
	    	else{
	    		Toast.makeText(getApplicationContext(), "Internet is not connected.", Toast.LENGTH_SHORT).show();
	    	}
	    	return true;

	    case R.id.action_add_contact:
	    	if(Utils.isNetworkAvailable(getApplicationContext()))
	    	addUser(item);
	    	else
	    		Toast.makeText(getApplicationContext(), "Internet is not connected.", Toast.LENGTH_SHORT).show();
			return true;
	    case R.id.action_mute:
			Mute iMute=new Mute(getApplicationContext());
			iMute.nickname=user.NickName;
			iMute.user=Utils.getLoggedNickName(getApplicationContext());
			iMute.toggleMute();
			if(iMute.isMuted()) 
			{
				item.setTitle("Unmute");
			}
			else
			{
				item.setTitle("Mute");
			}
				return true;
	    case R.id.action_secure:
			SecureMode iSecure=new SecureMode(getApplicationContext());
			iSecure.nickname=user.NickName;
			iSecure.user=Utils.getLoggedNickName(getApplicationContext());
			iSecure.toggleSecureMode();
			if(iSecure.isSecureModeEnabled()) 
			{

				item.setTitle("Disable Secure Mode");
	
			}
			else
			{
				item.setTitle("Enable Secure Mode");
			}
				return true;

	    case R.id.action_flash:
			Flash iFlash=new Flash(getApplicationContext());
			iFlash.nickname=user.NickName;
			iFlash.user=Utils.getLoggedNickName(getApplicationContext());
			iFlash.toggleFlash();
			if(iFlash.isFlashOn()) 
			{
				Log.i(TAG, "Flash Mode on");
				item.setTitle("Disable Flash Mode");
	
			}
			else
			{Log.i(TAG, "Flash Mode Off");
				item.setTitle("Enable Flash Mode");
			}
				return true;
				
	    case R.id.action_email_conversation:
	    	emailConversation();
	    	return true;
	    case R.id.action_add_as_favorite:
	    	Favorites iFav=new Favorites(getApplicationContext());
	    	iFav.nickname=user.NickName;
	    	iFav.user=Utils.getLoggedNickName(getApplicationContext());
	    	iFav.addAsFavorite();
	    	Toast.makeText(getApplicationContext(), user.NickName+ " added to favorites list.", Toast.LENGTH_SHORT).show();
	    	item.setVisible(false);
	    	return true;
	    case R.id.action_view_profile:
	    	Intent intent=new Intent(PMActivity.this,ProfileActivity.class);
	    	intent.putExtra("nickname", user.NickName);
	    	startActivity(intent);
	    	return true;
	    case R.id.action_recall_chat:
	    	if(Utils.isNetworkAvailable(getApplicationContext()))
	    	new RecallChatTask().execute((Void) null);
	    	else
	    		Toast.makeText(getApplicationContext(), "Internet is not connected.", Toast.LENGTH_SHORT).show();

	    		return true;	
	    default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	@SuppressWarnings("unused")
	private void emailConversation()
	{
		ContextWrapper c = new ContextWrapper(getApplicationContext());
//		File conversation=new File(c.getFilesDir().getAbsolutePath()+
//    			"/response.bin");

		
			Conversations xConv=new Conversations(getApplicationContext());
			String backup=xConv.getConversationText(user.NickName);
			try {

				  Utils.generateNoteOnSD("conversation.txt", backup);
		            String subject = "xChat Backup for "+ user.NickName;
		            String message = "xChat Backup for "+user.NickName+" on "+ Utils.getCurrentTimeStamp();
		            String filename=Environment.getExternalStorageDirectory()+"/xChat/conversation.txt";
		            Log.v(TAG, "Uri=" + Uri.parse("file://"+ 
		            				Environment.getExternalStorageDirectory()+
		            				"/xChat/conversations.txt"));
		            
		            Intent email = new Intent(Intent.ACTION_SEND);

		            email.putExtra(Intent.EXTRA_SUBJECT, subject);
		            email.putExtra(Intent.EXTRA_TEXT, message);
		            email.setType("message/rfc822");
		            email.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+ 
            				Environment.getExternalStorageDirectory()+
            				"/xChat/conversation.txt"));
		            
		            startActivity(Intent.createChooser(email, "Choose an Email client"));
		          
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
		
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId()==R.id.cmdSend)
		{
			layoutSmiley.setVisibility(View.GONE);
			boolean isNetworkAvailable=Utils.isNetworkAvailable(getApplicationContext());
			if(isNetworkAvailable){
	        Message=txtMessage.getText().toString();
			if(Message.length()>0)
			{
				String mid=Utils.GenerateRandom(16);
	        Conversations xConv=new Conversations(getApplicationContext());
	        xConv.Message=Message;
	        xConv.sender=user.NickName;
	        xConv.timeStamp=Utils.getCurrentTimeStamp();
	        xConv.fromUser=Utils.getLoggedNickName(PMActivity.this);
	        xConv.user=Utils.getLoggedNickName(getApplicationContext());
	        xConv.mId=mid;
	        xConv.addRecord();

	        if(Utils.DEV_MODE==Utils.DEV_MODE_EMULATOR)
	        {
	        	String Message="sender - "+user.NickName+"\n"+Utils.getLoggedNickName(getApplicationContext());
	        	Toast.makeText(getApplicationContext(), Message, Toast.LENGTH_LONG).show();
	        }
			bindChat();
			txtMessage.setText("");
			
			mAuthTask = new SendMessageTask();
			mAuthTask.execute(mid);
			}
			}
			else
			{
	    		Toast.makeText(getApplicationContext(), "Internet is not connected!!", Toast.LENGTH_SHORT).show();
			}
		}
		if(v.getId()==R.id.lblRequests)
		{
			boolean isNetworkAvailable=Utils.isNetworkAvailable(getApplicationContext());
			if(isNetworkAvailable){
			lblRequests.setVisibility(View.GONE);
			Intent intent=new Intent(PMActivity.this,BuddyRequestsActivity.class);
			startActivity(intent);
			}
			else{
	    		Toast.makeText(getApplicationContext(), "Internet is not connected!!", Toast.LENGTH_SHORT).show();
			}
		}
		if(v.getId()==R.id.imgAccept || v.getId()==R.id.imgReject){
			boolean isNetworkAvailable=Utils.isNetworkAvailable(getApplicationContext());
			if(isNetworkAvailable){
			layoutRequest.setVisibility(View.GONE);
			if(v.getId()==R.id.imgAccept) Response="Yes";
			else Response="No";
			BuddyRequest br=new BuddyRequest(getApplicationContext());
			br.getRequest(user.NickName);
			if(br!=null) br.deleteRecord(br);
			mRequestTask=new RespondRequestsTask();
			mRequestTask.execute((Void)null);
			}
			else{
	    		Toast.makeText(getApplicationContext(), "Internet is not connected!!", Toast.LENGTH_SHORT).show();
			}
		}

	}


	@SuppressWarnings("unused")
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
	if (v.getId() == R.id.lstChat) {
	    AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
	    Adapter adapter = lstChat.getAdapter();
	    Object item = adapter.getItem(info.position);
	    menu.setHeaderTitle("Choose");
	    MenuInflater m = getMenuInflater();  
        m.inflate(R.menu.chatmenu, menu);  
	}
	}
	@Override
	public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();  
        int position = (int) info.id;  
        Conversations chat = xChatADap.getItem(position);  

		switch(item.getItemId()){  
        case R.id.delete_item:  
             chat.deleteRecord(chat);
             xChatADap.remove(position);
             xChatADap.notifyDataSetChanged();  
             bindChat();
             return true;  
        case R.id.copy_item:
        	
        	ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE); 
        	 ClipData clip = ClipData.newPlainText("xChat", chat.Message);
        	 clipboard.setPrimaryClip(clip);
        	 return true;
        case R.id.recall_item:
        	return true;
   }  
   return super.onContextItemSelected(item);  
	}
	private void  bindChat()
	{
		registerForContextMenu(lstChat);
		Conversations xChat=new Conversations(getApplicationContext());
//		Log.i(TAG, "Loading chat for usr id -" + user.ID);
		xChatList = xChat.getConversations(user.NickName);

		xChatADap=new ChatAdapter(getApplicationContext(), 
				R.layout.chatroomrow, R.id.txtMessage, xChatList);
		AwesomeAdapter xChatADap1=new AwesomeAdapter(getApplicationContext(),  xChatList);
		xChatADap.User=user.NickName;
		lstChat.setAdapter(xChatADap1);
		xChat.markRead(user.NickName);
	}
   

	public class SendMessageTask extends AsyncTask<String, Void, Boolean> {
		String response;
		String mid;
		@Override
        protected void onPreExecute(){
           super.onPreExecute();

           Log.i(TAG, "Sending Personal Message to User " +user.NickName);
		}
	
		@Override
		protected Boolean doInBackground(String... params) {
			// TODO: attempt authentication against a network service.
			try {
				mid=params[0];
				if(Message.length()>0){
				List<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>(5);
				nameValuePairs.add(new BasicNameValuePair("to", user.NickName));
				nameValuePairs.add(new BasicNameValuePair("message", Message));
				nameValuePairs.add(new BasicNameValuePair("AccessToken",Utils.ReadPreference(getApplicationContext(), "AccessToken")));
				nameValuePairs.add(new BasicNameValuePair("apikey", getString(R.string.apikey)));
				nameValuePairs.add(new BasicNameValuePair("mid", mid));
				
				Flash iFlash=new Flash(getApplicationContext());
				iFlash.nickname=user.NickName;
				iFlash.user=Utils.getLoggedNickName(getApplicationContext());

				if(iFlash.isFlashOn()) 
				{
					response=Utils.postData("sendflash", nameValuePairs);
				}
				else
				{
				nameValuePairs.add(new BasicNameValuePair("fromusr", Utils.getLoggedNickName(PMActivity.this)));
				nameValuePairs.add(new BasicNameValuePair("fromid", (Utils.getLoggedUserId())));
				response=Utils.postData("sendpm", nameValuePairs);
				}
				}
			} 
			catch(Exception ex)
			{	 
				Log.e(TAG, ex.getMessage());

				
			}
			// TODO: register the new account here.
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			 ObjectMapper mapper = new ObjectMapper();
				try {
					aresponse= mapper.readValue(response, 
							new TypeReference<APIResponse>() {
					});
					if(aresponse.HttpResponse.equalsIgnoreCase("SUCCESS"))
					{
						Conversations xConv=new Conversations(getApplicationContext());
//						Log.i(TAG, "User -"+ user.NickName+"-"+mid);
						xConv.markSent(user.NickName, mid);
					}
					bindChat();
//					Log.i(TAG,response);
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


		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
		}
	}
	public class SendContactTask extends AsyncTask<String, Void, Boolean> {
		String response;
		String mid;
		@Override
        protected void onPreExecute(){
           super.onPreExecute();

           Log.i(TAG, "Sending Contact to User " +user.NickName);
		}
	
		@Override
		protected Boolean doInBackground(String... params) {
			// TODO: attempt authentication against a network service.
			try {
				mid=params[0];
				if(Message.length()>0){
				List<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>(5);
				nameValuePairs.add(new BasicNameValuePair("to", user.NickName));
				nameValuePairs.add(new BasicNameValuePair("number", sendNumber));
				nameValuePairs.add(new BasicNameValuePair("displayName", displayName));
				nameValuePairs.add(new BasicNameValuePair("AccessToken",Utils.ReadPreference(getApplicationContext(), "AccessToken")));
				nameValuePairs.add(new BasicNameValuePair("apikey", getString(R.string.apikey)));
				nameValuePairs.add(new BasicNameValuePair("fromusr", Utils.getLoggedNickName(PMActivity.this)));
				nameValuePairs.add(new BasicNameValuePair("fromid", (Utils.getLoggedUserId())));
				response=Utils.postData("sendContact", nameValuePairs);
				}
			} 
			catch(Exception ex)
			{	 
				Log.e(TAG, ex.getMessage());

				
			}
			// TODO: register the new account here.
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			 ObjectMapper mapper = new ObjectMapper();
				try {
					aresponse= mapper.readValue(response, 
							new TypeReference<APIResponse>() {
					});
					if(aresponse.HttpResponse.equalsIgnoreCase("SUCCESS"))
					{
						Conversations xConv=new Conversations(getApplicationContext());
//						Log.i(TAG, "User -"+ user.NickName+"-"+mid);
						xConv.markSent(user.NickName, mid);
					}
					bindChat();
//					Log.i(TAG,response);
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


		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
		}
	}

	public class RecallChatTask extends AsyncTask<Void, Void, Boolean> {
		@Override
        protected void onPreExecute(){
           super.onPreExecute();

//           Log.i(TAG, "Recalling Chat from User " +user.NickName);
		}
	
		@SuppressWarnings("unused")
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.
			String response;
			try {
				List<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>(5);
				nameValuePairs.add(new BasicNameValuePair("to", user.NickName));
				nameValuePairs.add(new BasicNameValuePair("message", Message));
				nameValuePairs.add(new BasicNameValuePair("AccessToken",Utils.ReadPreference(getApplicationContext(), "AccessToken")));
				nameValuePairs.add(new BasicNameValuePair("apikey", getString(R.string.apikey)));
				response=Utils.postData("RecallConversation", nameValuePairs);
				
//				 Log.i(TAG, response);
			} 
			catch(Exception ex)
			{	 
				Log.e(TAG, ex.getMessage());

				
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
		unregisterReceiver(iRequestReciever);
	}
	public void populateSmileys()
	{
		smileysList=Utils.prepareSmileysList();
//		Log.i(TAG, "Smileys "+ smileysList.size());
		sAdapter=new SmileyAdapter(getApplicationContext(), R.layout.smileyrow,R.id.imgSmiley,smileysList);
		gridSmiley.setAdapter(sAdapter);
	}

	
	private class BlockUserTask extends AsyncTask<Void, Void, Boolean> {

		@Override
        protected void onPreExecute(){
           super.onPreExecute();

        }
		
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.
		//	user.NickName="nvskumar";
			List<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>(3);
			nameValuePairs.add(new BasicNameValuePair("token", Utils.ReadPreference(getApplicationContext(), "AccessToken")));
			nameValuePairs.add(new BasicNameValuePair("apikey", getString(R.string.apikey)));
			nameValuePairs.add(new BasicNameValuePair("nickName",user.NickName));
			
			String response=Utils.postData("blockUser", nameValuePairs);

			ObjectMapper mapper = new ObjectMapper();

			try {
				aresponse= mapper.readValue(response, 
						new TypeReference<APIResponse>() {
				});

				
			//	Log.i(TAG,response);
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
//		 Log.i(TAG, response);

			Log.i(TAG,response);

			// TODO: register the new account here.
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mBlockTask = null;
/*			if(aresponse.HttpResponse.contains("success"))
				Toast.makeText(getApplicationContext(), "User Blocked!!", Toast.LENGTH_SHORT);
			else
				Toast.makeText(getApplicationContext(), "User Could not be blocked. Some Error Happened.", Toast.LENGTH_SHORT);
*/	if(aresponse.HttpResponse.equalsIgnoreCase("success"))
		{
			Blocked iBlocked=new Blocked(getApplicationContext());
			iBlocked.nickname=user.NickName;
			iBlocked.user=Utils.getLoggedNickName(getApplicationContext());
			iBlocked.BlockUser();
			Toast.makeText(getApplicationContext(), user.NickName+" Blocked Successfully.", Toast.LENGTH_SHORT).show();
					
		}
		else {
			Toast.makeText(getApplicationContext(), "An Error Happened, please try again later."+ aresponse.HttpResponse, Toast.LENGTH_SHORT).show();
		}
	
				
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
		//	showProgress(false);
		}
	}

	public class HotListUserTask extends AsyncTask<Void, Void, Boolean> {

		@Override
        protected void onPreExecute(){
           super.onPreExecute();

        }
		
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.
			List<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>(3);
			nameValuePairs.add(new BasicNameValuePair("token", Utils.ReadPreference(getApplicationContext(), "AccessToken")));
			nameValuePairs.add(new BasicNameValuePair("apikey", getString(R.string.apikey)));
			nameValuePairs.add(new BasicNameValuePair("nickName",user.NickName));
			
			String response=Utils.postData("hotListUser", nameValuePairs);

//			response=Utils.RestCall(Utils.WebServiceURL+"hotListUser/?token="+Utils.ReadPreference(getApplicationContext(), "AccessToken")
//					+"&apikey="+getString(R.string.apikey)+"&nickName="+user.NickName);
			ObjectMapper mapper = new ObjectMapper();

			try {
				aresponse= mapper.readValue(response, 
						new TypeReference<APIResponse>() {
				});

				
//				Log.i(TAG,response);
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
	//	 Log.i(TAG, response);

		//	Log.i(TAG,response);

			// TODO: register the new account here.
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mHotListTask= null;
			Toast.makeText(getApplicationContext(), aresponse.HttpResponse, Toast.LENGTH_SHORT).show();
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
		//	showProgress(false);
		}
	}

	public class BuddyRequestTask extends AsyncTask<Void, Void, Boolean> {

		@Override
        protected void onPreExecute(){
           super.onPreExecute();

        }
		
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.
		//	user.NickName="navraj";
			String Message="Hi,Lets be friends on xChat";

//			Log.i(TAG, "Executing URL-"+Utils.WebServiceURL+"SendRequest/?token="+Utils.ReadPreference(getApplicationContext(), "AccessToken")
	//				+"&apikey="+getString(R.string.apikey)+"&NickName="+user.NickName+"&Message="+Message);
			List<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>(3);
			nameValuePairs.add(new BasicNameValuePair("token", Utils.ReadPreference(getApplicationContext(), "AccessToken")));
			nameValuePairs.add(new BasicNameValuePair("apikey", getString(R.string.apikey)));
			nameValuePairs.add(new BasicNameValuePair("nickName",user.NickName));
			nameValuePairs.add(new BasicNameValuePair("Message",Message));
			
/*			String hresponse=Utils.RestCall(Utils.WebServiceURL+"SendRequest/?token="+Utils.ReadPreference(getApplicationContext(), "AccessToken")
					+"&apikey="+getString(R.string.apikey)+"&NickName="+user.NickName+"&Message="+Message);
*/
			String response=Utils.postData("SendRequest", nameValuePairs);
//			Log.i(TAG, "Post Response"+ response);

//			Utils.makeRestCall(iURL)
			ObjectMapper mapper = new ObjectMapper();

			try {
				aresponse= mapper.readValue(response, 
						new TypeReference<APIResponse>() {
				});

//				Log.i(TAG,response);
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
//		 Log.i(TAG, response);

			// TODO: register the new account here.
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mBuddyTask = null;
			if(aresponse.HttpResponse.equalsIgnoreCase("SUCCESS"))
			{
				Toast.makeText(getApplicationContext(), "Request Sent", Toast.LENGTH_SHORT).show();
			}
			else
			{
				Toast.makeText(getApplicationContext(), aresponse.HttpResponse, Toast.LENGTH_SHORT).show();
			}
				
		}

		@Override
		protected void onCancelled() {
			mBuddyTask = null;
		//	showProgress(false);
		}
	}
	class GetProfile extends AsyncTask<Void, Void, Boolean>
	{
		String ProfileImage;
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub
			List<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>(3);
			nameValuePairs.add(new BasicNameValuePair("NickName",user.NickName));
			nameValuePairs.add(new BasicNameValuePair("apikey", getString(R.string.apikey)));
			String response=Utils.postData("getProfile", nameValuePairs);
//			Log.i(TAG, response);
			ObjectMapper mapper = new ObjectMapper();
			ArrayList<com.kss.xchat.apiresponse.Roster> userRoster;
						try {
							userRoster= mapper.readValue(response, 
									new TypeReference<ArrayList<com.kss.xchat.apiresponse.Roster>>() {
							});
							Roster iProfile=new Roster(getApplicationContext());
							iProfile.NickName=userRoster.get(0).NickName;
							iProfile.Name=userRoster.get(0).Name;
							iProfile.City=userRoster.get(0).City;
							iProfile.State=userRoster.get(0).State;
							iProfile.Country=userRoster.get(0).Country;
							iProfile.Zip=userRoster.get(0).Zip;
							iProfile.ProfileImage=userRoster.get(0).ProfileImage;
							iProfile.Status=userRoster.get(0).Status;
							iProfile.addRecord();
							ProfileImage=userRoster.get(0).ProfileImage;

//							Log.i(TAG,response);
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
			return null;
		}
		@Override
        protected void onPreExecute(){
           super.onPreExecute();
        }
		@Override
		protected void onPostExecute(final Boolean success) {
			super.onPostExecute(success);
			try
    		{
    			byte[] decodedString = Base64.decode(ProfileImage, Base64.DEFAULT);
    			Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    			Drawable d = new BitmapDrawable(getResources(),decodedByte);
    			//iv.setImageBitmap(decodedByte);
    			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) 
    			getActionBar().setIcon(d);

    			
    		}
    		catch(Exception ex)
    		{
    			ex.printStackTrace();
    		}
		}

	}
	public class BuzzTask extends AsyncTask<Void, Void, Boolean> {

		@Override
        protected void onPreExecute(){
           super.onPreExecute();

        }
		
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.
			List<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>(3);
			nameValuePairs.add(new BasicNameValuePair("accesstoken", Utils.ReadPreference(getApplicationContext(), "AccessToken")));
			nameValuePairs.add(new BasicNameValuePair("apikey", getString(R.string.apikey)));
			nameValuePairs.add(new BasicNameValuePair("NickName",user.NickName));
			nameValuePairs.add(new BasicNameValuePair("fromUser",Utils.getLoggedUser(getApplicationContext())));
			String response=Utils.postData("buzzzUser", nameValuePairs);


//			ObjectMapper mapper = new ObjectMapper();

/*			try {
				aresponse= mapper.readValue(response, 
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
*/		 Log.i(TAG, response);

			// TODO: register the new account here.
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mBuddyTask = null;
			Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
   			 v.vibrate(2000);

		}

		@Override
		protected void onCancelled() {
			mBuzzTask= null;
		//	showProgress(false);
		}
	}
	@Override
	public void onMPResume() {
		// TODO Auto-generated method stub
		
	}
	public class SendPendingMessageTask extends AsyncTask<String, Void, Boolean> {
		String response;
		String mid;
		@Override
        protected void onPreExecute(){
           super.onPreExecute();
			pendingTaskProgress=true;

           Log.i(TAG, "Sending Personal Message to User " +user.NickName);
		}
	
		@Override
		protected Boolean doInBackground(String... params) {
			// TODO: attempt authentication against a network service.
			try {

				Conversations xConv=new Conversations(getApplicationContext());
				ArrayList<Conversations> unsentList = xConv.getUnsentConversations(user.NickName);
				if(unsentList.size()>0)
				{
					 ObjectMapper mapper = new ObjectMapper();
					for(int i=0;i<unsentList.size();i++)
					{
						Conversations iConv = unsentList.get(i);
						List<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>(5);
						nameValuePairs.add(new BasicNameValuePair("to", user.NickName));
						nameValuePairs.add(new BasicNameValuePair("message", iConv.Message));
						nameValuePairs.add(new BasicNameValuePair("AccessToken",Utils.ReadPreference(getApplicationContext(), "AccessToken")));
						nameValuePairs.add(new BasicNameValuePair("apikey", getString(R.string.apikey)));
						nameValuePairs.add(new BasicNameValuePair("mid", iConv.mId));
						
						Flash iFlash=new Flash(getApplicationContext());
						iFlash.nickname=user.NickName;
						iFlash.user=Utils.getLoggedNickName(getApplicationContext());

						if(iFlash.isFlashOn()) 
						{
							response=Utils.postData("sendflash", nameValuePairs);
						}
						else
						{
						nameValuePairs.add(new BasicNameValuePair("fromusr", Utils.getLoggedNickName(PMActivity.this)));
						nameValuePairs.add(new BasicNameValuePair("fromid", (Utils.getLoggedUserId())));
						response=Utils.postData("sendpm", nameValuePairs);
						
//						 Log.i(TAG, response);
						}
						aresponse= mapper.readValue(response, 
								new TypeReference<APIResponse>() {
						});
						if(aresponse.HttpResponse.equalsIgnoreCase("SUCCESS"))
						{
							iConv.markSent(user.NickName, mid);
						}
						
					}
				}

			} 
			catch(Exception ex)
			{	 
				Log.e(TAG, ex.getMessage());

				
			}
			// TODO: register the new account here.
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			pendingTaskProgress=false;
			bindChat();



		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
		}
	}
	public void displayRequest()
	{
	BuddyRequest br=new BuddyRequest(getApplicationContext());
	br=br.getRequest(user.NickName);
	if(br!=null)
	{
		layoutRequest.setVisibility(View.VISIBLE);
	}
	
	}
	public void sendPendingMessages()
	{
		if(!pendingTaskProgress) 
			{
			mSendPendingMessagesTask=new SendPendingMessageTask();
			mSendPendingMessagesTask.execute("");
			}
	}
  	public class RespondRequestsTask extends AsyncTask<Void, Void, Boolean> {
		@Override
        protected void onPreExecute(){
           super.onPreExecute();

		}
	
		@SuppressWarnings("unused")
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.
			String response;
			List<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("token", Utils.ReadPreference(getApplicationContext(), "AccessToken")));
			nameValuePairs.add(new BasicNameValuePair("apikey", getString(R.string.apikey)));
			nameValuePairs.add(new BasicNameValuePair("NickName", user.NickName));
			nameValuePairs.add(new BasicNameValuePair("Response", Response));
			nameValuePairs.add(new BasicNameValuePair("Message", Message));
			
			response=Utils.postData("replyBuddyRequest", nameValuePairs);
			
				/*response = Utils.RestCall(Utils.WebServiceURL+"replyBuddyRequest/?token="+
						Utils.ReadPreference(getApplicationContext(), "AccessToken")+"&apikey="+getString(R.string.apikey)
						+"&NickName="+user.NickName+"&Response="+Response+"&Message="+Message);*/
//				 Log.i("Response","Requests List -"+ response);
				 ObjectMapper mapper = new ObjectMapper();

					try {
						APIResponse aresponse= mapper.readValue(response, 
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
			mRequestTask= null;
			Toast.makeText(getApplicationContext(), "Request Replied To.", Toast.LENGTH_SHORT).show();	
			try
			{
				if(Response.equalsIgnoreCase("yes"))
				{
					Toast.makeText(getApplicationContext(), user.NickName+ " is added to your contact list", Toast.LENGTH_SHORT).show();
				}
				else
				{
					Toast.makeText(getApplicationContext(), "Request Rejected", Toast.LENGTH_SHORT).show();
					
				}
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}

		}

		@Override
		protected void onCancelled() {
			mRequestTask = null;
		}
	}

}

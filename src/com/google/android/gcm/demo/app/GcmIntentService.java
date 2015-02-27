package com.google.android.gcm.demo.app;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.kss.xchat.BuddyRequestsActivity;
import com.kss.xchat.ChatRoom;
import com.kss.xchat.ConversationsActivity;
import com.kss.xchat.PMActivity;
import com.kss.xchat.R;
import com.kss.xchat.RootActivity;
import com.kss.xchat.Utils;
import com.kss.xchat.apiresponse.BuddyRequest;
import com.kss.xchat.data.Blocked;
import com.kss.xchat.data.Conversations;
import com.kss.xchat.data.Mute;
import com.kss.xchat.data.RoomChat;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;
import android.os.Vibrator;

/**
 * This {@code IntentService} does the actual handling of the GCM message.
 * {@code GcmBroadcastReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
	public static final String MESSAGE_INTENT = "com.kss.xchat.MESSAGE";
	public static final String REQUEST_INTENT = "com.kss.xchat.REQUEST";
	SendMessageTask mAuthTask;
    NotificationCompat.Builder builder;

    public GcmIntentService() {
        super("GcmIntentService");
    }
    public static final String TAG = "xChatGCM";

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM will be
             * extended in the future with new message types, just ignore any message types you're
             * not interested in, or that you don't recognize.
             */
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Error","Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Error","Deleted messages on server: " + extras.toString());
            // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // This loop represents the service doing some work.

            	if(intent.getExtras().toString().equalsIgnoreCase(""))
            	{
            		Log.i(TAG, "Its a RM");
            	}
            	else
            	{
            		Log.i(TAG, "Its a PM");
            	}
                Log.i(TAG, "Received: " + intent.getExtras());
                RoomChat xChat=new RoomChat(getApplicationContext());
                String Type=intent.getStringExtra("Type");
                if(Type.equalsIgnoreCase("Contact"))
                {
                	Log.i(TAG, "Recieved Contact from "+intent.getExtras().toString()+ intent.getStringExtra("From"));

                }
                if(Type.equalsIgnoreCase("RM"))
                {
                	String RoomName=Utils.ReadPreference(getApplicationContext(), "ChatRoom");
                	Log.i(TAG, "Its an RM from "+intent.getExtras().toString()+ intent.getStringExtra("From"));
                    xChat.Message=intent.getStringExtra("MessageBody");
                    xChat.RoomID=intent.getStringExtra("RoomID");
                    xChat.RoomName=RoomName;
                    xChat.timeStamp=Utils.getCurrentTimeStamp();
                    xChat.sender=intent.getStringExtra("From");
                    if(!xChat.sender.equalsIgnoreCase(Utils.getLoggedNickName(getApplicationContext())))
                    xChat.addChatRecord();
                    Intent i = new Intent();
        	        i.setAction(MESSAGE_INTENT);
        	        getApplicationContext().sendBroadcast(i);
        	        if(!RootActivity.mActivityName.equalsIgnoreCase("ChatRoom"))
                    sendRoomNotification(RoomName,intent.getStringExtra("MessageBody"),Integer.parseInt(intent.getStringExtra("RoomID")));

                }
                if(Type.equalsIgnoreCase("PM"))
                {
                	Log.i(TAG, "Its a PM");

                	String contentType=intent.getStringExtra("ContentType");
                	if(contentType.equalsIgnoreCase("buzz")){

                		try {
                		    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                		    Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                		    r.play();
                		} catch (Exception e) {
                		    e.printStackTrace();
                		}
                		 Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                		 // Vibrate for 500 milliseconds
                		 if(!Utils.getBoolPreference(getApplicationContext(), "keyDND")) 
                			 v.vibrate(2000);
                		 //String Message =intent.getStringExtra("MessageBody");
                		/* if(Utils.getBoolPreference(getApplicationContext(), "keyDND")) Toast.makeText(getApplicationContext(), 
                				 "DND", 
                				 Toast.LENGTH_SHORT).show();*/
                		 Blocked iBlock=new Blocked(getApplicationContext());
                			iBlock.nickname=intent.getStringExtra("From");
                			iBlock.user=Utils.getLoggedNickName(getApplicationContext());
                		 boolean isBlocked=iBlock.isBlocked();
         				Log.i(TAG, iBlock.nickname+" blocked is "+isBlocked);

                		 if(!isBlocked){
                    		 Conversations xConv=new Conversations(getApplicationContext());
                  	        xConv.Message="Buzz";
                  	        xConv.sender=intent.getStringExtra("From");
                  	        xConv.timeStamp=Utils.getCurrentTimeStamp();
                  	        xConv.fromUser=intent.getStringExtra("From");
                 	        xConv.user=Utils.getLoggedNickName(getApplicationContext());
                  	       xConv.unread=1;
                  	        xConv.addRecord();
                  	        if(!Utils.getBoolPreference(getApplicationContext(), "keyDND"))
                     	        createMessageNotification1("You Recieved a buzz",xConv.sender);
                  	        Intent i = new Intent();
                  	        i.setAction(MESSAGE_INTENT);
                  	        getApplicationContext().sendBroadcast(i);
                			 
                		 }
                		 
                	}
                	if(contentType.equalsIgnoreCase("text")){
               		 Blocked iBlock=new Blocked(getApplicationContext());
               		 iBlock.nickname=intent.getStringExtra("From");
               		 iBlock.user=Utils.getLoggedNickName(getApplicationContext());
               		 boolean isBlocked=iBlock.isBlocked();
               		 Log.i(TAG,iBlock.nickname+"is"+String.valueOf(isBlocked));
               		 if(!isBlocked){

                		String Message =intent.getStringExtra("MessageBody");
               		 	
            	        Conversations xConv=new Conversations(getApplicationContext());
            	        xConv.Message=Message;
            	        xConv.sender=intent.getStringExtra("From");
            	        xConv.timeStamp=Utils.getCurrentTimeStamp();
            	        xConv.fromUser=intent.getStringExtra("From");
            	        xConv.user=Utils.getLoggedNickName(getApplicationContext());
            	        xConv.unread=1;
            	        xConv.addRecord();
            	        Log.i(TAG, "Inserted");
            	        Mute uMute=new Mute(getApplicationContext());
            	        uMute.nickname=xConv.sender;
            	        uMute.user=Utils.getLoggedNickName(getApplicationContext());
            	        
             	        if(!Utils.getBoolPreference(getApplicationContext(), "keyDND") && ! uMute.isMuted())
             	        	if(!(RootActivity.mActivityName.equalsIgnoreCase("ChatWindow") 
             	        			&& RootActivity.mTalkingto.equalsIgnoreCase(xConv.sender)))
            	        createMessageNotification1(xConv.Message,xConv.sender);
                         Intent i = new Intent();
             	        i.setAction(MESSAGE_INTENT);
             	        getApplicationContext().sendBroadcast(i);
             	       if(Utils.getBoolPreference(getApplicationContext(), "keyAutoReply"))
             	       {
             	    	  // Toast.makeText(getApplicationContext(), "Auto Reply Mode Enabled", Toast.LENGTH_SHORT).show();
             	    	   
             	    	   mAuthTask=new SendMessageTask();
             	    	   String autoReply=Utils.getStrPreference(getApplicationContext(), "keyAutoReplyText");
             	    	   if(autoReply==null || autoReply.length()==0) autoReply="Do not Disturb";
             	    	   mAuthTask.execute(xConv.sender,autoReply);
             	       }
               		 }
                	}
                	if(contentType.equalsIgnoreCase("recall")){
            	        Conversations xConv=new Conversations(getApplicationContext());
            	        xConv.recallChat(intent.getStringExtra("From"));
                        Intent i = new Intent();
             	        i.setAction(MESSAGE_INTENT);
             	        getApplicationContext().sendBroadcast(i);
            	        
                	}
                	if(contentType.equalsIgnoreCase("flash")){
               		 Blocked iBlock=new Blocked(getApplicationContext());
               		 iBlock.nickname=intent.getStringExtra("From");
               		 iBlock.user=Utils.getLoggedNickName(getApplicationContext());
               		 boolean isBlocked=iBlock.isBlocked();
               		 if(!isBlocked){

                			Log.i("Flash","Flash Message Recieved");
                			final String Message=intent.getStringExtra("From")+": "+intent.getStringExtra("MessageBody");
                			 Thread t = new Thread()
                			    {

                			        public void run()
                			        {

                			            Message myMessage = new Message();
                			            Bundle resBundle = new Bundle();
                			            resBundle.putString( "message", Message );
                			            myMessage.setData( resBundle );
                			            handler.sendMessage( myMessage );
                			        }
                			    };
                			    t.start();

             	       if(Utils.getBoolPreference(getApplicationContext(), "keyAutoReply"))
             	       {
             	    	  // Toast.makeText(getApplicationContext(), "Auto Reply Mode Enabled", Toast.LENGTH_SHORT).show();
             	    	   
             	    	   mAuthTask=new SendMessageTask();
             	    	   String reply=Utils.getStrPreference(getApplicationContext(), "keyAutoReplyText");
             	    	   if (reply.equalsIgnoreCase("")) reply="Do not disturb.";
             	    	   mAuthTask.execute(intent.getStringExtra("From"),reply);
             	       }
                	}

                Log.i(TAG, "Received: " + intent.getExtras());
                	}
                }
            	if(Type.equalsIgnoreCase("BuddyRequest")){
                	String contentType=intent.getStringExtra("ContentType");
            		if(contentType.equalsIgnoreCase("BuddyRequest")){
               		 Blocked iBlock=new Blocked(getApplicationContext());
               		 iBlock.nickname=intent.getStringExtra("From");
               		 iBlock.user=Utils.getLoggedNickName(getApplicationContext());
               		 boolean isBlocked=iBlock.isBlocked();
               		 if(!isBlocked){

            	        Conversations xConv=new Conversations(getApplicationContext());
            	        xConv.Message=intent.getStringExtra("MessageBody");
            	        xConv.sender=intent.getStringExtra("From");
            	        xConv.timeStamp=Utils.getCurrentTimeStamp();
            	        xConv.fromUser=intent.getStringExtra("From");
            	        xConv.user=Utils.getLoggedNickName(getApplicationContext());
            	        xConv.addRecord();
            	        BuddyRequest br=new BuddyRequest();
            	        br.NickName=xConv.fromUser;
            	        br.Message=xConv.Message;
            	        ConversationsActivity.requests.add(br);
            	        com.kss.xchat.data.BuddyRequest iRequest=new com.kss.xchat.data.BuddyRequest(getApplicationContext());
            	        iRequest.fromUser=xConv.fromUser;
            	        iRequest.timeStamp=Utils.getCurrentTimeStamp();
            	        iRequest.Message=xConv.Message;
            	        iRequest.addRecord();
            	        Log.i(TAG, "Inserted"); 
                		Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                		 // Vibrate for 500 milliseconds
             	        if(!Utils.getBoolPreference(getApplicationContext(), "keyDND"))
                		v.vibrate(1000);

                        Intent i = new Intent();
            	        i.setAction(MESSAGE_INTENT);
            	        getApplicationContext().sendBroadcast(i);

            	        if(!Utils.getBoolPreference(getApplicationContext(), "keyDND"))
            	        	raiseNotificationBuddyRequest(xConv.sender, xConv.Message);
            	        	//createBuddyNotification(xConv.sender,"Received friend Request from " +intent.getStringExtra("From"),xConv.sender );
            	        Intent r = new Intent();
            	        r.setAction(REQUEST_INTENT);
            	        getApplicationContext().sendBroadcast(r);
               		 }
            	}

            	
            }

        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
        }
        
    }
    private Handler handler = new Handler()
    {

          public void handleMessage( Message msg )
                {

                 Toast.makeText( getBaseContext(), msg.getData().getString( "message" ), Toast.LENGTH_LONG ).show();
                }
    };
    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String title,String msg) {
    	
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent roomIntent=new Intent(this,ChatRoom.class);
        roomIntent.putExtra("Source", "Notify");
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                roomIntent, 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.xchatlogo3)
        .setContentTitle(title)
        .setStyle(new NotificationCompat.BigTextStyle()
        .bigText(msg))
        .setContentText(msg);
        mBuilder.setAutoCancel(true);
        
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
private void sendRoomNotification(String title,String msg,int rId) {
    	
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent roomIntent=new Intent(this,ChatRoom.class);
        roomIntent.putExtra("Source", "Notify");
        roomIntent.putExtra("id", String.valueOf(rId));
        roomIntent.putExtra("name", title);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                roomIntent, 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.xchatlogo3)
        .setContentTitle(title)
        .setStyle(new NotificationCompat.BigTextStyle()
        .bigText(msg))
        .setContentText(msg);
        mBuilder.setAutoCancel(true);
        
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
	    Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
	    r.play();
    }
    private void createBuddyNotification(String title,String msg,String sender) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, BuddyRequestsActivity.class), 0);
        
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.xchatlogo3)
        .setContentTitle(title)
        .setStyle(new NotificationCompat.BigTextStyle()
        .bigText(msg))
        .setContentText(msg).setAutoCancel(true);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_BUDDY_REQUEST, mBuilder.build());
    }
    void createMessageNotification(String title,String sender) {
        NotificationManager mNotificationManager;
        final int NOTIFICATION_ID = 1;

        mNotificationManager = (NotificationManager)
               this.getSystemService(Context.NOTIFICATION_SERVICE);

       Intent intent=new Intent(this,ConversationsActivity.class);
       Conversations xConv=new Conversations(getApplicationContext());
       PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
               intent, 0);

       NotificationCompat.Builder mBuilder =
               new NotificationCompat.Builder(this)
       .setSmallIcon(R.drawable.xchatlogo3)
       .setContentTitle(title)
       .setStyle(new NotificationCompat.BigTextStyle()
       .bigText(xConv.getNotificationsString()))
       .setContentText(xConv.getNotificationsString()).setAutoCancel(true);

       mBuilder.setContentIntent(contentIntent);
       mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
	    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
	    Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
	    r.play();

   }
    final int NOTIFICATION_ID_CHAT = 2;
    final int NOTIFICATION_BUDDY_REQUEST = 3;

    void createMessageNotification1(String title,String sender) {
        Conversations xConv=new Conversations(getApplicationContext());
        int convCount=xConv.getHeadersCount();
        if(convCount==1)
        {
        	raiseNotificationSingle(sender,title);
        	Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
     	    Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
     	    r.play();
        	
        }
        else
        {
        	raiseNotification();
     	    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
     	    Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
     	    r.play();
        	
        }	

   }

	private void raiseNotification()
	{
		String contentTitle="Chat Alert!!";
		int smallIcon=R.drawable.xchatlogo3;
		Conversations xConv=new Conversations(getApplicationContext());
		String contentText=xConv.getNotificationsString();
		
		buildNotificationConversation(getApplicationContext(), contentTitle, contentText,
				Utils.getUserBitmap(getApplicationContext(), "navraj"), smallIcon, 
				xConv.getUnreadMessagesArray(), "xChat :"+contentText,1);
	}

	private void raiseNotificationBuddyRequest(String sender,String message)
	{
		String contentTitle="New Friend Request!!";
		int smallIcon=R.drawable.xchatlogo3;
		Conversations xConv=new Conversations(getApplicationContext());
	
		String contentText;

		contentText="New friend requests";
		
		buildNotificationRequest(getApplicationContext(), contentTitle, contentText,
				Utils.getUserBitmap(getApplicationContext(), sender), smallIcon, 
				xConv.getUnreadMessagesArray(sender), contentText,2);
	}

	private void raiseNotificationSingle(String sender,String message)
	{
		String contentTitle=sender ;
		int smallIcon=R.drawable.xchatlogo3;
		Conversations xConv=new Conversations(getApplicationContext());
		int count =xConv.getUnreadCount(sender);
		
		String contentText;
		if(count>1)
		contentText=xConv.getUnreadCount(sender)+ " New messages.";
		else
			if(message.length()>20) contentText=message.substring(0,19);
			else
				contentText=message;
		
		buildNotificationPM(getApplicationContext(), contentTitle, contentText,
				Utils.getUserBitmap(getApplicationContext(), sender), smallIcon, 
				xConv.getUnreadMessagesArray(sender), sender ,1,sender);
	}
	public static void buildNotificationPM(Context context,String contentTitle,
			String contentText,Bitmap largeIcon, int smallIcon,
			String[] bigText,String bigContentTitle,int notificationId,String sender)
	{

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
	    .setSmallIcon(smallIcon)
	    .setContentTitle(contentTitle)
	    .setContentText(contentText)
	    .setLargeIcon(largeIcon).setAutoCancel(true);
		
		Intent resultIntent = new Intent(context, PMActivity.class);
		resultIntent.putExtra("nickname", sender);
		resultIntent.putExtra("name", sender);
		resultIntent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(PMActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent =
		        stackBuilder.getPendingIntent(
		            0,
		            PendingIntent.FLAG_UPDATE_CURRENT
		        );


		mBuilder.setContentIntent(resultPendingIntent);

	NotificationCompat.InboxStyle inboxStyle =
	        new NotificationCompat.InboxStyle();
	// Sets a title for the Inbox style big view
	inboxStyle.setBigContentTitle(bigContentTitle);
	if(bigText!=null)
	for (int i=0; i < bigText.length; i++) {
		if(bigText[i].length()<20) 
	    inboxStyle.addLine(bigText[i]);
		else
			inboxStyle.addLine(bigText[i].substring(0, 18));
	}
	// Moves the big view style object into the notification object.
	mBuilder.setStyle(inboxStyle);

	NotificationManager mNotificationManager =
	(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	//mId allows you to update the notification later on.
	mNotificationManager.notify(notificationId, mBuilder.build());

	}

	public static void buildNotificationConversation(Context context,String contentTitle,
			String contentText,Bitmap largeIcon, int smallIcon,
			String[] bigText,String bigContentTitle,int notificationId)
	{

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
	    .setSmallIcon(smallIcon)
	    .setContentTitle(contentTitle)
	    .setContentText(contentText)
	    .setLargeIcon(largeIcon).setAutoCancel(true);
		
		Intent resultIntent = new Intent(context, ConversationsActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				resultIntent, 0);

		mBuilder.setContentIntent(contentIntent);

	NotificationCompat.InboxStyle inboxStyle =
	        new NotificationCompat.InboxStyle();
	// Sets a title for the Inbox style big view
	inboxStyle.setBigContentTitle(bigContentTitle);

	for (int i=0; i < bigText.length; i++) {
		if(bigText[i].length()<20) 
	    inboxStyle.addLine(bigText[i]);
		else
			inboxStyle.addLine(bigText[i].substring(0, 18));
	}
	// Moves the big view style object into the notification object.
	mBuilder.setStyle(inboxStyle);

	NotificationManager mNotificationManager =
	(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	//mId allows you to update the notification later on.
	mNotificationManager.notify(notificationId, mBuilder.build());

	}

	public static void buildNotificationRequest(Context context,String contentTitle,
			String contentText,Bitmap largeIcon, int smallIcon,
			String[] bigText,String bigContentTitle,int notificationId)
	{

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
	    .setSmallIcon(smallIcon)
	    .setContentTitle(contentTitle)
	    .setContentText(contentText)
	    .setLargeIcon(largeIcon).setAutoCancel(true);
		
		Intent resultIntent = new Intent(context, BuddyRequestsActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				resultIntent, 0);

		mBuilder.setContentIntent(contentIntent);

	NotificationCompat.InboxStyle inboxStyle =
	        new NotificationCompat.InboxStyle();
	// Sets a title for the Inbox style big view
	inboxStyle.setBigContentTitle(bigContentTitle);

	for (int i=0; i < bigText.length; i++) {
		if(bigText[i].length()<20) 
	    inboxStyle.addLine(bigText[i]);
		else
			inboxStyle.addLine(bigText[i].substring(0, 18));
	}
	// Moves the big view style object into the notification object.
	mBuilder.setStyle(inboxStyle);

	NotificationManager mNotificationManager =
	(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	//mId allows you to update the notification later on.
	mNotificationManager.notify(notificationId, mBuilder.build());

	}

    private void createPMNotification(String title,String msg,String sender) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent=new Intent(this,PMActivity.class);
        intent.putExtra("nickname", sender);
        intent.putExtra("name", sender);
        
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                intent, 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.ic_stat_gcm)
        .setContentTitle(title)
        .setStyle(new NotificationCompat.BigTextStyle()
        .bigText(msg))
        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
	    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
	    Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
	    r.play();

    }
    public void createROOMNotification(String title,String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, ChatRoom.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.xchatlogo3)
        .setContentTitle(title)
        .setStyle(new NotificationCompat.BigTextStyle()
        .bigText(msg))
        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID_CHAT, mBuilder.build());
    }

    private class SendMessageTask extends AsyncTask<String, Integer, Void> {

        @Override
        protected Void doInBackground(String... params) {
        	String response;
			try {
				List<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>(5);
				nameValuePairs.add(new BasicNameValuePair("to", params[0]));
				nameValuePairs.add(new BasicNameValuePair("message", params[1]));
				nameValuePairs.add(new BasicNameValuePair("fromusr", Utils.getLoggedNickName(getApplicationContext())));
				nameValuePairs.add(new BasicNameValuePair("fromid", (Utils.getLoggedUserId())));
				
				nameValuePairs.add(new BasicNameValuePair("AccessToken",Utils.ReadPreference(getApplicationContext(), "AccessToken")));
				nameValuePairs.add(new BasicNameValuePair("apikey", getString(R.string.apikey)));
				
				response=Utils.postData("sendpm", nameValuePairs);
				
				 Log.i(TAG, response);

			} 
			catch(Exception ex)
			{	 
				Log.e(TAG, ex.getMessage());

				
			}
            return null;
        }



        @Override
        protected void onPostExecute(Void res) {

        }

        @Override
        protected void onPreExecute() {
            // do something before execution
        }
    }
}

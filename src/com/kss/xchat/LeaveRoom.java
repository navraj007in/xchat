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
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class LeaveRoom extends Service {

    @Override
    public IBinder onBind(Intent intent) {
           // TODO: Return the communication channel to the service.
           throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
           // TODO Auto-generated method stub
    	super.onCreate();
          // Toast.makeText(getApplicationContext(), "Service Created.Leaving Chat Room", 1).show();
          // new LeaveRoomTask().execute((Void) null);
           
    }

    @Override
    public void onDestroy() {
           // TODO Auto-generated method stub
         //  Toast.makeText(getApplicationContext(), "Service Destroy", 1).show();
           super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
           // TODO Auto-generated method stub
          // Toast.makeText(getApplicationContext(), "Service Running ", 1).show();
    	Toast.makeText(getApplicationContext(), "Exiting Room", 1).show();
    	if(!ChatRoom.isXChatRunning)
           new LeaveRoomTask().execute((Void) null);
           return super.onStartCommand(intent, flags, startId);
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
					APIResponse aresponse = json;

					Log.i("LeaveRoom", "Left Room ");
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
		//	showProgress(false);
		}
	}
}

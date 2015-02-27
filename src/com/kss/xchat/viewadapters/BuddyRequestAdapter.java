package com.kss.xchat.viewadapters;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kss.xchat.APIResponse;
import com.kss.xchat.BuddyRequestsActivity;
import com.kss.xchat.ConversationsActivity;
import com.kss.xchat.R;
import com.kss.xchat.Utils;

import com.kss.xchat.apiresponse.BuddyRequest;
import com.kss.xchat.data.Profiles;


public class BuddyRequestAdapter extends ArrayAdapter<BuddyRequest>{
	private LayoutInflater mInflater;
    ArrayList<BuddyRequest> data;
    ImageView iv;
    RespondRequestsTask mRequestTask;
    public static BuddyRequestsActivity requestActivity;
    Context context;
    int position=0;
    public static String NickName,Response,Message;
	  public BuddyRequestAdapter(Context context, int resource,
              int textViewResourceId, ArrayList<BuddyRequest> objects) {               
super(context, resource, textViewResourceId, objects);
mInflater = (LayoutInflater) context.getSystemService(
Activity.LAYOUT_INFLATER_SERVICE);
data=objects;
this.context=context;
}

	 
		
      @Override
      public View getView(final int position, View convertView, ViewGroup parent) {   
         View vi=convertView;
     		if (convertView == null) {
    			vi = mInflater.inflate(R.layout.requestrow, null);
     		}
     		this.position=position;
            TextView txtNam=(TextView) vi.findViewById(R.id.txtName);
            if(data.get(position).Name!=null)
            txtNam.setText(data.get(position).Name+"("+data.get(position).NickName+")");
            else
            	txtNam.setText(data.get(position).NickName);
            Log.i("Request", data.get(position).NickName);
            TextView txtStatus=(TextView) vi.findViewById(R.id.txtStatus1);
            txtStatus.setText(data.get(position).Message);
            ImageView imgAccept=(ImageView)vi.findViewById(R.id.imgAccept);
            ImageView imgReject=(ImageView)vi.findViewById(R.id.imgReject);
            imgAccept.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(Utils.isNetworkAvailable(context)){
					Response="Yes";
					Message="Yes";
					NickName=data.get(position).NickName;
					mRequestTask=new RespondRequestsTask();
					mRequestTask.execute(NickName,Message,Response,null);
					
					Toast.makeText(context, "Request Accepted", Toast.LENGTH_SHORT).show();	
					}
					else
					{
			    		Toast.makeText(context, "Internet is not connected.", Toast.LENGTH_SHORT).show();
					}
				}
			});
            imgReject.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(Utils.isNetworkAvailable(context)){
					Response="No";
					Message="Sorry,Some other time.";
					NickName=data.get(position).NickName;
					mRequestTask=new RespondRequestsTask();
					mRequestTask.execute(NickName,Message,Response, null);

					Toast.makeText(context, "Request Rejected", Toast.LENGTH_SHORT).show();	
					}
					else
					{
			    		Toast.makeText(context, "Internet is not connected.", Toast.LENGTH_SHORT).show();
					}
				}
			});
         return vi;
}
  	public class RespondRequestsTask extends AsyncTask<String, Void, Boolean> {
  		String mResponse;
  		String mNickName;
  		@Override
        protected void onPreExecute(){
           super.onPreExecute();

		}
	
		@Override
		protected Boolean doInBackground(String... params) {
			// TODO: attempt authentication against a network service.
			String response;
			mResponse=params[2];
			mNickName=params[0];
			List<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("token", Utils.ReadPreference(context, "AccessToken")));
			nameValuePairs.add(new BasicNameValuePair("apikey", context.getString(R.string.apikey)));
			nameValuePairs.add(new BasicNameValuePair("NickName", mNickName));
			nameValuePairs.add(new BasicNameValuePair("Response", mResponse));
			nameValuePairs.add(new BasicNameValuePair("Message", params[1]));
			
			
			response=Utils.postData("replyBuddyRequest", nameValuePairs);
			
				/*response = Utils.RestCall(Utils.WebServiceURL+"replyBuddyRequest/?token="+
						Utils.ReadPreference(context, "AccessToken")+"&apikey="+context.getString(R.string.apikey)
						+"&NickName="+params[0]+"&Response="+params[2]+"&Message="+params[1]);*/
				 Log.i("Response","Requests List -"+ response);
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
			Toast.makeText(context, "Request Replied To.", Toast.LENGTH_SHORT).show();	
			try
			{
				if(mResponse.equalsIgnoreCase("yes"))
				{
					Profiles iProfile=new Profiles(context);
					iProfile.NickName=data.get(position).NickName;
					iProfile.Name=data.get(position).Name;
					iProfile.City=data.get(position).City;
					iProfile.NickName=data.get(position).Country;
					iProfile.Gender=data.get(position).Gender;
					iProfile.addRecord();
				}
				com.kss.xchat.data.BuddyRequest br=new com.kss.xchat.data.BuddyRequest(context);
				br.fromUser=mNickName;
				br.deleteRecord(br);
				data.remove(position);
				ConversationsActivity.requests.remove(position);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
			notifyDataSetChanged();
			if(data.size()==0) requestActivity.finish();

		}

		@Override
		protected void onCancelled() {
			mRequestTask = null;
		}
	}

}


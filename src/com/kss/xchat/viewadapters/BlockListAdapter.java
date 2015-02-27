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
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kss.xchat.APIResponse;
import com.kss.xchat.BuddyRequestsActivity;
import com.kss.xchat.R;
import com.kss.xchat.Utils;
import com.kss.xchat.data.Blocked;

public class BlockListAdapter extends ArrayAdapter<Blocked>{
	private LayoutInflater mInflater;
    ArrayList<Blocked> data;
    ImageView iv;
    UnblockTask mRequestTask;
    public static BuddyRequestsActivity requestActivity;
    Context context;
    public static String TAG="UNBLOCK";
    int position=0;
    public static String NickName,Response,Message;
	  public BlockListAdapter(Context context, int resource,
              int textViewResourceId, ArrayList<Blocked> objects) {               
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
    			vi = mInflater.inflate(R.layout.blockrow, null);
     		}
     		this.position=position;
            TextView txtNam=(TextView) vi.findViewById(R.id.txtName);
            	txtNam.setText(data.get(position).nickname);

            ImageView imgAccept=(ImageView)vi.findViewById(R.id.imgAccept);
            ImageView imgReject=(ImageView)vi.findViewById(R.id.imgReject);
            imgAccept.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Response="Yes";
					Message="Yes";
					NickName=data.get(position).nickname;
					Blocked iBlock=new Blocked(context);
					iBlock.nickname=data.get(position).nickname;
					iBlock.user=Utils.getLoggedNickName(context);
					Log.i(TAG, "Deleting "+ NickName+" ."+iBlock.getCount()+" Remaining");
					iBlock.unBlockUser();
					data.remove(position);
					notifyDataSetChanged();

					mRequestTask=new UnblockTask();
					mRequestTask.execute((Void) null);
					

				}
			});
            imgReject.setVisibility(View.GONE);
            return vi;
}
  	public class UnblockTask extends AsyncTask<Void, Void, Boolean> {
		@Override
        protected void onPreExecute(){
           super.onPreExecute();

		}
		APIResponse aresponse;
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.
			List<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("token", Utils.ReadPreference(context, "AccessToken")));
			nameValuePairs.add(new BasicNameValuePair("apikey", context.getString(R.string.apikey)));
			nameValuePairs.add(new BasicNameValuePair("NickName", NickName));
			
			String response=Utils.postData("unblockUser", nameValuePairs);
			
				/*response = Utils.RestCall(Utils.WebServiceURL+"unblockUser/?token="+
						Utils.ReadPreference(context, "AccessToken")+"&apikey="+context.getString(R.string.apikey)
						+"&NickName="+NickName);
*/
				ObjectMapper mapper = new ObjectMapper();

					try {
						aresponse= mapper.readValue(response, 
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

			if(aresponse.HttpResponse.equalsIgnoreCase("success"))
			{
			Toast.makeText(context, NickName+" Removed from Block List", Toast.LENGTH_SHORT).show();	
				
			}
			else
			{
				Toast.makeText(context, "Something bad happened.User could not be removed.", Toast.LENGTH_SHORT).show();
				
				
			}


		}

		@Override
		protected void onCancelled() {
			mRequestTask = null;
		}
	}
}

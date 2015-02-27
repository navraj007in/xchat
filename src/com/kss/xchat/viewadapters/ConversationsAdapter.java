package com.kss.xchat.viewadapters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kss.xchat.R;
import com.kss.xchat.Utils;
import com.kss.xchat.data.Conversations;
import com.kss.xchat.data.Profiles;
import com.kss.xchat.data.Roster;

public class ConversationsAdapter extends ArrayAdapter<Conversations>{
	private LayoutInflater mInflater;
    ArrayList<Conversations> data;
    ImageView iv;
    Context context;
    
	  public ConversationsAdapter(Context context, int resource,
              int textViewResourceId, ArrayList<Conversations> objects) {               
super(context, resource, textViewResourceId, objects);
mInflater = (LayoutInflater) context.getSystemService(
Activity.LAYOUT_INFLATER_SERVICE);
data=objects;
this.context=context;
}

	 
		
      @Override
      public View getView(int position, View convertView, ViewGroup parent) {   
         View vi=convertView;
     		if (convertView == null) {
    			vi = mInflater.inflate(R.layout.conversationsrow, null);


     		}
            TextView txtNam=(TextView) vi.findViewById(R.id.txtName);
            txtNam.setText(data.get(position).sender);
            TextView txtStatus=(TextView) vi.findViewById(R.id.txtStatus1);
            txtStatus.setText(data.get(position).Message);
            TextView lblUnread=(TextView) vi.findViewById(R.id.lblCount);
            if(data.get(position).unread==0) lblUnread.setVisibility(View.GONE);
            TextView lblTimeStamp=(TextView)vi.findViewById(R.id.lblTimeStamp);
            iv=(ImageView)vi.findViewById(R.id.imgRoster);
            try
            {
                lblTimeStamp.setText(Utils.parseTimeStamp(data.get(position).timeStamp));
            	
            }
            catch(Exception e)
            {
            	lblTimeStamp.setText(Utils.parseTimeStamp(Utils.getCurrentTimeStamp()));
            }
    		Conversations cAdap=new Conversations(context);
    		lblUnread.setText(String.valueOf(cAdap.getUnreadCount(data.get(position).sender)));
    		Profiles iProfile=new Profiles(context);
    		int i=iProfile.checkProfile(data.get(position).sender);
    		if(i>0) 
    		{
    			iProfile=iProfile.getProfile(data.get(position).sender);
                try
        		{
        			byte[] decodedString = Base64.decode(iProfile.ProfileImage, Base64.DEFAULT);
        			Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        			iv.setImageBitmap(decodedByte);
        			
        		}
        		catch(Exception ex)
        		{
        			ex.printStackTrace();
        		}

    		}
    		else
    		{
    			Roster iRoster=new Roster(context);
    			int j=iRoster.checkProfile(data.get(position).sender);
    			if(j>0)
    			{
    				iProfile=iRoster.getProfile(data.get(position).sender);

    				try
    	    		{
            			byte[] decodedString = Base64.decode(iProfile.ProfileImage, Base64.DEFAULT);
            			Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            			iv.setImageBitmap(decodedByte);
    	    		}
    	    		catch(Exception ex)
    	    		{
    	    			ex.printStackTrace();
    	    		}
    			}
    			else
    			{
    				new GetProfile().execute(data.get(position).sender);
    			}
    		}

         return vi;
}
  	class GetProfile extends AsyncTask<String, Void, Boolean>
	{
		String ProfileImage;
		@Override
		protected Boolean doInBackground(String... params) {
			// TODO Auto-generated method stub
			List<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>(3);
			nameValuePairs.add(new BasicNameValuePair("NickName",params[0]));
			nameValuePairs.add(new BasicNameValuePair("apikey", context.getString(R.string.apikey)));
			String response=Utils.postData("getProfile", nameValuePairs);
			ObjectMapper mapper = new ObjectMapper();
			com.kss.xchat.apiresponse.Roster userRoster;
						try {
							userRoster= mapper.readValue(response, 
									new TypeReference<com.kss.xchat.apiresponse.Roster>() {
							});
							Roster iProfile=new Roster(context);
							iProfile.NickName=userRoster.NickName;
							iProfile.Name=userRoster.Name;
							iProfile.City=userRoster.City;
							iProfile.State=userRoster.State;
							iProfile.Country=userRoster.Country;
							iProfile.Zip=userRoster.Zip;
							iProfile.ProfileImage=userRoster.ProfileImage;
							iProfile.Status=userRoster.Status;
							iProfile.addRecord();
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
			notifyDataSetChanged();
			
		}

	}

}


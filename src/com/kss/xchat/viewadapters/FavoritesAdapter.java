package com.kss.xchat.viewadapters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kss.xchat.PMActivity;
import com.kss.xchat.R;
import com.kss.xchat.Utils;
import com.kss.xchat.data.Favorites;
import com.kss.xchat.data.Profiles;
import com.kss.xchat.data.Roster;

public class FavoritesAdapter  extends ArrayAdapter<Favorites>{
	private LayoutInflater mInflater;
    ArrayList<Favorites> data;
    ImageView iv;
    Context context;

	  public FavoritesAdapter (Context context, int resource,
              int textViewResourceId, ArrayList<Favorites> objects) {               
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
    			vi = mInflater.inflate(R.layout.favoritesrow, null);


     		}
            TextView txtNam=(TextView) vi.findViewById(R.id.txtName);
            txtNam.setText(data.get(position).nickname);
            ImageView iv=(ImageView)vi.findViewById(R.id.imgRoster);
            ImageButton cmdDelete=(ImageButton)vi.findViewById(R.id.cmdDelete);
            cmdDelete.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Favorites iFav=new Favorites(context);
					iFav.nickname=data.get(position).nickname;
					iFav.user=Utils.getLoggedNickName(context);
					iFav.removeFromFavorites();
					data.remove(position);
					notifyDataSetChanged();
				}
			});

            txtNam.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
				Intent intent=new Intent(context,PMActivity.class);
				intent.putExtra("nickname", data.get(position).nickname);
				context.startActivity(intent);
				
				}
			});
            iv.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
				Intent intent=new Intent(context,PMActivity.class);
				intent.putExtra("nickname", data.get(position).nickname);
				intent.putExtra("name", data.get(position).nickname);

				context.startActivity(intent);
				
				}
			});
    		Profiles iProfile=new Profiles(context);
    		int i=iProfile.checkProfile(data.get(position).nickname);
    		if(i>0) 
    		{
    			iProfile=iProfile.getProfile(data.get(position).nickname);
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
    			int j=iRoster.checkProfile(data.get(position).nickname);
    			if(j>0)
    			{
    				iProfile=iRoster.getProfile(data.get(position).nickname);

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
    				new GetProfile().execute(data.get(position).nickname);
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


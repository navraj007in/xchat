package com.kss.xchat.viewadapters;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kss.xchat.ConversationsActivity;
import com.kss.xchat.PMActivity;
import com.kss.xchat.R;
import com.kss.xchat.Utils;
import com.kss.xchat.apiresponse.RecentlyJoined;

public class RecentlyJoinedAdapter extends ArrayAdapter<RecentlyJoined>{

	private LayoutInflater mInflater;
    ArrayList<RecentlyJoined> data;
    ImageView iv;
    Context context;

	  public RecentlyJoinedAdapter (Context context, int resource,
              int textViewResourceId, ArrayList<RecentlyJoined> objects) {               
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
    			vi = mInflater.inflate(R.layout.recentrow, null);
    		}
            TextView txtName=(TextView) vi.findViewById(R.id.lblName);
            TextView txtCountry=(TextView) vi.findViewById(R.id.lblCountry);
            ImageView imgUser=(ImageView)vi.findViewById(R.id.imgUser);
            
            try
    		{
    			byte[] decodedString = Base64.decode(data.get(position).ProfileImage, Base64.DEFAULT);
    			Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    			imgUser.setImageBitmap(decodedByte);
    			
    		}
    		catch(Exception ex)
    		{
    			ex.printStackTrace();
    		}
            TextView txtJoinDate=(TextView) vi.findViewById(R.id.lblJoinDate);
            txtCountry.setText(data.get(position).Country);
            txtCountry.setTextColor(Color.BLACK);
            txtName.setText(data.get(position).Name +" ("+ data.get(position).NickName+")");
            txtName.setTextColor(Color.BLACK);
            /*LinearLayout recentLayout=(LinearLayout)vi.findViewById(R.id.layoutRecent);
     		recentLayout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
			    	  Intent pmIntent=new Intent(context, PMActivity.class);
			    	  pmIntent.putExtra("nickname", data.get(position).NickName);
			    	  pmIntent.putExtra("name", data.get(position).Name);
			    	  context.startActivity(pmIntent);
				}
			});*/
            
            txtJoinDate.setText(Utils.parseRecentTimeStamp(data.get(position).DOJ));
            if(data.get(position).RoomName!=null) txtJoinDate.setText(txtJoinDate.getText().toString()+
            		"\nChat Room : "+ data.get(position).RoomName);
           txtJoinDate.setTextColor(Color.BLACK);


         return vi;
}

}

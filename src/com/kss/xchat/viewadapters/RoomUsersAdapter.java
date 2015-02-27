package com.kss.xchat.viewadapters;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kss.xchat.R;
import com.kss.xchat.apiresponse.RoomUsers;

public class RoomUsersAdapter extends ArrayAdapter<RoomUsers>{

	private LayoutInflater mInflater;
    ArrayList<RoomUsers> data;
    ImageView iv;
    Context context;

	  public RoomUsersAdapter (Context context, int resource,
              int textViewResourceId, ArrayList<RoomUsers> objects) {               
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
    			vi = mInflater.inflate(R.layout.roomrow, null);
    		}
            TextView txtCountry=(TextView) vi.findViewById(R.id.lblCountryName);
            Log.i("Room", data.get(position).Name);
            
            txtCountry.setText(data.get(position).Name);
            txtCountry.setTextColor(Color.BLACK);



         return vi;
}

}

package com.kss.xchat.viewadapters;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kss.xchat.R;
import com.kss.xchat.apiresponse.Room;

public class RoomsAdapter extends ArrayAdapter<Room>{
	private LayoutInflater mInflater;
    ArrayList<Room> data;
    ImageView iv;
    Context context;

	  public RoomsAdapter (Context context, int resource,
              int textViewResourceId, ArrayList<Room> objects) {               
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
            TextView txtUsersCount=(TextView)vi.findViewById(R.id.lblUsersCount);
            try
            {
                txtCountry.setText(data.get(position).RoomName);//+"("+String.valueOf(data.get(position).UsersCount)+")");
                txtCountry.setTextColor(Color.BLACK);
           	 txtUsersCount.setText(String.valueOf(data.get(position).UsersCount)+ " Users inside");
            }
            catch(Exception ex)
            {
           	 ex.printStackTrace();
            }

         return vi;
}

}


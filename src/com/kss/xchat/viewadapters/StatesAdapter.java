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
import com.kss.xchat.apiresponse.Countries;
import com.kss.xchat.apiresponse.States;

public class StatesAdapter extends ArrayAdapter<States>{
	private LayoutInflater mInflater;
    ArrayList<States> data;
    ImageView iv;
    Context context;

	  public StatesAdapter (Context context, int resource,
              int textViewResourceId, ArrayList<States> objects) {               
super(context, resource, textViewResourceId, objects);
mInflater = (LayoutInflater) context.getSystemService(
Activity.LAYOUT_INFLATER_SERVICE);
data=objects;
this.context=context;
}

	 
		
      @Override
      public View getView(int position, View convertView, ViewGroup parent) {   
         View vi=convertView;
//             vi = mInflater.inflate(R.layout., null);
     		if (convertView == null) {
    			vi = mInflater.inflate(R.layout.dashboard, null);


     		}
            TextView txtCountry=(TextView) vi.findViewById(R.id.txtHeading);
            
            txtCountry.setText(data.get(position).State1);
            txtCountry.setTextColor(Color.BLACK);



         return vi;
}

}


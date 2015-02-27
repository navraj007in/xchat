package com.kss.xchat.viewadapters;

import java.util.ArrayList;

import com.kss.xchat.R;
import com.kss.xchat.apiresponse.Countries;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class CountriesAdapter extends ArrayAdapter<Countries>{
	private LayoutInflater mInflater;
    ArrayList<Countries> data;
    ImageView iv;
    Context context;

	  public CountriesAdapter (Context context, int resource,
              int textViewResourceId, ArrayList<Countries> objects) {               
super(context, resource, textViewResourceId, objects);
mInflater = (LayoutInflater) context.getSystemService(
Activity.LAYOUT_INFLATER_SERVICE);
data=objects;
this.context=context;
}

	 
		
      @Override
      public View getView(int position, View convertView, ViewGroup parent) {   
         View vi=convertView;
         //    vi = mInflater.inflate(R.layout.categoryrow, null);
     		if (convertView == null) {
    			vi = mInflater.inflate(R.layout.categoryrow, null);


     		}

            TextView txtCountry=(TextView) vi.findViewById(R.id.lblCountryName);
            
            txtCountry.setText(data.get(position).Country1);
            txtCountry.setTextColor(Color.BLACK);


         return vi;
}

}

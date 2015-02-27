package com.kss.xchat.viewadapters;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kss.xchat.R;
import com.kss.xchat.apiresponse.Countries;

public class CategoriesAdapter extends ArrayAdapter<Countries>{
	public CategoriesAdapter(Context context, int resource) {
		super(context, resource);
		// TODO Auto-generated constructor stub
	}



	private LayoutInflater mInflater;
    ArrayList<Countries> data;
    ImageView iv;
    Context context;

	  public CategoriesAdapter(Context context, int resource,
              int textViewResourceId, ArrayList<Countries> objects) {               
super(context, resource, textViewResourceId, objects);
mInflater = (LayoutInflater) context.getSystemService(
Activity.LAYOUT_INFLATER_SERVICE);
data=objects;
this.context=context;
}

	 
		
	  @Override
	    public View getDropDownView(int position, View convertView,ViewGroup parent) {
	        return getCustomView(position, convertView, parent);
	    }
	 
	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	        return getCustomView(position, convertView, parent);
	    }      
	  public View getCustomView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		//return super.getView(position, convertView, parent);

	         View vi=convertView;
//             vi = mInflater.inflate(R.layout.categoryrow, null);
     		if (convertView == null) {
    			vi = mInflater.inflate(R.layout.categoryrow, null);

             TextView txtCountry=(TextView) vi.findViewById(R.id.lblCountryName);
             
             txtCountry.setText(data.get(position).Country1);
          //   txtCountry.setTextColor(Color.RED);

     		}



         return vi;
		}

      

}

package com.kss.xchat.viewadapters;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import com.kss.xchat.R;
import com.kss.xchat.data.Smiley;

public class SmileyAdapter extends ArrayAdapter<Smiley>{
	public SmileyAdapter(Context context, int resource) {
		super(context, resource);
		// TODO Auto-generated constructor stub
	}



	private LayoutInflater mInflater;
    ArrayList<Smiley> data;
    ImageView iv;
    Context context;

	  public SmileyAdapter(Context context, int resource,
              int textViewResourceId, ArrayList<Smiley> objects) {               
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
	         View vi=convertView;
     		if (convertView == null) {
    			vi = mInflater.inflate(R.layout.smileyrow, null);
             ImageView imgSmiley=(ImageView) vi.findViewById(R.id.imgSmiley);
             imgSmiley.setImageDrawable(context.getResources().getDrawable(data.get(position).smiley));
     		}



         return vi;
		}

      

}

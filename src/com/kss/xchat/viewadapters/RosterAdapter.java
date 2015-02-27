package com.kss.xchat.viewadapters;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.kss.xchat.R;
import com.kss.xchat.data.Profiles;

public class RosterAdapter extends ArrayAdapter<Profiles>{
	private LayoutInflater mInflater;
    ArrayList<Profiles> data;
    ImageView iv;
    Context context;

	  public RosterAdapter (Context context, int resource,
              int textViewResourceId, ArrayList<Profiles> objects) {               
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
    			vi = mInflater.inflate(R.layout.rosterrow, null);


     		}
            TextView txtNam=(TextView) vi.findViewById(R.id.txtName);
            txtNam.setText(data.get(position).Name+"-"+data.get(position).NickName);
            TextView txtStatus=(TextView) vi.findViewById(R.id.txtStatus1);
            txtStatus.setText("Available");
            ImageView iv=(ImageView)vi.findViewById(R.id.imgRoster);
			if(data.get(position).ProfileImage!=null)
            Log.i("Utils", data.get(position).ProfileImage);

            try
    		{
    			byte[] decodedString = Base64.decode(data.get(position).ProfileImage, Base64.DEFAULT);
    			Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
//    			Drawable d = new BitmapDrawable(context.getResources(),decodedByte);
    			iv.setImageBitmap(decodedByte);
    			
    		}
    		catch(Exception ex)
    		{
    			ex.printStackTrace();
    		}



         return vi;
}

}


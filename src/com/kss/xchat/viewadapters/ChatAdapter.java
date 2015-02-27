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
import com.kss.xchat.data.Conversations;

public class ChatAdapter extends ArrayAdapter<Conversations>{
	private LayoutInflater mInflater;
    ArrayList<Conversations> data;
    ImageView iv;
    Context context;
    public String User;
	  public ChatAdapter (Context context, int resource,
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
         //    vi = mInflater.inflate(R.layout.categoryrow, null);
     		if (convertView == null) {
    			vi = mInflater.inflate(R.layout.chatroomrow, null);
     		}
     		
            TextView txtSender=(TextView) vi.findViewById(R.id.txtSender);
            txtSender.setText(data.get(position).fromUser);
            txtSender.setTextColor(Color.BLUE);
            TextView txtMessage=(TextView)vi.findViewById(R.id.txtMessage);
            txtMessage.setText(data.get(position).Message);
            txtMessage.setTextColor(Color.BLACK);
            TextView txtTimestamp=(TextView)vi.findViewById(R.id.txtTimestamp);
            txtTimestamp.setText(data.get(position).timeStamp);
            TextView txtMyMessage=(TextView)vi.findViewById(R.id.txtMessage);
            if(!data.get(position).sender.equalsIgnoreCase(User))
            {
            	txtMyMessage.setText(data.get(position).Message);
            	
            }

         return vi;
}
public void remove(int position)
{
	data.remove(position);
	
}
}

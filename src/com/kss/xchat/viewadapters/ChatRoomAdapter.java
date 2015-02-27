package com.kss.xchat.viewadapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kss.xchat.PMActivity;
import com.kss.xchat.R;
import com.kss.xchat.Utils;

import com.kss.xchat.data.RoomChat;

public class ChatRoomAdapter extends ArrayAdapter<RoomChat>{
	private LayoutInflater mInflater;
    ArrayList<RoomChat> data;
    ImageView iv;
    Context context;
    LinearLayout layoutMessage;
	  public ChatRoomAdapter (Context context, int resource,
              int textViewResourceId, ArrayList<RoomChat> objects) {               
super(context, resource, textViewResourceId, objects);
mInflater = (LayoutInflater) context.getSystemService(
Activity.LAYOUT_INFLATER_SERVICE);
data=objects;
this.context=context;
}

	 
		
      @Override
      public View getView(final int position, View convertView, ViewGroup parent) {   
         View vi=convertView;
         //    vi = mInflater.inflate(R.layout.categoryrow, null);
     		if (convertView == null) {
    			vi = mInflater.inflate(R.layout.chatroomrow, null);


     		}

            TextView txtSender=(TextView) vi.findViewById(R.id.txtSender);
            TextView txtTimestamp=(TextView) vi.findViewById(R.id.txtTimestamp);
            
            txtSender.setText(data.get(position).sender);
            
//            txtSender.setTextColor(Color.BLACK);
            TextView txtMessage=(TextView)vi.findViewById(R.id.txtMessage);
            layoutMessage=(LinearLayout)vi.findViewById(R.id.layoutMessage);
//            layoutMessage.setBackground(context.getResources(R.drawable.rounded_corner));
          //  txtMessage.setText(data.get(position).Message);
			layoutMessage.setBackgroundResource(R.drawable.rounded_corner);

            txtMessage.setText(getSmiledText(context, data.get(position).Message));
            txtTimestamp.setText(Utils.parseTimeStamp(data.get(position).timeStamp));
            Linkify.addLinks(txtMessage, Linkify.ALL);

    		txtSender.setOnClickListener(new OnClickListener() {
    			
    			@Override
    			public void onClick(View v) {
    				// TODO Auto-generated method stub
    				Intent intent=new Intent(context,PMActivity.class);
    				intent.putExtra("nickname",data.get(position).sender);
    				intent.putExtra("name",data.get(position).sender);
    				
    				context.startActivity(intent);
    			}
    		});

//            if(data.get(position).sender.equalsIgnoreCase(Utils.getLoggedNickName(context)))
         return vi;
}
  	@SuppressWarnings("rawtypes")
	private static final HashMap<String, Integer> emoticons = new HashMap();
	static {
		emoticons.put(":)", R.drawable.smile);
	    emoticons.put(":-)", R.drawable.smile);
	    emoticons.put(":(", R.drawable.sad);
	    emoticons.put(":-(", R.drawable.sad);
	    emoticons.put(":D", R.drawable.broadsmile);
	    emoticons.put(":-D", R.drawable.broadsmile);
	    emoticons.put(";)", R.drawable.wink);
	    emoticons.put(";-)", R.drawable.wink);
	    emoticons.put(":'-(", R.drawable.cry);
	    emoticons.put(":-P", R.drawable.toungue);
	    emoticons.put("@};-", R.drawable.rose);
	    emoticons.put("<3", R.drawable.heart);


	}

	public static Spannable getSmiledText(Context context, String text) {

				SpannableStringBuilder builder = new SpannableStringBuilder(text);
				int index;

				for (index = 0; index < builder.length(); index++) {
				    for (Entry<String, Integer> entry : emoticons.entrySet()) {
				        int length = entry.getKey().length();
				        if (index + length > builder.length())
				            continue;
				        if (builder.subSequence(index, index + length).toString().equals(entry.getKey())) {
				            builder.setSpan(new ImageSpan(context, entry.getValue()), index, index + length,
				            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				            index += length - 1;
				            break;
				        }
				}
				}
				return builder;
				}

}

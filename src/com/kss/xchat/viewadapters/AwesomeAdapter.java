package com.kss.xchat.viewadapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import com.kss.xchat.R;
import com.kss.xchat.Utils;
import com.kss.xchat.data.Conversations;
import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
/**
 * AwesomeAdapter is a Custom class to implement custom row in ListView
 * 
 * @author Adil Soomro
 *
 */
public class AwesomeAdapter extends BaseAdapter{
	private Context mContext;
	private ArrayList<Conversations> mMessages;
	@SuppressWarnings({ "unchecked", "rawtypes" })
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
	//	Log.i("Smiley", text);
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
	public AwesomeAdapter(Context context, ArrayList<Conversations> messages) {
		super();
		this.mContext = context;
		this.mMessages = messages;
	}
	@Override
	public int getCount() {
		return mMessages.size();
	}
	@Override
	public Object getItem(int position) {		
		return mMessages.get(position);
	}
	@SuppressWarnings("deprecation")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Conversations message = (Conversations) this.getItem(position);

		ViewHolder holder; 
		if(convertView == null)
		{
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.sms_row, parent, false);
			holder.message = (TextView) convertView.findViewById(R.id.message_text);
			holder.timeStamp= (TextView) convertView.findViewById(R.id.lblTimeStamp);
			convertView.setTag(holder);
		}
		else
			holder = (ViewHolder) convertView.getTag();
		
		holder.message.setText(getSmiledText(mContext, message.Message));
		holder.imgSent=(ImageView)convertView.findViewById(R.id.imgSent);
		holder.layoutMessage=(RelativeLayout)convertView.findViewById(R.id.layoutMessage);
		LayoutParams lp = (LayoutParams) holder.layoutMessage.getLayoutParams();
		//check if it is a status message then remove background, and change text color.
		if(message.isStatusMessage())
		{
			holder.message.setBackgroundDrawable(null);
			lp.gravity = Gravity.LEFT;
			holder.message.setTextColor(R.color.textFieldColor);
		}
		else
		{		
			//Check whether message is mine to show green background and align to right
			if(message.sender.equalsIgnoreCase(message.fromUser))
			{
				holder.layoutMessage.setBackgroundResource(R.drawable.rounded_corner);
				lp.gravity = Gravity.RIGHT;
				
			//	lpTime.gravity = Gravity.RIGHT;

			}
			//If not mine then it is from sender to show orange background and align to left
			else
			{
				holder.layoutMessage.setBackgroundResource(R.drawable.roundedcorner);
				lp.gravity = Gravity.LEFT;
			//	lpTime.gravity = Gravity.LEFT;

			}
			holder.layoutMessage.setLayoutParams(lp);
			//holder.message.setTextColor(R.color.textColor);
			//holder.layoutTimestamp.setLayoutParams(lpTime);
			try
			{
                holder.timeStamp.setText(Utils.parseTimeStamp(message.timeStamp));
//				holder.timeStamp.setLayoutParams(lpTime);
			}
			catch(Exception ex)
			{
				holder.timeStamp.setText(message.timeStamp);
			//	holder.timeStamp.setLayoutParams(lpTime);
			
			}
			Linkify.addLinks(holder.message, Linkify.ALL);
		}
		Log.i("PMActivity", "Sent-"+message.sent);
		if(message.sent==1)
		{
			holder.imgSent.setVisibility(View.VISIBLE);
		}
		return convertView;
	}
	private static class ViewHolder
	{
		TextView message;
		TextView timeStamp;
		ImageView imgSent;
		RelativeLayout layoutMessage;
	}

	@Override
	public long getItemId(int position) {
		//Unimplemented, because we aren't using Sqlite.
		return position;
	}

}

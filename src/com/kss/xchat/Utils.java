package com.kss.xchat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.brickred.http.HttpHandler;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gcm.demo.app.GCMActivity;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.kss.xchat.data.Profiles;
import com.kss.xchat.data.Roster;
import com.kss.xchat.data.Smiley;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Base64;
import android.util.Log;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class Utils {
	public static String WebServiceURL="http://www.navraj.net/xchat/api/";
	public static String WebServicePOSTURL="https://www.navraj.net/xchat/rest/";

	public static String TAG="Utils";
	public static final int DEV_MODE_EMULATOR=1;
	public static final int DEV_MODE_DEVICE=2;
	public static int DEV_MODE=DEV_MODE_DEVICE;
    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";

    /**
     * Substitute you own sender ID here. This is the project number you got
     * from the API Console, as described in "Getting Started."
     */
    String SENDER_ID = "62717634560";
    public static String ADMOBID="ca-app-pub-9436114210349385/3817351351";
    public static String INTERSTITIALID="ca-app-pub-9436114210349385/5294084553";
    /**
     * Tag used on log messages.
     */



    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    Context context;
    String regid;

	public static String RestCall1(String iURL)
	{
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
		Log.i(TAG, "Starting URL-"+iURL);
		try {
		    HttpClient client = new DefaultHttpClient();  
		    String getURL = iURL;
		    HttpGet get = new HttpGet(getURL);
		    HttpResponse responseGet = client.execute(get);  
		    Log.i(TAG, "Got Entity");

		    HttpEntity resEntityGet = responseGet.getEntity();
		    if (resEntityGet != null) {  
		        // do something with the response
		        String response = EntityUtils.toString(resEntityGet);
		        Log.i(TAG, response);

		        return response;
		    }
		} catch (Exception e) {
			Log.i(TAG, e.getMessage());
		    e.printStackTrace();
		}
		return null;
	}
	
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		// if no network is available networkInfo will be null, otherwise check if we are connected
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		}
		return false;
	}
	public static String makeRestCallHTTPS(String iURL)
	{
		HttpHandler handler1 = new HttpHandler(iURL,null, null, 123);
		String response=handler1.makeRequest();

		return response;
	
	}
	public static String postData(String Suffix,List<NameValuePair> nameValuePairs) {
	    // Create a new HttpClient and Post Header
		
		HttpHandler handler1 = new HttpHandler(Utils.WebServicePOSTURL+Suffix,nameValuePairs, null, 123);
		String response=handler1.makeRequest();
		
		return response;
/*	    HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost(WebServicePOSTURL+Suffix);
	    
	    try {

	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	        // Execute HTTP Post Request
	        HttpResponse responseGet = httpclient.execute(httppost);
		    HttpEntity resEntityGet = responseGet.getEntity();
		    if (resEntityGet != null) {  
		        // do something with the response
		        String response = EntityUtils.toString(resEntityGet);
		        Log.i(TAG,"Post Response-"+ response);

		        return response;
		    }
		    else
		    {
		    	return "error:NULL";
		    }
	        
	    } catch (ClientProtocolException e) {
	    	e.printStackTrace();
	        // TODO Auto-generated catch block
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	    	e.printStackTrace();
	    }
	    catch(Exception e)
	    {
	    	e.printStackTrace();
	    }
    	return "error";
*/
	} 
	public static APIResponse makeRestCall(String iURL)
	{
		Log.i(TAG, "Starting URL-"+iURL);
		try {
		    HttpClient client = new DefaultHttpClient();  
		    String getURL = iURL;
		    HttpGet get = new HttpGet(getURL);
		    HttpResponse responseGet = client.execute(get);  
		    HttpEntity resEntityGet = responseGet.getEntity();  
		    if (resEntityGet != null) {  
		        // do something with the response
		        String response = EntityUtils.toString(resEntityGet);
		        Log.i(TAG, response);

		        return parseResponse(response);
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		}
		return null;
	}
	public static APIResponse parseResponse(String Response)
	{
		 ObjectMapper mapper = new ObjectMapper();

		try {
			APIResponse Json = mapper.readValue(Response, new TypeReference<APIResponse>() {
			});
			
			 Log.i("JSON","Parsing Successful");
			 Log.i("Response",Json.HttpResponse+"-"+Json.Token);
			 return Json;
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
}
	public static String parseTimeStamp(String mTimeStamp)
	{
		String todaysStamp=getCurrentTimeStamp();
		String todaysDate=todaysStamp.substring(0, 10);
		Log.i(TAG, todaysDate);
		String currentDate=mTimeStamp.substring(0, 10);
		Log.i(TAG, " Date-"+mTimeStamp);
		if(todaysDate.equalsIgnoreCase(currentDate)) return mTimeStamp.substring(11, 16);
		else 
		{
			String month=mTimeStamp.substring(5, 7);
			String day=mTimeStamp.substring(8, 10);
			String year=mTimeStamp.substring(0,4);
			
			String curYear=todaysStamp.substring(0, 4);
			String curMonth=todaysStamp.substring(5, 7);
			String curDay=todaysStamp.substring(8,10);
			if(curYear.equalsIgnoreCase(year)){
				 Calendar cal1 = new GregorianCalendar();
			        Calendar cal2 = new GregorianCalendar();
			        cal1.set(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day)); 
			        cal2.set(Integer.parseInt(curYear), Integer.parseInt(curMonth), Integer.parseInt(curDay));
//			        System.out.println("Days= "+daysBetween(cal1.getTime(),cal2.getTime()));
			        if(daysBetween(cal1.getTime(),cal2.getTime())==1)
			        {
			        	return "Yesterday";
			        }
			        else
			        {
						return day+"-"+month;

			        }
			}
			return day+"-"+month;
		}

	}
	
	public static String parseRecentTimeStamp(String mTimeStamp)
	{
		String todaysStamp=getCurrentTimeStamp();
		String todaysDate=todaysStamp.substring(0, 10);
		String currentDate=mTimeStamp.substring(0, 10);

		SimpleDateFormat sdf=new SimpleDateFormat("hh:mm");
		SimpleDateFormat outputFormat=new SimpleDateFormat("dd-MMM");
		if(todaysDate.equalsIgnoreCase(currentDate))
			return "Today ";
/*			try {
				//+sdf.parse(mTimeStamp.substring(11, 16)).toLocaleString();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				return mTimeStamp.substring(11, 16);
			}
*/		else 
		{
			String month=mTimeStamp.substring(5, 7);
			String day=mTimeStamp.substring(8, 10);
			String year=mTimeStamp.substring(0,4);
			
			String curYear=todaysStamp.substring(0, 4);
			String curMonth=todaysStamp.substring(5, 7);
			String curDay=todaysStamp.substring(8,10);
			if(curYear.equalsIgnoreCase(year)){
				 Calendar cal1 = new GregorianCalendar();
			        Calendar cal2 = new GregorianCalendar();
			        cal1.set(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day)); 
			        cal2.set(Integer.parseInt(curYear), Integer.parseInt(curMonth), Integer.parseInt(curDay));
//			        System.out.println("Days= "+daysBetween(cal1.getTime(),cal2.getTime()));
			        if(daysBetween(cal1.getTime(),cal2.getTime())==1)
			        {
			        	return "Yesterday";
			        }
			        else
			        {
			        	sdf=new SimpleDateFormat("yyyy-MM-dd");
						try {
							Date returnDate=sdf.parse(currentDate);
							
							return outputFormat.format(returnDate);
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							return day+"-"+month;
						}

			        }
			}
			return day+"-"+month;
		}

	}
	public static int daysBetween(Date d1, Date d2){
        return (int)( (d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
     }
	public static String getLoggedUserId()
	{
		return "1";
	}

	public static String getLoggedUser(Context context)
	{
		return ReadPreference(context,"loggedUser");
	}
	public static String getLoggedNickName(Context context)
	{
		return ReadPreference(context,"NickName");
	}
	public static String setLoggedUser(Context context,String userName)
	{
		return WritePreference(context, "loggedUser", userName);
	}
	
	public static String ReadPreference(Context context ,String prefName)
	{
		SharedPreferences pref = context.getSharedPreferences(GCMActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
		return pref.getString(prefName, "");
	}
	public static String WritePreference(Context context,String prefName
			,String value)
	{
		SharedPreferences pref = context.getSharedPreferences(GCMActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
        Editor editor = pref.edit();
        editor.putString(prefName, value);
        editor.commit();

		return "";
	}
	public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
              int count=is.read(bytes, 0, buffer_size);
              if(count==-1)
                  break;
              os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }	
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
	public static String GenerateRandom(int length)
	{
		char[] chars = "0123456789abcdefghijklmnopqrstuvwxyz_ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
		    char c = chars[random.nextInt(chars.length)];
		    sb.append(c);
		}
		String output = sb.toString();
		return output;

	}
	public static String BitmapToBase64(Bitmap bitmap)
	{
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();  
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
		byte[] byteArray = byteArrayOutputStream .toByteArray();
		return Base64.encodeToString(byteArray, Base64.DEFAULT);
	}
	public static String getCurrentTimeStamp(){
	    try {

	        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        String currentTimeStamp = dateFormat.format(new Date()); // Find todays date

	        return currentTimeStamp;
	    } catch (Exception e) {
	        e.printStackTrace();

	        return null;
	    }
	}
	public static String getDisplayableCurrentTimeStamp(){
	    try {

	        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	        String currentTimeStamp = dateFormat.format(new Date()); // Find todays date

	        return currentTimeStamp;
	    } catch (Exception e) {
	        e.printStackTrace();

	        return null;
	    }
	}
	public static ArrayList<Smiley> prepareSmileysList()
	{
		ArrayList<Smiley> smileysList=new ArrayList<Smiley>();
		Smiley happy=new Smiley(R.drawable.smile,":)");
		Smiley sad=new Smiley(R.drawable.sad,":(");
		Smiley wink=new Smiley(R.drawable.wink,";)");
		Smiley broadsmile=new Smiley(R.drawable.broadsmile,":D");

		Smiley toungue=new Smiley(R.drawable.toungue,":-P");
		Smiley cry=new Smiley(R.drawable.cry,":'-(");
		Smiley rose=new Smiley(R.drawable.rose,"@};-");
		Smiley heart=new Smiley(R.drawable.heart,"<3");

		smileysList.add(happy);
		smileysList.add(sad);
		smileysList.add(wink);
		smileysList.add(broadsmile);
		smileysList.add(toungue);
		smileysList.add(cry);
		smileysList.add(rose);
		smileysList.add(heart);

		return smileysList;
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static final HashMap<String, Integer> emoticons = new HashMap();
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
	static public String getStrPreference(Context context,String prefName)
	 {
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);			
			return preferences.getString(prefName,"");
		 
	 }
	static public boolean getBoolPreference(Context context,String prefName)
	 {
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);			
			return preferences.getBoolean(prefName,false);
		 
	 }
	public static void generateNoteOnSD(String sFileName, String sBody){
	    try
	    {
	        File root = new File(Environment.getExternalStorageDirectory(), "xChat");
	        if (!root.exists()) {
	            root.mkdirs();
	        }
	        File gpxfile = new File(root, sFileName);
	        FileWriter writer = new FileWriter(gpxfile);
	        writer.write(sBody);

	        writer.flush();
	        writer.close();

	    }
	    catch(IOException e)
	    {
	         e.printStackTrace();
	    }
	   }  
	public static Bitmap getUserBitmap(Context context,String nickname)
	{
		
		Profiles iProfile=new Profiles(context);

		int i=iProfile.checkProfile(nickname);
		if(i>0) 
		{
			iProfile=iProfile.getProfile(nickname);
            try
    		{
    			byte[] decodedString = Base64.decode(iProfile.ProfileImage, Base64.DEFAULT);
    			Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    			Drawable d = new BitmapDrawable(context.getResources(),decodedByte);
    			return decodedByte;
    		}
    		catch(Exception ex)
    		{
    			ex.printStackTrace();
    		}
		}
		else
		{
			Roster iRoster=new Roster(context);
			int j=iRoster.checkProfile(nickname);
			if(j>0)
			{
				iProfile=iRoster.getProfile(nickname);
	            try
	    		{
	    			byte[] decodedString = Base64.decode(iProfile.ProfileImage, Base64.DEFAULT);
	    			Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
	    			return decodedByte;
	    		}
	    		catch(Exception ex)
	    		{
	    			ex.printStackTrace();
	    		}
			}
			else
			{
			}
				
			
		}
		Drawable d=context.getResources().getDrawable(R.drawable.xchatlogo3);
		return convertToBitmap(d, 48, 48);

	}
	public static Bitmap convertToBitmap(Drawable drawable, int widthPixels, int heightPixels) {
	    Bitmap mutableBitmap = Bitmap.createBitmap(widthPixels, heightPixels, Bitmap.Config.ARGB_8888);
	    Canvas canvas = new Canvas(mutableBitmap);
	    drawable.setBounds(0, 0, widthPixels, heightPixels);
	    drawable.draw(canvas);

	    return mutableBitmap;
	}
	public static Spannable getSmiledText(Context context, String text) {
		Log.i("Smiley", text);
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
	public static void raiseNotification(Context context,String contentTitle,
			String contentText,Bitmap largeIcon, int smallIcon,
			String[] bigText,String bigContentTitle,int notificationId)
	{

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
	    .setSmallIcon(smallIcon)
	    .setContentTitle(contentTitle)
	    .setContentText(contentText)
	    .setLargeIcon(largeIcon);
		
		Intent resultIntent = new Intent(context, ConversationsActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				resultIntent, 0);

		mBuilder.setContentIntent(contentIntent);

	NotificationCompat.InboxStyle inboxStyle =
	        new NotificationCompat.InboxStyle();
	// Sets a title for the Inbox style big view
	inboxStyle.setBigContentTitle(bigContentTitle);

	for (int i=0; i < bigText.length; i++) {
		if(bigText[i].length()<20) 
	    inboxStyle.addLine(bigText[i]);
		else
			inboxStyle.addLine(bigText[i].substring(0, 18));
	}
	// Moves the big view style object into the notification object.
	mBuilder.setStyle(inboxStyle);

	NotificationManager mNotificationManager =
	(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	//mId allows you to update the notification later on.
	mNotificationManager.notify(notificationId, mBuilder.build());

	}
public static String convertDate(String date,String format1,String format2)
{
	SimpleDateFormat sourceFormat = new SimpleDateFormat(format1);
	SimpleDateFormat targetFormat = new SimpleDateFormat(format2);
	try {

	    String reformattedStr = targetFormat.format(sourceFormat.parse(date));
	    return reformattedStr;
	} catch (ParseException e) {
	    e.printStackTrace();
	}
	return date;
}
}

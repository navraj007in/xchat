package com.kss.xchat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kss.xchat.data.Profiles;
import com.kss.xchat.data.Roster;
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;

public class MyProfileActivity extends RootActivity {
	ImageView imgProfile;
	TextView lblName,lblGender,lblCity,lblState,lblStatus,lblCountry;
	public static String TAG="ProfileActivity";
	Profiles iProfile;
	String nickname;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_profile);
		lblName=(TextView)findViewById(R.id.lblName);
		lblGender=(TextView)findViewById(R.id.lblGender);
		lblCity=(TextView)findViewById(R.id.lblCity);
		lblState=(TextView)findViewById(R.id.lblState);
		lblCountry=(TextView)findViewById(R.id.lblCountry);
		lblStatus=(TextView)findViewById(R.id.lblStatus);

		lblName.setText(Utils.ReadPreference(getApplicationContext(), "Name"));
		lblGender.setText(Utils.ReadPreference(getApplicationContext(), "Gender"));
		lblCity.setText(Utils.ReadPreference(getApplicationContext(), "City"));
		lblState.setText(Utils.ReadPreference(getApplicationContext(), "State"));
		lblCountry.setText(Utils.ReadPreference(getApplicationContext(), "Country"));
		lblStatus.setText(Utils.ReadPreference(getApplicationContext(), "Status"));
		
		imgProfile=(ImageView)findViewById(R.id.imgProfile);
		imgProfile.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				//selectImage();
				return false;
			}
		});
		Intent intent=getIntent();
		nickname=intent.getStringExtra("nickname");
		iProfile=new Profiles(getApplicationContext());
		iProfile.ProfileImage=Utils.ReadPreference(getApplicationContext(), "ProfileImage");
		if(iProfile.ProfileImage!=null){
			byte[] decodedString = Base64.decode(iProfile.ProfileImage, Base64.DEFAULT);
			Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
			Drawable d = new BitmapDrawable(getResources(),decodedByte);
			getActionBar().setIcon(d);
			imgProfile.setImageDrawable(d);
			}
		
		//new GetProfile().execute((Void) null);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.action_edit:
	    	Intent intent=new Intent (MyProfileActivity.this, EditProfileActivity.class);
	    	startActivity(intent);
	    	return true;
	    default:
            return super.onOptionsItemSelected(item);
	    }
	}
	class GetProfile extends AsyncTask<Void, Void, Boolean>
	{
		String ProfileImage;
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try
			{
				List<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>(3);
				nameValuePairs.add(new BasicNameValuePair("NickName",nickname));
				nameValuePairs.add(new BasicNameValuePair("apikey", getString(R.string.apikey)));
				String response=Utils.postData("getProfile", nameValuePairs);
				Log.i(TAG, response);
				ObjectMapper mapper = new ObjectMapper();
				com.kss.xchat.apiresponse.Roster userRoster;
							try {
								userRoster= mapper.readValue(response, 
										new TypeReference<com.kss.xchat.apiresponse.Roster>() {
								});
								Roster iProfile=new Roster(getApplicationContext());
								iProfile.NickName=userRoster.NickName;
								iProfile.Name=userRoster.Name;
								iProfile.City=userRoster.City;
								iProfile.State=userRoster.State;
								iProfile.Country=userRoster.Country;
								iProfile.Zip=userRoster.Zip;
								iProfile.ProfileImage=userRoster.ProfileImage;
								iProfile.Status=userRoster.Status;
								iProfile.addRecord();
								ProfileImage=userRoster.ProfileImage;

								Log.i(TAG,response);
							} catch (JsonParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (JsonMappingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
				
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return null;
		}
		@Override
        protected void onPreExecute(){
           super.onPreExecute();
           getActionBar().setTitle("Loading "+ nickname+" 's profile..");
        }
		@Override
		protected void onPostExecute(final Boolean success) {
			super.onPostExecute(success);
			try
    		{
				if(ProfileImage!=null){
    			byte[] decodedString = Base64.decode(ProfileImage, Base64.DEFAULT);
    			Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    			Drawable d = new BitmapDrawable(getResources(),decodedByte);
    			getActionBar().setIcon(d);
    			imgProfile.setImageDrawable(d);
				}
    			lblName.setText(iProfile.Name);
    			lblGender.setText(iProfile.Gender);
    			lblState.setText(iProfile.State);
    			lblStatus.setText(iProfile.Status);
    			lblCity.setText(iProfile.City);
    			lblCountry.setText(iProfile.Country);
    			getActionBar().setTitle(iProfile.NickName+ " 's Profile");

    			
    		}
    		catch(Exception ex)
    		{
    			ex.printStackTrace();
    		}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.my_profile, menu);
		return true;
	}

	@Override
	public void onMPResume() {
		// TODO Auto-generated method stub
		
	}

}

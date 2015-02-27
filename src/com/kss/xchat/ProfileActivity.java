package com.kss.xchat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.content.Intent;
import android.database.Cursor;
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

public class ProfileActivity extends RootActivity {
ImageView imgProfile;
TextView lblName,lblGender,lblCity,lblState,lblStatus,lblCountry;
public static String TAG="ProfileActivity";
public static String ProfileImage;
Profiles iProfile;
String nickname;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		lblName=(TextView)findViewById(R.id.lblName);
		lblGender=(TextView)findViewById(R.id.lblGender);
		lblCity=(TextView)findViewById(R.id.lblCity);
		lblState=(TextView)findViewById(R.id.lblState);
		lblCountry=(TextView)findViewById(R.id.lblCountry);
		lblStatus=(TextView)findViewById(R.id.lblStatus);

		imgProfile=(ImageView)findViewById(R.id.imgProfile);
		imgProfile.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				//selectImage();
				Intent intent=new Intent(ProfileActivity.this,ViewProfileImageActivity.class);
				startActivity(intent);
				
				return false;
			}
		});
		Intent intent=getIntent();
		nickname=intent.getStringExtra("nickname");
		iProfile=new Profiles(getApplicationContext());

		int i=iProfile.checkProfile(nickname);
		if(i>0) 
		{
			iProfile=iProfile.getProfile(nickname);
			getActionBar().setTitle(iProfile.NickName+ " 's Profile");
			if(iProfile.Name!=null)
			lblName.setText(iProfile.Name);
			lblGender.setText(iProfile.Gender);
			lblState.setText(iProfile.State);
			lblStatus.setText(iProfile.Status);
			lblCity.setText(iProfile.City);
			lblCountry.setText(iProfile.Country);
			Log.e(TAG, iProfile.ProfileImage);
			try
    		{
    			byte[] decodedString = Base64.decode(iProfile.ProfileImage, Base64.DEFAULT);
    			Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    			Drawable d = new BitmapDrawable(getResources(),decodedByte);
    			imgProfile.setImageDrawable(d);
    			getActionBar().setIcon(d);
    			ProfileImage=iProfile.ProfileImage;
    			
    		}
    		catch(Exception ex)
    		{
    			ex.printStackTrace();
    		}
		}
		else
		{
			Roster iRoster=new Roster(getApplicationContext());
			int j=iRoster.checkProfile(nickname);
			if(j>0)
			{
				iProfile=iRoster.getProfile(nickname);
				getActionBar().setTitle(iProfile.NickName+ " 's Profile");

				try
	    		{
	    			byte[] decodedString = Base64.decode(iProfile.ProfileImage, Base64.DEFAULT);
	    			Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
	    			Drawable d = new BitmapDrawable(getResources(),decodedByte);
	    			imgProfile.setImageBitmap(decodedByte);
	    			getActionBar().setIcon(d);
	    			imgProfile.setImageDrawable(d);
	    			lblName.setText(iProfile.Name);
	    			lblGender.setText(iProfile.Gender);
	    			lblState.setText(iProfile.State);
	    			lblStatus.setText(iProfile.Status);
	    			lblCity.setText(iProfile.City);
	    			lblCountry.setText(iProfile.Country);
	    			ProfileImage=iProfile.ProfileImage;
	    			
	    		}
	    		catch(Exception ex)
	    		{
	    			ex.printStackTrace();
	    		}
			}
			else
				new GetProfile().execute((Void) null);
			
		}

		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.profile, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.action_refresh:
	    	new GetProfile().execute((Void) null);
	    	return true;
	    default:
            return super.onOptionsItemSelected(item);
	    }
	}
	class GetProfile extends AsyncTask<Void, Void, Boolean>
	{
		String uProfileImage;
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
				ArrayList<com.kss.xchat.apiresponse.Roster> userRoster;
							try {
								userRoster= mapper.readValue(response, 
										new TypeReference<ArrayList<com.kss.xchat.apiresponse.Roster>>() {
								});
								
								Profiles iProfile=new Profiles(getApplicationContext());
								iProfile.removeProfile(userRoster.get(0).NickName);
								iProfile.NickName=userRoster.get(0).NickName;
								iProfile.Name=userRoster.get(0).Name;
								iProfile.City=userRoster.get(0).City;
								iProfile.State=userRoster.get(0).State;
								iProfile.Country=userRoster.get(0).Country;
								iProfile.Zip=userRoster.get(0).Zip;
								iProfile.ProfileImage=userRoster.get(0).ProfileImage;
								iProfile.Status=userRoster.get(0).Status;
								iProfile.addRecord();

								Roster iRoster=new Roster(getApplicationContext());
								iRoster.deleteRecord(userRoster.get(0).NickName);
								iRoster.removeProfile(userRoster.get(0).NickName);
								iRoster.NickName=userRoster.get(0).NickName;
								iRoster.Name=userRoster.get(0).Name;
								iRoster.City=userRoster.get(0).City;
								iRoster.State=userRoster.get(0).State;
								iRoster.Country=userRoster.get(0).Country;
								iRoster.Zip=userRoster.get(0).Zip;
								iRoster.ProfileImage=userRoster.get(0).ProfileImage;
								iRoster.Status=userRoster.get(0).Status;
								iRoster.addRecord();
								uProfileImage=userRoster.get(0).ProfileImage;

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
				lblName.setText(iProfile.Name);
    			lblGender.setText(iProfile.Gender);
    			lblState.setText(iProfile.State);
    			lblStatus.setText(iProfile.Status);
    			lblCity.setText(iProfile.City);
    			lblCountry.setText(iProfile.Country);
    			getActionBar().setTitle(iProfile.NickName+ " 's Profile");
    			byte[] decodedString = Base64.decode(uProfileImage, Base64.DEFAULT);
    			Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    			Drawable d = new BitmapDrawable(getResources(),decodedByte);
    			getActionBar().setIcon(d);
    			imgProfile.setImageDrawable(d);
    			
    			
    			ProfileImage=uProfileImage;

    			
    		}
    		catch(Exception ex)
    		{
    			ex.printStackTrace();
    		}
		}
	}

	
	@Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                File f = new File(Environment.getExternalStorageDirectory().toString());
                for (File temp : f.listFiles()) {
                    if (temp.getName().equals("temp.jpg")) {
                        f = temp;
                        break;
                    }
                }
                try {
                    Bitmap bitmap;
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                    bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),
                            bitmapOptions); 
                    imgProfile.setImageBitmap(bitmap);

                    String path = android.os.Environment
                            .getExternalStorageDirectory()
                            + File.separator
                            + "Phoenix" + File.separator + "default";
                    f.delete();
                    OutputStream outFile = null;
                    File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
                    try {
                        outFile = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
                        outFile.flush();
                        outFile.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {

                    e.printStackTrace();

                }

            } else if (requestCode == 2) {

            	Uri selectedImage = data.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor c = getContentResolver().query(selectedImage,filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                Log.w("path of image from gallery......******************.........", picturePath+"");

                imgProfile.setImageBitmap(thumbnail);

            }

        }

    }
	@Override
	public void onMPResume() {
		// TODO Auto-generated method stub
		
	}    
	
}

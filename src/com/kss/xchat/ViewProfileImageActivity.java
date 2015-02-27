package com.kss.xchat;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.view.Menu;
import android.widget.ImageView;

public class ViewProfileImageActivity extends Activity {
ImageView img;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_profile_image);
		img=(ImageView)findViewById(R.id.img);
		try
		{
			String profileImage=ProfileActivity.ProfileImage;
			byte[] decodedString = Base64.decode(profileImage, Base64.DEFAULT);
			Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
			Drawable d = new BitmapDrawable(getResources(),decodedByte);
			img.setImageDrawable(d);

			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_profile_image, menu);
		return true;
	}

}

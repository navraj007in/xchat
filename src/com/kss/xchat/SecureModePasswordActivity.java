package com.kss.xchat;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class SecureModePasswordActivity extends Activity {
	EditText txtOldPassword;
	EditText txtNewPassword;
	EditText txtConfirmPassword;
	String mSecureModePassword;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_secure_mode_password);
		txtOldPassword=(EditText)findViewById(R.id.txtOldPassword);
		txtNewPassword=(EditText)findViewById(R.id.txtNewPassword);
		txtConfirmPassword=(EditText)findViewById(R.id.txtConfirmPassword);
		mSecureModePassword=Utils.ReadPreference(getApplicationContext(), "secureModePassword");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.secure_mode_password, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		try
		{
		    switch (item.getItemId()) {
		    case R.id.action_save:
		    	if(mSecureModePassword.equalsIgnoreCase(txtOldPassword.getText().toString()))
		    	{
		    		if(txtNewPassword.getText().toString().length()==0)
		    		{
			    		Toast.makeText(getApplicationContext(), "Password can not be blank!!",Toast.LENGTH_SHORT).show();
		    			
		    		}
		    		else if(txtNewPassword.getText().toString().equalsIgnoreCase(txtConfirmPassword.getText().toString()))
		    		{
		    			Utils.WritePreference(getApplicationContext(), "secureModePassword", txtNewPassword.getText().toString());
			    		Toast.makeText(getApplicationContext(), "Password Changed Successfully!!",Toast.LENGTH_SHORT).show();
			    		finish();
		    		}
		    		else
		    		{
			    		Toast.makeText(getApplicationContext(), "Passwords do not match!!",Toast.LENGTH_SHORT).show();
		    			
		    		}
		    	}
		    	else
		    	{
		    		Toast.makeText(getApplicationContext(), "Incorrect Old Password!!",Toast.LENGTH_SHORT).show();
		    	}
		    	return true;
		        default:
		            return super.onOptionsItemSelected(item);
		    }
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return true;
	}
}

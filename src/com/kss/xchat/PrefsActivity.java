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
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

public class PrefsActivity extends PreferenceActivity{
	int onStartCount = 0;
	public String TAG="PrefsActivity";
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	   super.onCreate(savedInstanceState);
	   addPreferencesFromResource(R.layout.prefs);
       onStartCount = 1;
       if (savedInstanceState == null) // 1st time
       {
           this.overridePendingTransition(R.anim.anim_slide_in_left,
                   R.anim.anim_slide_out_left);
       } else // already created so reverse animation
       { 
           onStartCount = 2;
       }

	   Preference fooBarPref = (Preference) findPreference("keyProfile");
	   fooBarPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
		
		@Override
		public boolean onPreferenceClick(Preference preference) {
			// TODO Auto-generated method stub
			Intent intent=new Intent(PrefsActivity.this,MyProfileActivity.class);
			intent.putExtra("nickname", Utils.getLoggedNickName(getApplicationContext()));
			startActivity(intent);
			return false;
		}
	});
	   Preference securePref=(Preference)findPreference("keySecureMode");
	   securePref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
		
		@Override
		public boolean onPreferenceClick(Preference preference) {
			// TODO Auto-generated method stub
		Intent intent=new Intent(PrefsActivity.this,SecureModePasswordActivity.class);
		startActivity(intent);
			return false;
		}
	});
	   Preference tosPref=(Preference)findPreference("keyTOS");
	   tosPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
		
		@Override
		public boolean onPreferenceClick(Preference preference) {
			// TODO Auto-generated method stub
		Intent intent=new Intent(PrefsActivity.this,TOSActivity.class);
		startActivity(intent);
			return false;
		}
	});
	   Preference blockPref=(Preference)findPreference("keyBlockList");
	   blockPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
		
		@Override
		public boolean onPreferenceClick(Preference preference) {
			// TODO Auto-generated method stub
			Intent intent=new Intent(PrefsActivity.this,BlockedActivity.class);
			startActivity(intent);
			
			return false;
		}
	});
	   Preference statusPref=(Preference)findPreference("keyStatus");
	   statusPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
		
		@Override
		public boolean onPreferenceClick(Preference preference) {
			// TODO Auto-generated method stub
	    	AlertDialog.Builder alert = new AlertDialog.Builder(PrefsActivity.this);

	    	alert.setTitle("Status");
	    	alert.setMessage("Update xChat Status");

	    	// Set an EditText view to get user input 
	    	final EditText input = new EditText(PrefsActivity.this);
	    	input.setText(Utils.ReadPreference(getApplicationContext(), "keyStatus"));
	    	alert.setView(input);

	    	alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	    	public void onClick(DialogInterface dialog, int whichButton) {
	    	  String value = input.getText().toString().trim();
	    	  // Do something with value!
	    	  Utils.WritePreference(getApplicationContext(), "keyStatus", value);
				new UpdateStatusTask().execute(value);

	    	  }
	    	});

	    	alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	    	  public void onClick(DialogInterface dialog, int whichButton) {
	    	    // Canceled.
	    	  }
	    	});

	    	alert.show();
			return false;
		}
	});
	   
	}
	@Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        if (onStartCount > 1) {
            this.overridePendingTransition(R.anim.anim_slide_in_right,
                    R.anim.anim_slide_out_right);

        } else if (onStartCount == 1) {
            onStartCount++;
        }

    }
	private class UpdateStatusTask extends AsyncTask<String, Void, Boolean> {
		APIResponse aresponse;
		@Override
        protected void onPreExecute(){
           super.onPreExecute();

        }
		
		@Override
		protected Boolean doInBackground(String... params) {
			// TODO: attempt authentication against a network service.
		//	user.NickName="nvskumar";
			try{
			List<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>(3);
			nameValuePairs.add(new BasicNameValuePair("token", Utils.ReadPreference(getApplicationContext(), "AccessToken")));
			nameValuePairs.add(new BasicNameValuePair("apikey", getString(R.string.apikey)));
			nameValuePairs.add(new BasicNameValuePair("status",params[0]));
			
			String response=Utils.postData("updateStatus", nameValuePairs);

			ObjectMapper mapper = new ObjectMapper();

			try {
				aresponse = mapper.readValue(response, 
						new TypeReference<APIResponse>() {
				});

				
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
		 Log.i(TAG, response);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		
			// TODO: register the new account here.
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
		try{
		if(aresponse.HttpResponse.equalsIgnoreCase("success"))
		{
			Toast.makeText(getApplicationContext(), "Status Changed Successfully", Toast.LENGTH_SHORT).show();
		}
		}
		catch(Exception e){
			e.printStackTrace();
		}
				
		}

		@Override
		protected void onCancelled() {
		//	showProgress(false);
		}
	}

}

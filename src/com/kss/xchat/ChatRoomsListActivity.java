package com.kss.xchat;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.kss.xchat.apiresponse.Countries;
import com.kss.xchat.apiresponse.States;
import com.kss.xchat.viewadapters.StatesAdapter;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class ChatRoomsListActivity extends RootActivity implements
ActionBar.OnNavigationListener,OnItemClickListener
{
	private GetCountriesTask mAuthTask = null;
	private GetCategoriesTask mCategoryTask=null;
	ListView lstCountries;
	Spinner spinCategories;
	Countries[] data ;
	States[] statedata;
	String[] name;
	String[] states;
	int[] id;
	int roomMode=0;
	CategoryClickListener spinListener=new CategoryClickListener();
	boolean mTwoPane;
	public static String TAG="RoomsList";
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	ArrayList<Countries> cList=new ArrayList<Countries>();
	ArrayList<States> sList=new ArrayList<States>();
	 private AdView adView;
	 private ProgressDialog pd;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat_rooms_list);

		loadAd();
		if (findViewById(R.id.item_detail_container) != null) {
			mTwoPane = true;
			Log.i(TAG, "Its a tablet");
		}
		else
		{
			Log.i(TAG, "Its a Phone");
		}

		lstCountries =(ListView)findViewById(R.id.lstCountries);
		lstCountries.setOnItemClickListener(this);
		spinCategories=(Spinner)findViewById(R.id.spinCountries);
		spinCategories.setOnItemSelectedListener(spinListener);
		try{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			final ActionBar actionBar = getActionBar();
			actionBar.setDisplayShowTitleEnabled(false);
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

			actionBar.setListNavigationCallbacks(
					new ArrayAdapter<String>(getActionBarThemedContextCompat(),
							android.R.layout.simple_list_item_1,
							android.R.id.text1, new String[] {
									getString(R.string.title_rooms_by_country),
									getString(R.string.title_rooms_by_category),
									 }), this);

		}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * Backward-compatible version of {@link ActionBar#getThemedContext()} that
	 * simply returns the {@link android.app.Activity} if
	 * <code>getThemedContext</code> is unavailable.
	 */
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private Context getActionBarThemedContextCompat() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			return getActionBar().getThemedContext();
		} else {
			return this;
		}
	}
	@Override
	public void onPause() {
	    super.onPause();
	    try{
	    if ((pd != null) && pd.isShowing())
	        pd.dismiss();
	    pd = null;
	    }
	    catch(Exception e)
	    {
	    	e.printStackTrace();
	    }
	
	    
	}
	private void loadAd()
	{
		adView = new AdView(this);
		 adView.setAdSize(AdSize.SMART_BANNER);
	    adView.setAdUnitId(Utils.ADMOBID);

	    // Add the AdView to the view hierarchy. The view will have no size
	    // until the ad is loaded.
	    LinearLayout layout = (LinearLayout) findViewById(R.id.adLayout);
	    layout.addView(adView);

	    // Create an ad request. Check logcat output for the hashed device ID to
	    // get test ads on a physical device.
	    AdRequest adRequest = new AdRequest.Builder()
	        .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
	        .addTestDevice("INSERT_YOUR_HASHED_DEVICE_ID_HERE")
	        .build();

	    // Start loading the ad in the background.
	    adView.loadAd(adRequest);

	}
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current dropdown position.
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current dropdown position.
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar()
				.getSelectedNavigationIndex());
	}


	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.chat_rooms_list, menu);
		return true;
	}*/

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		// TODO Auto-generated method stub
		if(mTwoPane)
		{
			if(Utils.isNetworkAvailable(getApplicationContext())){
			Intent intent=new Intent(ChatRoomsListActivity.this,
					RoomsListActivity.class);
			States selState=sList.get(position);
			if(roomMode==0) 
				{
				intent.putExtra("mode", 0);
				}
			else {
				intent.putExtra("mode", 1);
			}
			Log.i("Room", String.valueOf(selState.ID));
			intent.putExtra("id", String.valueOf(selState.ID));
			intent.putExtra("name",selState.State1 );
			startActivity(intent);
			}
			else
			{
				Toast.makeText(getApplicationContext(), "Internet is not connected!!", Toast.LENGTH_SHORT).show();
			}
		}
		else
		{
			if(Utils.isNetworkAvailable(getApplicationContext())){
				Intent intent=new Intent(ChatRoomsListActivity.this,
						RoomsListActivity.class);
				States selState=sList.get(position);
				if(roomMode==0) 
					{
					intent.putExtra("mode", 0);
					}
				else {
					intent.putExtra("mode", 1);
				}
				Log.i("Room", String.valueOf(selState.ID));
				intent.putExtra("id", String.valueOf(selState.ID));
				intent.putExtra("name",selState.State1 );
				startActivity(intent);
			}
			else
			{
				Toast.makeText(getApplicationContext(), "Internet is not connected!!", Toast.LENGTH_SHORT).show();
			}
			
		}
	}

	@Override
	public boolean onNavigationItemSelected(int position, long id) {
		// TODO Auto-generated method stub
		roomMode=position;
		if(position==0)
		{
	    	boolean isNetworkAvailable=Utils.isNetworkAvailable(getApplicationContext());
	    	if(isNetworkAvailable){
			Log.i(TAG, "Loading Countries List");
			mAuthTask = new GetCountriesTask();
			mAuthTask.execute((Void) null);
	    	}	
	    	else{
	    		Toast.makeText(getApplicationContext(), "Internet is not connected!!", Toast.LENGTH_SHORT).show();
	    	}

		}
		if(position==1)
		{
	    	boolean isNetworkAvailable=Utils.isNetworkAvailable(getApplicationContext());
	    	if(isNetworkAvailable){
			Log.i(TAG, "Loading Categories List");
			mCategoryTask = new GetCategoriesTask();
			mCategoryTask.execute((Void) null);
	    	}
	    	else{
	    		Toast.makeText(getApplicationContext(), "Internet is not connected!!", Toast.LENGTH_SHORT).show();
	    	}

		}

		return false;
	}
	private class CategoryClickListener implements OnItemSelectedListener
	{
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			// TODO Auto-generated method stub
	    	boolean isNetworkAvailable=Utils.isNetworkAvailable(getApplicationContext());
	    	if(isNetworkAvailable){
			if(roomMode==0)
			{
			Log.i(TAG,"Loading States");
			GetStatesTask stateTask = new GetStatesTask();
			stateTask.execute(String.valueOf(id[position]));
			}
			else
			{
				Log.i(TAG,"Loading Sub Categories");
				GetSubcategoriesTask subCategoryTask = new GetSubcategoriesTask();
				subCategoryTask.execute(String.valueOf(id[position]));
			}
	    	}
	    	else{
	    		Toast.makeText(getApplicationContext(), "Internet is not connected!!", Toast.LENGTH_SHORT).show();
	    	}

	    	
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
	public class GetCountriesTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.
			try{
				List<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>(1);
				nameValuePairs.add(new BasicNameValuePair("apikey", getString(R.string.apikey)));
				String response=Utils.postData("getCountries", nameValuePairs);
				
				Log.i(TAG, "Post Response for api call "+ response);


	/*			String response=Utils.RestCall(Utils.WebServiceURL+"getCountries" 
						+"/?apikey="+getString(R.string.apikey));
	*/
				 Gson gson = new Gson();
				 data= gson.fromJson(response, Countries[].class);
				 cList=new ArrayList<Countries>();
					name=new String[data.length];
					id=new int[data.length];

				for(int i=0;i<data.length;i++)
				{
					Log.i(TAG,data[i].Country1);
					Countries iCountry=new Countries();
					iCountry.ID=data[i].ID;
					iCountry.Country1=data[i].Country1;

					cList.add(iCountry);
	
					name[i]=data[i].Country1;
					id[i]=Integer.parseInt(data[i].ID);

				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
			
			

			// TODO: register the new account here.
			return true;
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			try{
			ArrayAdapter adapter = new ArrayAdapter(ChatRoomsListActivity.this,
			        android.R.layout.simple_spinner_item, name);
			        spinCategories.setAdapter(adapter);
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
		//	showProgress(false);
		}
	}

	public class GetCategoriesTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.
			try{
				List<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>(1);
				nameValuePairs.add(new BasicNameValuePair("apikey", getString(R.string.apikey)));
				String response=Utils.postData("getCategories", nameValuePairs);
				


//				String response=Utils.RestCall(Utils.WebServiceURL+"getCategories" +"/?apikey="+getString(R.string.apikey));

				 Gson gson = new Gson();
				 data= gson.fromJson(response, Countries[].class);
				 cList=new ArrayList<Countries>();
					name=new String[data.length];
					id=new int[data.length];


				for(int i=0;i<data.length;i++)
				{

					Countries iCountry=new Countries();
					iCountry.ID=data[i].ID;
					iCountry.Country1=data[i].Category1;
					
					cList.add(iCountry);

					name[i]=data[i].Category1;
					id[i]=Integer.parseInt(data[i].ID);
					
				}
				
			}
			catch(Exception e){
				e.printStackTrace();
			}
			

			// TODO: register the new account here.
			return true;
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			try{
			ArrayAdapter adapter = new ArrayAdapter(ChatRoomsListActivity.this,
			        android.R.layout.simple_spinner_item, name);
			        spinCategories.setAdapter(adapter);
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
		//	showProgress(false);
		}
	}

	public class GetStatesTask extends AsyncTask<String, Void, Boolean> {
		
		@Override
        protected void onPreExecute(){
           super.onPreExecute();
           try{
       	pd=new ProgressDialog(ChatRoomsListActivity.this);

    	pd.setTitle("Loading States List...");
    	pd.setMessage("Please wait.");
    	pd.setCancelable(false);
    	pd.setIndeterminate(true);
    	pd.show();
           }
           catch(Exception e){
        	   e.printStackTrace();
           }
        }

		@Override
		protected Boolean doInBackground(String... params	) {
			// TODO: attempt authentication against a network service.
			try{
				List<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>(1);
				nameValuePairs.add(new BasicNameValuePair("Country", params[0]));
				nameValuePairs.add(new BasicNameValuePair("apikey", getString(R.string.apikey)));
				String response=Utils.postData("getStates", nameValuePairs);

				 Gson gson = new Gson();
				 statedata= gson.fromJson(response, States[].class);
				 sList=new ArrayList<States>();
				Log.i(TAG,response);
				states=new String[statedata.length];
				for(int i=0;i<statedata.length;i++)
				{
					States iState=new States();
					iState.ID=statedata[i].ID;
					iState.Country=statedata[i].Country;
					iState.State1=statedata[i].State1;
					states[i]=statedata[i].State1;
					sList.add(iState);
					Log.i(TAG,iState.State1);
				}
				
			}
			catch(Exception e){
				e.printStackTrace();
			}

			

			// TODO: register the new account here.
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			try{
				StatesAdapter cAdap=new StatesAdapter(getApplicationContext(),
						R.layout.staterow,R.id.lblCountryName,sList);
				lstCountries.setAdapter(cAdap);
				if(pd!=null) pd.dismiss();
				//showProgress(false);
				if (success) {

				} else {
				}
				
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
		//	showProgress(false);
		}
	}

	public class GetSubcategoriesTask extends AsyncTask<String, Void, Boolean> {
		private ProgressDialog pd;
		@Override
        protected void onPreExecute(){
           super.onPreExecute();
           try{
       	pd=new ProgressDialog(ChatRoomsListActivity.this);

    	pd.setTitle("Loading Rooms List...");
    	pd.setMessage("Please wait.");
    	pd.setCancelable(false);
    	pd.setIndeterminate(true);
    	pd.show();
           }
           catch(Exception e)
           {
        	   e.printStackTrace();
           }
        }

		@Override
		protected Boolean doInBackground(String... params	) {
			// TODO: attempt authentication against a network service.
			try{
				List<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>(1);
				nameValuePairs.add(new BasicNameValuePair("CategoryCode", params[0]));
				nameValuePairs.add(new BasicNameValuePair("apikey", getString(R.string.apikey)));
				String response=Utils.postData("getSubCategories", nameValuePairs);


				 Gson gson = new Gson();
				 statedata= gson.fromJson(response, States[].class);
				 sList=new ArrayList<States>();
				Log.i(TAG,"SubCategories Response-"+response);
				for(int i=0;i<statedata.length;i++)
				{
					States iState=new States();
					iState.ID=statedata[i].ID;
					iState.State1=statedata[i].Name;
					
					sList.add(iState);
					Log.i(TAG,iState.State1);
				}
				
			}
			catch(Exception e){
				e.printStackTrace();
			}
			
			

			// TODO: register the new account here.
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			try{
				StatesAdapter cAdap=new StatesAdapter(getApplicationContext(),
						R.layout.countryrow,
						R.id.lblCountryName,
						sList);
				lstCountries.setAdapter(cAdap);
				if(pd!=null && pd.isShowing()) pd.dismiss();
				
			}
			catch(Exception e){
				e.printStackTrace();
			}
			//showProgress(false);
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
		//	showProgress(false);
		}
	}

	@Override
	public void onMPResume() {
		// TODO Auto-generated method stub
		
	}

}

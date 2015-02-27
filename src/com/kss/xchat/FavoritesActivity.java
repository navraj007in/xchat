package com.kss.xchat;

import java.util.ArrayList;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.kss.xchat.apiresponse.Roster;
import com.kss.xchat.viewadapters.FavoritesAdapter;
import android.os.Bundle;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ListView;

public class FavoritesActivity extends RootActivity {
	String TAG="RosterActivity";
//	GetRosterAsyncTask mAuthTask;
	ListView lstFavorites;
	public static ArrayList<Roster> roster;
	 private AdView adView;
	 ArrayList<com.kss.xchat.data.Favorites> myRoster;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_favorites);
		loadAd();
		lstFavorites=(ListView)findViewById(R.id.lstFavorites);
		com.kss.xchat.data.Favorites uRoster=new com.kss.xchat.data.Favorites(getApplicationContext());
		myRoster=uRoster.getFavoritesList();
	
		FavoritesAdapter rAdap=new FavoritesAdapter(getApplicationContext(), R.layout.favoritesrow, R.id.txtName,myRoster);
		lstFavorites.setAdapter(rAdap);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.roster, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.action_join_room:
            Intent intent = new Intent(this, ChatRoomsListActivity.class);
            startActivity(intent);
	    	return true;
	        default:
	            return super.onOptionsItemSelected(item);
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
	        .build();

	    // Start loading the ad in the background.
	    adView.loadAd(adRequest);

	}

	@Override
	public void onMPResume() {
		// TODO Auto-generated method stub
		
	}



}

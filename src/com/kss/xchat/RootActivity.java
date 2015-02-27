package com.kss.xchat;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.RelativeLayout;

public abstract class RootActivity extends Activity {
    int onStartCount = 0;
	static AdView mAdView;
    RelativeLayout layout;
    public static String mActivityName="RootActivity";
    public static String mTalkingto="None";
    public static boolean isXChatRunning=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onStartCount = 1;
        if (savedInstanceState == null) // 1st time
        {
            this.overridePendingTransition(R.anim.anim_slide_in_left,
                    R.anim.anim_slide_out_left);
        } else // already created so reverse animation
        { 
            onStartCount = 2;
        }
    }
    @Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		  if(null !=layout){
	            layout.removeAllViews();
	        }
		  isXChatRunning=false;
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
    abstract public void onMPResume();
    @Override
	protected void onResume() {
		super.onResume();
		isXChatRunning=true;
		layout = (RelativeLayout) findViewById(R.id.footer_ad);
	        if(null != layout){

	          
	            if(mAdView != null){
	                layout.addView(mAdView);
	                layout.setGravity(Gravity.CENTER_HORIZONTAL);
	            }
	            else{	
	            	setView();
	               // layout.addView(mAdView);
	            }
	        }
		
	        
	       
	        
		
		onMPResume();
	}
    void setView(){
   	 int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if(status == ConnectionResult.SUCCESS) {
       	 System.out.println("SUCCESS");
       	 mAdView = new AdView(this);
	         mAdView.setAdSize(AdSize.SMART_BANNER);
	         mAdView.setAdUnitId(Utils.ADMOBID);


	         // Create an ad request.
	         AdRequest.Builder adRequestBuilder = new AdRequest.Builder();
	         // Optionally populate the ad request builder.
	         adRequestBuilder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
	         // Add the AdView to the view hierarchy.
	         layout.addView(mAdView);

	         // Start loading the ad.
	         mAdView.loadAd(adRequestBuilder.build());
       	// return;
        }else{
       	 System.out.println("fail");
       	// return;
        }

        // Create a banner ad. The ad size and ad unit ID must be set before calling loadAd.
       
   }
}
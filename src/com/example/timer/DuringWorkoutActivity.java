package com.example.timer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;

import com.example.timer.IntenseWorkoutFragment.OnIntenseFragmentSelectedListener;
import com.example.timer.LowWorkoutFragment.OnLowFragmentSelectedListener;
import com.example.timer.WarmupFragment.OnWarmUpFragmentSelectedListener;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.Toast;

public class DuringWorkoutActivity extends Activity implements OnWarmUpFragmentSelectedListener,
																		OnIntenseFragmentSelectedListener,
																		OnLowFragmentSelectedListener
{
	
	
	
	private TabHost mTabHost;
	private Uri workoutUri;
	public HashMap<String, String> getData = new HashMap<String, String>();
	private NotificationCompat.Builder builder;
	private int notification_id=0;
	private int workout_cycles=0;
	private int count =-1 ;
	private WarmupFragment warmupFragment;
	private IntenseWorkoutFragment intenseFragment;
	private LowWorkoutFragment lowFragment;
	private FragmentTransaction ft;
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.during_workout_activity);
		
		Bundle extras = getIntent().getExtras();
		
		if(extras != null)
		{
				try{
					
					setWorkoutUri((Uri) extras.getParcelable(TimerProvider.CONTENT_ITEM_TYPE));
					getWorkoutData(getWorkoutUri()) ;
					workout_cycles= Integer.parseInt(getData.get("WORKOUT_CYCLES"));
										
				}catch(Exception e){
					Log.w("Exception",e.getMessage());
				}
		}
			
		initializeTabs();
			
	}
	
	
	public String getHashData(String key) {
		return getData.get(key);
	}
	
	public Uri getWorkoutUri()
	{
		return workoutUri;
	}
	public void setWorkoutUri(Uri workUri) {
		this.workoutUri = workUri;
	}
	
	public void resetFragements(String tabTag)
	{
		FragmentManager fm = getFragmentManager();
		warmupFragment = (WarmupFragment) fm.findFragmentByTag("WarmUp");
		intenseFragment = (IntenseWorkoutFragment) fm.findFragmentByTag("Intense");
		lowFragment = (LowWorkoutFragment) fm.findFragmentByTag("Low");
		
		if(tabTag =="Intense")
		{
			if(warmupFragment != null)
				warmupFragment.callReset();
			if(lowFragment != null)
				lowFragment.callReset();
		}
		else if(tabTag == "Low")
		{
			if(warmupFragment != null)
				warmupFragment.callReset();
			if(intenseFragment != null)
				intenseFragment.callReset();
		}
		else if(tabTag =="WarmUp")
		{
			count = 0;
			if(intenseFragment != null)
				intenseFragment.callReset();
			if(lowFragment != null)
				lowFragment.callReset();
		}
		else
		{
			if(warmupFragment != null)
				warmupFragment.callReset();
			if(intenseFragment != null)
				intenseFragment.callReset();
			if(lowFragment != null)
				lowFragment.callReset();
		}
		
		
		
	}
	
	private void initializeTabs()
	{
		mTabHost =(TabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup();
        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
			      	
			public void onTabChanged(String tabId) {
				createTabs(tabId);
				
			}
		});
        
        createTabContent();
	}
	
	private void createTabs(String tabId)
	{
		FragmentManager fm = getFragmentManager();
		warmupFragment = (WarmupFragment) fm.findFragmentByTag("WarmUp");
		intenseFragment = (IntenseWorkoutFragment) fm.findFragmentByTag("Intense");
		lowFragment = (LowWorkoutFragment) fm.findFragmentByTag("Low");
		
		 ft= fm.beginTransaction();
		
		if(warmupFragment != null)
			ft.detach(warmupFragment);
		if(intenseFragment != null)
			ft.detach(intenseFragment);
		if(lowFragment != null)
			ft.detach(lowFragment);
		
		if(tabId.equalsIgnoreCase("WarmUp"))
		{
			if(warmupFragment == null)
				ft.add(R.id.realtabcontent, new WarmupFragment(), "WarmUp");
			else
				ft.attach(warmupFragment);
		}
		else if(tabId.equalsIgnoreCase("Intense"))
		{
			if(intenseFragment == null)
				ft.add(R.id.realtabcontent, new IntenseWorkoutFragment(), "Intense");
			else
				ft.attach(intenseFragment);
		}
		else
			if(tabId.equalsIgnoreCase("Low"))
			{
				if(lowFragment == null)
					ft.add(R.id.realtabcontent, new LowWorkoutFragment(), "Low");
				else
					ft.attach(lowFragment);
			}
		ft.disallowAddToBackStack();
		ft.commitAllowingStateLoss();
	}
	
private void createTabContent()
{
	TabHost.TabSpec tSpecWarmup = mTabHost.newTabSpec("WarmUp");
    tSpecWarmup.setIndicator("WarmUp");
    tSpecWarmup.setContent(new CreateViewTabContent(getBaseContext()));
    mTabHost.addTab(tSpecWarmup);
    
    TabHost.TabSpec tSpecIntense = mTabHost.newTabSpec("Intense");
    tSpecIntense.setIndicator("Intense");
    tSpecIntense.setContent(new CreateViewTabContent(getBaseContext()));
    mTabHost.addTab(tSpecIntense);
    
    TabHost.TabSpec tSpecLow = mTabHost.newTabSpec("Low");
    tSpecLow.setIndicator("Low");
    tSpecLow.setContent(new CreateViewTabContent(getBaseContext()));
    mTabHost.addTab(tSpecLow);

}
private void getWorkoutData(Uri uri)  {
        
    	String[] projection = { 
        						WorkoutTable._ID,
        						WorkoutTable.WORKOUT_NAME,
        						WorkoutTable.WARMUP_HOUR,
        						WorkoutTable.WARMUP_MIN,
        						WorkoutTable.WARMUP_SEC,
        						WorkoutTable.HIGH_INTERVAL_HOUR,
        						WorkoutTable.HIGH_INTERVAL_MIN,
        						WorkoutTable.HIGH_INTERVAL_SEC,
        						WorkoutTable.LOW_INTERVAL_HOUR,
        						WorkoutTable.LOW_INTERVAL_MIN,
        						WorkoutTable.LOW_INTERVAL_SEC,
        						WorkoutTable.CYCLES
        						};
    	
        Cursor cursor = getContentResolver().query(uri, projection, null, null,null);
        StringBuilder warmupTimerBuilder = new StringBuilder();
        StringBuilder intenseTimerBuilder = new StringBuilder();
        StringBuilder lowTimerBuilder = new StringBuilder();
        String workoutName;
        

        
        if (cursor != null) 
        {
          cursor.moveToFirst();
          
          workoutName=(cursor.getString(cursor.getColumnIndexOrThrow(WorkoutTable.WORKOUT_NAME)));
          
          getData.put("WORKOUT_NAME", workoutName);
          
          warmupTimerBuilder.append(cursor.getString(cursor.getColumnIndexOrThrow(WorkoutTable.WARMUP_HOUR))+
        		  		":" +
          				cursor.getString(cursor.getColumnIndexOrThrow(WorkoutTable.WARMUP_MIN))+
          				":" +
          				cursor.getString(cursor.getColumnIndexOrThrow(WorkoutTable.WARMUP_SEC)));
          
          getData.put("WARMUP_TIMER", formatString(warmupTimerBuilder.toString()));
          
          intenseTimerBuilder.append(cursor.getString(cursor.getColumnIndexOrThrow(WorkoutTable.HIGH_INTERVAL_HOUR)) +
        		  		":" +
        		  		cursor.getString(cursor.getColumnIndexOrThrow(WorkoutTable.HIGH_INTERVAL_MIN)) +
        		  		":" +
        		  		cursor.getString(cursor.getColumnIndexOrThrow(WorkoutTable.HIGH_INTERVAL_SEC)));
          
          getData.put("INTENSE_TIMER", formatString(intenseTimerBuilder.toString()));
          
          lowTimerBuilder.append(cursor.getString(cursor.getColumnIndexOrThrow(WorkoutTable.LOW_INTERVAL_HOUR))+
        		  		":" +
        		  		cursor.getString(cursor.getColumnIndexOrThrow(WorkoutTable.LOW_INTERVAL_MIN)) +
        		  		":" +
        		  		cursor.getString(cursor.getColumnIndexOrThrow(WorkoutTable.LOW_INTERVAL_SEC)));
          
          getData.put("LOW_TIMER", formatString(lowTimerBuilder.toString()));
          
          getData.put("WORKOUT_CYCLES", cursor.getString(cursor.getColumnIndexOrThrow(WorkoutTable.CYCLES)));
          
         
          cursor.close();
        }
	}

	public long convertStringToMillisecond(String time)
	{
		String[] values = time.trim().split(":");
		int hr = Integer.parseInt(values[0]);
		int min = Integer.parseInt(values[1]);
		int sec = Integer.parseInt(values[2]);
		long millSecValue;
		
		millSecValue = (sec*1000 + min*60*1000 + hr*3600*1000);
	
		return millSecValue;
	}

	public String convertMillsecondsToString(long millSecValue) 
	{
		int seconds = (int) (millSecValue / 1000) % 60 ;
		int minutes = (int) ((millSecValue / (1000*60)) % 60);
		int hours   = (int) ((millSecValue / (1000*60*60)) % 24);
		
		
		String textTimer = hours+":"+minutes+":"+seconds;
				
		return formatString(textTimer);
	}
	
	public String formatString(String inputStr)
	{
		String outStr="";
		SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm:ss",Locale.US);
		try {
			outStr = outputFormat.format(outputFormat.parse(inputStr));
		} catch (ParseException e) {
			Log.w("ParceException", e.getMessage());
	}
	
	return outStr;
	
}
	
	public void onWarmUpCompleted() {
		try{
			mTabHost.setCurrentTabByTag("Intense");
		}catch(IllegalStateException e)
		{
			Log.w("Exception",e.getMessage());
		}
		
									
	}
	
	public void onIntenseWorkoutCompleted() {

			
		mTabHost.setCurrentTabByTag("Low");
		
	}
	
	public void onLowWorkoutCompleted() {
		
		if(count < workout_cycles - 1)
		{
			Log.w("Count from method",""+count);
			mTabHost.setCurrentTabByTag("Intense");
			count++;
		}
		
		else
		{
			mTabHost.setCurrentTabByTag("WarmUp");
			Toast.makeText(getBaseContext(), "Well Done! Activity Completed", Toast.LENGTH_LONG).show();
		}
	}

	
	
	public class CreateViewTabContent implements TabContentFactory{
        private Context mContext;
     
        public CreateViewTabContent(Context context){
            mContext = context;
        }
     
        
        public View createTabContent(String tag) {
            View v = new View(mContext);
            return v;
        }
    }
	
	public void setNotifications(CharSequence s, String contentTitle)
	{
		Intent resultIntent = new Intent(this, DuringWorkoutActivity.class);
		
		 
		resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
    	
		PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT | 
    	        									PendingIntent.FLAG_ONE_SHOT);
    	
    	builder = new NotificationCompat.Builder(this)
    	.setContentTitle(contentTitle)
		.setSmallIcon(R.drawable.pie_icon)
    	.setContentText(s)
    	.setAutoCancel(true);
    	
    	builder.setContentIntent(resultPendingIntent);
    	 		
    	
       	Notification notification = builder.build();
    	notification.flags |= Notification.FLAG_AUTO_CANCEL;
    	
    	NotificationManager nfmanager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
    	nfmanager.notify(notification_id,notification);
	}
	
	public void playRingtone()
	{
		Uri notificationRing = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		Ringtone ring = RingtoneManager.getRingtone(this, notificationRing);
		Vibrator vibe = (Vibrator)getBaseContext().getSystemService(VIBRATOR_SERVICE);
		ring.play();
		vibe.vibrate(1000);
		
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		
		if(count != workout_cycles && count != -1)
		{
			AlertDialog ad = new AlertDialog.Builder(this)
			.setMessage("Are you sure you want to quit Workout?")
			.setTitle("Workout Incomplete")
			.setCancelable(false)
		    .setPositiveButton(android.R.string.ok,
		                            new DialogInterface.OnClickListener() 
		    							{
		                                	public void onClick(DialogInterface dialog,int whichButton) 
		                                	{
		                                		resetFragements("All");
		                                		finish();
		                                	}
		                            })
		     .setNeutralButton(android.R.string.cancel,
		                            new DialogInterface.OnClickListener() {
		                                public void onClick(DialogInterface dialog,int whichButton) {
		                                    // User selects Cancel, continue workout
		                                }
		                            }).show();
		}
		else
		{
			super.onBackPressed();
		}
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	protected void onPause() {
		
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		
		outState.putInt("COUNT", count);
		outState.putInt("CURRENT_TAB", mTabHost.getCurrentTab());
		Log.w("Count from onSaveINstanceState",""+count);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
		
		count= savedInstanceState.getInt("COUNT");
		mTabHost.setCurrentTab(savedInstanceState.getInt("CURRENT_TAB"));
		Log.w("Count from onRestoreInstanceState",""+count);
	

	}
	

	

	
	
	
	
	
	
	
		
	

}

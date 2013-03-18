package com.example.timer;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

public class WorkoutDetails extends Activity{
	
	Button newWorkout;
	Button showWorkoutBtn;
	Button cancelBtn;
	
	EditText highIntervalhr;
	EditText lowIntervalhr;
	EditText warmupPeriodhr;
	
	EditText highIntervalmin;
	EditText lowIntervalmin;
	EditText warmupPeriodmin;
	
	EditText highIntervalsec;
	EditText lowIntervalsec;
	EditText warmupPeriodsec;
	
	EditText cycles;
	EditText workoutName;
	TimePicker warmupTimePicker;
	
	TimerDbAdapter mDatabaseAdapter;
	
	private Uri workoutUri;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workout_edit);
        
        newWorkout = (Button) findViewById(R.id.newBtn);
        
        highIntervalhr= (EditText)findViewById(R.id.highIntervalhr);
        lowIntervalhr=(EditText)findViewById(R.id.lowIntervalhr);
        warmupPeriodhr = (EditText)findViewById(R.id.warmuphr);
        
        highIntervalmin= (EditText)findViewById(R.id.highIntervalmin);
        lowIntervalmin=(EditText)findViewById(R.id.lowIntervalmin);
        warmupPeriodmin = (EditText)findViewById(R.id.warmupmin);
        
        highIntervalsec= (EditText)findViewById(R.id.highIntervalsec);
        lowIntervalsec=(EditText)findViewById(R.id.lowIntervalsec);
        warmupPeriodsec = (EditText)findViewById(R.id.warmupsec);
        
        warmupTimePicker = (TimePicker) findViewById(R.id.warmupTimePicker);
        
        cycles = (EditText) findViewById(R.id.cycles);
        
        workoutName = (EditText)findViewById(R.id.workoutName);
        
        Bundle extras = getIntent().getExtras();
        
        workoutUri = (savedInstanceState == null)? 
        				null: (Uri) savedInstanceState.getParcelable(TimerProvider.CONTENT_ITEM_TYPE);
        
        if(extras != null)
        {
        	newWorkout.setText("Update Workout");
        	workoutUri = extras.getParcelable(TimerProvider.CONTENT_ITEM_TYPE);
        	fillData(workoutUri);
        }
        
        
        
        newWorkout.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				 if (TextUtils.isEmpty(workoutName.getText().toString())) {
			          makeToast();
			        } 
				 else {
			          	setResult(RESULT_OK);
			          finish();
			        }
			      }

			});
       }
    
    private void fillData(Uri uri) {
        
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
        
        if (cursor != null) 
        {
          cursor.moveToFirst();
          
          workoutName.setText(cursor.getString(cursor.getColumnIndexOrThrow(WorkoutTable.WORKOUT_NAME)));
          
          warmupPeriodhr.setText(cursor.getString(cursor.getColumnIndexOrThrow(WorkoutTable.WARMUP_HOUR)));
          warmupPeriodmin.setText(cursor.getString(cursor.getColumnIndexOrThrow(WorkoutTable.WARMUP_MIN)));
          warmupPeriodsec.setText(cursor.getString(cursor.getColumnIndexOrThrow(WorkoutTable.WARMUP_SEC)));
          
          highIntervalhr.setText(cursor.getString(cursor.getColumnIndexOrThrow(WorkoutTable.HIGH_INTERVAL_HOUR)));
          highIntervalmin.setText(cursor.getString(cursor.getColumnIndexOrThrow(WorkoutTable.HIGH_INTERVAL_MIN)));
          highIntervalsec.setText(cursor.getString(cursor.getColumnIndexOrThrow(WorkoutTable.HIGH_INTERVAL_SEC)));
          
          lowIntervalhr.setText(cursor.getString(cursor.getColumnIndexOrThrow(WorkoutTable.LOW_INTERVAL_HOUR)));
          lowIntervalmin.setText(cursor.getString(cursor.getColumnIndexOrThrow(WorkoutTable.LOW_INTERVAL_MIN)));
          lowIntervalsec.setText(cursor.getString(cursor.getColumnIndexOrThrow(WorkoutTable.LOW_INTERVAL_SEC)));
          
          cycles.setText(cursor.getString(cursor.getColumnIndexOrThrow(WorkoutTable.CYCLES)));
         
          // Always close the cursor
          cursor.close();
        }
      }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putParcelable(TimerProvider.CONTENT_ITEM_TYPE, workoutUri);
      }

    @Override
    protected void onPause() {
      super.onPause();
      saveState();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_timer, menu);
        return true;
    }
    
    private void saveState() {
        String name = (String) workoutName.getText().toString();
        
        int hHr = Integer.parseInt(highIntervalhr.getText().toString());
        int hMin = Integer.parseInt(highIntervalmin.getText().toString());
        int hSec = Integer.parseInt(highIntervalsec.getText().toString());
        
        int lHr = Integer.parseInt(lowIntervalhr.getText().toString());
        int lMin = Integer.parseInt(lowIntervalmin.getText().toString());
        int lSec = Integer.parseInt(lowIntervalsec.getText().toString());
        
        int wHr = Integer.parseInt(warmupPeriodhr.getText().toString());
        int wMin = Integer.parseInt(warmupPeriodmin.getText().toString());
        int wSec = Integer.parseInt(warmupPeriodsec.getText().toString());
        
        int noCycle =  Integer.parseInt(cycles.getText().toString());
                
        ContentValues values = new ContentValues();
        
        values.put(WorkoutTable.WORKOUT_NAME,name);
        
        values.put(WorkoutTable.WARMUP_HOUR,wHr);
        values.put(WorkoutTable.WARMUP_MIN,wMin);
        values.put(WorkoutTable.WARMUP_SEC,wSec);
        
        values.put(WorkoutTable.HIGH_INTERVAL_HOUR,hHr);
        values.put(WorkoutTable.HIGH_INTERVAL_MIN,hMin);
        values.put(WorkoutTable.HIGH_INTERVAL_SEC,hSec);
        
        values.put(WorkoutTable.LOW_INTERVAL_HOUR,lHr);
        values.put(WorkoutTable.LOW_INTERVAL_MIN,lMin);
        values.put(WorkoutTable.LOW_INTERVAL_SEC,lSec);
        
        values.put(WorkoutTable.CYCLES,noCycle);
        
        
        if (workoutUri == null) {
          // New workout
          workoutUri = getContentResolver().insert(TimerProvider.CONTENT_URI, values);
        } else {
          // Update workout
          getContentResolver().update(workoutUri, values, null, null);
        }
      }
    
    

      private void makeToast() {
        Toast.makeText(WorkoutDetails.this, "Please specify name for the workout",
            Toast.LENGTH_LONG).show();
      }
    
    }

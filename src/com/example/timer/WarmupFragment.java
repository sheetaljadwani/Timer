package com.example.timer;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Fragment;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


public class WarmupFragment extends Fragment{
	
	private TextView workoutPhase;
	private TextView workoutTimer;
	private TextView workoutName;
	private Button startBtn;
	private Button resetBtn;
	private Button pauseBtn;
	
	private String workoutResetTimer;
	
	private String warmupTimer;
	private String buttonState=null;
	private String START_CLICKED="start_clicked";
	private String PAUSE_CLICKED="pause_clicked";
	private CountDownTimer timerObj;
	private CountDownTimer timerCopyObj;
		

	public String getWorkoutName() {
		return workoutName.getText().toString();
	}

	public void setWorkoutName(String workoutName) {
		this.workoutName.setText(workoutName);
	}
	
	private View view;
	
	private OnWarmUpFragmentSelectedListener mCallback;

    
    public interface OnWarmUpFragmentSelectedListener {
        
        public void onWarmUpCompleted();
    }
	
	public WarmupFragment()
	{
		
	}
	
	@Override
	public void onAttach(Activity activity) {
		
		super.onAttach(activity);
		
		 try {
	            mCallback = (OnWarmUpFragmentSelectedListener) activity;
	        } catch (ClassCastException e) {
	            throw new ClassCastException(activity.toString()
	                    + " must implement OnFragmentSelectedListener");
	        }
	        
	        
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);

		
	}
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		if (container == null) 
		{
			    return null; 
			    
		}
		
		view = 	(LinearLayout)inflater.inflate(R.layout.warmup_tab_fragment, container, false);
		
		workoutPhase = (TextView) view.findViewById(R.id.workoutPhase);
		workoutName = (TextView) view.findViewById(R.id.workoutName);
		workoutTimer = (TextView) view.findViewById(R.id.workoutTimer);
	
		startBtn = (Button)view.findViewById(R.id.WarmupstartBtn);
		resetBtn = (Button) view.findViewById(R.id.WarmupresetBtn);
		pauseBtn = (Button) view.findViewById(R.id.WarmuppauseBtn);
		
		
		
		if(savedInstanceState== null)
		{
			
			try {
				workoutName.setText(((DuringWorkoutActivity)getActivity()).getHashData("WORKOUT_NAME"));
				workoutTimer.setText(((DuringWorkoutActivity)getActivity()).getHashData("WARMUP_TIMER"));
				workoutResetTimer = workoutTimer.getText().toString();
				
			} catch (Exception e) {
				Log.w("Intent Exception",e.getMessage());
			}
			
			setButtons();
			
		}
		else
		{
			workoutName.setText(savedInstanceState.getCharArray("WORTKOUT_NAME").toString());
			workoutTimer.setText(savedInstanceState.getCharArray("WARMUP_TIMER").toString());
			workoutResetTimer=savedInstanceState.getCharSequence("WARMUP_RESET_TIMER").toString();
			startBtn.setVisibility(savedInstanceState.getInt("START_VISIBILITY_STATE"));
			pauseBtn.setVisibility(savedInstanceState.getInt("PAUSE_VISIBILITY_STATE"));
			resetBtn.setVisibility(savedInstanceState.getInt("RESET_VISIBILITY_STATE"));
		}
		
		
		
		
		workoutTimer.addTextChangedListener(new TextWatcher() {
					
			public void onTextChanged(CharSequence s, int start, int before, int count) {
						((DuringWorkoutActivity)getActivity()).setNotifications(s,workoutPhase.getText().toString());
				}

					public void afterTextChanged(Editable arg0) {
						// TODO Auto-generated method stub
						
					}

					public void beforeTextChanged(CharSequence s, int start,
							int count, int after) {
						// TODO Auto-generated method stub
						
					}
					
					
				});
		
		
		return view;

	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
			warmupTimer = workoutTimer.getText().toString();
			
		
			setRetainInstance(true);
	}
		
	
	@Override
	public void onResume() {
		
		super.onResume();
		
		
		
		if(buttonState == START_CLICKED)
		{
			startBtn.setVisibility(Button.INVISIBLE);
			pauseBtn.setVisibility(Button.VISIBLE);
			resetBtn.setEnabled(true);
			workoutTimer.setText(warmupTimer);
		}
		else if(buttonState == PAUSE_CLICKED)
		{
			pauseBtn.setVisibility(Button.INVISIBLE);
			resetBtn.setEnabled(true);
			startBtn.setVisibility(Button.VISIBLE);
			workoutTimer.setText(warmupTimer);
		}
		
	
		
		
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		
		super.onSaveInstanceState(outState);
		outState.putCharSequence("WARMPUP_TIMER", workoutTimer.getText().toString());
		outState.putCharSequence("WORKOUT_NAME", workoutName.getText().toString());
		outState.putCharSequence("WARMUP_RESET_TIMER", workoutResetTimer);
		outState.putInt("START_VISIBILITY_STATE", startBtn.getVisibility());
		outState.putInt("PAUSE_VISIBILITY_STATE", pauseBtn.getVisibility());
		outState.putInt("RESET_VISIBILITY_STATE", resetBtn.getVisibility());
		
	}
	
	
	
	private void setButtons()
	{
		
		startBtn.setOnClickListener(new View.OnClickListener() {
			
				public void onClick(View v) {
				
				((DuringWorkoutActivity)getActivity()).resetFragements(getTag());
				
				buttonState = START_CLICKED;
				
				long millisInFuture = ((DuringWorkoutActivity)getActivity()).convertStringToMillisecond(workoutTimer.getText().toString());
				
				((DuringWorkoutActivity)getActivity()).playRingtone();
				
				timerObj = new CountDownTimer(millisInFuture,1000) {
					
					@Override
					public void onTick(long millisUntilFinished) {
						 try {
							workoutTimer.setText(((DuringWorkoutActivity)getActivity()).convertMillsecondsToString(millisUntilFinished));
						} catch (Exception e) {
							Log.w("Exception", e.getMessage());
						}
						
					}
					@Override
					public void onFinish() {
						
						workoutTimer.setText(workoutResetTimer);
						mCallback.onWarmUpCompleted();
					}
				}.start();
				
				
				timerCopyObj = timerObj;
				
				resetBtn.setEnabled(true);
				startBtn.setVisibility(Button.INVISIBLE);
				pauseBtn.setVisibility(Button.VISIBLE);
			}
				
				
			
		});
		
		
		resetBtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				buttonState= null;
				if(timerCopyObj != null)
					timerCopyObj.cancel();
				timerCopyObj = null;
				workoutTimer.setText(workoutResetTimer);
				resetBtn.setEnabled(false);
				pauseBtn.setVisibility(Button.INVISIBLE);
				startBtn.setVisibility(Button.VISIBLE);
				workoutTimer.setText(workoutResetTimer);
			}
		});
		
		pauseBtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
				buttonState = PAUSE_CLICKED;
				if(timerObj != null)
					timerObj.cancel();
				timerObj = null;
				startBtn.setVisibility(Button.VISIBLE);
				resetBtn.setVisibility(Button.VISIBLE);
				pauseBtn.setVisibility(Button.INVISIBLE);
				
				
			}
		});
			
	}
	
	public void setWarmupTimer(String warmup)
	{
		workoutTimer.setText(warmup);
	}
	
	public void callReset()
	{
		resetBtn.performClick();
	}
			
	

	
	
		

}


	
	

	



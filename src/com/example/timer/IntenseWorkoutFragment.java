package com.example.timer;



import android.app.Activity;
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

public class IntenseWorkoutFragment extends Fragment {
	
	private View view;
	private TextView workoutPhase;
	private TextView workoutTimer;
	private TextView workoutName;
	private Button startBtn;
	private Button resetBtn;
	private Button pauseBtn;
	private String intenseResetTimer;
	private CountDownTimer highTimerObj;
	private CountDownTimer highTimerCopyObj;
	
	private String buttonState=null;
	private String START_CLICKED="start_clicked";
	private String PAUSE_CLICKED="pause_clicked";
	private String intenseTimer;
	private int continueflag = 0;
	
	private OnIntenseFragmentSelectedListener mCallback;

    
    public interface OnIntenseFragmentSelectedListener {
        
        public void onIntenseWorkoutCompleted();
    }
    
	public IntenseWorkoutFragment()
	{}
	
	@Override
	public void onAttach(Activity activity) {
		
		super.onAttach(activity);
		
		 try {
	            mCallback = (OnIntenseFragmentSelectedListener) activity;
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
		if (container == null) {
		    return null; }
		
		view = 	(LinearLayout)inflater.inflate(R.layout.intenseworkout_tab_fragment, container, false);
		
		workoutPhase = (TextView) view.findViewById(R.id.workoutIntensePhase);
		workoutName = (TextView) view.findViewById(R.id.workoutName);
		workoutTimer = (TextView) view.findViewById(R.id.workoutIntenseTimer);
		
		startBtn = (Button)view.findViewById(R.id.intensestartBtn);
		resetBtn = (Button) view.findViewById(R.id.intenseresetBtn);
		pauseBtn = (Button) view.findViewById(R.id.intensepauseBtn);
		
		if(savedInstanceState== null)
		{
			
			try {
				workoutName.setText(((DuringWorkoutActivity)getActivity()).getHashData("WORKOUT_NAME"));
				workoutTimer.setText(((DuringWorkoutActivity)getActivity()).getHashData("INTENSE_TIMER"));
				intenseResetTimer = workoutTimer.getText().toString();
				
			} catch (Exception e) {
				Log.w("Intent Exception",e.getMessage());
			}
			
			setButtons();
			
			
		}
		else
		{
			workoutName.setText(savedInstanceState.getCharArray("WORTKOUT_NAME").toString());
			workoutTimer.setText(savedInstanceState.getCharArray("HIGH_TIMER").toString());
			intenseResetTimer=savedInstanceState.getCharSequence("HIGH_RESET_TIMER").toString();
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
		if(continueflag != 1)
			startIntenseWorkout();
			
		return view;
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
			intenseTimer = workoutTimer.getText().toString();
			
			if(intenseTimer.compareTo(intenseResetTimer) != 0)
				continueflag = 1;
			else
				continueflag = 0;
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
			workoutTimer.setText(intenseTimer);
		}
		else if(buttonState == PAUSE_CLICKED)
		{
			pauseBtn.setVisibility(Button.INVISIBLE);
			resetBtn.setEnabled(true);
			startBtn.setVisibility(Button.VISIBLE);
			workoutTimer.setText(intenseTimer);
		}
			

	}
	
		
	public void startIntenseWorkout()
	{
		startBtn.performClick();
	}
	
	private void setButtons()
	{
		
		startBtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
						
				((DuringWorkoutActivity)getActivity()).resetFragements(getTag());
				
				buttonState = START_CLICKED;
				long millisInFuture = ((DuringWorkoutActivity)getActivity()).convertStringToMillisecond(workoutTimer.getText().toString());
				((DuringWorkoutActivity)getActivity()).playRingtone();
				highTimerObj = new CountDownTimer(millisInFuture,1000) {
					
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
						
						workoutTimer.setText(intenseResetTimer);
						continueflag=0;
						mCallback.onIntenseWorkoutCompleted();
					}
				}.start();
				
				
				highTimerCopyObj = highTimerObj;
				
				resetBtn.setEnabled(true);
				startBtn.setVisibility(Button.INVISIBLE);
				pauseBtn.setVisibility(Button.VISIBLE);
				
				
			}
		});
		
		
		resetBtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				buttonState = null;
				if(highTimerCopyObj != null)
					highTimerCopyObj.cancel();
				highTimerCopyObj = null;
				workoutTimer.setText(intenseResetTimer);
				resetBtn.setEnabled(false);
				pauseBtn.setVisibility(Button.INVISIBLE);
				startBtn.setVisibility(Button.VISIBLE);
				workoutTimer.setText(intenseResetTimer);
			}
		});
		
		pauseBtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				buttonState = PAUSE_CLICKED;
				if (highTimerObj != null)
					highTimerObj.cancel();
				highTimerObj = null;
				startBtn.setVisibility(Button.VISIBLE);
				resetBtn.setVisibility(Button.VISIBLE);
				pauseBtn.setVisibility(Button.INVISIBLE);
				
				
			}
		});
			
	}
	
	public void setIntenseTimer(String intense)
	{
		workoutTimer.setText(intense);
	}
	
	public void callReset()
	{
		resetBtn.performClick();
	}
	
		
	

}

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

public class LowWorkoutFragment extends Fragment {
	
	private View view;
	private TextView workoutPhase;
	private TextView workoutTimer;
	private TextView workoutName;
	private Button startBtn;
	private Button resetBtn;
	private Button pauseBtn;
	private String mildResetTimer;
	private CountDownTimer highTimerObj;
	private CountDownTimer highTimerCopyObj;
	
	private String buttonState=null;
	private String START_CLICKED="start_clicked";
	private String PAUSE_CLICKED="pause_clicked";
	
	private String mildTimer;
	private int continueFlag = 0;
	
	private OnLowFragmentSelectedListener mCallback;

    
    public interface OnLowFragmentSelectedListener {
        
        public void onLowWorkoutCompleted();
    }
	
	
	public LowWorkoutFragment()
	{}
	
	@Override
	public void onAttach(Activity activity) {
		
		super.onAttach(activity);
		
		 try {
	            mCallback = (OnLowFragmentSelectedListener) activity;
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
		
		view = (LinearLayout)inflater.inflate(R.layout.mildworkout_tab_fragment, container, false);
	
	
		workoutPhase = (TextView) view.findViewById(R.id.workoutPhase);
		workoutName = (TextView) view.findViewById(R.id.workoutName);
		workoutTimer = (TextView) view.findViewById(R.id.lowWorkoutTimer);
		
		startBtn = (Button)view.findViewById(R.id.mildstartBtn);
		resetBtn = (Button) view.findViewById(R.id.mildresetBtn);
		pauseBtn = (Button) view.findViewById(R.id.mildpauseBtn);
		
		if(savedInstanceState== null)
		{
			
			try {
				workoutName.setText(((DuringWorkoutActivity)getActivity()).getHashData("WORKOUT_NAME"));
				workoutTimer.setText(((DuringWorkoutActivity)getActivity()).getHashData("LOW_TIMER"));
				mildResetTimer = workoutTimer.getText().toString();
				
			} catch (Exception e) {
				Log.w("Intent Exception",e.getMessage());
			}
			
			setButtons();
			
		}
		else 
			workoutTimer.setText("Fragment Not Found!");
		
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
	
	
		if(continueFlag != 1)
			startLowWorkout();
		
		
	
		return view;
}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
			mildTimer = workoutTimer.getText().toString();
			if(mildTimer.compareTo(mildResetTimer) != 0)
				continueFlag =1;
			else
				continueFlag = 0;
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
			workoutTimer.setText(mildTimer);
		}
		else if(buttonState == PAUSE_CLICKED)
		{
			pauseBtn.setVisibility(Button.INVISIBLE);
			resetBtn.setEnabled(true);
			startBtn.setVisibility(Button.VISIBLE);
			workoutTimer.setText(mildTimer);
		}
		
		
	}
	
	public void startLowWorkout()
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
						
						workoutTimer.setText(mildResetTimer);
						continueFlag =0;
						mCallback.onLowWorkoutCompleted();
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
				workoutTimer.setText(mildResetTimer);
				resetBtn.setEnabled(false);
				pauseBtn.setVisibility(Button.INVISIBLE);
				startBtn.setVisibility(Button.VISIBLE);
				workoutTimer.setText(mildResetTimer);
			}
		});
		
		pauseBtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				buttonState = PAUSE_CLICKED;
				if(highTimerObj != null)
					highTimerObj.cancel();
				highTimerObj = null;
				startBtn.setVisibility(Button.VISIBLE);
				resetBtn.setVisibility(Button.VISIBLE);
				pauseBtn.setVisibility(Button.INVISIBLE);
				
				
			}
		});
			
	}
	
	public void setMildTimer(String mild)
	{
		workoutTimer.setText(mild);
	}
	
	public void callReset()
	{
		resetBtn.performClick();
	}

}

	




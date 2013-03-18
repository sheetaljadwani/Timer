package com.example.timer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TimerDbAdapter extends SQLiteOpenHelper{
	
	Context mCtxt;
	
	
	private  static final String DATABASE_NAME = "db";
	private  static final int VERSION = 1;
	
	
	public TimerDbAdapter(Context context) {
		super(context, DATABASE_NAME, null, VERSION);
		
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		try
		{
			WorkoutTable.onCreate(db);
		}
		catch(Exception e)
		{
			Log.w("Exception occured","Exception");
		}
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		WorkoutTable.onUpgrade(db, oldVersion, newVersion);
		
	}
	
	
}

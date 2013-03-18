package com.example.timer;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class WorkoutTable {
	
	public static final String TABLE_NAME = "workout";
	public static final String _ID = "_id";
	public static final String WORKOUT_NAME="workoutName";
	
	public static final String HIGH_INTERVAL_HOUR="highIntervalHour";
	public static final String HIGH_INTERVAL_MIN= "highIntervalMin";
	public static final String HIGH_INTERVAL_SEC = "highIntervalSec";
	public static final String HIGH_INTERVAL_SOUND = "highIntervalSound";
	
	public static final String LOW_INTERVAL_HOUR ="lowIntervalHour";
	public static final String LOW_INTERVAL_MIN= "lowIntervalMin";
	public static final String LOW_INTERVAL_SEC ="lowIntervalSec";
	public static final String LOW_INTERVAL_SOUND="lowIntervalSound";
	
	public static final String WARMUP_HOUR= "warmupHour";
	public static final String WARMUP_MIN="warmupMin";
	public static final String WARMUP_SEC="warmupSec";
	public static final String WARMUP_SOUND="warmupSound";
	
	public static final String CYCLES = "cycles";
	private static final String TIMER_TABLE_CREATE =
        "CREATE TABLE " + TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
        WORKOUT_NAME  +" TEXT, " +
        HIGH_INTERVAL_HOUR+"   INTEGER, " +
        HIGH_INTERVAL_MIN+"    INTEGER, " +
        HIGH_INTERVAL_SEC +"   INTEGER, " +
        HIGH_INTERVAL_SOUND+"   INTEGER, "+
        LOW_INTERVAL_HOUR+"   INTEGER, " +
        LOW_INTERVAL_MIN +"   INTEGER, " +
        LOW_INTERVAL_SEC +"   INTEGER, " +
        LOW_INTERVAL_SOUND+"   INTEGER, "+
        WARMUP_HOUR+"    INTEGER, " +
        WARMUP_MIN +"   INTEGER, " +
        WARMUP_SEC +"   INTEGER, " +
        WARMUP_SOUND +"  INTEGER, "+
        CYCLES+"  INTEGER);";

	
	public static void onCreate(SQLiteDatabase db)
	{
		db.execSQL(TIMER_TABLE_CREATE);
	}
	
	 public static void onUpgrade(SQLiteDatabase database, int oldVersion,
		      int newVersion) {
		    Log.w(WorkoutTable.class.getName(), "Upgrading database from version "
		        + oldVersion + " to " + newVersion
		        + ", which will destroy all old data");
		    database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		    onCreate(database);
		  }

}

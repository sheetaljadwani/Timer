package com.example.timer;

import java.util.Arrays;
import java.util.HashSet;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.example.timer.TimerDbAdapter;


public class TimerProvider extends ContentProvider{

	private TimerDbAdapter mDbHelper;
	
	private static final int WORKOUTS = 10;
	  private static final int WORKOUT_ID = 20;
	  
	private static final String AUTHORITY="com.example.timer.provider";
	
	private static final String BASE_PATH="workout";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY  + "/" + BASE_PATH);
	
	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/workouts";
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/workout";
	
	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	  static {
	    sURIMatcher.addURI(AUTHORITY, BASE_PATH, WORKOUTS);
	    sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", WORKOUT_ID);
	  }
	
	@Override
	public boolean onCreate() {
		mDbHelper = new TimerDbAdapter(getContext());
	    return false;
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
						String[] selectionArgs, String sortOrder) {
		
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		checkColumns(projection);
		
		queryBuilder.setTables(WorkoutTable.TABLE_NAME);
		int uriType = sURIMatcher.match(uri);
		switch(uriType)
		{
			case WORKOUTS:
		      break;
		    case WORKOUT_ID:
		      // Adding the ID to the original query
		      queryBuilder.appendWhere(WorkoutTable._ID + "="
		          + uri.getLastPathSegment());
		      break;
		    default:
		      throw new IllegalArgumentException("Unknown URI: " + uri);
		      
		}
		
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		
		Cursor cursor= queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		
		return cursor;
	}
	
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = mDbHelper.getWritableDatabase();
		long id = 0;
		
		switch (uriType) {
	    case WORKOUTS:
	      id = sqlDB.insert(WorkoutTable.TABLE_NAME, null, values);
	      break;
	    default:
	      throw new IllegalArgumentException("Unknown URI: " + uri);
	    }
		
	    getContext().getContentResolver().notifyChange(uri, null);
	    return Uri.parse(BASE_PATH + "/" + id);
		
	}
	
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		
		int uriType = sURIMatcher.match(uri);
	    SQLiteDatabase sqlDB = mDbHelper.getWritableDatabase();
	    int rowsUpdated = 0;
	    
	    switch (uriType) {
	    	case WORKOUTS:
	    		rowsUpdated = sqlDB.update(WorkoutTable.TABLE_NAME, values, selection, selectionArgs);
	    		break;
	    		
	    	case WORKOUT_ID:
	    		String id = uri.getLastPathSegment();
	    		if (TextUtils.isEmpty(selection)) {
	    			rowsUpdated = sqlDB.update(WorkoutTable.TABLE_NAME, values,WorkoutTable._ID + "=" + id,null);
	    		} 
	    		else {
	    			rowsUpdated = sqlDB.update(WorkoutTable.TABLE_NAME, 
	    					values,
	    					WorkoutTable._ID + "=" + id 
	    					+ " and " 
	    					+ selection,
	    					selectionArgs);
	    		}
	    		break;
	    	default:
	    		throw new IllegalArgumentException("Unknown URI: " + uri);
	    	}
	    
	    	getContext().getContentResolver().notifyChange(uri, null);
	    
	    return rowsUpdated;
	}
		  
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		
		int uriType = sURIMatcher.match(uri);
	    SQLiteDatabase sqlDB = mDbHelper.getWritableDatabase();
	    int rowsDeleted = 0;
	    
	    switch (uriType) {
	    	case WORKOUTS:
	    		rowsDeleted = sqlDB.delete(WorkoutTable.TABLE_NAME, selection, selectionArgs);
	    		break;
	    	case WORKOUT_ID:
	    		String id = uri.getLastPathSegment();
	    		if (TextUtils.isEmpty(selection)) {
	    			rowsDeleted = sqlDB.delete(WorkoutTable.TABLE_NAME,
	    										WorkoutTable._ID + "=" + id, null);
	    			} 
	    		else {
	    				rowsDeleted = sqlDB.delete(WorkoutTable.TABLE_NAME,
	    											WorkoutTable._ID + "=" + id + " and " + selection, selectionArgs);
	    			}
	    		break;
	    	default:
	    		throw new IllegalArgumentException("Unknown URI: " + uri);
	    }
	    
	    getContext().getContentResolver().notifyChange(uri, null);
	    return rowsDeleted;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	
	private void checkColumns(String[] projection) {
	    String[] available = { 
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
	    if (projection != null) {
	      HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
	      HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
	      // Check if all columns which are requested are available
	      if (!availableColumns.containsAll(requestedColumns)) {
	        throw new IllegalArgumentException("Unknown columns in projection");
	      }
	    }
	  }


}

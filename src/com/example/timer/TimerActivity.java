package com.example.timer;

import android.net.Uri;
import android.os.Bundle;
import android.app.ListActivity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class TimerActivity extends ListActivity implements LoaderCallbacks<Cursor>{

	
	  private static final int DELETE_ID = Menu.FIRST + 1;
	  private SimpleCursorAdapter adapter;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workoutlist);
        this.getListView().setDividerHeight(2);
        fillData();
        registerForContextMenu(getListView());
     }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.listmenu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	
    	switch (item.getItemId()) {
        	case R.id.insert:
        		createWorkout();
        		return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
        	case DELETE_ID:
        		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        		Uri uri = Uri.parse(TimerProvider.CONTENT_URI + "/" + info.id);
        		getContentResolver().delete(uri, null, null);
        		fillData();
          return true;
        }
        return super.onContextItemSelected(item);
    }
    
    private void createWorkout() {
        Intent i = new Intent(this, WorkoutDetails.class);
        startActivity(i);
      }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	
      super.onListItemClick(l, v, position, id);
//      Intent i = new Intent(this, WorkoutDetails.class);
//      Uri workoutUri = Uri.parse(TimerProvider.CONTENT_URI + "/" + id);
//      i.putExtra(TimerProvider.CONTENT_ITEM_TYPE, workoutUri);
//      startActivity(i);
      
      Intent intent = new Intent(this, DuringWorkoutActivity.class);
      Uri workoutUri = Uri.parse(TimerProvider.CONTENT_URI + "/" + id);
      intent.putExtra(TimerProvider.CONTENT_ITEM_TYPE, workoutUri);
      startActivity(intent);
//      
    }
    
    public void fillData()
    {
    	// Fields from the database (projection)
        // Must include the _id column for the adapter to work
        String[] from = new String[] { WorkoutTable.WORKOUT_NAME };
        // Fields on the UI to which we map
        int[] to = new int[] { R.id.label };

        getLoaderManager().initLoader(0, null, this);
        
        adapter = new SimpleCursorAdapter(this, R.layout.workout_row, null, from, to, 0);

        setListAdapter(adapter);
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
      super.onCreateContextMenu(menu, v, menuInfo);
      menu.add(0, DELETE_ID, 0, R.string.menu_delete);
    }
    
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    	String[] projection = { WorkoutTable._ID, WorkoutTable.WORKOUT_NAME };
        CursorLoader cursorLoader = new CursorLoader(this,TimerProvider.CONTENT_URI, projection, null, null, null);
        return cursorLoader;
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		adapter.swapCursor(data);
		
	}

	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.swapCursor(null);
		
	}
    
    
    
    
}

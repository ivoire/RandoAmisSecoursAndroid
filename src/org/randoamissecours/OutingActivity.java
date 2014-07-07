package org.randoamissecours;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DigitalClock;
import android.widget.TextView;

public class OutingActivity extends ActionBarActivity {
	public final static String TAG = "OutingActivity";
	
	private int mID;
	private TextView mName;
	private TextView mDescription;
	private TextView mBeginning;
	private TextView mEnding;
	private TextView mAlert;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_outing);

		// Ad the return caret with the icon to go back to the main screen
		// The parent screen is defined in the AndroidManifest
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// Get the outing ID
		Intent intent = getIntent();
		mID = intent.getIntExtra(MainActivity.OUTING_ID, 0);

		// Get the interface elements
		mName = (TextView) findViewById(R.id.name);
		mDescription = (TextView) findViewById(R.id.description);
		mBeginning = (TextView) findViewById(R.id.clockBeginning);
		mEnding = (TextView) findViewById(R.id.clockEnding);
		mAlert = (TextView) findViewById(R.id.clockAlert);

		// Grab the outing details from the database
		new LoadOutingFromDbTask().execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.outing, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// Load outing details from the database
	private class LoadOutingFromDbTask extends AsyncTask<Void, Integer, Outing> {
		@Override
		protected Outing doInBackground(Void... params) {
			Log.d(TAG, "Loading outing details from the database: begin");

            // Query the database for this outing
            OutingOpenHelper dbHelper = new OutingOpenHelper(getApplicationContext());
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String[] projection = {
            		OutingOpenHelper.COLUMN_NAME,
            		OutingOpenHelper.COLUMN_DESCRIPTION,
            		OutingOpenHelper.COLUMN_BEGINNING,
            		OutingOpenHelper.COLUMN_ENDING,
            		OutingOpenHelper.COLUMN_ALERT
            };
            String selection = OutingOpenHelper.COLUMN_ID + " = ?";
            String[] selectionArgs = {
            		String.valueOf(mID)
            };
            Cursor c = db.query(OutingOpenHelper.TABLE_NAME, projection, selection, selectionArgs, null, null, null);

            //TODO: handle errors
            c.moveToNext();
            
            // Create the Outing result
            Outing outing = new Outing(c.getString(c.getColumnIndexOrThrow(OutingOpenHelper.COLUMN_NAME)));

            outing.description = c.getString(c.getColumnIndexOrThrow(OutingOpenHelper.COLUMN_DESCRIPTION));
            outing.beginning = outing.parseDate(c.getString(c.getColumnIndexOrThrow(OutingOpenHelper.COLUMN_BEGINNING)));
            outing.ending = outing.parseDate(c.getString(c.getColumnIndexOrThrow(OutingOpenHelper.COLUMN_ENDING)));
            outing.alert = outing.parseDate(c.getString(c.getColumnIndexOrThrow(OutingOpenHelper.COLUMN_ALERT)));

            db.close();
            return outing;
		}

		protected void onPostExecute(Outing outing) {
    		Log.d(TAG, "Loading outing details from the database: finished");
    		mName.setText(outing.name);
    		mDescription.setText(outing.description);
    		// TODO: print the date in the right format
    		mBeginning.setText(outing.dateFormat(outing.beginning));
    		mEnding.setText(outing.dateFormat(outing.ending));
    		mAlert.setText(outing.dateFormat(outing.alert));
    	}
    }
}
package org.randoamissecours;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.randoamissecours.utils.HTTPHelper;


public class MainActivity extends ActionBarActivity {
	public final static String TAG = "MainActivity";
	public final static String OUTING_NAME = "org.randoamissecours.outing.name";
	public final static String OUTING_DESCRIPTION = "org.randoamissecours.outing.description";

	public final static int LOGIN_RESULT = 1;
	public final static String LOGIN_PREFS = "LoginPrefs";
	public final static String LOGIN_PREFS_USER_ID = "User_id";
	public final static String LOGIN_PREFS_USERNAME = "Username";
	public final static String LOGIN_PREFS_PROFILE_ID = "Profile_id";
	public final static String LOGIN_PREFS_APIKEY = "ApiKey";

	private OutingsAdapter adapter;
	private int mUserId;
	private String mUsername;
	private int mProfileId;
	private String mApiKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Load the default values for the global preferences
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
        PreferenceManager.setDefaultValues(this, R.xml.pref_data_sync, false);

        // Check the user credential
        SharedPreferences settings = getSharedPreferences(LOGIN_PREFS, 0);
        mUserId = settings.getInt(LOGIN_PREFS_USER_ID, -1);

        // No credentials: launch the login activity
        if (mUserId < 0) {
        	// Start the login Activity
        	Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        	// The credentials will be grabbed later
        	startActivityForResult(intent, LOGIN_RESULT);
        } else {
        	// Get the full credentials
            mUsername = settings.getString(LOGIN_PREFS_USERNAME, "");
            mProfileId = settings.getInt(LOGIN_PREFS_PROFILE_ID, -1);
            mApiKey = settings.getString(LOGIN_PREFS_APIKEY, "");
        }

        // Create and populate the list of Outings
        ListView listView = (ListView) findViewById(R.id.list);
        ArrayList<Outing> outings = new ArrayList<Outing>();
        this.adapter = new OutingsAdapter(this, outings);
        adapter.setNotifyOnChange(true);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        	@Override
        	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        		Outing outing = (Outing)adapter.getItem(position);
        		Intent intent = new Intent(MainActivity.this, OutingActivity.class);
        		intent.putExtra(OUTING_NAME, outing.name);
        		intent.putExtra(OUTING_DESCRIPTION, outing.description);
        		startActivity(intent);
        		}
        	});

        // Load Outings from the database
        new LoadFromDbTask().execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
    	if (requestCode == LOGIN_RESULT) {
    		if (resultCode == RESULT_OK) {
    			// Grab the credentials again
    			SharedPreferences settings = getSharedPreferences(LOGIN_PREFS, 0);
    	        mUserId = settings.getInt(LOGIN_PREFS_USER_ID, -1);
    	        mUsername = settings.getString(LOGIN_PREFS_USERNAME, "");
    			mProfileId = settings.getInt(LOGIN_PREFS_PROFILE_ID, -1);
    	        mApiKey = settings.getString(LOGIN_PREFS_APIKEY, "");
    		} else {
    			// Start the login Activity
            	Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            	// The credentials will be grabbed later
            	startActivityForResult(intent, LOGIN_RESULT);
    		}
    	}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_sync) {
        	// Check that network is available
        	ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        	NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        	if (networkInfo != null && networkInfo.isConnected()) {
        		new SyncTask().execute();
                return true;
        	} else {
        		Toast toast = Toast.makeText(this, "Network unavailable", Toast.LENGTH_SHORT);
        		toast.show();
        		return false;
        	}
        } else if (id == R.id.action_settings) {
        	// Start the settings Activity
        	Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        	startActivity(intent);
        	return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class LoadFromDbTask extends AsyncTask<Void, Integer, ArrayList<Outing> > {
		@Override
		protected ArrayList<Outing> doInBackground(Void... params) {
			Log.d(TAG, "Loading from the database: begin");
    		ArrayList<Outing> outings = new ArrayList<Outing>();

            // Populate the list from the cache
            OutingOpenHelper dbHelper = new OutingOpenHelper(getApplicationContext());
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String[] projection = {
            		OutingOpenHelper.COLUMN_ID,
            		OutingOpenHelper.COLUMN_NAME
            };
            Cursor c = db.query(OutingOpenHelper.TABLE_NAME, projection, null, null, null, null, null);
            while (c.moveToNext()) {
            	Log.d(TAG, "Adding one item");
            	Outing outing = new Outing(c.getString(c.getColumnIndexOrThrow(
            											OutingOpenHelper.COLUMN_NAME)));
            	outing.id = c.getInt(c.getColumnIndexOrThrow(OutingOpenHelper.COLUMN_ID));
            	outings.add(outing);
            	Log.d(TAG, String.format("   %s", outing.name));
            }
            db.close();
            return outings;
		}

		protected void onPostExecute(ArrayList<Outing> outings) {
    		Log.d(TAG, "Loading from the database: finished");
    		adapter.clear();
    		if (outings != null) {
    			adapter.addAll(outings);
    		}
    	}
    }

    private class SyncTask extends AsyncTask<Void, Integer, ArrayList<Outing> > {
    	protected ArrayList<Outing> doInBackground(Void... dummy) {
    		ArrayList<Outing> outings = new ArrayList<Outing>();
    		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    		String server = sharedPref.getString("server", "");

    		Log.d(TAG, String.format("Sync from the server: %s", server));
    		String Url = String.format("%s/api/1.0/outing/?user__id=%d&api_key=%s&username=%s",
    								   server, mUserId, mApiKey, mUsername);
    		JSONObject json = HTTPHelper.downloadJSON(Url);

    		if (json == null) {
    			return null;
    		}

    		// Prepare the database for writing the data
            OutingOpenHelper dbHelper = new OutingOpenHelper(getApplicationContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            // Clear the database
            db.delete(OutingOpenHelper.TABLE_NAME, null, null);

            // Parse the results
    		try {
    			JSONArray jArray = json.getJSONArray("objects");
    			for (int i = 0; i < jArray.length(); i++) {
    				JSONObject obj = jArray.getJSONObject(i);
    				Outing out = new Outing(obj);
    				outings.add(out);

    				ContentValues values = new ContentValues();
    				values.put(OutingOpenHelper.COLUMN_ID, out.id);
    				values.put(OutingOpenHelper.COLUMN_NAME, out.name);
    				values.put(OutingOpenHelper.COLUMN_DESCRIPTION, out.description);
    				values.put(OutingOpenHelper.COLUMN_STATUS, out.status);
    				// TODO: add the dates (beginning, ending, alert)
    				values.put(OutingOpenHelper.COLUMN_LATITUDE, out.latitude);
    				values.put(OutingOpenHelper.COLUMN_LONGITUDE, out.longitude);
    				db.insert(OutingOpenHelper.TABLE_NAME, null, values);
    			}
    		} catch (JSONException e) {
    			outings = null;
    		} finally {
    			db.close();
    		}
    		return outings;
    	}
    	protected void onPostExecute(ArrayList<Outing> outings) {
    		Log.d(TAG, "Sync from server: finished");
    		adapter.clear();
    		if (outings != null) {
    			adapter.addAll(outings);
    		} else {
    			Toast toast = Toast.makeText(getApplicationContext(), "Unable to synchronize", Toast.LENGTH_SHORT);
    			toast.show();
    		}
    	}
    }
}
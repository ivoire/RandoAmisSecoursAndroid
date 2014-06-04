package org.randoamissecours;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
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

        // Create and populate the list of Outings
        ListView listView = (ListView) findViewById(R.id.list);
        ArrayList<Outing> outings = new ArrayList<Outing>();
        this.adapter = new OutingsAdapter(this, outings);
        adapter.setNotifyOnChange(true);
        listView.setAdapter(adapter);

        // TODO: remove this code
        // Add outings manually for testing
        Outing outing = new Outing("Dent de Crolles");
        outings.add(outing);
        outing = new Outing("Mont Blanc");
        outings.add(outing);

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

        // Check the user credential
        SharedPreferences settings = getSharedPreferences(LOGIN_PREFS, 0);
        mUserId = settings.getInt(LOGIN_PREFS_USER_ID, -1);

        // No credentials: launch the login activity
        if (mUserId < 0) {
        	// Start the login Activity
        	Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        	// The credentials will be grabbed later
        	startActivityForResult(intent, LOGIN_RESULT);
        }
        
        // Get the full credentials
        mUsername = settings.getString(LOGIN_PREFS_USERNAME, "");
        mProfileId = settings.getInt(LOGIN_PREFS_PROFILE_ID, -1);
        mApiKey = settings.getString(LOGIN_PREFS_APIKEY, "");
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
        }
        return super.onOptionsItemSelected(item);
    }

    private class SyncTask extends AsyncTask<Void, Integer, ArrayList<Outing> > {
    	protected ArrayList<Outing> doInBackground(Void... dummy) {
    		System.out.println("Launching the task");
    		ArrayList<Outing> outings = new ArrayList<Outing>();
    		// TODO: sync with the server
    		JSONObject json = HTTPHelper.downloadJSON("http://10.0.2.2:8000/api/1.0/outing/");
    		
    		if (json == null) {
    			// TODO: alert the user of the error
    			return outings;
    		}

    		// Parse the array of outings
    		try {
    			JSONArray jArray = json.getJSONArray("objects");
    			for (int i = 0; i < jArray.length(); i++) {
    				JSONObject obj = jArray.getJSONObject(i);
    				outings.add(new Outing(obj));
    			}
    		} catch (JSONException e) {
    			// TODO: alert the user
    		}
    		return outings;
    	}
    	protected void onPostExecute(ArrayList<Outing> outings) {
    		System.out.println("Task finished");
    		adapter.clear();
    		if (outings != null) {
    			adapter.addAll(outings);
    		}
    	}
    }
}
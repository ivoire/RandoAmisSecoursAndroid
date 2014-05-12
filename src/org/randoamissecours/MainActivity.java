package org.randoamissecours;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {
	public final static String OUTING_NAME = "org.randoamissecours.outing.name";
	public final static String OUTING_DESCRIPTION = "org.randoamissecours.outing.description";
	
	public OutingsAdapter adapter;

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
    		outings.add(new Outing("TODO"));
    		return outings;
    	}
    	protected void onPostExecute(ArrayList<Outing> outings) {
    		System.out.println("Task finished");
    		adapter.clear();
    		adapter.addAll(outings);
    	}
    }
}
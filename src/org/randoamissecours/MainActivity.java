package org.randoamissecours;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;


public class MainActivity extends ActionBarActivity {
	public final static String OUTING_NAME = "org.randoamissecours.outing.name";
	public final static String OUTING_DESCRIPTION = "org.randoamissecours.outing.description";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create and populate the list of Outings
        ListView listView = (ListView) findViewById(R.id.list);
        ArrayList<Outing> outings = new ArrayList<Outing>();
        final OutingsAdapter adapter = new OutingsAdapter(this, outings);
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
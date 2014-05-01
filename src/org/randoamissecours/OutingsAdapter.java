package org.randoamissecours;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class OutingsAdapter extends ArrayAdapter<Outing> {
	public OutingsAdapter(Context context, ArrayList<Outing> outings) {
		 super(context, android.R.layout.simple_list_item_1, outings);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Outing outing = getItem(position);
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
		}
		TextView name = (TextView) convertView.findViewById(android.R.id.text1);
		name.setText(outing.name);
		return convertView;
	}
}
package org.randoamissecours;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.json.JSONException;
import org.json.JSONObject;


public class Outing {
	public int id;
	public String name;
	public String description;
	public int status;
	public Date beginning;
	public Date ending;
	public Date alert;
	public float latitude;
	public float longitude;

	public Outing(JSONObject object){
		try {
			this.id = object.getInt("id");
			this.name = object.getString("name");
			this.description = object.getString("description");
			this.status = object.getInt("status");
			this.beginning = parseDate(object.getString("beginning"));
			this.ending = parseDate(object.getString("ending"));
			this.alert = parseDate(object.getString("alert"));
			this.latitude = (float)object.getDouble("latitude");
			this.longitude = (float)object.getDouble("longitude");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public Outing(String name) {
		this.name = name;
	}

	// Parse a String and return the corresponding date
	// The format is something like '2014-05-29T15:00:12'
	public final Date parseDate(String str) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

		try {
			return sdf.parse(str);
		} catch (ParseException e) {
			return null;
		}
	}

	public final String dateFormat(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		return sdf.format(date);
	}
}
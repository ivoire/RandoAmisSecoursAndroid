package org.randoamissecours;

import java.util.Date;

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
			this.beginning = (Date) object.get("beginning");
			this.ending = (Date)object.get("ending");
			this.alert = (Date)object.get("alert");
			this.latitude = (float)object.getDouble("latitude");
			this.longitude = (float)object.getDouble("longitude");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	public Outing(String name) {
		this.name = name;
	}
}
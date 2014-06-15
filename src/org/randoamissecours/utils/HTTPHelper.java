package org.randoamissecours.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.json.JSONObject;

import android.util.Base64;
import android.util.Log;

public class HTTPHelper {
	public static final String TAG = "HTTPHelper";

	public static JSONObject downloadJSON(String Url) {
		return HTTPHelper.downloadJSON(Url, null, null);
	}

	public static JSONObject downloadJSON(String Url, String login, String password) {
		HttpURLConnection urlConnection = null;
		InputStream is;
		String result = null;
		try {
			URL url = new URL(Url);
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestProperty("Content-type", "application/json");

			// If login/password are not null we must add Basic Authentication
	    	if (login != null && password != null) {
	    		String credentials = login + ":" + password;
	    		String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
	    		urlConnection.setRequestProperty("Authorization", "Basic " + base64EncodedCredentials);
	    	}

			is = new BufferedInputStream(urlConnection.getInputStream());
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
    		StringBuilder sb = new StringBuilder();
    		
    		String line = null;
    		while ((line = reader.readLine()) != null) {
    			sb.append(line + "\n");
    		}
    		result = sb.toString();
		} catch (MalformedURLException e1) {
			Log.e(TAG, String.format("Malformated url: '%s'", Url));
		} catch (IOException e) {
			Log.e(TAG, "I/O exception");
		} finally {
			urlConnection.disconnect();
			if (result != null) {
				try {
					return new JSONObject(result);
				} catch (Exception e) {
					Log.e(TAG, String.format("Invalid JSON object: '%s'", result));
					return null;
				}
			}
		}
		return null;
    }
}
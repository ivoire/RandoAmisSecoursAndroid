package org.randoamissecours.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

public class HTTPHelper {
	public static JSONObject downloadJSON(String Url) {
		return HTTPHelper.downloadJSON(Url, null, null);
	}

	public static JSONObject downloadJSON(String Url, String login, String password) {
    	HttpUriRequest request = new HttpGet(Url);
    	request.setHeader("Content-type", "application/json");
    	
    	// If login/password are not null we must add Basic Authentication
    	// TODO: Basic auth
    	
    	InputStream is = null;
    	String result = null;
    	
    	try {
    		DefaultHttpClient client = new DefaultHttpClient();
    		HttpResponse response = client.execute(request);
    		HttpEntity entity = response.getEntity();
    		
    		is = entity.getContent();
    		BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
    		StringBuilder sb = new StringBuilder();
    		
    		String line = null;
    		while ((line = reader.readLine()) != null) {
    			sb.append(line + "\n");
    		}
    		result = sb.toString();
    	} catch (Exception e) {
    		result = null;
    	} finally {
    		try {
    			if (is != null) {
    				is.close();
    			}
    			if (result != null) {
    				return new JSONObject(result);
    			}
    		} catch (Exception e) {
    			// nothing to do
    		}
    	}
    	return null;
    }
}
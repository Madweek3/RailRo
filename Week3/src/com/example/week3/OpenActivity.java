package com.example.week3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

public class OpenActivity extends Activity {
	// public static ArrayList<HashMap<String, String>> roadMap;
	public static HashMap<String, Station> roadMap;
	public static ArrayList<Edge> roadEdge;
	
	Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_open);
		
		try {
			// ----------------------------- GET EVERY STATION INFOMATION FROM WEB SERVER
			String mlink = "http://madcamptest.dothome.co.kr/5/select.php";
			JSONArray data = new JSONArray(new JSONParse().execute(mlink).get());

			roadMap = new HashMap<String, Station>();
			
			for (int i = 0; i < data.length(); i++) {
				roadMap.put(data.getJSONObject(i).getString("STATION"), new Station(
						data.getJSONObject(i).getString("STATION"),
						data.getJSONObject(i).getInt("MAP_X"),
						data.getJSONObject(i).getInt("MAP_Y"),
						data.getJSONObject(i).getDouble("LAT"),
						data.getJSONObject(i).getDouble("LONG"), 
						data.getJSONObject(i).getLong("RATING")));
			}
			
			/*
			 * HashMap<String, String> temp = new HashMap<String, String>();
			 * temp.put("LINE", data.getJSONObject(i).getString("LINE"));
			 * temp.put("TAG", data.getJSONObject(i).getString("TAG"));
			 * temp.put("STATION", data.getJSONObject(i).getString("STATION"));
			 * temp.put("MAP_X", data.getJSONObject(i).getString("MAP_X"));
			 * temp.put("MAP_Y", data.getJSONObject(i).getString("MAP_Y"));
			 * temp.put("LAT", data.getJSONObject(i).getString("LAT"));
			 * temp.put("LONG", data.getJSONObject(i).getString("LONG"));
			 * 
			 * roadMap.add(temp);
			 */

			// ----------------------------- GET EVERY EDGE INFOMATION FROM WEB
			mlink = "http://madcamptest.dothome.co.kr/5/select_edge.php";
			data = new JSONArray(new JSONParse().execute(mlink).get());

			roadEdge = new ArrayList<Edge>();
			for (int i = 0; i < data.length(); i++) {
				roadEdge.add(new Edge(
						data.getJSONObject(i).getString("LINE"),
						data.getJSONObject(i).getString("FROM"),
						data.getJSONObject(i).getString("TO"),
						data.getJSONObject(i).getInt("MIN")));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		
		Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Intent intent = new Intent(OpenActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1500);   
	}
	
	class JSONParse extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			StringBuilder strBuilder = new StringBuilder();
			HttpClient hClient = new DefaultHttpClient();
			HttpGet hGet = new HttpGet(params[0]);

			try {
				HttpResponse response = hClient.execute(hGet);
				StatusLine sLine = response.getStatusLine();
				int statusCode = sLine.getStatusCode();

				if (statusCode == 200) {
					HttpEntity entity = response.getEntity();
					InputStream content = entity.getContent();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(content));
					String line;
					while ((line = reader.readLine()) != null)
						strBuilder.append(line);
				} 
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return strBuilder.toString();
		}
		
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
		}
	}


}

package com.example.week3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;

public class Fragment3 extends Fragment {
	private View view;
	ListView listv;
	TextView tvIsConnected;
	Button btn_b;
	Button btn_n;
	String output;
	String searcher = "developer";
	String[] values_name = {};
	String[] values_desc = {};
	String[] search_string;
	int page = 0;

	public Fragment3() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.fragment3, container, false);

		search_string = new String[2];
		search_string[0] = searcher;
		search_string[1] = Integer.toString(page);
		try {
			output = new HttpAsyncTask().execute(search_string).get();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ExecutionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// get reference to the views
		listv = (ListView) view.findViewById(R.id.list);
		tvIsConnected = (TextView) view.findViewById(R.id.tvIsConnected);
		btn_b = (Button) view.findViewById(R.id.f3_button_before);
		btn_n = (Button) view.findViewById(R.id.f3_button_next);
		btn_b.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				try {
					if (page != 0)
						page -= 4;
					search_string = new String[2];
					search_string[0] = searcher;
					search_string[1] = Integer.toString(page);
					output = new HttpAsyncTask().execute(search_string).get();
					if(page==0)
						btn_b.setEnabled(false);
					makeview(listv);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		btn_n.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				try {
					page += 4;
					search_string = new String[2];
					search_string[0] = searcher;
					search_string[1] = Integer.toString(page);
					output = new HttpAsyncTask().execute(search_string).get();
					if(page>=4)
						btn_b.setEnabled(true);
					makeview(listv);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		// check if you are connected or not
		if (isConnected()) {
			tvIsConnected.setBackgroundColor(0xFF00CC00);
			tvIsConnected.setText("You are conncted");
		} else {
			tvIsConnected.setText("You are NOT conncted");
		}

		makeview(listv);
		return view;
	}
	
	private void makeview(ListView listv){
		// call AsynTask to perform network operation on separate thread
				try {
					JSONObject json = new JSONObject(output)
							.getJSONObject("responseData");
					JSONArray jarray = json.getJSONArray("results");
					values_name = new String[jarray.length()];
					values_desc = new String[jarray.length()];
					for (int i = 0; i < jarray.length(); i++) {
						String name = jarray.getJSONObject(i).getString(
								"titleNoFormatting");
						String desc = jarray.getJSONObject(i).getString("content");
						values_name[i] = name;
						values_desc[i] = desc;
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						this.getActivity(), R.layout.list_elem, R.id.name, values_name);
				// ArrayAdapter<String> adapter2 = new
				// ArrayAdapter<String>(this.getActivity(),R.layout.list_elem,
				// R.id.desc,values_desc);
				listv.setAdapter(adapter);
				// listv.setAdapter(adapter2);
	}

	public static String GET(String my_search, String count) {
		URL url = null;
		StringBuilder builder = null;
		try {
			url = new URL(
					"https://ajax.googleapis.com/ajax/services/search/web?v=1.0&"
							+ "q=+" + my_search + "&newwindow=1&start=" + count);
			URLConnection connection = url.openConnection();
			String line;
			builder = new StringBuilder();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return builder.toString();
	}

	public boolean isConnected() {
		ConnectivityManager connMgr = (ConnectivityManager) getActivity()
				.getSystemService(Activity.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected())
			return true;
		else
			return false;
	}

	private class HttpAsyncTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {

			return GET(urls[0], urls[1]);
		}

		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

		}
	}
}

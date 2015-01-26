package com.example.week3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.android.gms.maps.SupportMapFragment;

public class Fragment3 extends Fragment {
	private View view;
	ListView listv;
	Button btn_b;
	Button btn_n;
	String output;
	static String searcher = "%EB%82%B4%EC%9D%BC%EB%A1%9C"; //내일로 -> UTF-8 인코딩
	static String extra = "";
	String[] values_name = {};
	String[] values_desc = {};
	String[] search_string;
	
	//private Grap user;
	int page = 0;
    private SupportMapFragment fragment;
    private Context context;
    ArrayAdapter<CharSequence> keywordlist;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// initialize
		view = inflater.inflate(R.layout.fragment3, container, false);
		context = getActivity();
		
		output = dataLoad();
		
		// add spinner
		Spinner sp = (Spinner) view.findViewById(R.id.keywordSpinner);
		sp.setPrompt("검색 키워드 바꾸기");
		keywordlist = ArrayAdapter.createFromResource(getActivity(), R.array.searchKeyword, android.R.layout.simple_spinner_item);
		keywordlist.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp.setAdapter(keywordlist);
		sp.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?>  parent, View view, int position, long id) {
            	try {
					extra = URLEncoder.encode(keywordlist.getItem(position).toString(), "UTF-8");
					output = dataLoad();
					makeview(listv);	
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
            }
            public void onNothingSelected(AdapterView<?>  parent) {
            }
        });
		
		// get reference to the views
		listv = (ListView) view.findViewById(R.id.list);
		btn_b = (Button) view.findViewById(R.id.f3_button_before);
		btn_n = (Button) view.findViewById(R.id.f3_button_next);
		btn_b.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (page != 0)
					page -= 4;

				output = dataLoad();
				if (page == 0)
					btn_b.setEnabled(false);
				makeview(listv);

			}
		});

		btn_n.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				page += 4;

				output = dataLoad();
				if (page >= 4)
					btn_b.setEnabled(true);
				makeview(listv);

			}
		});

		makeview(listv);
		return view;
	}
	
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
		if (((MainActivity) context).getCurrentPage() == 2) {
			FragmentManager fm = getChildFragmentManager();
			fragment = (SupportMapFragment) fm
					.findFragmentById(R.id.map_container);
			if (fragment == null) {
				fragment = SupportMapFragment.newInstance();
				fm.beginTransaction().replace(R.id.map_container, fragment)
						.commit();
			}
		}
	}

	private String dataLoad () {
		if (((MainActivity) context).getCurrentPage() == 2) {
			try {
				search_string = new String[2];

				search_string[0] = searcher + extra;
				search_string[1] = Integer.toString(page);

				return new HttpAsyncTask().execute(search_string).get();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				e1.printStackTrace();
			}
		}
		
		return "";
	}
	
	private void makeview(ListView listv) {
		// call AsynTask to perform network operation on separate thread
		try {
			JSONObject json = new JSONObject(output).getJSONObject("responseData");
			JSONArray jarray = json.getJSONArray("results");
			values_name = new String[jarray.length()];
			values_desc = new String[jarray.length()];
			for (int i = 0; i < jarray.length(); i++) {
				String name = jarray.getJSONObject(i).getString("titleNoFormatting");
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
		listv.setAdapter(adapter);
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

	private ProgressDialog pDialog;
	
	private class HttpAsyncTask extends AsyncTask<String, Void, String> {
		 @Override
	      protected void onPreExecute() {
			 pDialog = new ProgressDialog(context);
		     pDialog.setMessage("Getting Data ...");
		     pDialog.setIndeterminate(false);
		     pDialog.setCancelable(true);
			 pDialog.show();
	         super.onPreExecute();
	      }
		 
		@Override
		protected String doInBackground(String... urls) {
			return GET(urls[0], urls[1]);
		}

		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			pDialog.hide();
		}
	}

}
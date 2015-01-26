package com.example.week3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
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
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;



import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class Fragment3 extends Fragment {
	private View view;
	private GoogleMap gmap;
	private MapView mapView;
	   
	ListView listv;
	
	Button btn_b;
	Button btn_n;
	String output;
	static String searcher_org = "내일로";
	static String searcher = "%EB%82%B4%EC%9D%BC%EB%A1%9C"; //내일로 -> UTF-8 인코딩
	static String extra = "";
	String[] values_name = {};
	String[] values_desc = {};
	String[] values_url = {};
	String[] search_string;
	
	//private Grap user;
	int page = 0;
    // private SupportMapFragment fragment;
    private Context context;
    ArrayAdapter<CharSequence> keywordlist;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		MarkerOptions markerOptions = null;
	    Marker marker = null;
	      
		// initialize
		view = inflater.inflate(R.layout.fragment3, container, false);
		context = getActivity();

		mapView = (MapView) view.findViewById(R.id.mapedit);
		mapView.onCreate(savedInstanceState);
		
        if(mapView != null){
           MapsInitializer.initialize(getActivity());
           gmap = mapView.getMap();

           Station tempSt = OpenActivity.roadMap.get(searcher_org);
           if(tempSt != null){
        	   // Log.v("Ahha", tempSt.latitude + ", " + tempSt.longitude);
        	   LatLng latLng = new LatLng(tempSt.latitude, tempSt.longitude);
        	   
        	   gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        	   markerOptions = new MarkerOptions();
               markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.mark));
               markerOptions.position(latLng);
               gmap.addMarker(markerOptions);
           }
        }

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
					page = 0;
					btn_b.setEnabled(false);
					btn_n.setEnabled(true);
					
					output = dataLoad();
					makeview(listv);	
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
            }
            
            public void onNothingSelected(AdapterView<?>  parent) {}
        });
		
		// get reference to the views
		listv = (ListView) view.findViewById(R.id.list);

		listv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            	Intent i = new Intent(Intent.ACTION_VIEW);
            	Uri u = Uri.parse(values_url[position]);
            	i.setData(u);
            	startActivity(i);
            }
	    });
        
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

		output = dataLoad();
		makeview(listv);
		
		return view;
	}
	
//	@Override
//    public void onActivityCreated(Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//	
//        if (((MainActivity) context).getCurrentPage() == 2) {
//			FragmentManager fm = getChildFragmentManager();
//			fragment = (SupportMapFragment) fm.findFragmentById(R.id.map_container);
//			if (fragment == null) {
//				fragment = SupportMapFragment.newInstance();
//				fm.beginTransaction().replace(R.id.map_container, fragment)
//						.commit();
//			}
//		}
//	}

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
			values_url = new String[jarray.length()];
			
			for (int i = 0; i < jarray.length(); i++) {
				String name = jarray.getJSONObject(i).getString("titleNoFormatting");
				String desc = jarray.getJSONObject(i).getString("content").replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "");
				String url = jarray.getJSONObject(i).getString("url");
				values_name[i] = name;
				values_desc[i] = desc;
				values_url[i] = url;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		SearchAdapter adapter = new SearchAdapter();
		listv.setAdapter(adapter);
	}

	private class SearchAdapter extends BaseAdapter{
		public SearchAdapter(){
			
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final CustomSearchHolder holder;
			LayoutInflater mInflater = LayoutInflater.from(context);

			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.list_elem, parent, false);

				holder = new CustomSearchHolder();
				holder.mName = (TextView) convertView.findViewById(R.id.name);
				holder.mContent = (TextView) convertView.findViewById(R.id.desc);

				convertView.setTag(holder);
			} else
				holder = (CustomSearchHolder) convertView.getTag();

			holder.mName.setText(values_name[position]);
			holder.mContent.setText(values_desc[position]);

			return convertView;
		}

		class CustomSearchHolder {
			TextView mName;
			TextView mContent;
		}

		@Override
		public int getCount() {
			return values_name.length;
		}

		@Override
		public Object getItem(int position) {
			return values_name[position];
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}
	}
	
	private class HttpAsyncTask extends AsyncTask<String, Void, String> {
		private ProgressDialog pDialog;
		
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
			StringBuilder strBuilder = new StringBuilder();
			HttpClient hClient = new DefaultHttpClient();
			HttpGet hGet = new HttpGet("https://ajax.googleapis.com/ajax/services/search/web?v=1.0&"
					+ "q=+" + urls[0] + "&newwindow=1&start=" + urls[1]);

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
			/*
			URL url = null;
			StringBuilder builder = null;
			try {
				url = new URL(
						"https://ajax.googleapis.com/ajax/services/search/web?v=1.0&"
								+ "q=+" + urls[0] + "&newwindow=1&start=" + urls[1]);
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
			*/
		}

		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			pDialog.dismiss();
		}
	}

	@Override
	   public void onResume() {
	       super.onResume();
	       mapView.onResume();
	   }

	   @Override
	   public void onPause() {
	       super.onPause();
	       mapView.onPause();
	   }

	   @Override
	   public void onDestroy() {
	       super.onDestroy();
	       mapView.onDestroy();
	   }

	   @Override
	   public void onLowMemory() {
	       super.onLowMemory();
	       mapView.onLowMemory();
	   }
	
}
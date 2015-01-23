package com.example.week3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
// import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Filter;
import android.widget.TextView;
import android.widget.Toast;

public class Fragment1 extends Fragment {
	private View view;
	
	CustomAdapter mAdapter;
	ListView stationView;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment1, container, false);
		
		ArrayList<String> linelist = new ArrayList<String>();
		
		for(String key : MainActivity.roadMap.keySet()){
			Station value = MainActivity.roadMap.get(key);
			String temp = value.name;
			
			if(!linelist.contains(temp))
				linelist.add(temp);
		}
		
		Collections.sort(linelist);
		
		// SET LISTVIEW
		mAdapter = new CustomAdapter(getActivity(), R.layout.station_list, linelist);
		
		stationView = (ListView) view.findViewById(R.id.stationView);
        stationView.setItemsCanFocus(false);
        stationView.setTextFilterEnabled(true);
        
        stationView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	        	((ImageView)v.findViewById(R.id.imageView1)).setImageResource(R.drawable.empty);
	        	((ImageView)v.findViewById(R.id.imageView2)).setImageResource(R.drawable.empty);
	        	
	        	TextView tempText = (TextView) v.findViewById(R.id.label);
	        	ImageView tempImg3 = (ImageView)v.findViewById(R.id.imageView3);
	    
	        	if(MainActivity.trans.contains(tempText.getText().toString())){
	        		tempImg3.setImageResource(R.drawable.empty);
	        		toggleTo(tempText.getText().toString(), 0);
	        		MainActivity.trans.remove(tempText.getText().toString());
	        	}
	        	else{
	        		tempImg3.setImageResource(R.drawable.trans);
	        		toggleTo(tempText.getText().toString(), 3);
	        		MainActivity.trans.add(tempText.getText().toString());
	        	}
	        }
	    });
	     
        stationView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
        	@Override
            public boolean onItemLongClick(AdapterView<?> parent, View v, int pos, long id) {
	        	TextView tempText = (TextView) v.findViewById(R.id.label);
        		((ImageView)v.findViewById(R.id.imageView3)).setImageResource(R.drawable.empty);
        		
	        	if(MainActivity.start.equals("")){
	        		ImageView tempImg1 = (ImageView) v.findViewById(R.id.imageView1);
	        		tempImg1.setImageResource(R.drawable.start);
	        		toggleTo(tempText.getText().toString(), 1);
	        		MainActivity.start = tempText.getText().toString();
	        	}
	        	else if(MainActivity.end.equals("")){
	        		ImageView tempImg2 = (ImageView) v.findViewById(R.id.imageView2);
	        		tempImg2.setImageResource(R.drawable.end);
	        		toggleTo(tempText.getText().toString(), 2);
	        		MainActivity.end = tempText.getText().toString();
	        	}
	        	
	            return true;
	        }
	    });
        
        stationView.setAdapter(mAdapter);
        
        // SET EDITBOX
        EditText edit = (EditText) view.findViewById(R.id.editText);
        edit.setFocusable(false);
		edit.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				mAdapter.getFilter().filter(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		
		Button submitButton = (Button) view.findViewById(R.id.submitButton);
		
		submitButton.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		    	if(MainActivity.start.equals(""))
		    		Toast.makeText(getActivity(), "출발지를 설정해주세요", 100).show();
		    	else if(MainActivity.end.equals(""))
		    		Toast.makeText(getActivity(), "도착지를 설정해주세요", 100).show();
		    	else
		    		((MainActivity)getActivity()).setCurrentPage(1);
			}
		});
		return view;
	}
	
	/* --------------- STATUS INDEX --------------- */
    // 1 : START    2 : END    3 : TRANSFER 
	
	static void toggleTo(String lb, int to){
		// Log.v("Hello", start + "/" + end );
		if(lb.equals(MainActivity.start) && to != 1 && to != 2)
			MainActivity.start = "";
		
		if(lb.equals(MainActivity.end) && to != 1 && to != 2)
			MainActivity.end = "";
		
		if(MainActivity.trans.contains(lb) && to != 3)
			MainActivity.trans.remove(lb);
	}

	class CustomAdapter extends ArrayAdapter<String> {
		ArrayList<String> mList;
		ArrayList<String> fList;

		private LayoutInflater mInflater;
		private StationFilter filter;

		public CustomAdapter(Context context, int textViewResourceId,
				ArrayList<String> _mList) {
			super(context, textViewResourceId, _mList);
			mList = _mList;
			fList = _mList;

			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return fList.size();
		}

		@Override
		public String getItem(int position) {
			return fList.get(position);
		}

		@Override
		public Filter getFilter() {
			if (filter == null)
				filter = new StationFilter();

			return filter;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			CustomHolder holder;

			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.station_list, parent,
						false);

				// 홀더 생성 및 Tag로 등록
				holder = new CustomHolder();
				holder.mTextView = (TextView) convertView
						.findViewById(R.id.label);
				holder.mRating = (TextView) convertView
						.findViewById(R.id.rating);
				holder.mImage1 = (ImageView) convertView
						.findViewById(R.id.imageView1);
				holder.mImage2 = (ImageView) convertView
						.findViewById(R.id.imageView2);
				holder.mImage3 = (ImageView) convertView
						.findViewById(R.id.imageView3);

				convertView.setTag(holder);
			} else
				holder = (CustomHolder) convertView.getTag();

			if (MainActivity.trans.contains(fList.get(position)))
				holder.mImage3.setImageResource(R.drawable.trans);
			else
				holder.mImage3.setImageResource(R.drawable.empty);

			if (MainActivity.start.equals(fList.get(position)))
				holder.mImage1.setImageResource(R.drawable.start);
			else
				holder.mImage1.setImageResource(R.drawable.empty);

			if (MainActivity.end.equals(fList.get(position)))
				holder.mImage2.setImageResource(R.drawable.end);
			else
				holder.mImage2.setImageResource(R.drawable.empty);

			String star = "";
			float rate = MainActivity.roadMap.get(fList.get(position)).rating;
			if (rate != 0) {
				for (int i = 0; i < rate; i++)
					star += "★";
				star = star + " " + String.format("%.1f", rate);
			}
			holder.mRating.setText(star);

			holder.mTextView.setText(fList.get(position));

			return convertView;
		}

		private class CustomHolder {
			TextView mTextView;
			TextView mRating;
			ImageView mImage1;
			ImageView mImage2;
			ImageView mImage3;
		}

		private class StationFilter extends Filter {

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				String filterString = constraint.toString().toLowerCase();

				FilterResults results = new FilterResults();

				final ArrayList<String> list = mList;

				int count = list.size();
				final ArrayList<String> nlist = new ArrayList<String>(count);

				String filterableString;

				for (int i = 0; i < count; i++) {
					filterableString = list.get(i);
					if (filterableString.toLowerCase().contains(filterString)) {
						nlist.add(filterableString);
					}
				}

				results.values = nlist;
				results.count = nlist.size();

				return results;
			}

			@Override
			protected void publishResults(CharSequence constraint,
					FilterResults results) {
				fList = (ArrayList<String>) results.values;
				notifyDataSetChanged();
			}
		}
	}
}



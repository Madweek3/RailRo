package com.example.week3;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
// import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Filter;
import android.widget.TextView;

public class Fragment1 extends Fragment {
	private View view;
	
	public static ArrayList<String> trans;
	public static String start;
	public static String end;
	
	CustomAdapter mAdapter;
	ListView stationView;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment1, container, false);
		trans = new ArrayList<String>();
		
		ArrayList<String> linelist = new ArrayList<String>();
		
		for(int i = 0; i < MainActivity.roadMap.size(); i++){
			String temp = MainActivity.roadMap.get(i).name;
			
			if(!linelist.contains(temp))
				linelist.add(temp);
		}
		
		start = "";
		end = "";
		
		// SET LISTVIEW
		mAdapter = new CustomAdapter(getActivity(), R.layout.station_list, linelist);
		
		stationView = (ListView) view.findViewById(R.id.stationView);
		stationView.setAdapter(mAdapter);
        stationView.setItemsCanFocus(false);
        stationView.setTextFilterEnabled(true);
        
        stationView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	        	((ImageView)v.findViewById(R.id.imageView1)).setImageResource(R.drawable.empty);
	        	((ImageView)v.findViewById(R.id.imageView2)).setImageResource(R.drawable.empty);
	        	
	        	TextView tempText = (TextView) v.findViewById(R.id.label);
	        	ImageView tempImg3 = (ImageView)v.findViewById(R.id.imageView3);
	    
	        	if(trans.contains(tempText.getText().toString())){
	        		tempImg3.setImageResource(R.drawable.empty);
	        		toggleTo(tempText.getText().toString(), 0);
	        		trans.remove(tempText.getText().toString());
	        	}
	        	else{
	        		tempImg3.setImageResource(R.drawable.trans);
	        		toggleTo(tempText.getText().toString(), 1);
	        		trans.add(tempText.getText().toString());
	        	}
	        }
	    });
	     
        stationView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
        	@Override
            public boolean onItemLongClick(AdapterView<?> parent, View v, int pos, long id) {
        		
	        	TextView tempText = (TextView) v.findViewById(R.id.label);
        		((ImageView)v.findViewById(R.id.imageView3)).setImageResource(R.drawable.empty);
        		
	        	if(start.equals("")){
	        		ImageView tempImg1 = (ImageView) v.findViewById(R.id.imageView1);
	        		tempImg1.setImageResource(R.drawable.start);
	        		toggleTo(tempText.getText().toString(), 1);
	        		start = tempText.getText().toString();
	        	}
	        	else if(end.equals("")){
	        		ImageView tempImg2 = (ImageView) v.findViewById(R.id.imageView2);
	        		tempImg2.setImageResource(R.drawable.end);
	        		toggleTo(tempText.getText().toString(), 2);
	        		end = tempText.getText().toString();
	        	}
	        	
	            return true;
	        }
	    });
        
        // SET EDITBOX
        EditText edit = (EditText) view.findViewById(R.id.editText);

		edit.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				mAdapter.getFilter().filter(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				/*
				SparseBooleanArray checked = stationView.getCheckedItemPositions();
				for (int i = 0; i < MainActivity.roadMap.size(); i++) {
					if (checked.get(i) == true) {
						String name = mAdapter.getItem(i).toString();

						if (!trans.contains(name))
							trans.add(name);
					}
				}
				*/
			}

			@Override
			public void afterTextChanged(Editable s) {
				/*
				for (int i = 0; i < stationView.getCount(); i++)
					stationView.setItemChecked(i, false);

				mAdapter.getFilter().filter(s, new Filter.FilterListener() {
					public void onFilterComplete(int count) {
						mAdapter.notifyDataSetChanged();

						for (int i = 0; i < mAdapter.getCount(); i++) {
							if (trans.contains(mAdapter.getItem(i).toString()))
								stationView.setItemChecked(i, true);
							else
								stationView.setItemChecked(i, false);
						}

					}
				});
				*/
			}
		});

		return view;
	}
	
	/* --------------- STATUS INDEX
     * 1 : START
     * 2 : END
     * 3 : TRANSFER */
	
	static void toggleTo(String lb, int to){
		// Log.v("Hello", start + "/" + end );
		if(lb.equals(start) && to != 1 && to != 2)
			start = "";
		
		if(lb.equals(end) && to != 1 && to != 2)
			end = "";
		
		if(trans.contains(lb) && to != 3)
			trans.remove(lb);
	}
}

class CustomAdapter extends ArrayAdapter<String> {
	ArrayList<String> mList;
	ArrayList<String> fList;
	private StationFilter filter;
	
	public CustomAdapter (Context context, int textViewResourceId, ArrayList<String> _mList) {
		super(context, textViewResourceId, _mList);
		mList = _mList;
		fList = _mList;
	}
	
	@Override
	  public Filter getFilter() {
	   if (filter == null)
	    filter = new StationFilter();
	   
	   return filter;
	  }
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Context context = parent.getContext();
		
		TextView text = null;
	    ImageView img1 = null;
	    ImageView img2 = null;
	    ImageView img3 = null;
	    CustomHolder holder = null;
	     
	    if (convertView == null) {
	        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        convertView = inflater.inflate(R.layout.station_list, parent, false);
	         
	        text = (TextView) convertView.findViewById(R.id.label);
	        img1 = (ImageView) convertView.findViewById(R.id.imageView1);
	        img2 = (ImageView) convertView.findViewById(R.id.imageView2);
	        img3 = (ImageView) convertView.findViewById(R.id.imageView3);
	        
	        // 홀더 생성 및 Tag로 등록
	        holder = new CustomHolder();
	        holder.mTextView = text;
	        holder.mImage1 = img1;
	        holder.mImage2 = img2;
	        holder.mImage3 = img3;
	        
	        convertView.setTag(holder);
	    }
	    else {
	        holder = (CustomHolder) convertView.getTag();
	        text = holder.mTextView;
	        img1 = holder.mImage1;
	        img2 = holder.mImage2;
	        img3 = holder.mImage3;
	    }
	    
	    Log.v("POSITION", Integer.toString(position));
	    
	    text.setText(mList.get(position));
	    
		return convertView;
	}
	
	private class CustomHolder {
	    TextView mTextView;
	    ImageView mImage1;
	    ImageView mImage2;
	    ImageView mImage3;
	}
	
	private class StationFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			FilterResults results = new FilterResults();
		    
		    if (constraint == null || constraint.length() == 0) {
		        results.values = mList;
		        results.count = mList.size();
		    }
		    else {
		        ArrayList<String> tempList = new ArrayList<String>();
		         
		        for (String p : mList) {
		            if (p.toUpperCase().startsWith(constraint.toString().toUpperCase()))
		                tempList.add(p);
		        }
		         
		        results.values = tempList;
		        results.count = tempList.size();
		 
		    }
		    return results;
		}

		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			
			if (results.count == 0)
		        notifyDataSetInvalidated();
		    else {
		        mList = (ArrayList<String>) results.values;
		        notifyDataSetChanged();
		    }
		}
		
	}
}

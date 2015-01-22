package com.example.week3;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class Fragment2 extends Fragment {
	private View view;
	public static ArrayList<Station> visit_station;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment2, container, false);

		ArrayList<String> visited_list = new ArrayList<String>();

		for (int i = 0; i < visit_station.size(); i++)
			visited_list.add(visit_station.get(i).name);

		// ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, visited_list);
		CustomVisitAdapter mAdapter = new CustomVisitAdapter(getActivity(), R.layout.station_list, visited_list);
		ListView list = (ListView) view.findViewById(R.id.visitView);
		list.setDivider(new ColorDrawable(Color.TRANSPARENT));
		list.setAdapter(mAdapter);

		setListViewHeightBasedOnChildren(list);

		return view;
	}

	public void setListViewHeightBasedOnChildren(ListView listView) {
		ArrayAdapter<String> listAdapter = (ArrayAdapter<String>) listView.getAdapter();
		if (listAdapter == null) {
			return;
		}

		int totalHeight = listView.getPaddingTop()
				+ listView.getPaddingBottom();
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			if (listItem instanceof ViewGroup) {
				listItem.setLayoutParams(new LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			}
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}

	class CustomVisitAdapter extends ArrayAdapter<String> {
		ArrayList<String> mList;
		private LayoutInflater mInflater;

		public CustomVisitAdapter(Context context, int textViewResourceId, ArrayList<String> _mList) {
			super(context, textViewResourceId, _mList);
			mList = _mList;

			mInflater = LayoutInflater.from(context);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			CustomVisitHolder holder;

			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.visit_list, parent, false);

				// 홀더 생성 및 Tag로 등록
				holder = new CustomVisitHolder();
				holder.mLineImage = (ImageView) convertView.findViewById(R.id.lineImage);
				holder.mVisitText = (TextView) convertView.findViewById(R.id.visitText);
				holder.mMapButton = (ImageButton) convertView.findViewById(R.id.mapButton);

				convertView.setTag(holder);
			} else
				holder = (CustomVisitHolder) convertView.getTag();

			String s_name = mList.get(position);
			Station s_object = MainActivity.roadMap.get(s_name);
			
			holder.mVisitText.setText(s_name);
			if(s_object.line.equals("경부선")){
				if(position == 0)
					holder.mLineImage.setImageResource(R.drawable.gb_start);
				else if(position == mList.size()-1)
					holder.mLineImage.setImageResource(R.drawable.gb_end);
				else
					holder.mLineImage.setImageResource(R.drawable.gb_trans);
			}
			return convertView;
		}

		private class CustomVisitHolder {
			ImageView mLineImage;
			TextView mVisitText;
			ImageButton mMapButton;
		}
	}
}

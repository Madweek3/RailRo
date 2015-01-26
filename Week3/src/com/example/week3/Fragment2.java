package com.example.week3;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import com.kakao.KakaoLink;
import com.kakao.KakaoParameterException;
import com.kakao.KakaoTalkLinkMessageBuilder;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class Fragment2 extends Fragment {
	private View view;
	public static ArrayList<Station> visit_station;
	public static ArrayList<Integer> not_selected_station;
	ListView list;
	
	boolean hide = false;
	CustomVisitAdapter mAdapter;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment2, container, false);

		final ArrayList<String> visited_list = new ArrayList<String>();
		final ArrayList<Integer> real_visited_list = new ArrayList<Integer>();
		
		for (int i = 0; i < visit_station.size(); i++)
			visited_list.add(visit_station.get(i).name);

		for (int i = 0; i < not_selected_station.size(); i++)
			real_visited_list.add(not_selected_station.get(i));
		
		// ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, visited_list);
		mAdapter = new CustomVisitAdapter(getActivity(), R.layout.station_list, visited_list);
		
		list = (ListView) view.findViewById(R.id.visitView);
		list.setDivider(new ColorDrawable(Color.TRANSPARENT));
		list.setAdapter(mAdapter);
		setListViewHeightBasedOnChildren(list);

		CheckBox checkVisible = (CheckBox) view.findViewById(R.id.checkBox1);
		checkVisible .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				for (Integer i : real_visited_list) {
					if(isChecked){
						list.getChildAt(i).setLayoutParams(new ListView.LayoutParams(-1, 1));
						list.getChildAt(i).setVisibility(View.GONE);
						hide = true;
					}
					else{
						list.getChildAt(i).setLayoutParams(new ListView.LayoutParams(-1, -2));
						list.getChildAt(i).setVisibility(View.VISIBLE);
						hide = false;
					}
					
					setListViewHeightBasedOnChildren(list);
				}
			}
		});
		
		ImageButton kakao = (ImageButton) view.findViewById(R.id.kakaoButton);
		kakao.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				try {
					KakaoLink kakaoLink = KakaoLink.getKakaoLink(getActivity());
					final KakaoTalkLinkMessageBuilder kakaoTalkLinkMessageBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();
					
					String route = "";
					for(String p : visited_list){
						route += ("▽ " + p + "\n");
					}
					String text = /*"............☆ ☆ ☆ ☆ ☆ ☆\n"
							+ "..........☆\n" 
							+ "........☆\n"
							+ "......┏┓\n"
							+ "┏━┻┻━━┑┍━━━━━┑\n"
							+ "┗▣▣▣▣▣││▣▣▣▣▣│\n"
							+ "●≒≒●≒≒●≒≒●≒≒●≒\n\n"
							+*/ "내일로 여행 어디로 갈까???\n\n"
							+ "▼ 즐거운 출발~~ :)\n"
							+ route
							+ "▲ 도착!\n\nFrom MADCAMP";
					
					kakaoTalkLinkMessageBuilder.addImage("http://sangsangdeco.com/web/product/big/sangsangdeco_4757.jpg", 500, 500).addText(text).build();
					
					final String linkContents = kakaoTalkLinkMessageBuilder.build();
					kakaoLink.sendMessage(linkContents, getActivity());
				} catch (KakaoParameterException e) {
					e.printStackTrace();
				}
			}
		});
		
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
/*
		@Override
		public int getCount(){
			if (hide)
				return visit_station.size()- not_selected_station.size();
			else
				return visit_station.size();
		}
*/	
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final CustomVisitHolder holder;

			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.visit_list, parent, false);

				// 홀더 생성 및 Tag로 등록
				holder = new CustomVisitHolder();
				holder.mLineImage = (ImageView) convertView.findViewById(R.id.lineImage);
				holder.mVisitText = (TextView) convertView.findViewById(R.id.visitText);
				holder.mTransImage = (ImageView) convertView.findViewById(R.id.transImage);
				holder.mMapButton = (ImageButton) convertView.findViewById(R.id.mapButton);
				
				convertView.setTag(holder);
			} else
				holder = (CustomVisitHolder) convertView.getTag();

			String s_name = mList.get(position);
			Station s_object = OpenActivity.roadMap.get(s_name);
			
			
			// CHECK TRANSFER
			if (s_object.transfer && position != 0 && position != mList.size()-1) {
				String prev_line = s_object.getEdge(mList.get(position - 1)).line;
				String next_line = s_object.getEdge(mList.get(position + 1)).line;
				
				if(!prev_line.equals(next_line)){
					holder.mTransImage.setImageResource(R.drawable.transfer);
					s_name += "(" + prev_line + "->" + next_line + ")";
				}
			}
			holder.mVisitText.setText(s_name);
			
			String linecheck;
			if(position == 0)
				linecheck = s_object.getEdge(mList.get(1)).line;
			else
				linecheck = s_object.getEdge(mList.get(position-1)).line;
			
			if(linecheck.equals("경부선")){
				if(position == 0)
					holder.mLineImage.setImageResource(R.drawable.gb_start);
				else if(position == mList.size()-1)
					holder.mLineImage.setImageResource(R.drawable.gb_end);
				else
					holder.mLineImage.setImageResource(R.drawable.gb_trans);
			}
			else if(linecheck.equals("경북선")){
				if(position == 0)
					holder.mLineImage.setImageResource(R.drawable.gbk_start);
				else if(position == mList.size()-1)
					holder.mLineImage.setImageResource(R.drawable.gbk_end);
				else
					holder.mLineImage.setImageResource(R.drawable.gbk_trans);
			}
			else if(linecheck.equals("중앙선")){
				if(position == 0)
					holder.mLineImage.setImageResource(R.drawable.ja_start);
				else if(position == mList.size()-1)
					holder.mLineImage.setImageResource(R.drawable.ja_end);
				else
					holder.mLineImage.setImageResource(R.drawable.ja_trans);
			}
			
			holder.mMapButton.setOnClickListener(new View.OnClickListener() {
			    @Override
			    public void onClick(View v) {
			    	try {
			    		Fragment3.searcher_org = holder.mVisitText.getText().toString();
						Fragment3.searcher = URLEncoder.encode(holder.mVisitText.getText().toString(), "UTF-8");
						Fragment3.extra = URLEncoder.encode(" 가볼 만한 곳", "UTF-8");
						((MainActivity)v.getContext()).setCurrentPage(2);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			});
			
			return convertView;
		}

		private class CustomVisitHolder {
			ImageView mLineImage;
			TextView mVisitText;
			ImageView mTransImage;
			ImageButton mMapButton;
		}
	}
	
	
}

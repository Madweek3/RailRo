package com.example.week3;

import com.nhn.android.maps.NMapView;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class Fragment4 extends Fragment {
	private View view;
	private final String API_KEY="bd0d9df1a88ac0e1b6b0cbc4798c2dca";
	
	
	
	
	public Fragment4() {
		// TODO Auto-generated constructor stub
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment4, container, false);	
		return view;
	}
}

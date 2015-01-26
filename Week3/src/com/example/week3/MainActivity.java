package com.example.week3;

import java.util.ArrayList;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements OnClickListener {

	private final long	FINSH_INTERVAL_TIME    = 2000;
	private long		backPressedTime        = 0;
	
	private int NUM_PAGES = 4;
	
	private final static int FRAGMENT1 = 0;
	private final static int FRAGMENT2 = 1;
	private final static int FRAGMENT3 = 2;
	private final static int FRAGMENT4 = 3;
	private PagerAdapter TabPager;
	private CustomViewPager pager;
	private Button tabPage1, tabPage2, tabPage3, tabPage4;
	
	public static String start;
	public static String end;
	public static ArrayList<String> trans;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		MainActivity.start = "";
		MainActivity.end = "";
		MainActivity.trans = new ArrayList<String>();

		// VIEW PAGER
		pager = (CustomViewPager)findViewById(R.id.pager);
		TabPager = new PagerAdapter(getSupportFragmentManager());
		pager.setAdapter(TabPager);
		
		tabPage1 = (Button)findViewById(R.id.tabPage1);
		tabPage2 = (Button)findViewById(R.id.tabPage2);
		tabPage3 = (Button)findViewById(R.id.tabPage3);
		tabPage4 = (Button)findViewById(R.id.tabPage4);
		
		pager.setAdapter(TabPager);
		pager.setCurrentItem(FRAGMENT1);
		
		tabPage1.setOnClickListener(this);
		tabPage1.setSelected(true);
		tabPage1.setTextColor(Color.parseColor("#eeeeee"));
		
		tabPage2.setOnClickListener(this);
		tabPage3.setOnClickListener(this);
		tabPage4.setOnClickListener(this);
		
		pager.setOnPageChangeListener(new OnPageChangeListener(){

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageSelected(int position) {

				tabPage1.setSelected(false);
				tabPage2.setSelected(false);
				tabPage3.setSelected(false);
				tabPage4.setSelected(false);
				tabPage1.setTextColor(Color.parseColor("#000000"));
				tabPage2.setTextColor(Color.parseColor("#000000"));
				tabPage3.setTextColor(Color.parseColor("#000000"));
				tabPage4.setTextColor(Color.parseColor("#000000"));
				
				switch (position) {
				case FRAGMENT1:
					tabPage1.setSelected(true);
					tabPage1.setTextColor(Color.parseColor("#eeeeee"));
					TabPager.notifyDataSetChanged();
					break;
				case FRAGMENT2:
					tabPage2.setSelected(true);
					tabPage2.setTextColor(Color.parseColor("#eeeeee"));
					TabPager.notifyDataSetChanged();
					break;
				case FRAGMENT3:
					tabPage3.setSelected(true);
					tabPage3.setTextColor(Color.parseColor("#eeeeee"));
					TabPager.notifyDataSetChanged();
					break;
				case FRAGMENT4:
					tabPage4.setSelected(true);
					tabPage4.setTextColor(Color.parseColor("#eeeeee"));
					TabPager.notifyDataSetChanged();
					break;
				}
			}
		});
	}
	
	private class PagerAdapter extends FragmentPagerAdapter {

		public PagerAdapter(android.support.v4.app.FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
 
				switch (position) {
				case FRAGMENT1:
					return new Fragment1();
				case FRAGMENT2:
					return new Fragment2();
				case FRAGMENT3:
					return new Fragment3();
				case FRAGMENT4:
					return new Fragment4();
				}
			
			return null;
			
		}

		@Override
		public int getCount() {
			return NUM_PAGES;
		}
		
		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}
	}

	@Override
	public void onClick(View v) {

			switch (v.getId()) {
			case R.id.tabPage1:
				setSelected(tabPage1);
				pager.setCurrentItem(FRAGMENT1);
				TabPager.notifyDataSetChanged();
				break;
			case R.id.tabPage2:
				setSelected(tabPage2);
				pager.setCurrentItem(FRAGMENT2);
				TabPager.notifyDataSetChanged();
				break;
			case R.id.tabPage3:
				setSelected(tabPage3);
				pager.setCurrentItem(FRAGMENT3);
				break;
			case R.id.tabPage4:
				setSelected(tabPage4);
				pager.setCurrentItem(FRAGMENT4);
				break;
			}
		
	}
	
	public void setSelected(Button btn){
		tabPage1.setSelected(false);
		tabPage2.setSelected(false);
		tabPage3.setSelected(false);
		tabPage4.setSelected(false);
		
		tabPage1.setTextColor(Color.parseColor("#000000"));
		tabPage2.setTextColor(Color.parseColor("#000000"));
		tabPage3.setTextColor(Color.parseColor("#000000"));
		tabPage4.setTextColor(Color.parseColor("#000000"));

		btn.setSelected(true);
		btn.setTextColor(Color.parseColor("#eeeeee"));
	}
	
	public int getCurrentPage(){
		return pager.getCurrentItem();
	}
	
	public void setCurrentPage(int pos) {
		switch (pos) {
		case 0:
			setSelected(tabPage1);
			pager.setCurrentItem(FRAGMENT1);
			TabPager.notifyDataSetChanged();
			break;
		case 1:
			setSelected(tabPage2);
			pager.setCurrentItem(FRAGMENT2);
			TabPager.notifyDataSetChanged();
			break;
		case 2:
			setSelected(tabPage3);
			pager.setCurrentItem(FRAGMENT3);
			break;
		case 3:
			setSelected(tabPage4);
			pager.setCurrentItem(FRAGMENT4);
			break;
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onBackPressed() {
		long tempTime = System.currentTimeMillis();
		long intervalTime = tempTime - backPressedTime;

		if (0 <= intervalTime && FINSH_INTERVAL_TIME >= intervalTime) {
			// super.onBackPressed();
			moveTaskToBack(true);
			finish();
			android.os.Process.killProcess(android.os.Process.myPid());
		} else {
			backPressedTime = tempTime;
			Toast.makeText(getApplicationContext(), "'뒤로'버튼을 한번 더 누르시면 종료됩니다.",
					Toast.LENGTH_SHORT).show();
		}

	}
	
}
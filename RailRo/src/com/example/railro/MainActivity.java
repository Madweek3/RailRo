package com.example.railro;

import android.app.ActionBar;
import android.os.Bundle;
import android.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends FragmentActivity {
	ViewPager mPager;
	TabPagerAdapter mPagerAdapter;
	ActionBar actionBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// SET PAGER ADAPTER TO THE ACTION BAR
		mPagerAdapter = new TabPagerAdapter(getSupportFragmentManager());
		mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setOffscreenPageLimit(2);
		mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				actionBar = getActionBar();
				actionBar.setSelectedNavigationItem(position);
				mPagerAdapter.notifyDataSetChanged();

			}
		});
		
		mPager.setAdapter(mPagerAdapter);

		actionBar = getActionBar();
		
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		ActionBar.TabListener tabListener = new ActionBar.TabListener() {
			@Override
			public void onTabReselected(android.app.ActionBar.Tab tab,
					FragmentTransaction ft) {
			}

			@Override
			public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
				mPager.setCurrentItem(tab.getPosition());
			}

			@Override
			public void onTabUnselected(android.app.ActionBar.Tab tab,
					FragmentTransaction ft) {
			}
		};
		
		// ADD NEW TABS
		actionBar.addTab(actionBar.newTab().setText("Page1")
				.setTabListener(tabListener));
		actionBar.addTab(actionBar.newTab().setText("Page2")
				.setTabListener(tabListener));
		actionBar.addTab(actionBar.newTab().setText("Page3")
				.setTabListener(tabListener));

		mPager.setCurrentItem(0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}

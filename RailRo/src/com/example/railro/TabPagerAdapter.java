package com.example.railro;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class TabPagerAdapter extends FragmentStatePagerAdapter {
	public TabPagerAdapter(FragmentManager fragmentManager) {
		super(fragmentManager);
	}
	
	@Override
	public Fragment getItem(int i) {
		switch (i) {
		case 0:
			return new Page1();
		case 1:
			return new Page2();
		case 2:
			return new Page3();
		}
		return null;
	}

	@Override
	public int getCount() {
		return 3;
	}
	
	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}
	
}
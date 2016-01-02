package com.milopong.monolink.adapter;


import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class Pager_Adapter extends FragmentPagerAdapter 
{
	private List<Fragment> fragments;
	
	public Pager_Adapter(FragmentManager fm, List<Fragment> fragments) 
	{
		super(fm);
		this.fragments = fragments;
	}
	
	public Fragment getItem(int position) 
	{
		return this.fragments.get(position);
	}
	
	public int getCount() 
	{
		return this.fragments.size();
	}
	
}

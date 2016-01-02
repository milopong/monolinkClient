package com.milopong.monolink.utils;

import java.util.Comparator;

import com.milopong.monolink.model.Schedule;

public class TimeAscCompare implements Comparator<Schedule> {
	
	@Override
	public int compare(Schedule arg0, Schedule arg1){
		
		return arg0.getStartTime().compareTo(arg1.getStartTime());
	}

}

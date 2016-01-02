package com.milopong.monolink.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.milopong.monolink.R;
import com.milopong.monolink.model.Schedule;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

public class CalendarActivity extends Activity
		implements WeekView.MonthChangeListener, WeekView.EventClickListener, WeekView.EventLongPressListener {
	private static final int TYPE_DAY_VIEW = 1;
	private int mWeekViewType = TYPE_DAY_VIEW;
	private WeekView mWeekView;
	private ArrayList<Schedule> schedules;
	private final String eventColor[] = { "DC6E6E", "#A875B2", "#71A0D4", "#7CC9A6", "#ADD371", "#DBDB71", "#BF76B1" };

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_calendar);
		Intent intent = getIntent();
		schedules = new ArrayList<Schedule>();
		schedules = (ArrayList<Schedule>) intent.getSerializableExtra("schedules");
		mWeekViewType = 1;
		Log.d("aa", "arrayList Size1:"+schedules.size());
		// Get a reference for the week view in the layout.
		mWeekView = (WeekView) findViewById(R.id.weekView);
		mWeekView.setNumberOfVisibleDays(mWeekViewType);
		// Show a toast message about the touched event.
		mWeekView.setOnEventClickListener(this);

		// The week view has infinite scrolling horizontally. We have to provide
		// the events of a
		// month every time the month changes on the week view.
		mWeekView.setMonthChangeListener(this);

		// Set long press listener for events.
		mWeekView.setEventLongPressListener(this);

		// Set up a date time interpreter to interpret how the date and time
		// will be formatted in
		// the week view. This is optional.
		setupDateTimeInterpreter(false);
	}

	/**
	 * Set up a date time interpreter which will show short date values when in
	 * week view and long date values otherwise.
	 * 
	 * @param shortDate
	 *            True if the date values should be short.
	 */
	private void setupDateTimeInterpreter(final boolean shortDate) {
		mWeekView.setDateTimeInterpreter(new DateTimeInterpreter() {
			@Override
			public String interpretDate(Calendar date) {
				SimpleDateFormat weekdayNameFormat = new SimpleDateFormat("EEE", Locale.getDefault());
				String weekday = weekdayNameFormat.format(date.getTime());
				SimpleDateFormat format = new SimpleDateFormat(" M/d", Locale.getDefault());

				// All android api level do not have a standard way of getting
				// the first letter of
				// the week day name. Hence we get the first char
				// programmatically.
				// Details:
				// http://stackoverflow.com/questions/16959502/get-one-letter-abbreviation-of-week-day-of-a-date-in-java#answer-16959657
				if (shortDate)
					weekday = String.valueOf(weekday.charAt(0));
				return weekday.toUpperCase() + format.format(date.getTime());
			}

			@Override
			public String interpretTime(int hour) {
				return hour > 11 ? (hour - 12) + " PM" : (hour == 0 ? "12 AM" : hour + " AM");
			}
		});
	}

	@Override
	public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {

		// Populate the week view with some events.
		List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();

		Calendar startTime = Calendar.getInstance();
		Calendar endTime = (Calendar) startTime.clone();

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try {

			for (int i = 0; i < schedules.size(); i++) {
				startTime = Calendar.getInstance();
				startTime.setTime(formatter.parse(schedules.get(i).getStartTime()));
				endTime = (Calendar) startTime.clone();
				endTime.setTime(formatter.parse(schedules.get(i).getEndTime()));

				WeekViewEvent event = new WeekViewEvent(schedules.get(i).getNo(), schedules.get(i).getName(), startTime,
						endTime);
				int index = (int) (Math.random() * 6);
				event.setColor(Color.parseColor(eventColor[index + 1]));

				int thisMonth = startTime.get(Calendar.MONTH);

				if (newMonth == thisMonth + 1) {
					events.add(event);
				}

			}

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return events;
	}

	@Override
	public void onEventClick(WeekViewEvent event, RectF eventRect) {
		int no = (int) event.getId();

		for (int i = 0; i < schedules.size(); i++) {
			if (schedules.get(i).getNo() == no) {
				showSchedule(schedules.get(i));
			}
		}

	}

	@Override
	public void onEventLongPress(WeekViewEvent event, RectF eventRect) {
		Toast.makeText(this, "Long pressed event: " + event.getName(), Toast.LENGTH_SHORT).show();
	}

	public void showSchedule(Schedule schedule) {
		Intent intentScheduleInfoActivity = new Intent(CalendarActivity.this, ScheduleInfoActivity.class);
		Schedule aSchedule = new Schedule();
		aSchedule = schedule;
		intentScheduleInfoActivity.putExtra("schedule", aSchedule);
		startActivity(intentScheduleInfoActivity);
	}

}

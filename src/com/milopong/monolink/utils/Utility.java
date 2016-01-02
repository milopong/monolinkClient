package com.milopong.monolink.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class which has Utility methods
 * 
 */
public class Utility {
	
	public static final String MyPREFERENCES = "MyPrefs";
	public static final String Name = "nameKey";
	public static final String Phone = "phoneKey";
	public static final String Email = "emailKey";
	public static final String Photo = "PhotoKey";
	public static final String Birth = "birthKey";
	public static final String Sex = "sexKey";
	private static Pattern pattern;
	private static Matcher matcher;
	
	
	public static enum Mode {LIST,FAVORITE};
	// NickName Patter
	private static final String NICKNAME_PATTERN = "^[가-힣a-z0-9]*$";

	// Email Pattern
	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	// Phone Patter
	private static final String PHONE_PATTERN = "^[0-9]*$";

	public static boolean emailValidate(String email) {
		pattern = Pattern.compile(EMAIL_PATTERN);
		matcher = pattern.matcher(email);
		return matcher.matches();
	}

	public static boolean nickNameValidate(String nickName) {
		pattern = Pattern.compile(NICKNAME_PATTERN);
		matcher = pattern.matcher(nickName);
		return matcher.matches();
	}

	public static boolean phoneValidate(String phone) {
		pattern = Pattern.compile(PHONE_PATTERN);
		matcher = pattern.matcher(phone);
		return matcher.matches();
	}

	public static boolean isNotNull(String txt) {
		return txt != null && txt.trim().length() > 0 ? true : false;
	}

	public static boolean isCorrenctLength(String txt, int minLength,
			int maxLength) {
		if (txt.length() >= minLength && txt.length() <= maxLength) {
			return true;
		} else {
			return false;
		}
	}
	
	public static Calendar StringToCal(String date) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm",
				Locale.KOREA);
		Date scheduleTime = null;
		try {
			scheduleTime = sdf.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(scheduleTime);

		return cal;

	}
}
package com.mnopi.mnopiapi;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public final class Utils {
	
	/**
	 * Converts date in a String correctly formatted for the Mnopi API
	 * 
	 * @param date
	 * @return
	 */
	public static String getFormattedDate(Calendar date){
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String formattedDate = dateFormat.format(date.getTime());
		return formattedDate;
		
	}

}

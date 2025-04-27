package utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UtilsMethods {
	
	public static double dateDiffInDays(Date date1, Date date2) {
		return  Math.abs( ((date1.getTime() - date2.getTime())
                / (1000 * 60 * 60 * 24)));
	}
	public static Date parseDate(String string) {
		Date date = null;
		try {
			date = new SimpleDateFormat("dd/MM/yyyy").parse(string);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	

}

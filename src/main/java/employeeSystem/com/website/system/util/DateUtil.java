package employeeSystem.com.website.system.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

	private static SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat sdf3 = new SimpleDateFormat("yyyyMMdd");

	public static Calendar toCalendar(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal;
	}

	public static Date StringToDate(String strDate) throws ParseException {
		String sampleDate1 = "2018-02-02 18:00:00";
		String sampleDate2 = "2018-02-02";
		String sampleDate3 = "20180202";
		Date date = new Date();
		if (strDate.length() == sampleDate1.length()) {
			date = sdf1.parse(strDate);
		} else if (strDate.length() == sampleDate2.length()) {
			date = sdf2.parse(strDate);
		} else if (strDate.length() == sampleDate3.length()) {
			date = sdf3.parse(strDate);
		}
		return date;
	}

}
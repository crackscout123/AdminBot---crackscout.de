package de.crackscout.Managers;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeHandler {
	
	
	public static final SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss");
	public static final SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd");

	 public static void func() {
		 	// method 1
	        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
	        System.out.println(timestamp);                      // 2023-03-22 23:29:28.047
		 
	        // method 2 - via Date
		 	Date date = new Date();
	        System.out.println(new Timestamp(date.getTime()));  // 2023-03-22 23:29:28.047
	                                                            // number of milliseconds since January 1, 1970, 00:00:00 GMT
	        System.out.println(timestamp.getTime());            // 1679524168047

	        System.out.println(sdf1.format(timestamp));         // 2023-03-22 23:29:28

	        System.out.println(sdf2.format(timestamp));         // 23:29:28

	        System.out.println(sdf3.format(timestamp));         // 2023-03-22

	 }
	 
}


/** 
 *
 * @author Joel Rzepka - crackscout.de
 *
 * @date 22.03.2023 - 23:26:44
 *
 */
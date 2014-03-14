import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.LocalDate;






public class DateAndTime {
	
	public static void main(String[] args) {
		
		 Date dNow = new Date( );
	      SimpleDateFormat ft = 
	      new SimpleDateFormat ("hh:mm:ss a");

	      System.out.println("Current Date: " + ft.format(dNow));
	   }
		
	}




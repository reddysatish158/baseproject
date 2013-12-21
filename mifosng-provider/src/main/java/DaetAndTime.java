import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.joda.time.LocalDate;


 
public class DaetAndTime {
     
     
   
	public static void main(String[] args) {
       
	/*	 Date date = new Date();
		    SimpleDateFormat dateFormatCN = new SimpleDateFormat("dd MMMM yyyy",new Locale("fr"));
		    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
	 System.out.println(  dateFormatCN.format(date));
	 System.out.println(  dateFormat.format(date));   */
		
		LocalDate localDate=new LocalDate();
		   SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
		System.out.println(dateFormat.format(localDate.toDate()));
	}
}
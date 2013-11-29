import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.joda.time.LocalDate;


 
public class DaetAndTime {
     
     
   
	public static void main(String[] args) {
       
    
		String pattern = "dd MMMMM yyyy";
		SimpleDateFormat simpleDateFormat =
		        new SimpleDateFormat("dd MMMMM yyyy", new Locale("en"));

		String date = simpleDateFormat.format(new LocalDate().toDate());
		System.out.println(date.toString());
 
}
}
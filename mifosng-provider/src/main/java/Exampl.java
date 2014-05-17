import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Exampl {

  
    public static void main(String[] args) throws ParseException {

    	String date="20-May-14";
    	SimpleDateFormat formatter = new SimpleDateFormat("dd-MMMM-yy");
    	Date date2=formatter.parse(date);
    	System.out.println(date2);
    	   SimpleDateFormat formatter1 = new SimpleDateFormat("dd MMMM yyyy");
    	   System.out.println(formatter1.format(date2));
		

    }

}
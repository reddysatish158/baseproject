

import java.io.File;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.util.Date;

import org.joda.time.LocalDate;

public class DateAndTime {

    public static void main(String[] args) throws Exception {

    	BigDecimal clientBal=new BigDecimal(-100);
    	BigDecimal price=new BigDecimal(50);
     BigDecimal result=clientBal.add(price);
     
     System.out.println(result.compareTo(BigDecimal.ZERO));
    	
}
}


import java.io.File;
import java.io.FileWriter;
import java.util.Date;

import org.joda.time.LocalDate;

public class DateAndTime {

    public static void main(String[] args) throws Exception {

    	Date date=new Date();
    	String string=date.getHours()+""+date.getMinutes()+""+date.getSeconds();
    	
    	String path=System.getProperty("user.home") + File.separator + ".mifosx"+File.separator +"DefaultDemoTenant"+File.separator
               +"SheduleLogFile"+ File.separator+"Requestor"+ File.separator +"Requester_"+new LocalDate().toString().replace("-","")+"_"+string+".log";
   	 
    	
    	System.out.println(date.getHours()+""+date.getMinutes()+""+date.getSeconds());
    	System.out.println(path);
   	 
		 File fileHandler = new File(path.trim());
	    //  fileHandler.mkdir();
		 fileHandler.createNewFile();
		 FileWriter fw = new FileWriter(fileHandler);
		 
		 System.out.println(path);
	     
}
}
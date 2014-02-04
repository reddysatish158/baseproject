import org.joda.time.DateTimeZone;
import org.joda.time.LocalTime;





public class DateAndTime {

    public static void main(String[] args) throws Exception {
/*
    	 String string=String.format("%0" + 2 + "d", 6);
    	 System.out.println(string);
    	
    	LocalTime date=new LocalTime();*/
    	 final DateTimeZone zone = DateTimeZone.forID("Pacific/Fiji");
    	  LocalTime date=new LocalTime(zone);
    	System.out.println(date.getHourOfDay()+"_"+date.getMinuteOfHour()+"_"+date.getSecondOfMinute());
	     //final DateTimeZone zone = DateTimeZone.forID("Pacific/Fiji");
	     System.out.println(zone.toTimeZone());
	   
    	
}
}
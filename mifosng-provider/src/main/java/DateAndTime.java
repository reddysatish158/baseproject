import java.util.Date;




public class DateAndTime {

    public static void main(String[] args) throws Exception {

    	Date chargedate=new Date();
    	Date date2=new Date();
    	@SuppressWarnings("deprecation")
		Date chargeEnddate=new Date(2014,2,20);
    	
    	int res=chargeEnddate.compareTo(date2);
    	
    	if(res == 0 || res == 1){
    		System.out.println(res);
    	}
    	
}
}
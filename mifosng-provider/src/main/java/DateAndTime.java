import java.math.BigDecimal;




public class DateAndTime {
	
	public static void main(String[] args) {
		
		BigDecimal bigDecimal=new BigDecimal(-5000);
		BigDecimal bigDecimal2=new BigDecimal(2000);
       BigDecimal bigDecimal3=bigDecimal.add(bigDecimal2);
       System.out.println(bigDecimal3.compareTo(BigDecimal.ZERO));
		
	}

}


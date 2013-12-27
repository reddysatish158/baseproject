package org.mifosplatform.billing.billingmaster.api;
import java.math.BigDecimal;

public class DateAndTime {

    public static void main(String[] args) throws Exception {
     BigDecimal bal=new BigDecimal(-293450);
     BigDecimal price=new BigDecimal(15000);
     BigDecimal sum=bal.add(price);
     System.out.println(sum);
     System.out.println(sum.compareTo(BigDecimal.ZERO));
    }
}
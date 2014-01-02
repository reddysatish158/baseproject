package org.mifosplatform.billing.scheduledjobs.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.mifosplatform.billing.clientbalance.domain.ClientBalance;
import org.mifosplatform.billing.clientbalance.domain.ClientBalanceRepository;
import org.mifosplatform.billing.order.data.OrderData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScheduleJob {
	
private final ClientBalanceRepository clientBalanceRepository;	

@Autowired
public ScheduleJob(final ClientBalanceRepository clientBalanceRepository){
	
	this.clientBalanceRepository=clientBalanceRepository;
}

        
          public boolean checkClientBalanceForOrderrenewal(OrderData orderData,Long clientId) {
        	  boolean isAmountSufficient=false;
          ClientBalance clientBalance=this.clientBalanceRepository.findByClientId(clientId);
               
          BigDecimal  orderPrice=new BigDecimal(orderData.getPrice());
          BigDecimal  reqRenewalAmount=orderPrice.divide(new BigDecimal(2),RoundingMode.CEILING);
     
          if(clientBalance!=null){        	  
        	     BigDecimal resultanceBal=clientBalance.getBalanceAmount().add(reqRenewalAmount);   
        	    
        	    if(resultanceBal.compareTo(BigDecimal.ZERO) != 1){
        	    	isAmountSufficient=true;
        	    }
          }
		
		return isAmountSufficient;
	}

}

package org.mifosplatform.billing.scheduledjobs.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.billing.billingorder.service.BillingOrderReadPlatformService;
import org.mifosplatform.billing.billingorder.service.GenerateBill;
import org.mifosplatform.billing.clientbalance.domain.ClientBalance;
import org.mifosplatform.billing.clientbalance.domain.ClientBalanceRepository;
import org.mifosplatform.billing.order.data.OrderData;
import org.mifosplatform.billing.order.domain.OrderPrice;
import org.mifosplatform.billing.service.DiscountMasterData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScheduleJob {
	
private final ClientBalanceRepository clientBalanceRepository;	
private final BillingOrderReadPlatformService billingOrderReadPlatformService;
private final GenerateBill generateBill;

@Autowired
public ScheduleJob(final ClientBalanceRepository clientBalanceRepository,final BillingOrderReadPlatformService billingOrderReadPlatformService,
		final GenerateBill generateBill){
	
	this.clientBalanceRepository=clientBalanceRepository;
	this.billingOrderReadPlatformService=billingOrderReadPlatformService;
	this.generateBill=generateBill;
}

        
public boolean checkClientBalanceForOrderrenewal(OrderData orderData,Long clientId, List<OrderPrice> orderPrices) {
	
           BigDecimal discountAmount=BigDecimal.ZERO;

           List<DiscountMasterData> discountMasterDatas = billingOrderReadPlatformService.retrieveDiscountOrders(orderData.getId(),orderPrices.get(0).getId());
        	  DiscountMasterData discountMasterData=null;
        	  if(!discountMasterDatas.isEmpty()){
        		  discountMasterData=discountMasterDatas.get(0);
        	  }
        	
        	  boolean isAmountSufficient=false;
              ClientBalance clientBalance=this.clientBalanceRepository.findByClientId(clientId);
              BigDecimal  orderPrice=new BigDecimal(orderData.getPrice());
          
    	     if(this.generateBill.isDiscountApplicable(new LocalDate(),discountMasterData,new LocalDate().plusMonths(1))){
  			 discountMasterData = this.generateBill.calculateDiscount(discountMasterData, BigDecimal.ZERO, orderPrice);
  			 discountAmount=discountMasterData.getDiscountAmount();
  		    }
    	    orderPrice=orderPrice.subtract(discountAmount);
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

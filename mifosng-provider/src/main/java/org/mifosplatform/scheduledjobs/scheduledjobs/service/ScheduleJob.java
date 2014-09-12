package org.mifosplatform.scheduledjobs.scheduledjobs.service;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;

import org.joda.time.LocalDate;
import org.json.JSONObject;
import org.mifosplatform.finance.billingorder.service.BillingOrderReadPlatformService;
import org.mifosplatform.finance.billingorder.service.GenerateBill;
import org.mifosplatform.finance.clientbalance.domain.ClientBalance;
import org.mifosplatform.finance.clientbalance.domain.ClientBalanceRepository;
import org.mifosplatform.finance.data.DiscountMasterData;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.portfolio.contract.data.SubscriptionData;
import org.mifosplatform.portfolio.contract.service.ContractPeriodReadPlatformService;
import org.mifosplatform.portfolio.order.data.OrderData;
import org.mifosplatform.portfolio.order.domain.Order;
import org.mifosplatform.portfolio.order.domain.OrderPrice;
import org.mifosplatform.portfolio.order.domain.OrderRepository;
import org.mifosplatform.portfolio.order.service.OrderWritePlatformService;
import org.mifosplatform.portfolio.plan.domain.StatusTypeEnum;
import org.mifosplatform.scheduledjobs.scheduledjobs.data.JobParameterData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonElement;

@Service
public class ScheduleJob {

private final ClientBalanceRepository clientBalanceRepository;	
private final BillingOrderReadPlatformService billingOrderReadPlatformService;
private final GenerateBill generateBill;
private final OrderRepository orderRepository;
private final ContractPeriodReadPlatformService contractPeriodReadPlatformService;
private final FromJsonHelper fromApiJsonHelper;
private final OrderWritePlatformService orderWritePlatformService;

@Autowired
public ScheduleJob(final ClientBalanceRepository clientBalanceRepository,final BillingOrderReadPlatformService billingOrderReadPlatformService,
final GenerateBill generateBill,final OrderRepository orderRepository,final ContractPeriodReadPlatformService contractPeriodReadPlatformService,
final FromJsonHelper fromApiJsonHelper,final OrderWritePlatformService orderWritePlatformService){

this.clientBalanceRepository=clientBalanceRepository;
this.billingOrderReadPlatformService=billingOrderReadPlatformService;
this.generateBill=generateBill;
this.orderRepository=orderRepository;
this.contractPeriodReadPlatformService=contractPeriodReadPlatformService;
this.fromApiJsonHelper=fromApiJsonHelper;
this.orderWritePlatformService=orderWritePlatformService;

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
         BigDecimal orderPrice=new BigDecimal(orderData.getPrice());
          
       if(this.generateBill.isDiscountApplicable(new LocalDate(),discountMasterData,new LocalDate().plusMonths(1))){
          discountMasterData = this.generateBill.calculateDiscount(discountMasterData, BigDecimal.ZERO, orderPrice);
          discountAmount=discountMasterData.getDiscountAmount();
         }
        orderPrice=orderPrice.subtract(discountAmount);
        BigDecimal reqRenewalAmount=orderPrice;//orderPrice.divide(new BigDecimal(2),RoundingMode.CEILING);
    
         if(clientBalance!=null){
            BigDecimal resultanceBal=clientBalance.getBalanceAmount().add(reqRenewalAmount);
         if(resultanceBal.compareTo(BigDecimal.ZERO) != 1){
         isAmountSufficient=true;
         }
          }

return isAmountSufficient;
}



public void ProcessAutoExipiryDetails(OrderData orderData, FileWriter fw, LocalDate exipirydate, JobParameterData data, Long clientId) {
  
	try{

      if(!(orderData.getStatus().equalsIgnoreCase(StatusTypeEnum.DISCONNECTED.toString()) || orderData.getStatus().equalsIgnoreCase(StatusTypeEnum.PENDING.toString())))
          {

           if (orderData.getEndDate().equals(exipirydate) || exipirydate.isAfter(orderData.getEndDate()))
              {
                 JSONObject jsonobject = new JSONObject();
                     if(data.getIsAutoRenewal().equalsIgnoreCase("Y")){
                    	 
                            Order order=this.orderRepository.findOne(orderData.getId());
                            List<OrderPrice> orderPrice=order.getPrice();
                         boolean isSufficientAmountForRenewal=this.checkClientBalanceForOrderrenewal(orderData,clientId,orderPrice);

                          if(isSufficientAmountForRenewal){
	                            List<SubscriptionData> subscriptionDatas=this.contractPeriodReadPlatformService.retrieveSubscriptionDatabyContractType("Month(s)",1);
	                            jsonobject.put("renewalPeriod",subscriptionDatas.get(0).getId());	
	                            jsonobject.put("description","Order Renewal By Scheduler");
	                            final JsonElement parsedCommand = this.fromApiJsonHelper.parse(jsonobject.toString());
	                            final JsonCommand command = JsonCommand.from(jsonobject.toString(),parsedCommand,this.fromApiJsonHelper,"RENEWAL",order.getClientId(), null,
	                              null,order.getClientId(), null, null, null,null, null, null,null);
	                            fw.append("sending json data for Renewal Order is : "+jsonobject.toString()+"\r\n");
	                            this.orderWritePlatformService.renewalClientOrder(command,orderData.getId());
	                            fw.append("Client Id"+clientId+" With this Orde"+orderData.getId()+" has been renewaled for one month via " +"Auto Exipiry on Dated"+exipirydate);

                          }else{
	                             SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
	                             jsonobject.put("disconnectReason","Date Expired");
	                             jsonobject.put("disconnectionDate",dateFormat.format(orderData.getEndDate().toDate()));
	                             jsonobject.put("dateFormat","dd MMMM yyyy");
	                             jsonobject.put("locale","en");
	                             fw.append("sending json data for Disconnecting the Order is : "+jsonobject.toString()+"\r\n");
	                             
	                             final JsonElement parsedCommand = this.fromApiJsonHelper.parse(jsonobject.toString());
	                             final JsonCommand command = JsonCommand.from(jsonobject.toString(),parsedCommand,this.fromApiJsonHelper,"DissconnectOrder",order.getClientId(), null,
	                                    null,order.getClientId(), null, null, null,null, null, null,null);
	                             this.orderWritePlatformService.disconnectOrder(command,	orderData.getId());
	                             fw.append("Client Id"+order.getClientId()+" With this Orde"+order.getId()+" has been disconnected via Auto Exipiry on Dated"+exipirydate);
                         }
                   }else if (orderData.getEndDate().equals(exipirydate) || exipirydate.isAfter(orderData.getEndDate())){

	                           SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
	                           jsonobject.put("disconnectReason","Date Expired");
	                           jsonobject.put("disconnectionDate",dateFormat.format(orderData.getEndDate().toDate()));
	                           jsonobject.put("dateFormat","dd MMMM yyyy");
	                           jsonobject.put("locale","en");
	                           final JsonElement parsedCommand = this.fromApiJsonHelper.parse(jsonobject.toString());
	                           final JsonCommand command = JsonCommand.from(jsonobject.toString(),parsedCommand,this.fromApiJsonHelper,"DissconnectOrder",clientId, null,
	                                  null,clientId, null, null, null,null, null, null,null);
	                           this.orderWritePlatformService.disconnectOrder(command,	orderData.getId());
	                           fw.append("Client Id"+clientId+" With this Orde"+orderData.getId()+" has been disconnected via Auto Exipiry on Dated"+exipirydate);
                  }
           }
       }
    }catch(IOException exception){
         exception.printStackTrace();
      }
   catch(Exception exception){
	   exception.printStackTrace();
}

}

}
package org.mifosplatform.billing.eventaction.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.codehaus.jettison.json.JSONObject;
import org.mifosplatform.billing.association.data.AssociationData;
import org.mifosplatform.billing.association.exception.HardwareDetailsNotFoundException;
import org.mifosplatform.billing.association.service.HardwareAssociationReadplatformService;
import org.mifosplatform.billing.billingorder.api.BillingOrderApiResourse;
import org.mifosplatform.billing.billingorder.service.InvoiceClient;
import org.mifosplatform.billing.contract.data.SubscriptionData;
import org.mifosplatform.billing.contract.service.ContractPeriodReadPlatformService;
import org.mifosplatform.billing.eventaction.data.ActionDetaislData;
import org.mifosplatform.billing.eventaction.data.EventActionProcedureData;
import org.mifosplatform.billing.eventaction.domain.EventAction;
import org.mifosplatform.billing.eventaction.domain.EventActionRepository;
import org.mifosplatform.billing.order.domain.Order;
import org.mifosplatform.billing.order.domain.OrderRepository;
import org.mifosplatform.billing.transactionhistory.service.TransactionHistoryWritePlatformService;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class EventActionWritePlatformServiceImpl implements ActiondetailsWritePlatformService{
	
	
	
	private final EventActionRepository eventActionRepository;
    private final ActionDetailsReadPlatformService actionDetailsReadPlatformService;	
    private final HardwareAssociationReadplatformService hardwareAssociationReadplatformService;
    private final ContractPeriodReadPlatformService contractPeriodReadPlatformService;
    private final InvoiceClient invoiceClient;
    private final OrderRepository orderRepository;
    private final FromJsonHelper fromApiJsonHelper;
    private final BillingOrderApiResourse billingOrderApiResourse;
    
 

	@Autowired
	public EventActionWritePlatformServiceImpl(final ActionDetailsReadPlatformService actionDetailsReadPlatformService,final EventActionRepository eventActionRepository,
			final HardwareAssociationReadplatformService hardwareAssociationReadplatformService,final ContractPeriodReadPlatformService contractPeriodReadPlatformService,
			final TransactionHistoryWritePlatformService transactionHistoryWritePlatformService,final InvoiceClient invoiceClient,final OrderRepository orderRepository,
			final FromJsonHelper fromApiJsonHelper,final BillingOrderApiResourse billingOrderApiResourse)
	{
		this.eventActionRepository=eventActionRepository;
        this.actionDetailsReadPlatformService=actionDetailsReadPlatformService;
        this.hardwareAssociationReadplatformService=hardwareAssociationReadplatformService;
        this.contractPeriodReadPlatformService=contractPeriodReadPlatformService;
        this.invoiceClient=invoiceClient;
        this.orderRepository=orderRepository;
        this.fromApiJsonHelper=fromApiJsonHelper;
        this.billingOrderApiResourse=billingOrderApiResourse;
	}
	
	
	@Override
	public void AddNewActions(List<ActionDetaislData> actionDetaislDatas,final Long clientId,final String resourceId) {
    try{
    	
		if(actionDetaislDatas!=null){
			
			for(ActionDetaislData detailsData:actionDetaislDatas){
				
				     EventActionProcedureData actionProcedureData=this.actionDetailsReadPlatformService.checkCustomeValidationForEvents(clientId, EventActionConstants.EVENT_ACTIVE_ORDER,detailsData.getaActionName(),resourceId);
				  
				     if(actionProcedureData.isCheck()){
				    	 
				    	  SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
				    	 JSONObject jsonObject=new JSONObject();
				    	 EventAction eventAction=null;
				    	 List<SubscriptionData> subscriptionDatas=this.contractPeriodReadPlatformService.retrieveSubscriptionDatabyContractType("Month(s)",1);
				           if(actionProcedureData.getActionName().equalsIgnoreCase(EventActionConstants.ACTION_ACTIVE)){
					  
					          AssociationData associationData=this.hardwareAssociationReadplatformService.retrieveSingleDetails(actionProcedureData.getOrderId());
 					           if(associationData ==null){
						           throw new HardwareDetailsNotFoundException(actionProcedureData.getOrderId().toString());
					            }
   					    	 jsonObject.put("renewalPeriod",subscriptionDatas.get(0).getId());	
				        	 jsonObject.put("description","Order Renewal By Scheduler");
				        	  eventAction=new EventAction(new Date(), "CREATE", "PAYMENT",EventActionConstants.ACTION_RENEWAL.toString(),"/orders/renewal", 
				        			 Long.parseLong(resourceId), jsonObject.toString(),actionProcedureData.getOrderId(),clientId);
				        	  this.eventActionRepository.save(eventAction);
				         
				           }else if(actionProcedureData.getActionName().equalsIgnoreCase(EventActionConstants.ACTION_NEW)){
				        	  
				        	   jsonObject.put("billAlign","false");
				        	   jsonObject.put("contractPeriod",subscriptionDatas.get(0).getId());
				        	   jsonObject.put("dateFormat","dd MMMM yyyy");
                               jsonObject.put("locale","en");
				        	   jsonObject.put("paytermCode","Monthly");
				        	   jsonObject.put("planCode",actionProcedureData.getPlanId());
				        	   jsonObject.put("start_date",dateFormat.format(new Date()));
				        	   
				        	   eventAction=new EventAction(new Date(), "CREATE", "PAYMENT",actionProcedureData.getActionName(),"/orders/"+clientId, 
					        			 Long.parseLong(resourceId), jsonObject.toString(),null,clientId);
				        	   
				        	   this.eventActionRepository.save(eventAction);
				        	   
				           }else if(actionProcedureData.getActionName().equalsIgnoreCase(EventActionConstants.ACTION_DISCONNECT)){
				        	   
				        	   eventAction=new EventAction(new Date(), "CREATE", "PAYMENT",EventActionConstants.ACTION_ACTIVE.toString(),"/orders/reconnect"+clientId, 
				        	   Long.parseLong(resourceId), jsonObject.toString(),actionProcedureData.getOrderId(),clientId);
				        	   this.eventActionRepository.save(eventAction);
				        	   
				          }else if(detailsData.getaActionName().equalsIgnoreCase(EventActionConstants.ACTION_INVOICE)){
				        	  
				        	
				        	  jsonObject.put("dateFormat","dd MMMM yyyy");
                              jsonObject.put("locale","en");
				        	  jsonObject.put("systemDate",dateFormat.format(new Date()));
				        	  
				        	  if(detailsData.IsSynchronous().equalsIgnoreCase("N")){
				        		  
				        	  eventAction=new EventAction(new Date(), "CREATE",EventActionConstants.EVENT_ACTIVE_ORDER.toString() ,EventActionConstants.ACTION_INVOICE.toString(),"/billingorder/"+clientId, 
						        	   Long.parseLong(resourceId), jsonObject.toString(),Long.parseLong(resourceId),clientId);
						        	   this.eventActionRepository.save(eventAction);
				              }else{
				            	  
				        	/* EventActionData eventActionData=new EventActionData(clientId,"Create",EventActionConstants.EVENT_ACTIVE_ORDER.toString(),EventActionConstants.ACTION_INVOICE,
				        			 jsonObject.toString(),actionProcedureData.getOrderId(), actionProcedureData.getOrderId(), clientId);
				        	  this.processEventActionService.ProcessEventActions(eventActionData);*/
				            	  Order order=this.orderRepository.findOne(new Long(resourceId));


				  			//JsonObject jsonObject3=new JsonObject();
				            	  jsonObject.put("dateFormat","dd MMMM yyyy");
	                              jsonObject.put("locale","en");
					        	  jsonObject.put("systemDate",dateFormat.format(order.getStartDate()));
					        	  
				  			/*final JsonElement parsedCommand = this.fromApiJsonHelper.parse(jsonObject.toString());
				  			final JsonCommand command = JsonCommand.from(jsonObject.toString(),parsedCommand,this.fromApiJsonHelper,"CreateInvoice",order.getClientId(), null,
									null,order.getClientId(), null, null, null,null, null, null);*/

				  				//CommandProcessingResult commandProcessingResult=this.orderWritePlatformService.createOrder(eventActionData.getClientId(), command);
				  			//  this.invoiceClient.createInvoiceBill(command);
				  			this.billingOrderApiResourse.retrieveBillingProducts(order.getClientId(),jsonObject.toString());
				  				//For Transaction History

				  			
				            	//  this.invoiceClient.invoicingSingleClient(clientId,new LocalDate(order.getStartDate()).plusDays(1));
				          }
				          }
				  }
				
			}
		}
		
    }catch(Exception exception){
    	exception.printStackTrace();
    }
    
   
	}

	/*@Override
	public void ProcessEventActions(EventActionData eventActionData) {
      
		try{
			 
			if(eventActionData.getActionName().equalsIgnoreCase(EventActionConstants.ACTION_RENEWAL)){
				
				String jsonObject=eventActionData.getJsonData();
				final JsonElement parsedCommand = this.fromApiJsonHelper.parse(jsonObject);
				final JsonCommand command = JsonCommand.from(jsonObject,parsedCommand,this.fromApiJsonHelper,"RenewalOrder",eventActionData.getClientId(), null,
						null,eventActionData.getClientId(), null, null, null,null, null, null);
				
			    	this.orderWritePlatformService.renewalClientOrder(command,eventActionData.getOrderId());
			    	OrderHistory orderHistory=new OrderHistory(eventActionData.getOrderId(),new LocalDate(),new LocalDate(),null,"Renewal",Long.valueOf(1));
			    	Order order=this.orderRepository.findOne(eventActionData.getOrderId());
					this.orderHistory.save(orderHistory);
					
					transactionHistoryWritePlatformService.saveTransactionHistory(eventActionData.getClientId(),"ORDER_"+UserActionStatusTypeEnum.RENEWAL_BEFORE_AUTOEXIPIRY.toString(), order.getStartDate(),
							"PlanId:"+order.getPlanId(),"Renewal Period: 1 Month","OrderID:"+order.getId(),"Billing Align:"+order.getbillAlign());
							

			}else if(eventActionData.getActionName().equalsIgnoreCase(EventActionConstants.ACTION_ACTIVE)){
				
				Order order=this.orderRepository.findOne(eventActionData.getOrderId());
				this.orderWritePlatformService.reconnectOrder(eventActionData.getOrderId());
				
				transactionHistoryWritePlatformService.saveTransactionHistory(eventActionData.getClientId(),"ORDER_"+UserActionStatusTypeEnum.RECONNECTION.toString(), order.getStartDate(),
						"Price:"+order.getAllPriceAsString(),"PlanId:"+order.getPlanId(),"contarctPeriod: 1 Month","Services:"+order.getAllServicesAsString(),"OrderID:"+order.getId(),"Billing Align:"+order.getbillAlign());
				
				
			}else if(eventActionData.getActionName().equalsIgnoreCase(EventActionConstants.ACTION_DISCONNECT)){
				
				String jsonObject=eventActionData.getJsonData();
				final JsonElement parsedCommand = this.fromApiJsonHelper.parse(jsonObject);
				final JsonCommand command = JsonCommand.from(jsonObject,parsedCommand,this.fromApiJsonHelper,"DissconnectOrder",eventActionData.getClientId(), null,
						null,eventActionData.getClientId(), null, null, null,null, null, null);
				this.orderWritePlatformService.disconnectOrder(command,	eventActionData.getOrderId());
				
			}else if(eventActionData.getActionName().equalsIgnoreCase(EventActionConstants.ACTION_NEW)){

				String jsonObject=eventActionData.getJsonData();
				final JsonElement parsedCommand = this.fromApiJsonHelper.parse(jsonObject);
				final JsonCommand command = JsonCommand.from(jsonObject,parsedCommand,this.fromApiJsonHelper,"CreateOrder",eventActionData.getClientId(), null,
						null,eventActionData.getClientId(), null, null, null,null, null, null);
			Long plancode=command.longValueOfParameterNamed("planCode");
				CommandProcessingResult commandProcessingResult=this.orderWritePlatformService.createOrder(eventActionData.getClientId(), command);
				//For Transaction History
	   			transactionHistoryWritePlatformService.saveTransactionHistory(eventActionData.getClientId(), "New Order", new Date(),
	   			     "PlanId:"+plancode,"contarctPeriod: One Month","OrderID:"+commandProcessingResult.resourceId(),
	   			     "BillingAlign:false");
			
			}else if(eventActionData.getActionName().equalsIgnoreCase(EventActionConstants.ACTION_INVOICE)){

				String jsonObject=eventActionData.getJsonData();
				final JsonElement parsedCommand = this.fromApiJsonHelper.parse(jsonObject);
				final JsonCommand command = JsonCommand.from(jsonObject,parsedCommand,this.fromApiJsonHelper,"CreateInvoice",eventActionData.getClientId(), null,
						null,eventActionData.getClientId(), null, null, null,null, null, null);

				//CommandProcessingResult commandProcessingResult=this.orderWritePlatformService.createOrder(eventActionData.getClientId(), command);
			   this.invoiceClient.createInvoiceBill(command);
				//For Transaction History

			}

			
			EventAction eventAction=this.eventActionRepository.findOne(eventActionData.getId());
	    	eventAction.updateStatus();
	    	this.eventActionRepository.save(eventAction);
		}catch(DataIntegrityViolationException exception){
			
			
		}
		
	}
	*/
}

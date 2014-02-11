package org.mifosplatform.billing.action.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;



import org.codehaus.jettison.json.JSONObject;
import org.joda.time.LocalDate;
import org.mifosplatform.billing.action.data.ActionDetaislData;
import org.mifosplatform.billing.action.data.EventActionProcedureData;
import org.mifosplatform.billing.action.domain.EventAction;
import org.mifosplatform.billing.action.domain.EventActionRepository;
import org.mifosplatform.billing.association.data.AssociationData;
import org.mifosplatform.billing.association.exception.HardwareDetailsNotFoundException;
import org.mifosplatform.billing.association.service.HardwareAssociationReadplatformService;
import org.mifosplatform.billing.contract.data.SubscriptionData;
import org.mifosplatform.billing.contract.service.ContractPeriodReadPlatformService;
import org.mifosplatform.billing.order.domain.Order;
import org.mifosplatform.billing.order.domain.OrderHistory;
import org.mifosplatform.billing.order.domain.OrderHistoryRepository;
import org.mifosplatform.billing.order.domain.OrderRepository;
import org.mifosplatform.billing.order.service.OrderWritePlatformService;
import org.mifosplatform.billing.plan.domain.UserActionStatusTypeEnum;
import org.mifosplatform.billing.scheduledjobs.data.EventActionData;
import org.mifosplatform.billing.transactionhistory.service.TransactionHistoryWritePlatformService;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonElement;


@Service
public class EventActionWritePlatformServiceImpl implements ActiondetailsWritePlatformService{
	
	
	
	private final JdbcTemplate jdbcTemplate;
	private final EventActionRepository eventActionRepository;
    private final ActionDetailsReadPlatformService actionDetailsReadPlatformService;	
    private final FromJsonHelper fromApiJsonHelper;
    private final OrderWritePlatformService orderWritePlatformService;
    private final HardwareAssociationReadplatformService hardwareAssociationReadplatformService;
    private final ContractPeriodReadPlatformService contractPeriodReadPlatformService;
    private final TransactionHistoryWritePlatformService transactionHistoryWritePlatformService;
    private final OrderHistoryRepository orderHistory;
    private final OrderRepository orderRepository;
 

	@Autowired
	public EventActionWritePlatformServiceImpl(final TenantAwareRoutingDataSource dataSource,final ActionDetailsReadPlatformService actionDetailsReadPlatformService,
			final EventActionRepository eventActionRepository,final FromJsonHelper fromJsonHelper,final OrderWritePlatformService orderWritePlatformService,
			final HardwareAssociationReadplatformService hardwareAssociationReadplatformService,final ContractPeriodReadPlatformService contractPeriodReadPlatformService,
			final TransactionHistoryWritePlatformService transactionHistoryWritePlatformService,final OrderHistoryRepository orderHistory,final OrderRepository orderRepository)
	{
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.eventActionRepository=eventActionRepository;
        this.actionDetailsReadPlatformService=actionDetailsReadPlatformService;
        this.fromApiJsonHelper=fromJsonHelper;
        this.orderWritePlatformService=orderWritePlatformService;
        this.hardwareAssociationReadplatformService=hardwareAssociationReadplatformService;
        this.contractPeriodReadPlatformService=contractPeriodReadPlatformService;
        this.transactionHistoryWritePlatformService=transactionHistoryWritePlatformService;
        this.orderHistory=orderHistory;
        this.orderRepository=orderRepository;
	}
	
	@Transactional
	@Override
	public void AddNewActions(List<ActionDetaislData> actionDetaislDatas,final Long clientId,final String resourceId) {
    try{
    	
		if(actionDetaislDatas!=null){
			
			for(ActionDetaislData detailsData:actionDetaislDatas){
				
				     EventActionProcedureData actionProcedureData=this.actionDetailsReadPlatformService.checkCustomeValidationForEvents(clientId, EventActionConstants.ACTION_INVOICE,detailsData.getaActionName(),resourceId);
				  
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
				        	   }
				        	
					  
				  }
				
			}
		}
		
    }catch(Exception exception){
    	exception.printStackTrace();
    }
    
   
	}

	@Override
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
			}
			
			EventAction eventAction=this.eventActionRepository.findOne(eventActionData.getId());
	    	eventAction.updateStatus();
	    	this.eventActionRepository.save(eventAction);
		}catch(DataIntegrityViolationException exception){
			
			
		}
		
	}
	
}

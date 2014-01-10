package org.mifosplatform.billing.action.service;

import java.util.Date;
import java.util.List;

import org.codehaus.jettison.json.JSONObject;
import org.mifosplatform.billing.action.data.ActionDetaislData;
import org.mifosplatform.billing.action.data.EventActionProcedureData;
import org.mifosplatform.billing.action.domain.EventAction;
import org.mifosplatform.billing.action.domain.EventActionRepository;
import org.mifosplatform.billing.association.data.AssociationData;
import org.mifosplatform.billing.association.exception.HardwareDetailsNotFoundException;
import org.mifosplatform.billing.association.service.HardwareAssociationReadplatformService;
import org.mifosplatform.billing.contract.data.SubscriptionData;
import org.mifosplatform.billing.contract.service.ContractPeriodReadPlatformService;
import org.mifosplatform.billing.contract.service.ContractPeriodWritePlatformService;
import org.mifosplatform.billing.order.service.OrderWritePlatformService;
import org.mifosplatform.billing.scheduledjobs.data.EventActionData;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

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
 

	@Autowired
	public EventActionWritePlatformServiceImpl(final TenantAwareRoutingDataSource dataSource,final ActionDetailsReadPlatformService actionDetailsReadPlatformService,
			final EventActionRepository eventActionRepository,final FromJsonHelper fromJsonHelper,final OrderWritePlatformService orderWritePlatformService,
			final HardwareAssociationReadplatformService hardwareAssociationReadplatformService,final ContractPeriodReadPlatformService contractPeriodReadPlatformService)
	{
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.eventActionRepository=eventActionRepository;
        this.actionDetailsReadPlatformService=actionDetailsReadPlatformService;
        this.fromApiJsonHelper=fromJsonHelper;
        this.orderWritePlatformService=orderWritePlatformService;
        this.hardwareAssociationReadplatformService=hardwareAssociationReadplatformService;
        this.contractPeriodReadPlatformService=contractPeriodReadPlatformService;
	}
	
	@Override
	public void AddNewActions(List<ActionDetaislData> actionDetaislDatas,final Long clientId,final String resourceId) {
    try{
    	
		if(actionDetaislDatas!=null){
			
			for(ActionDetaislData detailsData:actionDetaislDatas){
				  if(detailsData.getaActionName().equalsIgnoreCase(EventActionConstants.ACTION_RENEWAL)){
					  
					  EventActionProcedureData actionProcedureData=this.actionDetailsReadPlatformService.checkCustomeValidationForEvents(clientId, EventActionConstants.ACTION_INVOICE,detailsData.getaActionName(),resourceId);
					  
					  AssociationData associationData=this.hardwareAssociationReadplatformService.retrieveSingleDetails(actionProcedureData.getOrderId());
					  if(associationData ==null){
						  throw new HardwareDetailsNotFoundException(actionProcedureData.getOrderId().toString());
					  }
					  
				         if(actionProcedureData.isCheck()){
				        	 
				        	 JSONObject jsonObject=new JSONObject();
				        	 List<SubscriptionData> subscriptionDatas=this.contractPeriodReadPlatformService.retrieveSubscriptionDatabyContractType("Month(s)",1); 
   					    	 jsonObject.put("renewalPeriod",subscriptionDatas.get(0).getId());	
				        	 jsonObject.put("description","Order Renewal By Scheduler");
				        	 EventAction eventAction=new EventAction(new Date(), "CREATE", "PAYMENT",detailsData.getaActionName(),"/orders/renewal", 
				        			 Long.parseLong(resourceId), jsonObject.toString(),actionProcedureData.getOrderId(),clientId);
				        	 this.eventActionRepository.save(eventAction);
				         }
				  }else{
					  
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
				final JsonCommand command = JsonCommand.from(jsonObject,parsedCommand,this.fromApiJsonHelper,"DissconnectOrder",eventActionData.getClientId(), null,
						null,eventActionData.getClientId(), null, null, null,null, null, null);
			    	this.orderWritePlatformService.renewalClientOrder(command,eventActionData.getOrderId());
			    	
			    	EventAction eventAction=this.eventActionRepository.findOne(eventActionData.getId());
			    	eventAction.updateStatus();
			    	this.eventActionRepository.save(eventAction);
			}
			
			
		}catch(DataIntegrityViolationException exception){
			
			
		}
		
	}
	
}

package org.mifosplatform.workflow.eventactionmapping.service;


import java.util.Date;
import java.util.List;

import org.mifosplatform.finance.billingorder.service.InvoiceClient;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.portfolio.association.data.HardwareAssociationData;
import org.mifosplatform.portfolio.association.service.HardwareAssociationReadplatformService;
import org.mifosplatform.portfolio.order.domain.OrderHistoryRepository;
import org.mifosplatform.portfolio.order.domain.OrderRepository;
import org.mifosplatform.portfolio.order.service.OrderWritePlatformService;
import org.mifosplatform.portfolio.transactionhistory.service.TransactionHistoryWritePlatformService;
import org.mifosplatform.provisioning.processrequest.domain.ProcessRequest;
import org.mifosplatform.provisioning.processrequest.domain.ProcessRequestDetails;
import org.mifosplatform.provisioning.processrequest.domain.ProcessRequestRepository;
import org.mifosplatform.provisioning.provisioning.api.ProvisioningApiConstants;
import org.mifosplatform.scheduledjobs.scheduledjobs.data.EventActionData;
import org.mifosplatform.workflow.eventaction.domain.EventAction;
import org.mifosplatform.workflow.eventaction.domain.EventActionRepository;
import org.mifosplatform.workflow.eventaction.service.EventActionConstants;
import org.mifosplatform.workflow.eventaction.service.ProcessEventActionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonElement;


@Service
public class ProcessEventActionServiceImpl implements ProcessEventActionService {

	
	
	private final EventActionRepository eventActionRepository;
    private final FromJsonHelper fromApiJsonHelper;
    private final OrderWritePlatformService orderWritePlatformService;
    private final TransactionHistoryWritePlatformService transactionHistoryWritePlatformService;
    private final OrderHistoryRepository orderHistory;
    private final OrderRepository orderRepository;
    private final InvoiceClient invoiceClient;
    private final ProcessRequestRepository processRequestRepository;
    private final HardwareAssociationReadplatformService hardwareAssociationReadplatformService;
    
 

	@Autowired
	public ProcessEventActionServiceImpl(final EventActionRepository eventActionRepository,final FromJsonHelper fromJsonHelper,
			final OrderWritePlatformService orderWritePlatformService,final TransactionHistoryWritePlatformService transactionHistoryWritePlatformService,
			final OrderHistoryRepository orderHistory,final OrderRepository orderRepository,final InvoiceClient invoiceClient,
			final ProcessRequestRepository processRequestRepository,final HardwareAssociationReadplatformService hardwareAssociationReadplatformService)
	{
		this.eventActionRepository=eventActionRepository;
        this.fromApiJsonHelper=fromJsonHelper;
        this.orderWritePlatformService=orderWritePlatformService;
        this.orderHistory=orderHistory;
        this.orderRepository=orderRepository;
        this.invoiceClient=invoiceClient;
        this.transactionHistoryWritePlatformService=transactionHistoryWritePlatformService;
        this.processRequestRepository=processRequestRepository;
        this.hardwareAssociationReadplatformService=hardwareAssociationReadplatformService;
	}
	
	@Override
	@Transactional
	public void ProcessEventActions(EventActionData eventActionData) {
		
		EventAction eventAction=this.eventActionRepository.findOne(eventActionData.getId());
		try{
			 
			if(eventActionData.getActionName().equalsIgnoreCase(EventActionConstants.ACTION_RENEWAL)){
				
				String jsonObject=eventActionData.getJsonData();
				final JsonElement parsedCommand = this.fromApiJsonHelper.parse(jsonObject);
				final JsonCommand command = JsonCommand.from(jsonObject,parsedCommand,this.fromApiJsonHelper,"RenewalOrder",eventActionData.getClientId(), null,
						null,eventActionData.getClientId(), null, null, null,null, null, null,null);
				
			    	this.orderWritePlatformService.renewalClientOrder(command,eventActionData.getOrderId());
			    	/*OrderHistory orderHistory=new OrderHistory(eventActionData.getOrderId(),new LocalDate(),new LocalDate(),null,"Renewal",Long.valueOf(0),null);
			    	Order order=this.orderRepository.findOne(eventActionData.getOrderId());
					this.orderHistory.save(orderHistory);
					
					transactionHistoryWritePlatformService.saveTransactionHistory(eventActionData.getClientId(),"ORDER_"+UserActionStatusTypeEnum.RENEWAL_BEFORE_AUTOEXIPIRY.toString(), order.getStartDate(),
							"PlanId:"+order.getPlanId(),"Renewal Period: 1 Month","OrderID:"+order.getId(),"Billing Align:"+order.getbillAlign());
							
*/
			}else if(eventActionData.getActionName().equalsIgnoreCase(EventActionConstants.ACTION_ACTIVE)){
				
				//Order order=this.orderRepository.findOne(eventActionData.getOrderId());
				this.orderWritePlatformService.reconnectOrder(eventActionData.getOrderId());
			/*	
				transactionHistoryWritePlatformService.saveTransactionHistory(eventActionData.getClientId(),"ORDER_"+UserActionStatusTypeEnum.RECONNECTION.toString(), order.getStartDate(),
						"Price:"+order.getAllPriceAsString(),"PlanId:"+order.getPlanId(),"contarctPeriod: 1 Month","Services:"+order.getAllServicesAsString(),"OrderID:"+order.getId(),"Billing Align:"+order.getbillAlign());
				*/
				
			}else if(eventActionData.getActionName().equalsIgnoreCase(EventActionConstants.ACTION_DISCONNECT)){
				
				String jsonObject=eventActionData.getJsonData();
				final JsonElement parsedCommand = this.fromApiJsonHelper.parse(jsonObject);
				final JsonCommand command = JsonCommand.from(jsonObject,parsedCommand,this.fromApiJsonHelper,"DissconnectOrder",eventActionData.getClientId(), null,
						null,eventActionData.getClientId(), null, null, null,null, null, null,null);
				this.orderWritePlatformService.disconnectOrder(command,	eventActionData.getOrderId());
				
			}else if(eventActionData.getActionName().equalsIgnoreCase(EventActionConstants.ACTION_NEW)){

				String jsonObject=eventActionData.getJsonData();
				final JsonElement parsedCommand = this.fromApiJsonHelper.parse(jsonObject);
				final JsonCommand command = JsonCommand.from(jsonObject,parsedCommand,this.fromApiJsonHelper,"CreateOrder",eventActionData.getClientId(), null,
						null,eventActionData.getClientId(), null, null, null,null, null, null,null);
			
				CommandProcessingResult commandProcessingResult=this.orderWritePlatformService.createOrder(eventActionData.getClientId(), command);
				/*//For Transaction History
	   			transactionHistoryWritePlatformService.saveTransactionHistory(eventActionData.getClientId(), "New Order", new Date(),
	   			     "PlanId:"+plancode,"contarctPeriod: One Month","OrderID:"+commandProcessingResult.resourceId(),
	   			     "BillingAlign:false");*/
			
			}else if(eventActionData.getActionName().equalsIgnoreCase(EventActionConstants.ACTION_INVOICE)){

				try{
				String jsonObject=eventActionData.getJsonData();
				final JsonElement parsedCommand = this.fromApiJsonHelper.parse(jsonObject);
				final JsonCommand command = JsonCommand.from(jsonObject,parsedCommand,this.fromApiJsonHelper,"CreateInvoice",eventActionData.getClientId(), null,
						null,eventActionData.getClientId(), null, null, null,null, null, null,null);

				//CommandProcessingResult commandProcessingResult=this.orderWritePlatformService.createOrder(eventActionData.getClientId(), command);
			   this.invoiceClient.createInvoiceBill(command);
				}catch(Exception exception){
					
				}
				//For Transaction History

			}else if(eventActionData.getActionName().equalsIgnoreCase(EventActionConstants.ACTION_SEND_PROVISION)){

				try{
				
		List<HardwareAssociationData> associationDatas= this.hardwareAssociationReadplatformService.retrieveClientAllocatedHardwareDetails(eventActionData.getClientId());
					if(!associationDatas.isEmpty()){
		   Long none=new Long(0);
					ProcessRequest processRequest=new ProcessRequest(eventActionData.getClientId(), none,ProvisioningApiConstants.PROV_STALKER,'N', null,
							ProvisioningApiConstants.REQUEST_TERMINATE,none);
					ProcessRequestDetails processRequestDetails=new ProcessRequestDetails(none,none,null,"success",associationDatas.get(0).getProvSerialNum(), 
							new Date(), null, new Date(),null,'N', ProvisioningApiConstants.REQUEST_TERMINATE,null);
					processRequest.add(processRequestDetails);
					this.processRequestRepository.save(processRequest);
					}
					
				}catch(Exception exception){
					
				}
				//For Transaction History

			}
			
			
	    	eventAction.updateStatus('Y');
	    	this.eventActionRepository.save(eventAction);
	    	
		}catch(DataIntegrityViolationException exception){
			eventAction.updateStatus('F');
	    	this.eventActionRepository.save(eventAction);
			exception.printStackTrace();
		}catch (Exception exception) {
	//EventAction eventAction=this.eventActionRepository.findOne(eventActionData.getId());
	    	eventAction.updateStatus('F');
	    	this.eventActionRepository.save(eventAction);
			
		}
		
	}
}

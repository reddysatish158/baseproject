package org.mifosplatform.billing.eventactionmapping.service;

import org.mifosplatform.billing.eventaction.domain.EventAction;
import org.mifosplatform.billing.eventaction.domain.EventActionRepository;
import org.mifosplatform.billing.eventaction.service.EventActionConstants;
import org.mifosplatform.billing.eventaction.service.ProcessEventActionService;
import org.mifosplatform.billing.order.domain.Order;
import org.mifosplatform.billing.order.domain.OrderHistoryRepository;
import org.mifosplatform.billing.order.domain.OrderRepository;
import org.mifosplatform.billing.order.service.OrderWritePlatformService;
import org.mifosplatform.billing.scheduledjobs.data.EventActionData;
import org.mifosplatform.billing.transactionhistory.service.TransactionHistoryWritePlatformService;
import org.mifosplatform.finance.billingorder.service.InvoiceClient;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
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
    
 

	@Autowired
	public ProcessEventActionServiceImpl(final EventActionRepository eventActionRepository,final FromJsonHelper fromJsonHelper,
			final OrderWritePlatformService orderWritePlatformService,final TransactionHistoryWritePlatformService transactionHistoryWritePlatformService,
			final OrderHistoryRepository orderHistory,final OrderRepository orderRepository,final InvoiceClient invoiceClient)
	{
		this.eventActionRepository=eventActionRepository;
        this.fromApiJsonHelper=fromJsonHelper;
        this.orderWritePlatformService=orderWritePlatformService;
        this.orderHistory=orderHistory;
        this.orderRepository=orderRepository;
        this.invoiceClient=invoiceClient;
        this.transactionHistoryWritePlatformService=transactionHistoryWritePlatformService;
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
						null,eventActionData.getClientId(), null, null, null,null, null, null);
				
			    	this.orderWritePlatformService.renewalClientOrder(command,eventActionData.getOrderId());
			    	/*OrderHistory orderHistory=new OrderHistory(eventActionData.getOrderId(),new LocalDate(),new LocalDate(),null,"Renewal",Long.valueOf(0),null);
			    	Order order=this.orderRepository.findOne(eventActionData.getOrderId());
					this.orderHistory.save(orderHistory);
					
					transactionHistoryWritePlatformService.saveTransactionHistory(eventActionData.getClientId(),"ORDER_"+UserActionStatusTypeEnum.RENEWAL_BEFORE_AUTOEXIPIRY.toString(), order.getStartDate(),
							"PlanId:"+order.getPlanId(),"Renewal Period: 1 Month","OrderID:"+order.getId(),"Billing Align:"+order.getbillAlign());
							
*/
			}else if(eventActionData.getActionName().equalsIgnoreCase(EventActionConstants.ACTION_ACTIVE)){
				
				Order order=this.orderRepository.findOne(eventActionData.getOrderId());
				this.orderWritePlatformService.reconnectOrder(eventActionData.getOrderId());
			/*	
				transactionHistoryWritePlatformService.saveTransactionHistory(eventActionData.getClientId(),"ORDER_"+UserActionStatusTypeEnum.RECONNECTION.toString(), order.getStartDate(),
						"Price:"+order.getAllPriceAsString(),"PlanId:"+order.getPlanId(),"contarctPeriod: 1 Month","Services:"+order.getAllServicesAsString(),"OrderID:"+order.getId(),"Billing Align:"+order.getbillAlign());
				*/
				
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
						null,eventActionData.getClientId(), null, null, null,null, null, null);

				//CommandProcessingResult commandProcessingResult=this.orderWritePlatformService.createOrder(eventActionData.getClientId(), command);
			   this.invoiceClient.createInvoiceBill(command);
				}catch(Exception exception){
					
				}
				//For Transaction History

			}
			
			
	    	eventAction.updateStatus('Y');
	    	this.eventActionRepository.save(eventAction);
	    	
		}catch(DataIntegrityViolationException exception){
			exception.printStackTrace();
		}catch (Exception exception) {
		//	EventAction eventAction=this.eventActionRepository.findOne(eventActionData.getId());
	    	/*eventAction.updateStatus('F');
	    	this.eventActionRepository.save(eventAction);*/
			
		}
		
	}
}

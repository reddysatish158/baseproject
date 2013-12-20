package org.mifosplatform.billing.hardwareswapping.service;




import java.util.Date;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.joda.time.LocalDate;
import org.mifosplatform.billing.association.service.HardwareAssociationWriteplatformService;
import org.mifosplatform.billing.eventorder.service.PrepareRequestWriteplatformService;
import org.mifosplatform.billing.hardwareswapping.serialization.HardwareSwappingCommandFromApiJsonDeserializer;
import org.mifosplatform.billing.inventory.domain.InventoryItemDetailsAllocation;
import org.mifosplatform.billing.inventory.service.InventoryItemDetailsWritePlatformService;
import org.mifosplatform.billing.order.domain.Order;
import org.mifosplatform.billing.order.domain.OrderHistory;
import org.mifosplatform.billing.order.domain.OrderHistoryRepository;
import org.mifosplatform.billing.order.domain.OrderRepository;
import org.mifosplatform.billing.plan.domain.Plan;
import org.mifosplatform.billing.plan.domain.PlanRepository;
import org.mifosplatform.billing.plan.domain.UserActionStatusTypeEnum;
import org.mifosplatform.billing.transactionhistory.service.TransactionHistoryWritePlatformService;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HardwareSwappingWriteplatformServiceImpl implements HardwareSwappingWriteplatformService {

	private final PlatformSecurityContext context;
	private final HardwareAssociationWriteplatformService associationWriteplatformService;
	private final InventoryItemDetailsWritePlatformService inventoryItemDetailsWritePlatformService;
	private final PrepareRequestWriteplatformService prepareRequestWriteplatformService;
	private final OrderRepository orderRepository;
	private final PlanRepository  planRepository;
	private final TransactionHistoryWritePlatformService transactionHistoryWritePlatformService;
	private final HardwareSwappingCommandFromApiJsonDeserializer fromApiJsonDeserializer;
	private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	private final OrderHistoryRepository orderHistoryRepository;
	  
	@Autowired
	public HardwareSwappingWriteplatformServiceImpl(final PlatformSecurityContext context,final HardwareAssociationWriteplatformService associationWriteplatformService,
			final InventoryItemDetailsWritePlatformService inventoryItemDetailsWritePlatformService,final PrepareRequestWriteplatformService prepareRequestWriteplatformService,
			final OrderRepository orderRepository,final PlanRepository planRepository,final TransactionHistoryWritePlatformService historyWritePlatformService,
			final HardwareSwappingCommandFromApiJsonDeserializer apiJsonDeserializer,final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
			final OrderHistoryRepository orderHistoryRepository) {
 
		this.context=context;
		this.associationWriteplatformService=associationWriteplatformService;
		this.inventoryItemDetailsWritePlatformService=inventoryItemDetailsWritePlatformService;
		this.prepareRequestWriteplatformService=prepareRequestWriteplatformService;
		this.orderRepository=orderRepository;
		this.planRepository=planRepository;
		this.transactionHistoryWritePlatformService=historyWritePlatformService;
		this.fromApiJsonDeserializer=apiJsonDeserializer;
		this.commandsSourceWritePlatformService=commandsSourceWritePlatformService;
		this.orderHistoryRepository=orderHistoryRepository;

	}
	
	
	

@Override
@Transactional
public CommandProcessingResult dohardWareSwapping(Long entityId,JsonCommand command) {
		
	try{
		Long userId=this.context.authenticatedUser().getId();
		this.fromApiJsonDeserializer.validateForCreate(command.json());
		Long associationId=command.longValueOfParameterNamed("associationId");
		String serialNo=command.stringValueOfParameterNamed("serialNo");
		Long orderId=command.longValueOfParameterNamed("orderId");
		Long planId=command.longValueOfParameterNamed("planId");
		Long saleId=command.longValueOfParameterNamed("saleId");
		String provisionNum=command.stringValueOfParameterNamed("provisionNum");
		
		//DeAssociate Hardware
		this.associationWriteplatformService.deAssociationHardware(associationId);
		
		//DeAllocate HardWare
		InventoryItemDetailsAllocation inventoryItemDetailsAllocation=this.inventoryItemDetailsWritePlatformService.deAllocateHardware(serialNo, entityId);
		
		String requstStatus =UserActionStatusTypeEnum.DISCONNECTION.toString();
		Order order=this.orderRepository.findOne(orderId);
		
		Plan plan=this.planRepository.findOne(order.getPlanId());
		this.prepareRequestWriteplatformService.prepareNewRequest(order,plan,requstStatus);
		
		JSONObject allocation = new JSONObject();
		 JSONObject allocation1 = new JSONObject();
		 JSONArray  serialNumber=new JSONArray();
		 
		  
		 allocation.put("itemMasterId",inventoryItemDetailsAllocation.getItemMasterId());
		 allocation.put("clientId",entityId);
		 allocation.put("orderId",saleId);
		 allocation.put("serialNumber",provisionNum);
		 allocation.put("status","allocated");
		 allocation.put("isNewHw","N");
		 
		 serialNumber.put(allocation);
		 allocation1.put("quantity",1);
		 allocation1.put("itemMasterId",inventoryItemDetailsAllocation.getItemMasterId());
		 allocation1.put("serialNumber",serialNumber);
		 
		//ReAllocate HardWare
			//this.inventoryItemDetailsWritePlatformService.allocateHardware(command);
			CommandWrapper commandWrapper = new CommandWrapperBuilder().allocateHardware().withJson(allocation1.toString()).build();
			final CommandProcessingResult result = commandsSourceWritePlatformService.logCommandSource(commandWrapper);
	   //for Reassociation With New SerialNumber
		//	this.associationWriteplatformService.createAssociation(command);
			
		
			requstStatus =UserActionStatusTypeEnum.ACTIVATION.toString();
			CommandProcessingResult processingResult=this.prepareRequestWriteplatformService.prepareNewRequest(order,plan,requstStatus);	
				
				//For Transaction History
				transactionHistoryWritePlatformService.saveTransactionHistory(order.getClientId(), "Hardware Swap",new Date(),"Old Serial No:"+serialNo
						+ " is replaced with New "+provisionNum);
				//For Order History
				OrderHistory orderHistory=new OrderHistory(order.getId(),new LocalDate(),new LocalDate(),null,"Hardware Swap",userId);
		
				this.orderHistoryRepository.save(orderHistory);
		return processingResult;		
	}catch(Exception exception){
		return null;
	}
	
	}
	
	

}

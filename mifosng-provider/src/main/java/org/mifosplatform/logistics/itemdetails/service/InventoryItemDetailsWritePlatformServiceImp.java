package org.mifosplatform.logistics.itemdetails.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationConstants;
import org.mifosplatform.infrastructure.configuration.domain.GlobalConfigurationProperty;
import org.mifosplatform.infrastructure.configuration.domain.GlobalConfigurationRepository;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.logistics.item.domain.ItemMaster;
import org.mifosplatform.logistics.item.domain.ItemRepository;
import org.mifosplatform.logistics.itemdetails.data.AllocationHardwareData;
import org.mifosplatform.logistics.itemdetails.domain.InventoryGrn;
import org.mifosplatform.logistics.itemdetails.domain.InventoryGrnRepository;
import org.mifosplatform.logistics.itemdetails.domain.InventoryItemDetails;
import org.mifosplatform.logistics.itemdetails.domain.InventoryItemDetailsAllocation;
import org.mifosplatform.logistics.itemdetails.domain.InventoryItemDetailsAllocationRepository;
import org.mifosplatform.logistics.itemdetails.domain.InventoryItemDetailsRepository;
import org.mifosplatform.logistics.itemdetails.exception.ActivePlansFoundException;
import org.mifosplatform.logistics.itemdetails.exception.OrderQuantityExceedsException;
import org.mifosplatform.logistics.itemdetails.serialization.InventoryItemAllocationCommandFromApiJsonDeserializer;
import org.mifosplatform.logistics.itemdetails.serialization.InventoryItemCommandFromApiJsonDeserializer;
import org.mifosplatform.logistics.mrn.domain.InventoryTransactionHistory;
import org.mifosplatform.logistics.mrn.domain.InventoryTransactionHistoryJpaRepository;
import org.mifosplatform.logistics.onetimesale.data.AllocationDetailsData;
import org.mifosplatform.logistics.onetimesale.domain.OneTimeSale;
import org.mifosplatform.logistics.onetimesale.domain.OneTimeSaleRepository;
import org.mifosplatform.logistics.onetimesale.service.OneTimeSaleReadPlatformService;
import org.mifosplatform.portfolio.association.data.AssociationData;
import org.mifosplatform.portfolio.association.data.HardwareAssociationData;
import org.mifosplatform.portfolio.association.service.HardwareAssociationReadplatformService;
import org.mifosplatform.portfolio.association.service.HardwareAssociationWriteplatformService;
import org.mifosplatform.portfolio.order.exceptions.NoGrnIdFoundException;
import org.mifosplatform.portfolio.order.service.OrderReadPlatformService;
import org.mifosplatform.portfolio.transactionhistory.service.TransactionHistoryWritePlatformService;
import org.mifosplatform.provisioning.provisioning.service.ProvisioningWritePlatformService;
import org.mifosplatform.scheduledjobs.uploadstatus.domain.UploadStatus;
import org.mifosplatform.scheduledjobs.uploadstatus.domain.UploadStatusRepository;
import org.mifosplatform.workflow.eventactionmapping.exception.EventActionMappingNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;


@Service
public class InventoryItemDetailsWritePlatformServiceImp implements InventoryItemDetailsWritePlatformService{
	
	
	private PlatformSecurityContext context;
	private InventoryItemDetailsRepository inventoryItemDetailsRepository;
	private InventoryGrnRepository inventoryGrnRepository;
	private FromJsonHelper fromJsonHelper;
	private UploadStatusRepository uploadStatusRepository;
	private TransactionHistoryWritePlatformService transactionHistoryWritePlatformService;
	private InventoryItemCommandFromApiJsonDeserializer inventoryItemCommandFromApiJsonDeserializer;
	private InventoryItemAllocationCommandFromApiJsonDeserializer inventoryItemAllocationCommandFromApiJsonDeserializer;
	private InventoryItemDetailsAllocationRepository inventoryItemDetailsAllocationRepository; 
	private InventoryItemDetailsReadPlatformService inventoryItemDetailsReadPlatformService;
	private OneTimeSaleRepository oneTimeSaleRepository;
	private InventoryTransactionHistoryJpaRepository inventoryTransactionHistoryJpaRepository;
	private GlobalConfigurationRepository configurationRepository;
	private HardwareAssociationReadplatformService associationReadplatformService;
	private HardwareAssociationWriteplatformService associationWriteplatformService;
	private final ItemRepository itemRepository;
    private final OneTimeSaleReadPlatformService oneTimeSaleReadPlatformService;
    private final OrderReadPlatformService orderReadPlatformService;
    private final ProvisioningWritePlatformService provisioningWritePlatformService;
	public final static String CONFIG_PROPERTY="Implicit Association";
	
	
	@Autowired
	public InventoryItemDetailsWritePlatformServiceImp(final InventoryItemDetailsReadPlatformService inventoryItemDetailsReadPlatformService, 
			final PlatformSecurityContext context, final InventoryGrnRepository inventoryitemRopository,
			final InventoryItemCommandFromApiJsonDeserializer inventoryItemCommandFromApiJsonDeserializer,final InventoryItemAllocationCommandFromApiJsonDeserializer inventoryItemAllocationCommandFromApiJsonDeserializer, 
			final InventoryItemDetailsAllocationRepository inventoryItemDetailsAllocationRepository,final OneTimeSaleReadPlatformService oneTimeSaleReadPlatformService, 
			final OneTimeSaleRepository oneTimeSaleRepository,final InventoryItemDetailsRepository inventoryItemDetailsRepository,final FromJsonHelper fromJsonHelper, 
			final UploadStatusRepository uploadStatusRepository,final TransactionHistoryWritePlatformService transactionHistoryWritePlatformService,
			final InventoryTransactionHistoryJpaRepository inventoryTransactionHistoryJpaRepository,final GlobalConfigurationRepository  configurationRepository,
			final HardwareAssociationReadplatformService associationReadplatformService,final HardwareAssociationWriteplatformService associationWriteplatformService,
			final ItemRepository itemRepository,final OrderReadPlatformService orderReadPlatformService,final ProvisioningWritePlatformService provisioningWritePlatformService) 
	{
		this.inventoryItemDetailsReadPlatformService = inventoryItemDetailsReadPlatformService;
		this.context=context;
		this.inventoryItemDetailsRepository=inventoryItemDetailsRepository;
		this.inventoryGrnRepository=inventoryitemRopository;
		this.inventoryItemCommandFromApiJsonDeserializer = inventoryItemCommandFromApiJsonDeserializer;
		this.inventoryItemAllocationCommandFromApiJsonDeserializer = inventoryItemAllocationCommandFromApiJsonDeserializer;
		this.inventoryItemDetailsAllocationRepository = inventoryItemDetailsAllocationRepository;
		this.oneTimeSaleReadPlatformService=oneTimeSaleReadPlatformService;
		this.oneTimeSaleRepository = oneTimeSaleRepository;
		this.fromJsonHelper=fromJsonHelper;
		this.uploadStatusRepository=uploadStatusRepository;
		this.transactionHistoryWritePlatformService = transactionHistoryWritePlatformService;
		this.inventoryTransactionHistoryJpaRepository = inventoryTransactionHistoryJpaRepository;
		this.configurationRepository=configurationRepository;
		this.associationReadplatformService=associationReadplatformService;
		this.associationWriteplatformService=associationWriteplatformService;
		this.itemRepository=itemRepository;
		this.orderReadPlatformService=orderReadPlatformService;
		this.provisioningWritePlatformService=provisioningWritePlatformService;
		
	}
	
	private final static Logger logger = (Logger) LoggerFactory.getLogger(InventoryItemDetailsWritePlatformServiceImp.class);
	
	
	
	@SuppressWarnings("unused")
	@Transactional
	@Override
	public CommandProcessingResult addItem(final JsonCommand command,Long flag) {

		
		InventoryItemDetails inventoryItemDetails=null;

		try{
			
			//context.authenticatedUser();
			
			this.context.authenticatedUser();
			
			inventoryItemCommandFromApiJsonDeserializer.validateForCreate(command);
			
			inventoryItemDetails = InventoryItemDetails.fromJson(command,fromJsonHelper);
			Long flag1 = command.longValueOfParameterNamed("flag");
			InventoryGrn inventoryGrn = inventoryGrnRepository.findOne(inventoryItemDetails.getGrnId());
			List<Long> itemMasterId = this.inventoryItemDetailsReadPlatformService.retriveSerialNumberForItemMasterId(inventoryItemDetails.getSerialNumber());
			
			if(itemMasterId.contains(inventoryItemDetails.getItemMasterId())){
				
				throw new PlatformDataIntegrityException("validation.error.msg.inventory.item.duplicate.serialNumber", "validation.error.msg.inventory.item.duplicate.serialNumber", "validation.error.msg.inventory.item.duplicate.serialNumber","validation.error.msg.inventory.item.duplicate.serialNumber");
			}
			 
			
			if(inventoryGrn != null){
				inventoryItemDetails.setOfficeId(inventoryGrn.getOfficeId());
				if(inventoryGrn.getReceivedQuantity() < inventoryGrn.getOrderdQuantity()){
					inventoryGrn.setReceivedQuantity(inventoryGrn.getReceivedQuantity()+1);
					this.inventoryGrnRepository.save(inventoryGrn);
				}else{
					
					throw new OrderQuantityExceedsException(inventoryGrn.getOrderdQuantity());
				}
			}else{
				
				throw new NoGrnIdFoundException(inventoryItemDetails.getGrnId());
			}
			this.inventoryItemDetailsRepository.save(inventoryItemDetails);
			InventoryTransactionHistory transactionHistory = InventoryTransactionHistory.logTransaction(new LocalDate().toDate(), inventoryItemDetails.getId(),"Item Detail",inventoryItemDetails.getSerialNumber(), inventoryItemDetails.getItemMasterId(), inventoryItemDetails.getGrnId(), inventoryGrn.getOfficeId());
			//InventoryTransactionHistory transactionHistory = InventoryTransactionHistory.logTransaction(new LocalDate().toDate(),inventoryItemDetails.getId(),"Item Detail",inventoryItemDetails.getSerialNumber(),inventoryGrn.getOfficeId(),inventoryItemDetails.getClientId(),inventoryItemDetails.getItemMasterId());
			inventoryTransactionHistoryJpaRepository.save(transactionHistory);
			/*++processRecords;
             processStatus="Processed";*/
			
			
		} catch (DataIntegrityViolationException dve){
			
			handleDataIntegrityIssues(command,dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}
			
		return new CommandProcessingResultBuilder().withEntityId(inventoryItemDetails.getId()).build();
	}
	
	
		private void handleDataIntegrityIssues(final JsonCommand element, final DataIntegrityViolationException dve) {

	         Throwable realCause = dve.getMostSpecificCause();
	        if (realCause.getMessage().contains("serial_no_constraint")){
	        	throw new PlatformDataIntegrityException("validation.error.msg.inventory.item.duplicate.serialNumber", "validation.error.msg.inventory.item.duplicate.serialNumber", "validation.error.msg.inventory.item.duplicate.serialNumber","");
	        	
	        }


	        logger.error(dve.getMessage(), dve);   	
	}
		@Transactional
		@Override
		public CommandProcessingResult updateItem(Long id,JsonCommand command)
		{
	        try{
	        	  
	        	this.context.authenticatedUser();
	        	this.inventoryItemCommandFromApiJsonDeserializer.validateForUpdate(command.json());
	        	
	        	InventoryItemDetails inventoryItemDetails=ItemretrieveById(id);
	        	final String oldHardware =inventoryItemDetails.getProvisioningSerialNumber();
	        	final String oldSerilaNumber =inventoryItemDetails.getSerialNumber();
	        	final Map<String, Object> changes = inventoryItemDetails.update(command);  
	        	
	        	if(!changes.isEmpty()){
	        		this.inventoryItemDetailsRepository.save(inventoryItemDetails);
	        	}
	        
	        	
	        	if(!oldHardware.equalsIgnoreCase(inventoryItemDetails.getProvisioningSerialNumber())){
	          	  
	        		this.provisioningWritePlatformService.updateHardwareDetails(inventoryItemDetails.getClientId(),inventoryItemDetails.getSerialNumber(),oldSerilaNumber,
	        				inventoryItemDetails .getProvisioningSerialNumber(),oldHardware);
	        		
	        	}
	        	
	        	return new CommandProcessingResult(id);
	        	
	        }catch(DataIntegrityViolationException dve){
	        	handleDataIntegrityIssues(command, dve);
	        	return null;        }
			
	         
	}
		private InventoryItemDetails ItemretrieveById(Long id) {
            
			InventoryItemDetails itemId=this.inventoryItemDetailsRepository.findOne(id);
	              if (itemId== null) { throw new EventActionMappingNotFoundException(id.toString()); }
		          return itemId;	
		}

		@Transactional
		@Override
		public CommandProcessingResult allocateHardware(JsonCommand command) {
			Long id = null;
			try{
				context.authenticatedUser();
				
				this.context.authenticatedUser();
				inventoryItemAllocationCommandFromApiJsonDeserializer.validateForCreate(command.json());
				
				/*
				 * data comming from the client side is stored in inventoryItemAllocation
				 * */
				 final JsonElement element = fromJsonHelper.parse(command.json());
			        
			        
			        JsonArray allocationData = fromJsonHelper.extractJsonArrayNamed("serialNumber", element);
			        int i=1;
			        for(JsonElement j:allocationData){
			        	
			        	InventoryItemDetailsAllocation inventoryItemDetailsAllocation=null;
						AllocationHardwareData allocationHardwareData = null;
						InventoryItemDetails inventoryItemDetails=null;
			        	inventoryItemDetailsAllocation = InventoryItemDetailsAllocation.fromJson(j,fromJsonHelper);
			        	try{
			        		allocationHardwareData = inventoryItemDetailsReadPlatformService.retriveInventoryItemDetail(inventoryItemDetailsAllocation.getSerialNumber());
			        	
							if(allocationHardwareData == null){
								throw new PlatformDataIntegrityException("invalid.serial.no", "invalid.serial.no","serialNumber");
							}
							
							if(!allocationHardwareData.getQuality().equalsIgnoreCase("Good")){
								throw new PlatformDataIntegrityException("product.not.in.good.condition", "product.not.in.good.condition","product.not.in.good.condition");
			        		}
														
							if(allocationHardwareData.getClientId()!=null && allocationHardwareData.getClientId()!=0){
								
								if(allocationHardwareData.getClientId()>0){
									throw new PlatformDataIntegrityException("SerialNumber "+inventoryItemDetailsAllocation.getSerialNumber()+" already allocated.", "SerialNumber "+inventoryItemDetailsAllocation.getSerialNumber()+ "already allocated.","serialNumber"+i);	
								
								
								//throw new PlatformDataIntegrityException("SerialNumber "+inventoryItemDetailsAllocation.getSerialNumber()+" already allocated.", "SerialNumber "+inventoryItemDetailsAllocation.getSerialNumber()+ "already allocated.","serialNumber"+i);
								}}
							}catch(EmptyResultDataAccessException e){
								throw new PlatformDataIntegrityException("SerialNumber SerialNumber"+i+" doest not exist.","SerialNumber SerialNumber"+i+" doest not exist.","serialNumber"+i);
							}
			        	
			        	inventoryItemDetails = inventoryItemDetailsRepository.findOne(allocationHardwareData.getItemDetailsId());
			        	
						inventoryItemDetails.setItemMasterId(inventoryItemDetailsAllocation.getItemMasterId());
						inventoryItemDetails.setClientId(inventoryItemDetailsAllocation.getClientId());
						inventoryItemDetails.setStatus("Used");
						
						
						this.inventoryItemDetailsRepository.save(inventoryItemDetails);
						this.inventoryItemDetailsRepository.flush();
						this.inventoryItemDetailsAllocationRepository.save(inventoryItemDetailsAllocation);
						this.inventoryItemDetailsAllocationRepository.flush();
						OneTimeSale ots = this.oneTimeSaleRepository.findOne(inventoryItemDetailsAllocation.getOrderId());
						ots.setHardwareAllocated("ALLOCATED");
						this.oneTimeSaleRepository.save(ots);
						this.oneTimeSaleRepository.flush();
						final String isHwSwap = fromJsonHelper.extractStringNamed("isNewHw", j);
					
						if(isHwSwap.equalsIgnoreCase("Y")){
						this.transactionHistoryWritePlatformService.saveTransactionHistory(ots.getClientId(), "Allocation", ots.getSaleDate(),
								"Units:"+ots.getUnits(),"ChargeCode:"+ots.getChargeCode(),"Quantity:"+ots.getQuantity(),"ItemId:"+ots.getItemId(),"SerialNumber:"+inventoryItemDetailsAllocation.getSerialNumber());
						}

						InventoryTransactionHistory transactionHistory = InventoryTransactionHistory.logTransaction(new LocalDate().toDate(), 
								ots.getId(),"Allocation",inventoryItemDetailsAllocation.getSerialNumber(), inventoryItemDetailsAllocation.getItemMasterId(),
								inventoryItemDetails.getOfficeId(),inventoryItemDetailsAllocation.getClientId());
						
						inventoryTransactionHistoryJpaRepository.save(transactionHistory);
						inventoryTransactionHistoryJpaRepository.flush();
						id = inventoryItemDetailsAllocation.getId();
						i++;
						
						  //For Plan And HardWare Association
						GlobalConfigurationProperty configurationProperty=this.configurationRepository.findOneByName(CONFIG_PROPERTY);
						
						if(configurationProperty.isEnabled()){
							configurationProperty=this.configurationRepository.findOneByName(ConfigurationConstants.CPE_TYPE);
							
							if(configurationProperty.getValue().equalsIgnoreCase(ConfigurationConstants.CONFIR_PROPERTY_SALE)){
								
						         ItemMaster itemMaster=this.itemRepository.findOne(inventoryItemDetails.getItemMasterId());
						    	
						    	//   PlanHardwareMapping hardwareMapping=this.hardwareMappingRepository.findOneByItemCode(itemMaster.getItemCode());
						    	   
						    	//   if(hardwareMapping!=null){
						    		   
						    		   List<HardwareAssociationData> allocationDetailsDatas=this.associationReadplatformService.retrieveClientAllocatedPlan(ots.getClientId(),itemMaster.getItemCode());						    		   
						    		   if(!allocationDetailsDatas.isEmpty())
						    		   {
						    				this.associationWriteplatformService.createNewHardwareAssociation(ots.getClientId(),allocationDetailsDatas.get(0).getPlanId(),inventoryItemDetails.getSerialNumber(),allocationDetailsDatas.get(0).getorderId());
						    				if(isHwSwap.equalsIgnoreCase("Y")){
						    				transactionHistoryWritePlatformService.saveTransactionHistory(ots.getClientId(), "Association", new Date(),"Serial No:"
						    				+inventoryItemDetailsAllocation.getSerialNumber(),"Item Code:"+allocationDetailsDatas.get(0).getItemCode());
						    				}
						    				
						    		   }
						    		   
						    	//   }
							}
						    }
						
						
						
					}
			      
				
			}catch(DataIntegrityViolationException dve){
				handleDataIntegrityIssues(command, dve); 
				return new CommandProcessingResult(Long.valueOf(-1));
			}
			return new CommandProcessingResultBuilder().withCommandId(1L).withEntityId(id).build();
			/*command is has to be changed to command.commandId() in the above code*/
		}
		
		
		public void genarateException(String uploadStatus,Long orderId,Long processRecords){
			String processStatus="New Unprocessed";
			String errormessage="item details already exist";
			LocalDate currentDate = new LocalDate();
			currentDate.toDate();
			if(uploadStatus.equalsIgnoreCase("UPLOADSTATUS")){
				UploadStatus uploadStatusObject = this.uploadStatusRepository.findOne(orderId);
				uploadStatusObject.update(currentDate,processStatus,processRecords,null,errormessage,null);
				this.uploadStatusRepository.save(uploadStatusObject);
			}
			
			 throw new PlatformDataIntegrityException("received.quantity.is.nill.hence.your.item.details.will.not.be.saved","","");
		}

         
		@Override
		public InventoryItemDetailsAllocation deAllocateHardware(String serialNo,Long clientId) {
			try
			{
				AllocationDetailsData allocationDetailsData=this.oneTimeSaleReadPlatformService.retrieveAllocationDetailsBySerialNo(serialNo);
				InventoryItemDetailsAllocation inventoryItemDetailsAllocation=null;
				   if(allocationDetailsData!=null){
					   
					    inventoryItemDetailsAllocation =this.inventoryItemDetailsAllocationRepository.findOne(allocationDetailsData.getId());
					   
					      inventoryItemDetailsAllocation.deAllocate();
					      this.inventoryItemDetailsAllocationRepository.save(inventoryItemDetailsAllocation);
					      
					      InventoryItemDetails inventoryItemDetails=this.inventoryItemDetailsRepository.findOne(allocationDetailsData.getItemDetailId());
					      
					      inventoryItemDetails.delete();
					      
					      this.inventoryItemDetailsRepository.save(inventoryItemDetails);
					     
					  	InventoryTransactionHistory transactionHistory = InventoryTransactionHistory.logTransaction(new LocalDate().toDate(), 
					  			inventoryItemDetailsAllocation.getOrderId(),"De Allocation",inventoryItemDetailsAllocation.getSerialNumber(), inventoryItemDetailsAllocation.getItemMasterId(),
								inventoryItemDetailsAllocation.getClientId(),inventoryItemDetails.getOfficeId());
					  	inventoryTransactionHistoryJpaRepository.save(transactionHistory);
					   
				   }
				   return inventoryItemDetailsAllocation;
			}catch(DataIntegrityViolationException exception){
				handleDataIntegrityIssues(null, exception);
				
				return null;
			}
			
		}


		@Override
		public CommandProcessingResult deAllocateHardware(JsonCommand command) {

           try{
        	   
        	   String serialNo=command.stringValueOfParameterNamed("serialNo");
        	   Long clientId=command.longValueOfParameterNamed("clientId");
        	 Long activeorders=this.orderReadPlatformService.retrieveClientActiveOrderDetails(clientId,serialNo);
        	   
        	   if(activeorders!= 0){
        		   throw new ActivePlansFoundException();
        	   }
        	   List<AssociationData> associationDatas=this.associationReadplatformService.retrieveClientAssociationDetails(clientId);
        	    
        	   for(AssociationData associationData:associationDatas ){
        		   
        		   this.associationWriteplatformService.deAssociationHardware(associationData.getId());
        		   
        	   }
        	   
        	   InventoryItemDetailsAllocation inventoryItemDetailsAllocation=this.deAllocateHardware(serialNo, clientId);
        	   
        	   OneTimeSale oneTimeSale=this.oneTimeSaleRepository.findOne(inventoryItemDetailsAllocation.getOrderId());
        	   
        	   oneTimeSale.setStatus();
        	   this.oneTimeSaleRepository.save(oneTimeSale);
        	   String itemCode=null;
        	   if(!associationDatas.isEmpty()){
        		   itemCode=associationDatas.get(0).getItemCode();
        	   }else{
        		   itemCode=oneTimeSale.getItemId().toString();
        	   }
        		transactionHistoryWritePlatformService.saveTransactionHistory(clientId, "Device Return", new Date(),"Serial Number :"
	    				+inventoryItemDetailsAllocation.getSerialNumber(),"Item Code:"+itemCode,"Order Id: "+inventoryItemDetailsAllocation.getOrderId());
        	   
        	   return new CommandProcessingResult(command.entityId());
           }catch(DataIntegrityViolationException exception){
        	   
        	   return new CommandProcessingResult(Long.valueOf(-1));
           }
		}
}




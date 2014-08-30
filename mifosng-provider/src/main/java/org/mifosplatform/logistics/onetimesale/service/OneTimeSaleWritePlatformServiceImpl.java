package org.mifosplatform.logistics.onetimesale.service;

import java.math.BigDecimal;
import java.util.List;

import org.mifosplatform.billing.pricing.service.PriceReadPlatformService;
import org.mifosplatform.finance.data.DiscountMasterData;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.api.JsonQuery;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.logistics.item.data.ItemData;
import org.mifosplatform.logistics.item.domain.ItemMaster;
import org.mifosplatform.logistics.item.domain.ItemRepository;
import org.mifosplatform.logistics.item.service.ItemReadPlatformService;
import org.mifosplatform.logistics.itemdetails.exception.ActivePlansFoundException;
import org.mifosplatform.logistics.itemdetails.service.InventoryItemDetailsWritePlatformService;
import org.mifosplatform.logistics.onetimesale.data.OneTimeSaleData;
import org.mifosplatform.logistics.onetimesale.domain.OneTimeSale;
import org.mifosplatform.logistics.onetimesale.domain.OneTimeSaleRepository;
import org.mifosplatform.logistics.onetimesale.serialization.OneTimesaleCommandFromApiJsonDeserializer;
import org.mifosplatform.portfolio.order.data.CustomValidationData;
import org.mifosplatform.portfolio.order.service.OrderDetailsReadPlatformServices;
import org.mifosplatform.portfolio.transactionhistory.service.TransactionHistoryWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


@Service
public class OneTimeSaleWritePlatformServiceImpl implements OneTimeSaleWritePlatformService{
	
	private final PlatformSecurityContext context;
	private final OneTimeSaleRepository  oneTimeSaleRepository;
	private final ItemRepository itemMasterRepository;
	
	private final OneTimesaleCommandFromApiJsonDeserializer apiJsonDeserializer;
	private final InvoiceOneTimeSale invoiceOneTimeSale;
	private final ItemReadPlatformService itemReadPlatformService;
	private final OneTimeSaleReadPlatformService oneTimeSaleReadPlatformService;
	private final FromJsonHelper fromJsonHelper;
	private final TransactionHistoryWritePlatformService transactionHistoryWritePlatformService;
	private final PriceReadPlatformService priceReadPlatformService;
	private final InventoryItemDetailsWritePlatformService inventoryItemDetailsWritePlatformService;
	private final OrderDetailsReadPlatformServices orderDetailsReadPlatformServices;
	
	@Autowired
	public OneTimeSaleWritePlatformServiceImpl(final PlatformSecurityContext context,final OneTimeSaleRepository oneTimeSaleRepository,
			final ItemRepository itemMasterRepository,final OneTimesaleCommandFromApiJsonDeserializer apiJsonDeserializer,
			final InvoiceOneTimeSale invoiceOneTimeSale,final ItemReadPlatformService itemReadPlatformService,final FromJsonHelper fromJsonHelper,
			final TransactionHistoryWritePlatformService transactionHistoryWritePlatformService,final OneTimeSaleReadPlatformService oneTimeSaleReadPlatformService,
			final PriceReadPlatformService priceReadPlatformService,final InventoryItemDetailsWritePlatformService inventoryItemDetailsWritePlatformService,
			final OrderDetailsReadPlatformServices orderDetailsReadPlatformServices)
	{
		this.context=context;
		this.oneTimeSaleRepository=oneTimeSaleRepository;
		this.itemMasterRepository=itemMasterRepository;
		this.apiJsonDeserializer=apiJsonDeserializer;
		this.invoiceOneTimeSale=invoiceOneTimeSale;
		this.itemReadPlatformService=itemReadPlatformService;
		this.oneTimeSaleReadPlatformService=oneTimeSaleReadPlatformService;
		this.fromJsonHelper=fromJsonHelper;
		this.transactionHistoryWritePlatformService = transactionHistoryWritePlatformService;
		this.priceReadPlatformService=priceReadPlatformService;
		this.inventoryItemDetailsWritePlatformService=inventoryItemDetailsWritePlatformService;
		this.orderDetailsReadPlatformServices=orderDetailsReadPlatformServices;
		
	}
	
	@Transactional
	@Override
	public CommandProcessingResult createOneTimeSale(JsonCommand command,Long clientId) {
		
		try{
			this.context.authenticatedUser();
			
			this.apiJsonDeserializer.validateForCreate(command.json());
			final JsonElement element = fromJsonHelper.parse(command.json());
		    final Long itemId=command.longValueOfParameterNamed("itemId");
		    
			//Check for Custome_Validation
			CustomValidationData customValidationData   = this.orderDetailsReadPlatformServices.checkForCustomValidations(clientId,"Rental", command.json());
			if(customValidationData.getErrorCode() != 0 && customValidationData.getErrorMessage() != null){
				throw new ActivePlansFoundException(customValidationData.getErrorMessage()); 
			}
			ItemMaster item=this.itemMasterRepository.findOne(itemId);
			OneTimeSale oneTimeSale=OneTimeSale.fromJson(clientId,command,item);
			this.oneTimeSaleRepository.saveAndFlush(oneTimeSale);
			List<OneTimeSaleData> oneTimeSaleDatas = oneTimeSaleReadPlatformService.retrieveOnetimeSaleDate(clientId);
			  JsonObject jsonObject =new JsonObject();
			final String saleType = command.stringValueOfParameterNamed("saleType");
			if(saleType.equalsIgnoreCase("FirstSale")){
				
				for (OneTimeSaleData oneTimeSaleData : oneTimeSaleDatas) {
					this.invoiceOneTimeSale.invoiceOneTimeSale(clientId,oneTimeSaleData);
					updateOneTimeSale(oneTimeSaleData);
				}
			}
			
			transactionHistoryWritePlatformService.saveTransactionHistory(oneTimeSale.getClientId(),"Item Sale", oneTimeSale.getSaleDate(),
					"TotalPrice:"+oneTimeSale.getTotalPrice(),"Quantity:"+oneTimeSale.getQuantity(),"Units:"+oneTimeSale.getUnits(),"OneTimeSaleID:"+oneTimeSale.getId());
			
			JsonArray serialData = fromJsonHelper.extractJsonArrayNamed("serialNumber", element);
        	 
        	 for(JsonElement je:serialData){
        		 
        		 JsonObject serialNumber=je.getAsJsonObject();
        		 serialNumber.addProperty("clientId",oneTimeSale.getClientId());
        		 serialNumber.addProperty("orderId",oneTimeSale.getId());
        	     
        	 }
        	 
        	 jsonObject.addProperty("itemMasterId", oneTimeSale.getItemId());
        	 jsonObject.addProperty("quantity", oneTimeSale.getQuantity());
        	 jsonObject.add("serialNumber", serialData);
        	 JsonCommand jsonCommand=new JsonCommand(null, jsonObject.toString(),element, fromJsonHelper, null, null, null, null, null, null, null, 
        			                       null, null, null, null,null);
			this.inventoryItemDetailsWritePlatformService.allocateHardware(jsonCommand);
			return new CommandProcessingResult(Long.valueOf(oneTimeSale.getId()));
		} catch (DataIntegrityViolationException dve) {
			 handleCodeDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
			
		}
		}
	
	private void handleCodeDataIntegrityIssues(JsonCommand command,DataIntegrityViolationException dve) {
		// TODO Auto-generated method stub
		
	}
	public void updateOneTimeSale(OneTimeSaleData oneTimeSaleData) {
		
		OneTimeSale oneTimeSale = oneTimeSaleRepository.findOne(oneTimeSaleData.getId());
		oneTimeSale.setDeleted('y');
		oneTimeSaleRepository.save(oneTimeSale);
		
	}
	
	@Override
	public ItemData calculatePrice(Long itemId, JsonQuery query) {

		try{
			this.context.authenticatedUser();
			this.apiJsonDeserializer.validateForPrice(query.parsedJson());
			final Integer quantity = fromJsonHelper.extractIntegerWithLocaleNamed("quantity", query.parsedJson());
			ItemMaster itemMaster=this.itemMasterRepository.findOne(itemId);
			if(itemMaster == null)
			{
				throw new RuntimeException();
			}
			BigDecimal TotalPrice=itemMaster.getUnitPrice().multiply(new BigDecimal(quantity));
			List<ItemData> itemCodeData = this.oneTimeSaleReadPlatformService.retrieveItemData();
			List<DiscountMasterData> discountdata = this.priceReadPlatformService.retrieveDiscountDetails();
			ItemData itemData = this.itemReadPlatformService.retrieveSingleItemDetails(itemId);
			return new ItemData(itemCodeData,itemData,TotalPrice,quantity,discountdata);
		}catch (DataIntegrityViolationException dve) {
			 handleCodeDataIntegrityIssues(null, dve);
			return null;
	}
		

}
	@Override
	public CommandProcessingResult deleteOneTimeSale(JsonCommand command, Long entityId) {
		
		OneTimeSale oneTimeSale = null;
		try{
			oneTimeSale = oneTimeSaleRepository.findOne(entityId);
			oneTimeSale.setIsDeleted('Y');
			oneTimeSaleRepository.save(oneTimeSale);
			transactionHistoryWritePlatformService.saveTransactionHistory(oneTimeSale.getClientId(),"Cancel Item Sale", oneTimeSale.getSaleDate(),
					"TotalPrice:"+oneTimeSale.getTotalPrice(),"Quantity:"+oneTimeSale.getQuantity(),"Units:"+oneTimeSale.getUnits(),"OneTimeSaleID:"+oneTimeSale.getId());
			
		}catch(DataIntegrityViolationException dve){
			handleCodeDataIntegrityIssues(null, dve);
		}
		return new CommandProcessingResult(Long.valueOf(oneTimeSale.getId()));
	}
}
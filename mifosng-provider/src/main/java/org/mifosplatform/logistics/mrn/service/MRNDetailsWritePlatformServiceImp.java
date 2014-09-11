package org.mifosplatform.logistics.mrn.service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.logistics.agent.domain.ItemSale;
import org.mifosplatform.logistics.agent.domain.ItemSaleRepository;
import org.mifosplatform.logistics.itemdetails.domain.InventoryItemDetails;
import org.mifosplatform.logistics.itemdetails.domain.InventoryItemDetailsRepository;
import org.mifosplatform.logistics.itemdetails.exception.SerialNumberNotFoundException;
import org.mifosplatform.logistics.mrn.api.MRNDetailsJpaRepository;
import org.mifosplatform.logistics.mrn.data.MRNMoveDetailsData;
import org.mifosplatform.logistics.mrn.domain.InventoryTransactionHistory;
import org.mifosplatform.logistics.mrn.domain.InventoryTransactionHistoryJpaRepository;
import org.mifosplatform.logistics.mrn.domain.MRNDetails;
import org.mifosplatform.logistics.mrn.serialization.MRNDetailsCommandFromApiJsonDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MRNDetailsWritePlatformServiceImp implements MRNDetailsWritePlatformService{

	private final static Logger logger = (Logger) LoggerFactory.getLogger(MRNDetailsWritePlatformServiceImp.class);
	
	private final MRNDetailsJpaRepository mrnDetailsJpaRepository;
	private final ItemSaleRepository itemSaleRepository;
	private final PlatformSecurityContext context; 
	private final MRNDetailsCommandFromApiJsonDeserializer apiJsonDeserializer;
	private final MRNDetailsReadPlatformService mrnDetailsReadPlatformService;
	private final InventoryTransactionHistoryJpaRepository inventoryTransactionHistoryJpaRepository;
	private final InventoryItemDetailsRepository inventoryItemDetailsRepository;
	@Autowired
	public MRNDetailsWritePlatformServiceImp(final MRNDetailsJpaRepository mrnDetailsJpaRepository
			, final PlatformSecurityContext context
			, final MRNDetailsCommandFromApiJsonDeserializer apiJsonDeserializer
			,final MRNDetailsReadPlatformService mrnDetailsReadPlatformService
			, final InventoryTransactionHistoryJpaRepository inventoryTransactionHistoryJpaRepository
			,final InventoryItemDetailsRepository inventoryItemDetailsRepository
			,final ItemSaleRepository itemSaleRepository) {
		this.mrnDetailsJpaRepository = mrnDetailsJpaRepository;
		this.context = context;
		this.apiJsonDeserializer = apiJsonDeserializer;
		this.mrnDetailsReadPlatformService = mrnDetailsReadPlatformService;
		this.inventoryTransactionHistoryJpaRepository = inventoryTransactionHistoryJpaRepository;
		this.inventoryItemDetailsRepository = inventoryItemDetailsRepository;
		this.itemSaleRepository=itemSaleRepository;
	}
	
	
	@Transactional
	@Override
	public CommandProcessingResult createMRNDetails(JsonCommand command) {
		context.authenticatedUser();
		apiJsonDeserializer.validateForCreate(command.json());
		MRNDetails mrnDetails = null;
		try {
			
			mrnDetails = MRNDetails.formJson(command);
			mrnDetailsJpaRepository.save(mrnDetails);
			
			
		}catch(DataIntegrityViolationException dve){
			handleDataIntegrityIssues(command, dve);
		}catch (ParseException e) {
			throw new PlatformDataIntegrityException("invalid.date.format", "invalid.date.format", "purchaseDate");
		}
		return new CommandProcessingResultBuilder().withEntityId(mrnDetails.getId()).build();
	}
	
	@Transactional
	@Override
	public CommandProcessingResult moveMRN(JsonCommand command) {
		context.authenticatedUser();
		apiJsonDeserializer.validateForMove(command.json());
		InventoryTransactionHistory transactionHistory = null;
		try {
			final Long mrnId = command.longValueOfParameterNamed("mrnId");
			MRNMoveDetailsData mrnMoveDetailsData = MRNMoveDetailsData.fromJson(command,mrnId);
			
			MRNDetails mrnDetails = mrnDetailsJpaRepository.findOne(mrnId);
			List<Long> itemMasterId = mrnDetailsReadPlatformService.retriveItemMasterId(mrnId);
			
			final List<String> serialNumber = mrnDetailsReadPlatformService.retriveSerialNumbers(mrnDetails.getFromOffice(),mrnId);
			
			if(!serialNumber.contains(mrnMoveDetailsData.getSerialNumber())){
				throw new PlatformDataIntegrityException("invalid.serialnumber.allocation", "invalid.serialnumber.allocation", "serialNumber","");
			}
			
			List<Long> itemDetailsId = mrnDetailsReadPlatformService.retriveItemDetailsId(mrnMoveDetailsData.getSerialNumber(), itemMasterId.get(0));
			InventoryItemDetails details = inventoryItemDetailsRepository.findOne(itemDetailsId.get(0));
			
			
			
			if(details.getOfficeId().equals(mrnDetails.getToOffice())){
				throw new PlatformDataIntegrityException("invalid.move.operation", "invalid.move.operation", "invalid.move.operation");
			}
			details.setOfficeId(mrnDetails.getToOffice());
			if(mrnDetails.getReceivedQuantity() < mrnDetails.getOrderdQuantity()){
				mrnDetails.setReceivedQuantity(mrnDetails.getReceivedQuantity()+1);
				mrnDetails.setStatus("Pending");
			} else if(mrnDetails.getReceivedQuantity().equals(mrnDetails.getOrderdQuantity())){
				throw new PlatformDataIntegrityException("received.quantity.is.full", "received.quantity.is.full", "received.quantity.is.full");
			}
			
			transactionHistory = InventoryTransactionHistory.logTransaction(mrnMoveDetailsData.getMovedDate(), mrnId,"MRN", mrnMoveDetailsData.getSerialNumber(), itemMasterId.get(0), mrnDetails.getFromOffice(), mrnDetails.getToOffice());
			//InventoryTransactionHistory transactionHistory = InventoryTransactionHistory.logTransaction(mrnMoveDetailsData.getMovedDate(),mrnMoveDetailsData.getMrnId(),"MRN",mrnMoveDetailsData.getSerialNumber(),mrnDetails.getFromOffice(),mrnDetails.getToOffice(),itemMasterId.get(0));
			
			details.setOfficeId(mrnDetails.getToOffice());
			inventoryItemDetailsRepository.save(details);
			inventoryTransactionHistoryJpaRepository.save(transactionHistory);
			if(mrnDetails.getOrderdQuantity().equals(mrnDetails.getReceivedQuantity())){
				mrnDetails.setStatus("Completed");
			}
			mrnDetailsJpaRepository.save(mrnDetails);
			
			
		} catch (ParseException e) {
			throw new PlatformDataIntegrityException("invalid.moved.date", "invalid.moved.date", "invalid.moved.date");
		} catch (EmptyResultDataAccessException e) {
			throw new PlatformDataIntegrityException("serial.number.doest.not.exist", "serial.number.doest.not.exist", "serial.number.doest.not.exist");
		}
	
		
		/*
		 * take data from mrndetails update office id with tooffice id in itemdetails table then update mrntransaction history table 
		 * */
		
		return new CommandProcessingResultBuilder().withEntityId(transactionHistory.getId()).build();
	}
	
	
	
	private void handleDataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve) {

        Throwable realCause = dve.getMostSpecificCause();
       if (realCause.getMessage().contains("serial_no_constraint")){
       	throw new PlatformDataIntegrityException("validation.error.msg.inventory.mrn.duplicate.entry", "validation.error.msg.inventory.mrn.duplicate.entry", "validation.error.msg.inventory.mrn.duplicate.entry","");
       	
       }
       logger.error(dve.getMessage(), dve);   	
}


	@Override
	public CommandProcessingResult moveItemSale(JsonCommand command) {
		
		context.authenticatedUser();
		apiJsonDeserializer.validateForMove(command.json());
		InventoryTransactionHistory transactionHistory = null;
		try {
			final Long itemId = command.longValueOfParameterNamed("itemId");
			//MRNMoveDetailsData mrnMoveDetailsData = MRNMoveDetailsData.fromJson(command,itemId);
			final String serialNumber= command.stringValueOfParameterNamed("serialNumber");
			
			ItemSale mrnDetails = itemSaleRepository.findOne(itemId);
			List<Long> itemMasterId = mrnDetailsReadPlatformService.retriveItemMasterIdForSale(itemId);
			
			final List<String> serialNumbers = mrnDetailsReadPlatformService.retriveSerialNumbersForItems(mrnDetails.getPurchaseFrom(),itemId,serialNumber);
			if(serialNumbers == null || serialNumbers.size() == 0){
				
				throw new SerialNumberNotFoundException(serialNumber);
			}
			List<Long> itemDetailsId = mrnDetailsReadPlatformService.retriveItemDetailsId(serialNumber, itemMasterId.get(0));
			InventoryItemDetails details = inventoryItemDetailsRepository.findOne(itemDetailsId.get(0));
			
			/*if(details.getOfficeId().equals(mrnDetails.getAgentId())){
				throw new PlatformDataIntegrityException("invalid.move.operation", "invalid.move.operation", "invalid.move.operation");
			}*/
		
			details.setOfficeId(mrnDetails.getPurchaseBy());
			
			if(mrnDetails.getReceivedQuantity() < mrnDetails.getOrderQuantity()){
				mrnDetails.setReceivedQuantity(mrnDetails.getReceivedQuantity()+1);
				mrnDetails.setStatus("Pending");
			
		
			} else if(mrnDetails.getReceivedQuantity().equals(mrnDetails.getOrderQuantity())){
				throw new PlatformDataIntegrityException("received.quantity.is.full", "received.quantity.is.full", "received.quantity.is.full");
			}
			
			transactionHistory = InventoryTransactionHistory.logTransaction(new Date(), itemId,"Move ItemSale",serialNumber, itemMasterId.get(0), mrnDetails.getPurchaseFrom(), mrnDetails.getPurchaseBy());
			//InventoryTransactionHistory transactionHistory = InventoryTransactionHistory.logTransaction(mrnMoveDetailsData.getMovedDate(),mrnMoveDetailsData.getMrnId(),"MRN",mrnMoveDetailsData.getSerialNumber(),mrnDetails.getFromOffice(),mrnDetails.getToOffice(),itemMasterId.get(0));
			
			details.setOfficeId(mrnDetails.getPurchaseBy());
			inventoryItemDetailsRepository.save(details);
			inventoryTransactionHistoryJpaRepository.save(transactionHistory);
			if(mrnDetails.getOrderQuantity().equals(mrnDetails.getReceivedQuantity())){
				mrnDetails.setStatus("Completed");
			}
			itemSaleRepository.save(mrnDetails);
			
			
		}/* catch (EmptyResultDataAccessException e) {
			throw new PlatformDataIntegrityException("serial.number.doest.not.exist", "serial.number.doest.not.exist", "serial.number.doest.not.exist");
		}*/
		catch (EmptyResultDataAccessException e) {
			//throw new PlatformDataIntegrityException("invalid.moved.date", "invalid.moved.date", "invalid.moved.date");
			throw new PlatformDataIntegrityException("serial.number.doest.not.exist", "serial.number.doest.not.exist", "serial.number.doest.not.exist");
			//handleDataIntegrityIssues(command, e);
			//e.printStackTrace();
		} 
	
		
		/*
		 * take data from mrndetails update office id with tooffice id in itemdetails table then update mrntransaction history table 
		 * */
		
		return new CommandProcessingResultBuilder().withEntityId(transactionHistory.getId()).build();
	}
}

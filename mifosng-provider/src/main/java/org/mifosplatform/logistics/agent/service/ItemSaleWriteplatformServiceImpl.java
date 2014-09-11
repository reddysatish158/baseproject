package org.mifosplatform.logistics.agent.service;

import java.math.BigDecimal;

import org.mifosplatform.billing.taxmapping.domain.TaxMap;
import org.mifosplatform.billing.taxmapping.domain.TaxMapRepository;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.logistics.agent.domain.ItemSale;
import org.mifosplatform.logistics.agent.domain.ItemSaleInvoice;
import org.mifosplatform.logistics.agent.domain.ItemSaleInvoiceRepository;
import org.mifosplatform.logistics.agent.domain.ItemSaleRepository;
import org.mifosplatform.logistics.agent.serialization.AgentItemSaleCommandFromApiJsonDeserializer;
import org.mifosplatform.logistics.item.domain.ItemMaster;
import org.mifosplatform.logistics.item.domain.ItemRepository;
import org.mifosplatform.logistics.item.exception.ItemNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class ItemSaleWriteplatformServiceImpl implements ItemSaleWriteplatformService{

	private final PlatformSecurityContext context;
	private final ItemSaleRepository itemSaleRepository;
	private final TaxMapRepository taxMapRepository;
	private final ItemSaleInvoiceRepository itemSaleInvoiceRepository;
	private final AgentItemSaleCommandFromApiJsonDeserializer apiJsonDeserializer;
	private final ItemRepository itemRepository;
	
	@Autowired
	public ItemSaleWriteplatformServiceImpl(final PlatformSecurityContext context,final ItemSaleRepository itemSaleRepository,
			final ItemSaleInvoiceRepository itemSaleInvoiceRepository,final AgentItemSaleCommandFromApiJsonDeserializer apiJsonDeserializer,
			final ItemRepository itemRepository,final TaxMapRepository taxMapRepository){
		
		   this.context=context;
		   this.itemSaleRepository=itemSaleRepository;
		   this.itemSaleInvoiceRepository=itemSaleInvoiceRepository;
		   this.apiJsonDeserializer=apiJsonDeserializer;
		   this.itemRepository=itemRepository;
		   this.taxMapRepository=taxMapRepository;
		
	}
	
	
	
	@Transactional
	@Override
	public CommandProcessingResult createNewItemSale(JsonCommand command) {

        try{
        	
        	this.context.authenticatedUser();
        	this.apiJsonDeserializer.validateForCreate(command.json());
        	ItemSale itemSale=ItemSale.fromJson(command);
        	if(itemSale.getPurchaseFrom().equals(itemSale.getPurchaseBy())){
        		
        		throw new PlatformDataIntegrityException("invalid.move.operation", "invalid.move.operation", "invalid.move.operation");
        	}
            ItemMaster itemMaster=this.itemRepository.findOne(itemSale.getItemId());
            TaxMap taxMap=this.taxMapRepository.findOneByChargeCode(itemSale.getChargeCode());
          	ItemSaleInvoice invoice=ItemSaleInvoice.fromJson(command);
            BigDecimal taxAmount=BigDecimal.ZERO;
            BigDecimal taxrate=BigDecimal.ZERO;
            if(taxMap != null){
            	taxrate=taxMap.getRate();
            	if(taxMap.getType().equalsIgnoreCase("percentage")){
            		taxAmount=invoice.getChargeAmount().multiply(taxrate.divide(new BigDecimal(100)));
            	}else{
            		taxAmount=invoice.getChargeAmount().add(taxrate);
            	}
            }
            if(itemMaster == null){
        	  throw new ItemNotFoundException(itemSale.getItemId().toString());
            }
          
      
        	//this.calculateTaxAmount(invoice.getChargeAmount(),invoice.getTaxPercantage());
 
        	invoice.updateAmounts(taxAmount);
        	invoice.setTaxpercentage(taxrate);
        	itemSale.setItemSaleInvoice(invoice);
        	
        	this.itemSaleRepository.save(itemSale);
           return new CommandProcessingResult(itemSale.getId());        	
        }catch(DataIntegrityViolationException dve){
        	handleCodeDataIntegrityIssues(command, dve);
        	return new CommandProcessingResult(new Long(-1));
        	
        }
		
		
	}


	private void handleCodeDataIntegrityIssues(JsonCommand command,DataIntegrityViolationException dve) {
	
		Throwable realCause = dve.getMostSpecificCause();
	        throw new PlatformDataIntegrityException("error.msg.cund.unknown.data.integrity.issue",
	                "Unknown data integrity issue with resource: " + realCause.getMessage());
		
	}

	/*private BigDecimal calculateTaxAmount(BigDecimal chargeAmount,BigDecimal taxPercantage) {
		
		BigDecimal taxAmount=BigDecimal.ZERO;
		taxAmount=chargeAmount.multiply(taxPercantage.divide(new BigDecimal(100)));
		return taxAmount;
		
		
		
	}*/

}

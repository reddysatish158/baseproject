package org.mifosplatform.logistics.onetimesale.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.billing.discountmaster.domain.DiscountMaster;
import org.mifosplatform.billing.discountmaster.domain.DiscountMasterRepository;
import org.mifosplatform.billing.taxmaster.data.TaxMappingRateData;
import org.mifosplatform.finance.adjustment.service.AdjustmentReadPlatformService;
import org.mifosplatform.finance.billingorder.commands.BillingOrderCommand;
import org.mifosplatform.finance.billingorder.commands.InvoiceTaxCommand;
import org.mifosplatform.finance.billingorder.data.BillingOrderData;
import org.mifosplatform.finance.billingorder.domain.BillingOrder;
import org.mifosplatform.finance.billingorder.domain.Invoice;
import org.mifosplatform.finance.billingorder.service.BillingOrderReadPlatformService;
import org.mifosplatform.finance.billingorder.service.BillingOrderWritePlatformService;
import org.mifosplatform.finance.billingorder.service.GenerateBill;
import org.mifosplatform.finance.billingorder.service.GenerateBillingOrderService;
import org.mifosplatform.finance.clientbalance.data.ClientBalanceData;
import org.mifosplatform.finance.data.DiscountMasterData;
import org.mifosplatform.logistics.onetimesale.data.OneTimeSaleData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InvoiceOneTimeSale {

	private final GenerateBill generateBill;
	private final BillingOrderReadPlatformService billingOrderReadPlatformService;
	private final BillingOrderWritePlatformService billingOrderWritePlatformService;
	private final GenerateBillingOrderService generateBillingOrderService;
	private final AdjustmentReadPlatformService adjustmentReadPlatformService;
	private final DiscountMasterRepository discountMasterRepository;
	@Autowired
	public InvoiceOneTimeSale(GenerateBill generateBill,BillingOrderReadPlatformService billingOrderReadPlatformService,
			BillingOrderWritePlatformService billingOrderWritePlatformService,GenerateBillingOrderService generateBillingOrderService,
			AdjustmentReadPlatformService adjustmentReadPlatformService,final DiscountMasterRepository discountMasterRepository) {
		this.generateBill = generateBill;
		this.billingOrderReadPlatformService = billingOrderReadPlatformService;
		this.billingOrderWritePlatformService = billingOrderWritePlatformService;
		this.generateBillingOrderService = generateBillingOrderService;
		this.adjustmentReadPlatformService = adjustmentReadPlatformService;
		this.discountMasterRepository=discountMasterRepository;
	}

	public void invoiceOneTimeSale(Long clientId, OneTimeSaleData oneTimeSaleData) {
		List<BillingOrderCommand> billingOrderCommands = new ArrayList<BillingOrderCommand>();
		
			// check whether one time sale is invoiced
			// N - not invoiced
			// y - invoiced
			if (oneTimeSaleData.getIsInvoiced().equalsIgnoreCase("N")) {
				BillingOrderData billingOrderData = new BillingOrderData(oneTimeSaleData.getId(),oneTimeSaleData.getClientId(),	new LocalDate().toDate(),
						oneTimeSaleData.getChargeCode(), "NRC",	oneTimeSaleData.getTotalPrice());
				
				List<InvoiceTaxCommand> listOfTaxes = calculateTax(billingOrderData);
				 
				DiscountMaster discountMaster=this.discountMasterRepository.findOne(oneTimeSaleData.getDiscountId());
				
				BigDecimal discountAmount = BigDecimal.ZERO;
				DiscountMasterData discountMasterData=new DiscountMasterData(discountMaster.getId(),discountMaster.getDiscountCode(),discountMaster.getDiscountDescription(),
						discountMaster.getDiscountType(),discountMaster.getDiscountRate(),null,null);
				
				
					
			    discountMasterData = this.calculateDiscount(discountMasterData, discountAmount, billingOrderData.getPrice());
					
				
				
				
				this.createBillingOrderCommand(billingOrderData,new LocalDate(), new LocalDate(), new LocalDate(), new LocalDate(),
						billingOrderData.getPrice(), listOfTaxes, discountMasterData);
				
				
				
				
				
				BillingOrderCommand billingOrderCommand = new BillingOrderCommand(billingOrderData.getClientOrderId(),new Long(0),billingOrderData.getClientId(),
						new LocalDate().toDate(), null,new LocalDate().toDate(), null,billingOrderData.getChargeCode(),	billingOrderData.getChargeType(), null,
						billingOrderData.getDurationType(), null,billingOrderData.getPrice(), null, listOfTaxes,new LocalDate().toDate(), new LocalDate().toDate(),discountMasterData,billingOrderData.getTaxInclusive());
				        billingOrderCommands.add(billingOrderCommand);
				        
				        
				List<BillingOrder> listOfBillingOrders = billingOrderWritePlatformService.createBillingProduct(billingOrderCommands);
				// calculation of invoice
				Invoice invoice = this.generateBillingOrderService.generateInvoice(billingOrderCommands);

				// To fetch record from client_balance table
				List<ClientBalanceData> clientBalancesDatas = adjustmentReadPlatformService.retrieveAllAdjustments(clientId);
				
				this.billingOrderWritePlatformService.updateClientBalance(invoice,clientBalancesDatas);

				// Insertion into invoice table
				//Invoice invoice = billingOrderWritePlatformService.createInvoice(invoiceCommand, clientBalancesDatas);

				// Updation of invoice id in invoice_tax table
				//billingOrderWritePlatformService.updateInvoiceTax(invoice,billingOrderCommands, listOfBillingOrders);

				// Updation of invoice id in charge table
				//billingOrderWritePlatformService.updateInvoiceCharge(invoice,listOfBillingOrders);
				
				// update column is_invoiceed of one-time-sale
				
			

			} else {

			}
		}
	

	/*public void updateOneTimeSale(OneTimeSaleData oneTimeSaleData) {
		EventOrder oneTimeSale = eventOrderRepository.findOne(oneTimeSaleData.getId());
		oneTimeSale.setInvoiced();
		eventOrderRepository.save(oneTimeSale);
		
		
		OneTimeSale oneTimeSale = oneTimeSaleRepository.findOne(oneTimeSaleData.getId());
		oneTimeSale.setDeleted('y');
		oneTimeSaleRepository.save(oneTimeSale);
		
	}*/

	public List<InvoiceTaxCommand> calculateTax(BillingOrderData billingOrderData) {

		List<TaxMappingRateData> taxMappingRateDatas = billingOrderReadPlatformService.retrieveTaxMappingDate(billingOrderData.getClientId(),billingOrderData.getChargeCode());
		if(taxMappingRateDatas.isEmpty()){
			taxMappingRateDatas = billingOrderReadPlatformService.retrieveDefaultTaxMappingDate(billingOrderData.getClientId(),billingOrderData.getChargeCode());
		}
		List<InvoiceTaxCommand> invoiceTaxCommands = generateBill.generateInvoiceTax(taxMappingRateDatas,billingOrderData.getPrice(),billingOrderData.getClientId());
		// List<InvoiceTax> listOfTaxes = billingOrderWritePlatformService.createInvoiceTax(invoiceTaxCommand);
		return invoiceTaxCommands;
	}
	
	// create billing order command
	public BillingOrderCommand createBillingOrderCommand(BillingOrderData billingOrderData,LocalDate chargeStartDate,
			LocalDate chargeEndDate,LocalDate invoiceTillDate,LocalDate nextBillableDate,BigDecimal price,List<InvoiceTaxCommand> listOfTaxes,DiscountMasterData discountMasterData){
		
		return new BillingOrderCommand(
				billingOrderData.getClientOrderId(),
				billingOrderData.getOderPriceId(),
				billingOrderData.getClientId(), chargeStartDate.toDate(),
				nextBillableDate.toDate(), chargeEndDate.toDate(),
				billingOrderData.getBillingFrequency(),
				billingOrderData.getChargeCode(),
				billingOrderData.getChargeType(),
				billingOrderData.getChargeDuration(),
				billingOrderData.getDurationType(), invoiceTillDate.toDate(), price,
				billingOrderData.getBillingAlign(), listOfTaxes,
				billingOrderData.getStartDate(), billingOrderData.getEndDate(),discountMasterData,billingOrderData.getTaxInclusive());
	}
	
	// Discount Applicable Logic
	public Boolean isDiscountApplicable(DiscountMasterData discountMasterData) {
		boolean isDiscountApplicable = true;
		
		return isDiscountApplicable;

	}

	// Discount End Date calculation if null
	@SuppressWarnings("deprecation")
	public Date getDiscountEndDateIfNull(DiscountMasterData discountMasterData) {
		Date discountDate = discountMasterData.getDiscountEndDate();
		if (discountMasterData.getDiscountEndDate() == null) {
			discountDate = new Date(2099, 0, 01);
		}
		return discountDate;

	}
	
	// to check price not less than zero
	public BigDecimal chargePriceNotLessThanZero(BigDecimal chargePrice,BigDecimal discountPrice){
		
		chargePrice = chargePrice.subtract(discountPrice);
		if(chargePrice.compareTo(discountPrice) < 0){
			chargePrice = BigDecimal.ZERO;
		}
		return chargePrice;
		
	}
	
	// if is percentage
	public boolean isDiscountPercentage(DiscountMasterData discountMasterData){
		boolean isDiscountPercentage = false;
		if(discountMasterData.getDiscounType().equalsIgnoreCase("percentage")){
																
			isDiscountPercentage = true;
		}
		return isDiscountPercentage;
	}
	
	// if is discount
	public boolean isDiscountFlat(DiscountMasterData discountMasterData){
		boolean isDiscountFlat = false;
		if(discountMasterData.getDiscounType().equalsIgnoreCase("flat")){
			isDiscountFlat = true;
		}
		return isDiscountFlat;
	}
	

	// Discount calculation 
	public DiscountMasterData calculateDiscount(DiscountMasterData discountMasterData,BigDecimal discountAmount,BigDecimal chargePrice){
		if(isDiscountPercentage(discountMasterData)){
			
			if(discountMasterData.getdiscountRate().compareTo(new BigDecimal(100)) ==-1 ||
			 discountMasterData.getdiscountRate().compareTo(new BigDecimal(100)) == 0){
				
			discountAmount = this.calculateDiscountPercentage(discountMasterData.getdiscountRate(), chargePrice);
			discountMasterData.setDiscountAmount(discountAmount);
			chargePrice = this.chargePriceNotLessThanZero(chargePrice, discountAmount);
			discountMasterData.setDiscountedChargeAmount(chargePrice);
			
			}
			
		}
		
		if(isDiscountFlat(discountMasterData)){
			
			
			BigDecimal   p=this.calculateDiscountFlat(discountMasterData.getdiscountRate(), chargePrice);
			discountMasterData.setDiscountedChargeAmount(p);
			discountAmount = chargePrice.subtract(p);
			discountMasterData.setDiscountAmount(discountAmount);
			
		}
		return discountMasterData;
	
	}
	
	// Dicount Percent calculation
	public BigDecimal calculateDiscountPercentage(BigDecimal discountRate,BigDecimal chargePrice){
		
		return chargePrice.multiply(discountRate.divide(new BigDecimal(100)));
	}
	
	// Discount Flat calculation
	public BigDecimal calculateDiscountFlat(BigDecimal discountRate,BigDecimal chargePrice){
		
		return chargePrice.subtract(discountRate);
	}

}

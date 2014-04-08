package org.mifosplatform.finance.billingorder.service;

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.finance.billingorder.commands.BillingOrderCommand;
import org.mifosplatform.finance.billingorder.data.BillingOrderData;
import org.mifosplatform.finance.billingorder.domain.Invoice;
import org.mifosplatform.finance.clientbalance.data.ClientBalanceData;
import org.mifosplatform.finance.clientbalance.service.ClientBalanceReadPlatformService;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReverseInvoice {
	
	private BillingOrderReadPlatformService billingOrderReadPlatformService;
	private GenerateReverseBillingOrderService generateReverseBillingOrderService;
	private GenerateBillingOrderService generateBillingOrderService;
	private PlatformSecurityContext context;
	private final ClientBalanceReadPlatformService clientBalanceReadPlatformService;
	private final BillingOrderWritePlatformService billingOrderWritePlatformService;
	
	
	@Autowired
	public ReverseInvoice(BillingOrderReadPlatformService billingOrderReadPlatformService,PlatformSecurityContext context,
			GenerateReverseBillingOrderService generateReverseBillingOrderService,final BillingOrderWritePlatformService billingOrderWritePlatformService,
			final ClientBalanceReadPlatformService  balanceReadPlatformService,GenerateBillingOrderService generateBillingOrderService){
		
		this.context = context;
		this.billingOrderReadPlatformService = billingOrderReadPlatformService;
		this.generateReverseBillingOrderService = generateReverseBillingOrderService;
		this.clientBalanceReadPlatformService=balanceReadPlatformService;
		this.billingOrderWritePlatformService=billingOrderWritePlatformService;
		this.generateBillingOrderService=generateBillingOrderService;
	}
	
	 
	public BigDecimal reverseInvoiceServices(Long orderId,Long clientId,LocalDate disconnectionDate){
	    Invoice invoice=null;
		List<BillingOrderData> billingOrderProducts = this.billingOrderReadPlatformService.getReverseBillingOrderData(clientId, disconnectionDate, orderId);
		List<BillingOrderCommand> billingOrderCommands = this.generateReverseBillingOrderService.generateReverseBillingOrder(billingOrderProducts,disconnectionDate);
		if(billingOrderCommands.get(0).getChargeType().equalsIgnoreCase("RC")){
			 invoice = this.generateBillingOrderService. generateInvoice(billingOrderCommands);	
		}else{
	      invoice = this.generateReverseBillingOrderService.generateNegativeInvoice(billingOrderCommands);
		}
		
		List<ClientBalanceData> clientBalancesDatas = clientBalanceReadPlatformService.retrieveAllClientBalances(clientId);
		
		this.billingOrderWritePlatformService.updateClientBalance(invoice,clientBalancesDatas);
		
		billingOrderWritePlatformService.updateBillingOrder(billingOrderCommands);
		billingOrderWritePlatformService.updateOrderPrice(billingOrderCommands);
		 
		return null;
	}

}

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
public class ReconnectionInvoice {
	
	private BillingOrderReadPlatformService billingOrderReadPlatformService;
	private GenerateReconnectionBillingOrderService generateReconnectionBillingOrderService;
	private GenerateBillingOrderService generateBillingOrderService;
	private PlatformSecurityContext context;
	private final ClientBalanceReadPlatformService clientBalanceReadPlatformService;
	private final BillingOrderWritePlatformService billingOrderWritePlatformService;
	
	
	
	@Autowired
	public ReconnectionInvoice(BillingOrderReadPlatformService billingOrderReadPlatformService,PlatformSecurityContext context,
			GenerateReconnectionBillingOrderService generateReconnectionBillingOrderService,final BillingOrderWritePlatformService billingOrderWritePlatformService,
			final ClientBalanceReadPlatformService  balanceReadPlatformService,GenerateBillingOrderService generateBillingOrderService){
		
		this.context = context;
		this.billingOrderReadPlatformService = billingOrderReadPlatformService;
		this.generateReconnectionBillingOrderService = generateReconnectionBillingOrderService;
		this.clientBalanceReadPlatformService=balanceReadPlatformService;
		this.billingOrderWritePlatformService=billingOrderWritePlatformService;
		this.generateBillingOrderService=generateBillingOrderService;
	}
	
	 
	public BigDecimal reconnectionInvoiceServices(Long orderId,Long clientId,LocalDate disconnectionDate){
		
		List<BillingOrderData> billingOrderProducts = this.billingOrderReadPlatformService.getReconnectionBillingOrderData(clientId, orderId);
		List<BillingOrderCommand> billingOrderCommands = this.generateReconnectionBillingOrderService.generateReconnectionBillingOrder(billingOrderProducts,disconnectionDate);
		Invoice invoice = this.generateBillingOrderService.generateInvoice(billingOrderCommands);
		
		List<ClientBalanceData> clientBalancesDatas = clientBalanceReadPlatformService.retrieveAllClientBalances(clientId);
		
		this.billingOrderWritePlatformService.updateClientBalance(invoice,clientBalancesDatas);
		
		billingOrderWritePlatformService.updateBillingOrder(billingOrderCommands);
		billingOrderWritePlatformService.updateOrderPrice(billingOrderCommands);
		 
		return null;
	}

}

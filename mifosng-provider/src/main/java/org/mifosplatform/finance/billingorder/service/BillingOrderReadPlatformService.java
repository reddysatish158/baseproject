package org.mifosplatform.finance.billingorder.service;

import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.billing.taxmaster.data.TaxMappingRateData;
import org.mifosplatform.finance.billingorder.data.BillingOrderData;
import org.mifosplatform.finance.billingorder.data.GenerateInvoiceData;
import org.mifosplatform.finance.data.DiscountMasterData;
import org.mifosplatform.portfolio.order.data.OrderPriceData;

public interface BillingOrderReadPlatformService {

	
	List<TaxMappingRateData> retrieveTaxMappingDate(Long clientId, String chargeCode);

	List<OrderPriceData> retrieveInvoiceTillDate(Long clientOrderId);

	List<BillingOrderData> retrieveBillingOrderData(Long clientId,LocalDate localDate, Long planId);

	List<BillingOrderData> retrieveOrderIds(Long clientId, LocalDate processDate);

	List<GenerateInvoiceData> retrieveClientsWithOrders(LocalDate processDate);

	List<DiscountMasterData> retrieveDiscountOrders(Long orderId,Long orderPriceId);

	List<TaxMappingRateData> retrieveDefaultTaxMappingDate(Long clientId,
			String chargeCode);

	List<BillingOrderData> getReverseBillingOrderData(Long clientId,LocalDate disconnectionDate, Long orderId);

	List<BillingOrderData> getReconnectionBillingOrderData(Long clientId,Long orderId);

	TaxMappingRateData retriveExemptionTaxDetails(Long clientId);




}

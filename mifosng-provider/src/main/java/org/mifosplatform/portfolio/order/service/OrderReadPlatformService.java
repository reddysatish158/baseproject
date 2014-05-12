package org.mifosplatform.portfolio.order.service;

import java.util.List;

import org.mifosplatform.billing.payterms.data.PaytermData;
import org.mifosplatform.portfolio.order.data.OrderData;
import org.mifosplatform.portfolio.order.data.OrderDiscountData;
import org.mifosplatform.portfolio.order.data.OrderHistoryData;
import org.mifosplatform.portfolio.order.data.OrderLineData;
import org.mifosplatform.portfolio.order.data.OrderPriceData;
import org.mifosplatform.portfolio.plan.data.PlanCodeData;

public interface OrderReadPlatformService {

	List<PlanCodeData> retrieveAllPlatformData(Long planId);

	List<PaytermData> retrieveAllPaytermData();
	
	List<OrderPriceData> retrieveOrderPriceData(Long orderId);
	
	List<PaytermData> getChargeCodes(Long planCode);
	
	List<OrderPriceData> retrieveOrderPriceDetails(Long orderId, Long clientId);
	
	List<OrderData> retrieveClientOrderDetails(Long clientId);
	
	List<OrderHistoryData> retrieveOrderHistoryDetails(Long orderId);
	
	List<OrderData> getActivePlans(Long clientId, String planType);
	
	OrderData retrieveOrderDetails(Long orderId);
	
	Long getRetrackId(Long entityId);
	
	String getOSDTransactionType(Long id);
	
	String checkRetrackInterval(Long entityId);
	
	List<OrderLineData> retrieveOrderServiceDetails(Long orderId);
	
	List<OrderDiscountData> retrieveOrderDiscountDetails(Long orderId);

	Long retrieveClientActiveOrderDetails(Long clientId, String serialNo);
	

}

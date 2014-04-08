package org.mifosplatform.finance.billingorder.service;

import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.finance.billingorder.commands.BillingOrderCommand;
import org.mifosplatform.finance.billingorder.data.BillingOrderData;
import org.mifosplatform.finance.billingorder.domain.Invoice;

public interface GenerateReconnectionBillingOrderService {

	List<BillingOrderCommand> generateReconnectionBillingOrder(
			List<BillingOrderData> products, LocalDate reconnectionDate);


}

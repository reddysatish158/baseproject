package org.mifosplatform.finance.payments.service;

import java.util.List;

import org.mifosplatform.finance.payments.data.PaymentData;

public interface PaymentReadPlatformservice {

	List<PaymentData> retrieveClientPaymentDetails(Long clientId);

}

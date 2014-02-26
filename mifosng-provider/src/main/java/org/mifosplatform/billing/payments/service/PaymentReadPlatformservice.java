package org.mifosplatform.billing.payments.service;

import java.util.List;

import org.mifosplatform.billing.payments.data.PaymentData;

public interface PaymentReadPlatformservice {

	List<PaymentData> retrieveClientPaymentDetails(Long clientId);

}

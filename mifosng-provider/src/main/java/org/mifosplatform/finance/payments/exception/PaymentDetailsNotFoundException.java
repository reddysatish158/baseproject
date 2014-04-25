package org.mifosplatform.finance.payments.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class PaymentDetailsNotFoundException extends AbstractPlatformDomainRuleException {


	private static final long serialVersionUID = -2726286660273906232L;
	public PaymentDetailsNotFoundException(String paymentCode) {
		super("error.msg.payments.payment.details.invalid", "Payment Details Not Found"+paymentCode+". ",paymentCode);
	}
}

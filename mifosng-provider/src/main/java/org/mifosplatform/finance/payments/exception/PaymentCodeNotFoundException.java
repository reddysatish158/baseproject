package org.mifosplatform.finance.payments.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class PaymentCodeNotFoundException extends AbstractPlatformDomainRuleException {

	
	
	/**
	 * manually generated serialversion id by rahman
	 */
	private static final long serialVersionUID = -2726286660273906232L;

	public PaymentCodeNotFoundException(String paymentCode) {
		super("error.msg.payments.payment.code.invalid", "Invalid Payment Code "+paymentCode+". ",paymentCode);
	}
}

package org.mifosplatform.finance.payments.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;


public class ReceiptNoDuplicateException extends AbstractPlatformDomainRuleException {

    public ReceiptNoDuplicateException() {
        super("error.msg.billing.order.not.found", "ReceiptNo with this number is already exist ");
    }
    
    public ReceiptNoDuplicateException(String msg) {
        super("error.msg.receipt.no.duplicate.exception", " ReceiptNo with this number is already exist ", msg);
    }

	
}

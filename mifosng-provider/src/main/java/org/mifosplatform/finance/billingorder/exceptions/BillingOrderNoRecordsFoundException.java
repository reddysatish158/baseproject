package org.mifosplatform.finance.billingorder.exceptions;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;


public class BillingOrderNoRecordsFoundException extends AbstractPlatformDomainRuleException {

    public BillingOrderNoRecordsFoundException() {
        super("error.msg.billing.order.not.found", " Billing order not found ");
    }
    
    public BillingOrderNoRecordsFoundException(String msg) {
        super("error.msg.no.bills.to.generate", " No Bills TO Generate ", msg);
    }

	public BillingOrderNoRecordsFoundException(Long planCode) {
		 super("error.msg.no.active.price.available.for.this.plan", "No Active Prices Available For This Plan ", planCode);
		 
	}
	
	public BillingOrderNoRecordsFoundException(String msg,Long billId) {
		 super("error.msg.no.generate.pdf.file.for.this.statement", "No Generate Pdf File For This Statement ", billId);
		 
	}
	
}

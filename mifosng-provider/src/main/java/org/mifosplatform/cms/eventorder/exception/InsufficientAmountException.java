package org.mifosplatform.cms.eventorder.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;


public class InsufficientAmountException extends AbstractPlatformDomainRuleException {

    public InsufficientAmountException() {
        super("error.msg.insufficient.amount.amount", "Insufficient Amount to Book The Order");
    }
    
    public InsufficientAmountException(Long clientId) {
        super("error.msg.insufficient.balance.", "Insufficient Amount to Book The Order");
    }
   
}

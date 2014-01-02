package org.mifosplatform.billing.media.exceptions;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;


public class NoEventPriceFoundException extends AbstractPlatformDomainRuleException {

    public NoEventPriceFoundException() {
        super("error.msg.event.price.not.found", "No event price found");
    }
    
   
}

package org.mifosplatform.portfolio.order.exceptions;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;


public class NoGrnIdFoundException extends AbstractPlatformDomainRuleException {

    

	public NoGrnIdFoundException(Long orderId) {
		 super("error.msg.order.quantity..exceeds", "Grn id "+orderId+" not found. ",orderId);
		 
	}
}

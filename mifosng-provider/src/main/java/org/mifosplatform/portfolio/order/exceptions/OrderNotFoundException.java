package org.mifosplatform.portfolio.order.exceptions;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class OrderNotFoundException extends AbstractPlatformDomainRuleException {

	public OrderNotFoundException(Long orderId) {
		super("error.msg.Order.not.found.with.this.identifier","Order not found with this identifier",orderId);
		
	}

}

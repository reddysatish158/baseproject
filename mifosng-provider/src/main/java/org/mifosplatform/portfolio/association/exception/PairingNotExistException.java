package org.mifosplatform.portfolio.association.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;


public class PairingNotExistException extends AbstractPlatformDomainRuleException {


	public PairingNotExistException(Long orderId) {
		 super("error.msg.please.pair.hardware.with.plan", "Please pair hardware with plan",orderId);
		 
	}
}

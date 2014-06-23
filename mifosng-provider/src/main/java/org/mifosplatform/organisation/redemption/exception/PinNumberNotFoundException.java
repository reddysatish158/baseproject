package org.mifosplatform.organisation.redemption.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class PinNumberNotFoundException extends AbstractPlatformDomainRuleException {


	public PinNumberNotFoundException(final String pinNumber) {
		super("error.msg.redemption.pinNumber.invalid", "PinNumber with this " + pinNumber + " does not exist", pinNumber);
    }

}

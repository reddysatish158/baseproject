package org.mifosplatform.organisation.redemption.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class PinNumberNotFoundException extends
		AbstractPlatformResourceNotFoundException {

	public PinNumberNotFoundException(final String pinNumber) {
		super("error.msg.redemption.pinNumber.invalid", "PinNumber with this " + pinNumber + " does not exist", pinNumber);
    }

}

package org.mifosplatform.billing.association.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;


@SuppressWarnings("serial")
public class HardwareDetailsNotFoundException extends AbstractPlatformDomainRuleException {

    public HardwareDetailsNotFoundException() {
        super("error.msg.hardware.details.not.found", "Hardware details are not found");
    }
    
    public HardwareDetailsNotFoundException(Long orderId) {
        super("error.msg.hardware.details.not.found", "Hardware details are not found", orderId);
    }

}

package org.mifosplatform.portfolio.association.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;


@SuppressWarnings("serial")
public class HardwareDetailsNotFoundException extends AbstractPlatformDomainRuleException {

    public HardwareDetailsNotFoundException() {
        super("error.msg.hardware.details.not.found", "Hardware details are not found");
    }
    
    public HardwareDetailsNotFoundException(Long orderId) {
        super("error.msg.hardware.details.not.found", "Hardware details are not found", orderId);
    }
    
    public HardwareDetailsNotFoundException(String orderId) {
        super("error.msg.associate.hardware.for.reconnect", "Pleass associate the hardware in order to reconnect", orderId);
    }

}

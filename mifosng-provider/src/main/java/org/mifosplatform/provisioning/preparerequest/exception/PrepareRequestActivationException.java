package org.mifosplatform.provisioning.preparerequest.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;


public class PrepareRequestActivationException extends AbstractPlatformDomainRuleException {

    public PrepareRequestActivationException() {
        super("error.msg.request.sent.for.activation", "Request is already sent for activation");
    }
    
}

package org.mifosplatform.workflow.eventactionmapping.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;


public class EventNameDuplicateException extends AbstractPlatformDomainRuleException {

  
    public EventNameDuplicateException(String msg) {
        super("error.msg.event.name.already.configured", "Event Name already Configured with this Action Name", msg);
    }

	
}

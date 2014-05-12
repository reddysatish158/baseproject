package org.mifosplatform.cms.media.exceptions;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;


public class NoEventMasterFoundException extends AbstractPlatformDomainRuleException {

    public NoEventMasterFoundException() {
        super("error.msg.movie.not.found", "Event With this id does not exist");
    }
    
   
}

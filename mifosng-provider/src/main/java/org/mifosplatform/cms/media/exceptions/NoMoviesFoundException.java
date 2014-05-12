package org.mifosplatform.cms.media.exceptions;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;


public class NoMoviesFoundException extends AbstractPlatformDomainRuleException {

    public NoMoviesFoundException() {
        super("error.msg.movie.not.found", " Movie Format Does Not Exist ");
    }
    
   
}

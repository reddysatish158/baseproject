package org.mifosplatform.organisation.ippool.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;


public class IpAddresAllocatedException extends AbstractPlatformDomainRuleException {

  
    public IpAddresAllocatedException(String msg) {
        super("error.msg.ipaddress.already.allocated.please.select.another.iprange", "Ipaddress already allocated please select another iprange", msg);
    }

	
}

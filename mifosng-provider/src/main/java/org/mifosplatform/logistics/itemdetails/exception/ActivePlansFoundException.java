package org.mifosplatform.logistics.itemdetails.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;


public class ActivePlansFoundException extends AbstractPlatformDomainRuleException {

    public ActivePlansFoundException() {
        super("error.msg.billing.active.plans.found", " plans are activated with this hardWare ");
    }

	public ActivePlansFoundException(String errorCode) {
		
		 super(errorCode, "plans are activated with this hardWare",errorCode);
	}

	public ActivePlansFoundException(Long clientId) {
		
		
		super("error.msg.close.active.plans.found.for.client.close", "Client cannot be closed because of actvie plans",clientId);
	}

	/*public ActivePlansFoundException(String serialNumber) {
		
		super("error.msg.close.active.plans.found.for.hardWare", "Can not Update Serial Num",serialNumber);
	}*/
    
   
}

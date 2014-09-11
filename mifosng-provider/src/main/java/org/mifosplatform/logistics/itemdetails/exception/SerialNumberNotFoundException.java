package org.mifosplatform.logistics.itemdetails.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;


public class SerialNumberNotFoundException extends AbstractPlatformDomainRuleException {

    

	public SerialNumberNotFoundException(String serialNumber) {
		 super("error.msg.serialnumber.not.found", "Serial Number not found"+serialNumber,serialNumber,"");
		 
		 
	}
}

package org.mifosplatform.billing.selfcare.service;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class SelfCareIdNotFoundException extends AbstractPlatformDomainRuleException{

	public SelfCareIdNotFoundException(final Long id){
		super("error.msg.clientId.not.found", "Client not found with this "+id,id);
	}
	

}

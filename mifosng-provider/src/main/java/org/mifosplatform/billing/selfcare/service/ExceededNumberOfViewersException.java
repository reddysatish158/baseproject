package org.mifosplatform.billing.selfcare.service;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;
import org.mifosplatform.portfolio.client.exception.ClientStatusException;

public class ExceededNumberOfViewersException extends AbstractPlatformDomainRuleException {
	public ExceededNumberOfViewersException(final Long clientId) {
		super("error.msg.exceeded.no.of.Viewers", "Maximum Number of allowable viewers exceeded with this Client");
		// TODO Auto-generated constructor stub
	}

}

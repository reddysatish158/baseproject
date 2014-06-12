package org.mifosplatform.organisation.randomgenerator.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class AlreadyProcessedException extends AbstractPlatformDomainRuleException {

	public AlreadyProcessedException() {
		super("error.msg.voucher.no.isprocessed.exception", " VoucherPin is already generated with this batchName   ", " VoucherPin is already generated with this batchName ");
		// TODO Auto-generated constructor stub
	}
	
	public AlreadyProcessedException(String msg) {
		super("error.msg.voucher.no.isprocessed.exception", " VoucherPin is already generated with this batchName   ", msg);
		// TODO Auto-generated constructor stub
	}

}

package org.mifosplatform.finance.adjustment.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class AdjustmentCodeNotFoundException extends AbstractPlatformDomainRuleException{

	public AdjustmentCodeNotFoundException(String adjustmentCode) {
		super("error.msg.adjustment.code.invalid", "Invalid Adjustment Code "+adjustmentCode+". ",adjustmentCode);
	}
}

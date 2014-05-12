package org.mifosplatform.finance.billingorder.exceptions;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;


public class NoPromotionFoundException extends AbstractPlatformDomainRuleException {

   

	public NoPromotionFoundException(Long promoId) {
		 super("error.msg.no.promotion.found.with.this.id", "No Promotions are found", promoId);
		 
	}
}

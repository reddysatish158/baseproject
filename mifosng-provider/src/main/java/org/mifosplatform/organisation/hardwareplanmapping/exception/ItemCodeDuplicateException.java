package org.mifosplatform.organisation.hardwareplanmapping.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;


public class ItemCodeDuplicateException extends AbstractPlatformDomainRuleException {

  
    public ItemCodeDuplicateException(String msg) {
        super("error.msg.item.code.already.configured", "Item Code already Configured with this plan", msg);
    }

	
}

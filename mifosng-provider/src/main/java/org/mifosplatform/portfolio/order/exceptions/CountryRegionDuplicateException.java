package org.mifosplatform.portfolio.order.exceptions;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;


public class CountryRegionDuplicateException extends AbstractPlatformDomainRuleException {

    public CountryRegionDuplicateException() {
        super("error.msg.country.region.already.exist", "Country Region with this Mapping Alredy Exist");
    }
    
   
}

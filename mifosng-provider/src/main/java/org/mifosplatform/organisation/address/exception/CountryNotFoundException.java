package org.mifosplatform.organisation.address.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class CountryNotFoundException extends AbstractPlatformResourceNotFoundException {
	
	public CountryNotFoundException(final String id) {
        super("error.msg.country.not.found", "country with this id"+id+"not exist",id);
        
    }
}

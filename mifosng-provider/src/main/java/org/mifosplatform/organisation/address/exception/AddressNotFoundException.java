package org.mifosplatform.organisation.address.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class AddressNotFoundException extends AbstractPlatformResourceNotFoundException{
	
	public AddressNotFoundException(final String id){
		 super("error.msg.address.not.found", "address with this id"+id+"not exist",id);
	}

}

package org.mifosplatform.logistics.supplier.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface SupplierWritePlatformService {

	CommandProcessingResult createSupplier(JsonCommand command);

}

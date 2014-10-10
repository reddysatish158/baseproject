package org.mifosplatform.finance.billingmaster.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;


public interface BillMasterWritePlatformService {

	CommandProcessingResult createBillMaster(JsonCommand command, Long entityId);



}

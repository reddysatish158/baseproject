package org.mifosplatform.portfolio.association.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface HardwareAssociationWriteplatformService {

	void createNewHardwareAssociation(Long clientId, Long id, String serialNo, Long orderId);

	CommandProcessingResult createAssociation(JsonCommand command);

	CommandProcessingResult updateAssociation(JsonCommand command);

	CommandProcessingResult deAssociationHardware(Long orderId);

}

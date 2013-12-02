package org.mifosplatform.billing.association.service;

import java.util.List;

import org.mifosplatform.billing.association.data.AssociationData;
import org.mifosplatform.billing.association.data.HardwareAssociationData;


public interface HardwareAssociationReadplatformService {

	List<HardwareAssociationData> retrieveClientHardwareDetails(Long clientId);

	List<HardwareAssociationData> retrieveClientAllocatedPlan(Long clientId, String itemCode);

	List<AssociationData> retrieveClientAssociationDetails(Long clientId);

	AssociationData retrieveSingleDetails(Long id);

	List<AssociationData> retrieveHardwareData(Long clientId);

	List<AssociationData> retrieveplanData(Long clientId);

}

package org.mifosplatform.billing.entitlements.service;

import java.util.List;

import org.mifosplatform.billing.entitlements.data.ClientEntitlementData;
import org.mifosplatform.billing.entitlements.data.EntitlementsData;
import org.mifosplatform.billing.entitlements.data.StakerData;


public interface EntitlementReadPlatformService {
	

	List<EntitlementsData> getProcessingData(Long id);

	ClientEntitlementData getClientData(Long clientId);

	StakerData getData(String mac);
	
	
	
}

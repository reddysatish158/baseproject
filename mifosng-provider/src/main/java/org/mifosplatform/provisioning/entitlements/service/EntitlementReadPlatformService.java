package org.mifosplatform.provisioning.entitlements.service;

import java.util.List;

import org.mifosplatform.provisioning.entitlements.data.ClientEntitlementData;
import org.mifosplatform.provisioning.entitlements.data.EntitlementsData;
import org.mifosplatform.provisioning.entitlements.data.StakerData;


public interface EntitlementReadPlatformService {
	

	List<EntitlementsData> getProcessingData(Long id, String provisioningSystem, String serviceType);

	ClientEntitlementData getClientData(Long clientId);

	StakerData getData(String mac);

	List<EntitlementsData> getBeeniusProcessingData(Long no, String provisioningSystem);
	
	
	
	
}

package org.mifosplatform.provisioning.provisioning.service;

import java.util.List;

import org.mifosplatform.billing.paymode.data.McodeData;
import org.mifosplatform.provisioning.provisioning.data.ProcessRequestData;
import org.mifosplatform.provisioning.provisioning.data.ProvisioningCommandParameterData;
import org.mifosplatform.provisioning.provisioning.data.ProvisioningData;

public interface ProvisioningReadPlatformService {

	List<ProvisioningData> getProvisioningData();

	List<McodeData> retrieveProvisioningCategory();

	List<McodeData> retrievecommands();

	ProvisioningData retrieveIdData(Long id);

	List<ProvisioningCommandParameterData> retrieveCommandParams(Long id);

	Long getHardwareDetails(String oldHardWare, Long clientId, String name);
	
	List<ProcessRequestData> getProcessRequestData(Long clientId);

	ProcessRequestData getProcessRequestIDData(Long id);
}

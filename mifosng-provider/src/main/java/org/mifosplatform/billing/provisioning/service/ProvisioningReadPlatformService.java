package org.mifosplatform.billing.provisioning.service;

import java.util.List;

import org.mifosplatform.billing.paymode.data.McodeData;
import org.mifosplatform.billing.provisioning.data.ProvisioningCommandParameterData;
import org.mifosplatform.billing.provisioning.data.ProvisioningData;
import org.mifosplatform.billing.provisioning.data.ServiceParameterData;

public interface ProvisioningReadPlatformService {

	List<ProvisioningData> getProvisioningData();

	List<McodeData> retrieveProvisioningCategory();

	List<McodeData> retrievecommands();

	ProvisioningData retrieveIdData(Long id);

	List<ProvisioningCommandParameterData> retrieveCommandParams(Long id);

	List<ServiceParameterData> getSerivceParameters(Long orderId);

	List<ServiceParameterData> getProvisionedSerivceParameters(Long orderId);
	
}

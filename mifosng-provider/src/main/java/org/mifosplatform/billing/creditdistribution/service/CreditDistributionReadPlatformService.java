package org.mifosplatform.billing.creditdistribution.service;

import org.mifosplatform.billing.creditdistribution.data.CreditDistributionData;
import org.mifosplatform.infrastructure.core.service.Page;

public interface CreditDistributionReadPlatformService {


	Page<CreditDistributionData> getClientDistributionData(Long clientId);
}

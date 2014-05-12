package org.mifosplatform.finance.creditdistribution.service;

import org.mifosplatform.finance.creditdistribution.data.CreditDistributionData;
import org.mifosplatform.infrastructure.core.service.Page;

public interface CreditDistributionReadPlatformService {


	Page<CreditDistributionData> getClientDistributionData(Long clientId);
}

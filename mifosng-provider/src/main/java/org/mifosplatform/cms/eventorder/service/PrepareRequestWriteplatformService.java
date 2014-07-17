package org.mifosplatform.cms.eventorder.service;

import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.portfolio.order.domain.Order;
import org.mifosplatform.portfolio.plan.domain.Plan;

public interface PrepareRequestWriteplatformService {

	CommandProcessingResult prepareNewRequest(Order order,Plan plan, String requstStatus);



}

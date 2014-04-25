package org.mifosplatform.portfolio.plan.service;

import java.util.List;

import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.portfolio.contract.data.SubscriptionData;
import org.mifosplatform.portfolio.plan.data.BillRuleData;
import org.mifosplatform.portfolio.plan.data.PlanData;
import org.mifosplatform.portfolio.plan.data.ServiceData;
import org.mifosplatform.portfolio.plan.data.SystemData;

public interface PlanReadPlatformService {
	List<ServiceData> retrieveAllServices();
	List<PlanData> retrievePlanData(String planType);
	List<SubscriptionData> retrieveSubscriptionData();
	List<EnumOptionData> retrieveNewStatus();
	List<ServiceData> getselectedService(List<ServiceData> data,List<ServiceData> services);
	List<BillRuleData> retrievebillRules();
	PlanData retrievePlanData(Long planCode);
	//List<ServiceData> retrievePrcingDetails(Long planId);
	List<SystemData> retrieveSystemData();
	List<ServiceData> retrieveSelectedServices(Long planId);
	List<EnumOptionData> retrieveVolumeTypes();



}

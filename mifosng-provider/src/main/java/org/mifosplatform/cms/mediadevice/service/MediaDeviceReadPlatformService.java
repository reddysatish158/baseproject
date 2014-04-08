package org.mifosplatform.cms.mediadevice.service;

import java.util.List;

import org.mifosplatform.billing.plan.data.PlanData;
import org.mifosplatform.cms.mediadevice.data.MediaDeviceData;


public interface MediaDeviceReadPlatformService {

	List<MediaDeviceData> retrieveDeviceDataDetails(String deviceId);
	MediaDeviceData retrieveDeviceDetails(String deviceId);
	List<PlanData> retrievePlanDetails(Long clientId);
	List<PlanData> retrievePlanPostpaidDetails(Long clientId);
	

}

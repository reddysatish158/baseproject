package org.mifosplatform.cms.mediadevice.service;

import java.util.List;

import org.mifosplatform.cms.mediadevice.data.MediaDeviceData;
import org.mifosplatform.portfolio.plan.data.PlanData;


public interface MediaDeviceReadPlatformService {

	List<MediaDeviceData> retrieveDeviceDataDetails(String deviceId);
	
	MediaDeviceData retrieveDeviceDetails(String deviceId);
	
	List<PlanData> retrievePlanDetails(Long clientId);
	
	List<PlanData> retrievePlanPostpaidDetails(Long clientId);
	
	Long retrieveDeviceDataDetails(Long clientId, String string);

	MediaDeviceData retrieveClientDetails(String clientId);
	

}

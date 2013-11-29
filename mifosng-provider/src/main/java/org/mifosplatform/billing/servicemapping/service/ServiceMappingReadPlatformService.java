package org.mifosplatform.billing.servicemapping.service;

import java.util.List;

import org.mifosplatform.billing.servicemapping.data.ServiceCodeData;
import org.mifosplatform.billing.servicemapping.data.ServiceMappingData;

public interface ServiceMappingReadPlatformService {
	
	
	List<ServiceCodeData> getServiceCode();
	
	List<ServiceMappingData> getServiceMapping();

	ServiceMappingData getServiceMapping(Long serviceMappingId);
	
//	ServiceMappingData getServiceMapping(Long serviceMappingId);

}

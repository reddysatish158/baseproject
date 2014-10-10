package org.mifosplatform.portfolio.servicemapping.service;

import java.util.List;

import org.mifosplatform.portfolio.servicemapping.data.ServiceCodeData;
import org.mifosplatform.portfolio.servicemapping.data.ServiceMappingData;

public interface ServiceMappingReadPlatformService {
	
	
	List<ServiceCodeData> getServiceCode();
	
	List<ServiceMappingData> getServiceMapping();

	ServiceMappingData getServiceMapping(Long serviceMappingId);
	
//	ServiceMappingData getServiceMapping(Long serviceMappingId);

}

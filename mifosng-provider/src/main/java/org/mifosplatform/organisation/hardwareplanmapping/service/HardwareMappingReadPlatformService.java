package org.mifosplatform.organisation.hardwareplanmapping.service;

import java.util.List;

import org.mifosplatform.organisation.hardwareplanmapping.data.HardwareMappingDetailsData;

public interface HardwareMappingReadPlatformService {

	List<HardwareMappingDetailsData> getPlanDetailsByItemCode(String itemCode, Long clientId);

}

package org.mifosplatform.organisation.region.service;

import java.util.List;

import org.mifosplatform.organisation.address.data.StateDetails;
import org.mifosplatform.organisation.region.data.RegionData;
import org.mifosplatform.organisation.region.data.RegionDetailsData;

public interface RegionReadPlatformService {

	List<StateDetails> getAvailableStates(Long countryId);

	List<RegionData> getRegionDetails();

	RegionData getSingleRegionDetails(Long regionId);

	List<RegionDetailsData> getRegionDetailsData(Long regionId);
	List<RegionDetailsData> getCountryRegionDetails(Long regionId,Long stateId);

}

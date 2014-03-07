package org.mifosplatform.billing.eventaction.service;

import org.mifosplatform.billing.eventaction.data.VolumeDetailsData;

public interface EventActionReadPlatformService {

	VolumeDetailsData retrieveVolumeDetails(Long id);

}

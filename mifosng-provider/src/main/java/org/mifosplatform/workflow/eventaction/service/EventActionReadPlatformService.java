package org.mifosplatform.workflow.eventaction.service;

import org.mifosplatform.workflow.eventaction.data.VolumeDetailsData;

public interface EventActionReadPlatformService {

	VolumeDetailsData retrieveVolumeDetails(Long id);

}

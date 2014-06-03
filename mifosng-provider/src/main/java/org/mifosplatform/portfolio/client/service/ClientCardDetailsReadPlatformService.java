package org.mifosplatform.portfolio.client.service;

import java.util.List;

import org.mifosplatform.portfolio.client.data.ClientCardDetailsData;

public interface ClientCardDetailsReadPlatformService {

	List<ClientCardDetailsData> retrieveClientDetails(Long clientId);

	ClientCardDetailsData retrieveClient(Long id, String type, Long clientId);
	
	
	

}

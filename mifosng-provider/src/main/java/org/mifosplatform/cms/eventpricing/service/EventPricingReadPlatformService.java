/**
 * 
 */
package org.mifosplatform.cms.eventpricing.service;

import java.util.List;

import org.mifosplatform.cms.eventpricing.data.ClientTypeData;
import org.mifosplatform.cms.eventpricing.data.EventPricingData;
import org.mifosplatform.cms.eventpricing.domain.EventPricing;

/**
 * Interface for {@link EventPricing} Read Service
 * 
 * @author pavani
 *
 */
public interface EventPricingReadPlatformService {

	
	/**
	 * Method for retrieving {@link EventPricing} {@link List}
	 * 
	 * @param eventId
	 * @return
	 */
	List<EventPricingData> retrieventPriceData(Long eventId);
	
	/**
	 * Method for Retrieving {@link ClientTypeData}
	 * 
	 * @return
	 */
	List<ClientTypeData> clientType();
	
	/**
	 * Method for retrieving single {@link EventPricing}
	 * 
	 * @param eventPriceId
	 * @return
	 */
	EventPricingData  retrieventPriceDetails(Long eventPriceId);

}

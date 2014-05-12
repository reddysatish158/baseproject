package org.mifosplatform.cms.eventorder.service;

import java.math.BigDecimal;
import java.util.List;

import org.mifosplatform.cms.eventmaster.data.EventMasterData;
import org.mifosplatform.cms.eventorder.data.EventOrderData;
import org.mifosplatform.cms.eventorder.data.EventOrderDeviceData;
import org.mifosplatform.logistics.onetimesale.data.OneTimeSaleData;

public interface EventOrderReadplatformServie {
	
	List<OneTimeSaleData> retrieveEventOrderData(Long clientId);

	boolean CheckClientCustomalidation(Long clientId);

	List<EventOrderData> getTheClientEventOrders(Long clientId);

	List<EventOrderDeviceData> getDevices(Long clientId);

	List<EventMasterData> getEvents();

	BigDecimal retriveEventPrice(String fType, String oType, Long clientId);

	Long getCurrentRow(String fType, String oType, Long clientId);
	
}

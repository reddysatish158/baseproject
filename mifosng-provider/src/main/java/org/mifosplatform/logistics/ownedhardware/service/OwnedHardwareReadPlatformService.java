package org.mifosplatform.logistics.ownedhardware.service;

import java.util.List;

import org.mifosplatform.logistics.item.data.ItemData;
import org.mifosplatform.logistics.ownedhardware.data.OwnedHardwareData;

public interface OwnedHardwareReadPlatformService {

	public List<OwnedHardwareData> retriveOwnedHardwareData(Long clientId);
	
	public List<ItemData> retriveTemplate();
	
	public List<String> retriveSerialNumbers();
	
	public List<OwnedHardwareData> retriveSingleOwnedHardwareData(Long id);

	public int retrieveClientActiveDevices(Long clientId);

	public int retrieveNoOfActiveUsers(Long clientId);
	
}

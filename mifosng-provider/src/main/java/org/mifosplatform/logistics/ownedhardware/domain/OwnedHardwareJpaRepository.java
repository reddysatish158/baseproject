package org.mifosplatform.logistics.ownedhardware.domain;

import org.mifosplatform.logistics.ownedhardware.data.OwnedHardware;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OwnedHardwareJpaRepository extends JpaRepository<OwnedHardware, Long>,JpaSpecificationExecutor<OwnedHardware> {
	
	OwnedHardware findBySerialNumber(String serialNumber);
	
	@Query("from OwnedHardware ownedHardware where ownedHardware.provisioningSerialNumber =:deviceId and ownedHardware.isDeleted is 'N'")
	OwnedHardware findByProvisioningSerialNumber(@Param("deviceId") String deviceId);
	
	@Query("from OwnedHardware ownedHardware where ownedHardware.serialNumber =:serialNumber and ownedHardware.clientId =:clientId  and ownedHardware.isDeleted is 'N'")
	OwnedHardware findBySerialNumber(@Param("serialNumber") String serialNumber,@Param("clientId") Long clientId);

    
}

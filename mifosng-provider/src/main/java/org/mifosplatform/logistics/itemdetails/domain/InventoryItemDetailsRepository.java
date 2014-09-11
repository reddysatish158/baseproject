package org.mifosplatform.logistics.itemdetails.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InventoryItemDetailsRepository extends JpaRepository<InventoryItemDetails, Long>, JpaSpecificationExecutor<InventoryItemDetails>{

	@Query("from InventoryItemDetails item where item.serialNumber = :macId")
	InventoryItemDetails getInventoryItemDetailBySerialNum(@Param("macId") String macId);

}



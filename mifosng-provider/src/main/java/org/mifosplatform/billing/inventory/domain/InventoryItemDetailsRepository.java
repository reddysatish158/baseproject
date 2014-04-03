package org.mifosplatform.billing.inventory.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InventoryItemDetailsRepository extends JpaRepository<InventoryItemDetails, Long>, JpaSpecificationExecutor<InventoryItemDetails>{
	
	 @Query("from  InventoryItemDetails itemdetail where itemdetail.serialNumber  =:serialNumber")
	 InventoryItemDetails getInventoryItemDetailBySerialNum(@Param("serialNumber") String serialNumber);

}



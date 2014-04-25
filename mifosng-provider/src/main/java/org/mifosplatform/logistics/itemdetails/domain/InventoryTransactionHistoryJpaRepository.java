package org.mifosplatform.logistics.itemdetails.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InventoryTransactionHistoryJpaRepository extends
		JpaRepository<InventoryTransactionHistory, Long>, JpaSpecificationExecutor<InventoryTransactionHistory> {

}

package org.mifosplatform.portfolio.order.domain;

import java.util.List;

import org.mifosplatform.portfolio.association.domain.HardwareAssociation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface HardwareAssociationRepository  extends JpaRepository<HardwareAssociation, Long>,
JpaSpecificationExecutor<HardwareAssociation>{

	HardwareAssociation findOneByOrderId(Long orderId);
	
	@Query("from HardwareAssociation association where association.serialNo =? and association.isDeleted='N'")
	List<HardwareAssociation> findOneByserialNo(String serialNum);
}

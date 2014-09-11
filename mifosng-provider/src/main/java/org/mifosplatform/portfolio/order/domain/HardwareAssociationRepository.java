package org.mifosplatform.portfolio.order.domain;

import java.util.List;

import org.mifosplatform.portfolio.association.domain.HardwareAssociation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HardwareAssociationRepository  extends JpaRepository<HardwareAssociation, Long>,
JpaSpecificationExecutor<HardwareAssociation>{

	//HardwareAssociation findOneByOrderId(Long orderId);
	
	@Query("from HardwareAssociation association where association.serialNo =:serialNum and association.isDeleted='N'")
    List<HardwareAssociation> findOneByserialNo(@Param("serialNum") String serialNum);
	
	@Query("from HardwareAssociation association where association.orderId =:orderId and association.isDeleted='N'")
    HardwareAssociation findOneByOrderId(@Param("orderId")Long orderId);

}

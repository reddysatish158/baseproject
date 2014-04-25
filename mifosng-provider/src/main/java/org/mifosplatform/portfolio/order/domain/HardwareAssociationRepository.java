package org.mifosplatform.portfolio.order.domain;

import org.mifosplatform.portfolio.association.domain.HardwareAssociation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface HardwareAssociationRepository  extends JpaRepository<HardwareAssociation, Long>,
JpaSpecificationExecutor<HardwareAssociation>{

	HardwareAssociation findOneByOrderId(Long orderId);
}

package org.mifosplatform.organisation.hardwaremapping.domain;

import org.mifosplatform.organisation.hardwareplanmapping.domain.PlanHardwareMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PlanHardwareMappingRepository  extends
JpaRepository<PlanHardwareMapping, Long>,
JpaSpecificationExecutor<PlanHardwareMapping>{
	
	PlanHardwareMapping findOneByPlanCode(String planCode);
	PlanHardwareMapping findOneByItemCode(String itemCode);

}

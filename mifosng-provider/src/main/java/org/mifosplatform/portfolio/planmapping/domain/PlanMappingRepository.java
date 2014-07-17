package org.mifosplatform.portfolio.planmapping.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PlanMappingRepository extends JpaRepository<PlanMapping, Long>,JpaSpecificationExecutor<PlanMapping> {

}

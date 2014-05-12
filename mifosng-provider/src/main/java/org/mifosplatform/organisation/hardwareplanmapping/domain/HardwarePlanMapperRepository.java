package org.mifosplatform.organisation.hardwareplanmapping.domain;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface HardwarePlanMapperRepository  extends
JpaRepository<HardwarePlanMapper, Long>,
JpaSpecificationExecutor<HardwarePlanMapper>{

}

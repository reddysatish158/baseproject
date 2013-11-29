package org.mifosplatform.billing.servicemapping.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ServiceMappingRepository extends JpaRepository<ServiceMapping, Long>,JpaSpecificationExecutor<ServiceMapping>{

}

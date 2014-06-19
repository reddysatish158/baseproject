package org.mifosplatform.provisioning.preparerequest.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PrepareRequsetRepository extends JpaRepository<PrepareRequest, Long>,
JpaSpecificationExecutor<PrepareRequest>{

}

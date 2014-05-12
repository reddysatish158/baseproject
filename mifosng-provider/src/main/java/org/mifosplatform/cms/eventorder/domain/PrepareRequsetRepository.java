package org.mifosplatform.cms.eventorder.domain;

import org.mifosplatform.cms.eventorder.domain.PrepareRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PrepareRequsetRepository extends JpaRepository<PrepareRequest, Long>,
JpaSpecificationExecutor<PrepareRequest>{

}

package org.mifosplatform.workflow.eventactionmapping.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EventActionMappingRepository extends

JpaRepository<EventActionMapping, Long>,
JpaSpecificationExecutor<EventActionMapping>{

}

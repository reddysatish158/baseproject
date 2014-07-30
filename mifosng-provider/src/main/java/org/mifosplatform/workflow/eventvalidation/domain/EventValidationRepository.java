package org.mifosplatform.workflow.eventvalidation.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EventValidationRepository extends JpaRepository<EventValidation, Long>,JpaSpecificationExecutor<EventValidation>{

}

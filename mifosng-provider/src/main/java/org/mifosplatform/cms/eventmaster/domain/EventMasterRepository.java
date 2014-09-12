package org.mifosplatform.cms.eventmaster.domain;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Interface for {@link EventMaster} Repository 
 * extends {@link JpaRepository} and {@link JpaSpecificationExecutor}
 * 
 * @author pavani
 *
 */
public interface EventMasterRepository extends JpaRepository<EventMaster, Long>,
JpaSpecificationExecutor<EventMaster>{

}

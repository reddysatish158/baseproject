package org.mifosplatform.organisation.groupsDetails.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface GroupsDetailsRepository extends JpaRepository<GroupsDetails, Long>,JpaSpecificationExecutor<GroupsDetails>{

}

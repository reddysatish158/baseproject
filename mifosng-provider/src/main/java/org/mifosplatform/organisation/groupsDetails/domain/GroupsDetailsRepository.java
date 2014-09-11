package org.mifosplatform.organisation.groupsDetails.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GroupsDetailsRepository extends JpaRepository<GroupsDetails, Long>,JpaSpecificationExecutor<GroupsDetails>{

	
	@Query("from GroupsDetails groupsDetails where groupsDetails.groupName =:groupName")
	GroupsDetails findOneByGroupName(@Param("groupName") String groupName);

}

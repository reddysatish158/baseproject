package org.mifosplatform.billing.loginhistory.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Long>,JpaSpecificationExecutor<LoginHistory>{

	@Query("from LoginHistory lh where lh.userName=:userName")
	LoginHistory findOneByName(@Param("userName")String userName);
	
}

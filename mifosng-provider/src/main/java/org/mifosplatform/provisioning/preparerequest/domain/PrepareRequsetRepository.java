package org.mifosplatform.provisioning.preparerequest.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PrepareRequsetRepository extends JpaRepository<PrepareRequest, Long>,
JpaSpecificationExecutor<PrepareRequest>{

@Query("from PrepareRequest prepareReq where prepareReq.id= (select max(prepareReq1.id) from PrepareRequest prepareReq1 where prepareReq1.orderId=:orderId AND prepareReq1.requestType = 'ACTIVATION')")
PrepareRequest getLatestRequestByOrderId(@Param("orderId") Long orderId);

}

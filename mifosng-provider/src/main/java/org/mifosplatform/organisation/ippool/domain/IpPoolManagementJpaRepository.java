package org.mifosplatform.organisation.ippool.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IpPoolManagementJpaRepository extends JpaRepository<IpPoolManagementDetail, Long>, 
               JpaSpecificationExecutor<IpPoolManagementDetail> {
	
	 @Query("from IpPoolManagementDetail ipPoolManagementDetail where ipPoolManagementDetail.ipAddress =:ipAddress and ipPoolManagementDetail.status is 'F'")
	 IpPoolManagementDetail findIpAddressData(@Param("ipAddress") String ipAddress);
	
	 @Query("from IpPoolManagementDetail ipPoolManagementDetail where ipPoolManagementDetail.ipAddress =:ipAddress and ipPoolManagementDetail.status is 'A'")
	 IpPoolManagementDetail findAllocatedIpAddressData(@Param("ipAddress") String ipAddress);
	
	 @Query("from IpPoolManagementDetail ipPoolManagementDetail where ipPoolManagementDetail.ipAddress =:ipAddress")
	 IpPoolManagementDetail findByIpAddress(@Param("ipAddress") String ipAddress);

	

}

package org.mifosplatform.billing.ippool.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.AbstractPersistable;


@Entity
@Table(name = "b_ippool_details")
public class IpPoolManagementDetail extends AbstractPersistable<Long>{
	
	@Column(name = "ip_address")
	private String ipAddress;

	@Column(name="client_id", nullable = true)
	private Long clientId;
	
	@Column(name="status", nullable = false)
	private char status;
	
	@Column(name="pool_name", nullable = false)
	private String ipPoolDescription;
	
	
	public IpPoolManagementDetail(){
		
	}

	public IpPoolManagementDetail(String ipAddress, String ipPoolDescription) {
		// TODO Auto-generated constructor stub
		this.ipAddress=ipAddress;
		this.status='F';
		this.ipPoolDescription=ipPoolDescription;
	}
}

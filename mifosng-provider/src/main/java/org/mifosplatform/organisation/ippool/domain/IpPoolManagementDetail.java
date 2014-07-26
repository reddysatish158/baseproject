package org.mifosplatform.organisation.ippool.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.AbstractPersistable;


@SuppressWarnings("serial")
@Entity
@Table(name = "b_ippool_details")
public class IpPoolManagementDetail extends AbstractPersistable<Long>{
	
	@Column(name = "ip_address")
	private String ipAddress;

	@Column(name="client_id", nullable = true)
	private Long clientId;
	
	@Column(name="status", nullable = false)
	private char status;
	
	@Column(name="pool_name")
	private String ipPoolDescription;
	
	@Column(name="notes", nullable = false)
	private String notes;
	
	@Column(name="type")
	private Long type;
	
	@Column(name="subnet")
	private Long subnet;

	public IpPoolManagementDetail(){
		
	}

	public IpPoolManagementDetail(String ipAddress, char status, Long type, String notes,Long subnet) {
		
		this.ipAddress=ipAddress;
		this.status=status;
		this.type=type;
		this.notes=notes;
		this.subnet=subnet;
		
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public Long getClientId() {
		return clientId;
	}

	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}

	public char getStatus() {
		return status;
	}

	public void setStatus(char status) {
		this.status = status;
	}

	public String getIpPoolDescription() {
		return ipPoolDescription;
	}

	public void setIpPoolDescription(String ipPoolDescription) {
		this.ipPoolDescription = ipPoolDescription;
	}


	public String getNotes() {
		return notes;
	}

	public Long getType() {
		return type;
	}

	public void setType(Long type) {
		this.type = type;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	public Long getSubnet() {
		return subnet;
	}

	public void setSubnet(Long subnet) {
		this.subnet = subnet;
	}
	
}

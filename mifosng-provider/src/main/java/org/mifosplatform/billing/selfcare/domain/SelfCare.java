package org.mifosplatform.billing.selfcare.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name="b_clientuser",uniqueConstraints = @UniqueConstraint(name = "username", columnNames = { "username","unique_reference"}))
public class SelfCare extends AbstractPersistable<Long>{

	@Column(name="client_id")
	private Long clientId;
	
	@Column(name="username")
	private String userName;
	
	@Column(name="password")
	private String password;
	
	@Column(name="unique_reference")
	private String uniqueReference;
	
	@Column(name="status")
	private String status;
	
	@Column(name="is_deleted")
	private Boolean isDeleted=false;
	
	public SelfCare() {
		// TODO Auto-generated constructor stub
	}
	public SelfCare(final Long clientId, final String userName, final String password, final String uniqueReference, final Boolean isDeleted){
		this.clientId = clientId;
		this.userName = userName;
		this.password = password;
		this.uniqueReference = uniqueReference;
		this.isDeleted = isDeleted;
		this.status="INACTIVE";
	}
	public static SelfCare fromJson(JsonCommand command) {
		final String userName = command.stringValueOfParameterNamed("userName");
		final String uniqueReference = command.stringValueOfParameterNamed("uniqueReference");
		SelfCare selfCare = new SelfCare();
		selfCare.setUserName(userName);
		selfCare.setUniqueReference(uniqueReference);
		selfCare.setIsDeleted(false);
		selfCare.setStatus("INACTIVE");
		return selfCare;
		
	}
	
	public static SelfCare fromJsonODP(JsonCommand command) {
		String userName = command.stringValueOfParameterNamed("userName");
		String uniqueReference = command.stringValueOfParameterNamed("uniqueReference");
		String password = command.stringValueOfParameterNamed("password");
		SelfCare selfCare = new SelfCare();
		selfCare.setUserName(userName);
		selfCare.setUniqueReference(uniqueReference);
		selfCare.setPassword(password);
		selfCare.setIsDeleted(false);
		selfCare.setStatus("ACTIVE");
		return selfCare;
		
	}
	
	public Long getClientId() {
		return clientId;
	}
	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUniqueReference() {
		return uniqueReference;
	}
	public void setUniqueReference(String uniqueReference) {
		this.uniqueReference = uniqueReference;
	}
	public Boolean getIsDeleted() {
		return isDeleted;
	}
	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	
}

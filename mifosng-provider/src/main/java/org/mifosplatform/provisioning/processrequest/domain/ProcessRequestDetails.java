package org.mifosplatform.provisioning.processrequest.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.StringUtils;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.domain.AbstractAuditableCustom;
import org.mifosplatform.useradministration.domain.AppUser;

@Entity
@Table(name = "b_process_request_detail")
public class ProcessRequestDetails extends
		AbstractAuditableCustom<AppUser, Long> {

	@ManyToOne
	@JoinColumn(name = "processrequest_id")
	private ProcessRequest processRequest;

	@Column(name = "orderlinbe_id")
	private Long orderlinId;

	@Column(name = "service_id")
	private Long serviceId;

	@Column(name = "sent_message")
	private String sentMessage;

	@Column(name = "receive_message")
	private String receiveMessage;

	@Column(name = "is_deleted")
	private char isDeleted;

	@Column(name = "hardware_id")
	private String hardwareId;

	@Column(name = "received_status")
	private String receivedStatus;

	@Column(name = "request_type")
	private String requestType;

	@Column(name = "service_type")
	private String serviceType;
	
	@Column(name = "sent_date")
	private Date sentDate;

	@Column(name = "received_date")
	private Date receivedDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "start_date")
	private Date startDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "end_date")
	private Date endDate;

	public ProcessRequestDetails() {

	}

	public ProcessRequestDetails(Long orderlinId, Long serviceId,String sentMessage, String recievedMessage, String hardwareId,
			Date startDate, Date endDate, Date sentDate, Date recievedDate,char isDeleted, String requestType,String serviceType) {

		this.orderlinId = orderlinId;
		this.serviceId = serviceId;
		this.sentMessage = sentMessage;
		this.receiveMessage = recievedMessage;
		this.isDeleted = isDeleted;
		this.requestType = requestType;
		this.hardwareId = hardwareId;
		this.startDate = startDate;
		this.endDate = endDate;
		this.sentDate = sentDate;
		this.receivedDate = recievedDate;
		this.serviceType=serviceType;

	}

	
	public void update(ProcessRequest processRequest) {

		this.processRequest = processRequest;

	}

	public void updateStatus(JsonCommand command) {
		this.receivedStatus = command.stringValueOfParameterNamed("receivedStatus");
		this.receiveMessage = command.stringValueOfParameterNamed("receiveMessage");		
	}

	public ProcessRequest getProcessRequest() {
		return processRequest;
	}

	public Long getOrderlinId() {
		return orderlinId;
	}

	public Long getServiceId() {
		return serviceId;
	}

	public String getSentMessage() {
		return sentMessage;
	}

	public String getReceiveMessage() {
		return receiveMessage;
	}

	public char getIsDeleted() {
		return isDeleted;
	}

	public String getHardwareId() {
		return hardwareId;
	}

	public String getReceivedStatus() {
		return receivedStatus;
	}

	public String getRequestType() {
		return requestType;
	}

	public Date getSentDate() {
		return sentDate;
	}

	public Date getReceivedDate() {
		return receivedDate;
	}

	public Date getStartDate() {
		return startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void updateDetails(JsonCommand command) {

		
		  final String firstnameParamName = "hardwareId";
	        if (command.isChangeInStringParameterNamed(firstnameParamName, this.hardwareId)) {
	            final String newValue = command.stringValueOfParameterNamed(firstnameParamName);
	            this.hardwareId= StringUtils.defaultIfEmpty(newValue, null);
	        }

	        final String sentMessageParamName="sentMessage";
	        if(command.isChangeInStringParameterNamed(sentMessageParamName,this.sentMessage)){
	        	final String newValue = command.stringValueOfParameterNamed(sentMessageParamName);
	        	this.sentMessage=newValue;
	        }
		
	}

	public void update(String provSerilaNum) {
		
		this.hardwareId=provSerilaNum;
		
	}

	public String getServiceType() {
		return serviceType;
	}
	
	

}

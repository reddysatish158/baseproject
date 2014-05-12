package org.mifosplatform.organisation.message.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "b_message_params")
public class BillingMessageParam extends AbstractPersistable<Long>{

	

	@ManyToOne
    @JoinColumn(name="msgtemplate_id")
    private BillingMessageTemplate billingMessageTemplate;
	
	@Column(name = "parameter_name")
	private String parameterName;

	@Column(name="sequence_no")
	private Long sequenceNo;
	
	public BillingMessageParam(){
		//default-constructor
	}

	public BillingMessageParam(Long sequenceNo, String parameterName) {
		
		this.sequenceNo=sequenceNo;
		this.parameterName=parameterName;
	}
	public String getParameterName() {
		return parameterName;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	public Long getSequenceNo() {
		return sequenceNo;
	}

	public void setSequenceNo(Long sequenceNo) {
		this.sequenceNo = sequenceNo;
	}

	public void update(BillingMessageTemplate billingMessageTemplate) {

		this.billingMessageTemplate=billingMessageTemplate;
	}
}

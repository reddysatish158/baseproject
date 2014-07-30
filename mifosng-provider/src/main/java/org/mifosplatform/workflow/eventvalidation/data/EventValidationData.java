package org.mifosplatform.workflow.eventvalidation.data;

import java.util.List;
import org.mifosplatform.billing.paymode.data.McodeData;

public class EventValidationData {
	
	 Long id;
	 String eventName;
	 String process;
	 String codeValue;
	 String prePost;
	 private List<McodeData> eventData;
	 String isDeleted;
	 
	public String getCodeValue() {
		return codeValue;
	}

	public void setCodeValue(String codeValue) {
		this.codeValue = codeValue;
	}

	public EventValidationData() {
		// TODO Auto-generated constructor stub
	}

	public EventValidationData(List<McodeData> templateData) {
		
		this.eventData=templateData;
	}

	public EventValidationData(Long id, String eventName, String process,
			String prePost, String isDeleted) {
		
		this.id = id;
		this.eventName = eventName;
		this.process = process;
		this.prePost = prePost;
		this.isDeleted = isDeleted;
	}

	public String getBatchName() {
		return process;
	}
	
	public void setBatchName(String process) {
		this.process = process;
	}
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public String getProcess() {
		return process;
	}

	public void setProcess(String process) {
		this.process = process;
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public List<McodeData> getEventData() {
		return eventData;
	}

	public void setEventData(List<McodeData> eventData) {
		this.eventData = eventData;
	}

	
}

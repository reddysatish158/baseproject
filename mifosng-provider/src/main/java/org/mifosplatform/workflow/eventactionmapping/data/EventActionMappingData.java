package org.mifosplatform.workflow.eventactionmapping.data;

import java.util.Date;
import java.util.List;

import org.mifosplatform.billing.paymode.data.McodeData;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;

public class EventActionMappingData {
	
	 
	 String process;
	 Long id;
	 String eventName;
	 String actionName;
	 String codeValue;
	 private List<McodeData> actionData;
	 private List<McodeData> eventData;
	 String isDeleted;
	 
	public EventActionMappingData(Long id,String eventName,String actionName,String process,String codeValue,List<EventActionMappingData> actionData,List<EventActionMappingData> eventData) {
		// TODO Auto-generated constructor stub
		this.id=id;
		this.eventName=eventName;
		this.actionName=actionName;
		this.process=process;
		this.codeValue=codeValue;
		
	}
	
	public String getCodeValue() {
		return codeValue;
	}

	public void setCodeValue(String codeValue) {
		this.codeValue = codeValue;
	}

	public EventActionMappingData() {
		// TODO Auto-generated constructor stub
	}

	public EventActionMappingData(List<McodeData> actionData,
			List<McodeData> eventData) {
		// TODO Auto-generated constructor stub
		this.actionData=actionData;
		this.eventData=eventData;
	}

	public EventActionMappingData(Long id, String eventName,
			String actionName, String process, String isDeleted) {
		this.id=id;
		this.eventName=eventName;
		this.actionName=actionName;
		this.process=process;
		this.isDeleted=isDeleted;
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

	public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	public List<McodeData> getActionData() {
		return actionData;
	}

	public void setActionData(List<McodeData> actionData) {
		this.actionData = actionData;
	}

	public List<McodeData> getEventData() {
		return eventData;
	}

	public void setEventData(List<McodeData> eventData) {
		this.eventData = eventData;
	}

	
}

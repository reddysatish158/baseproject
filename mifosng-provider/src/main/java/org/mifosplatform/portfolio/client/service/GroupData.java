package org.mifosplatform.portfolio.client.service;

public class GroupData {
	
	private final Long id;
	private final String groupName;
	
	public GroupData(Long id,String groupName){
		this.id = id;
		this.groupName  = groupName;
	}
	
	public Long getId(){
		return id;
	}
	
	public String getGroupName(){
		return groupName;
	}

}

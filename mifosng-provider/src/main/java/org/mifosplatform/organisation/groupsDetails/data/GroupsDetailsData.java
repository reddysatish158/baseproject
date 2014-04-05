package org.mifosplatform.organisation.groupsDetails.data;

public class GroupsDetailsData {

	private Long id;
	private String groupName;
	private String groupAddress;
	private Long countNo;
	
	public GroupsDetailsData(Long id,String groupName,String groupAddress,Long countNo){
		
		this.id = id;
		this.groupName = groupName;
		this.groupAddress = groupAddress;
		this.countNo = countNo;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getGroupAddress() {
		return groupAddress;
	}

	public void setGroupAddress(String groupAddress) {
		this.groupAddress = groupAddress;
	}

	public Long getCountNo() {
		return countNo;
	}

	public void setCountNo(Long countNo) {
		this.countNo = countNo;
	}
}

package org.mifosplatform.organisation.groupsDetails.data;

public class GroupsDetailsData {

	private Long id;
	private String groupName;
	private String groupAddress;
	private Long countNo;
	private String attribute1;
	private String attribute2;
	private String attribute3;
	private String attribute4;
	private String isProvision;
	
	public GroupsDetailsData(Long id,String groupName,String groupAddress,Long countNo, String attribute1, 
			                       String attribute2, String attribute3, String attribute4,String isProvision){
		
		this.id = id;
		this.groupName = groupName;
		this.groupAddress = groupAddress;
		this.countNo = countNo;
		this.attribute1 = attribute1;
		this.attribute2	= attribute2;
		this.attribute3 = attribute3;
		this.attribute4 = attribute4;
		this.isProvision = isProvision;
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

	public String getAttribute1() {
		return attribute1;
	}

	public void setAttribute1(String attribute1) {
		this.attribute1 = attribute1;
	}

	public String getAttribute2() {
		return attribute2;
	}

	public void setAttribute2(String attribute2) {
		this.attribute2 = attribute2;
	}

	public String getAttribute3() {
		return attribute3;
	}

	public void setAttribute3(String attribute3) {
		this.attribute3 = attribute3;
	}

	public String getAttribute4() {
		return attribute4;
	}

	public void setAttribute4(String attribute4) {
		this.attribute4 = attribute4;
	}

	public String getIsProvision() {
		return isProvision;
	}

	public void setIsProvision(String isProvision) {
		this.isProvision = isProvision;
	}

	
	
}

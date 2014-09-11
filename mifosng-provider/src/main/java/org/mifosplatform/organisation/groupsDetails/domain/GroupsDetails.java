package org.mifosplatform.organisation.groupsDetails.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "b_group")
public class GroupsDetails extends AbstractPersistable<Long>{

	@Column(name = "group_name")
	private String groupName;
	
	@Column(name = "group_address")
	private String groupAddress;
	
	@Column(name = "attribute1")
	private String attribute1;
	
	@Column(name = "attribute2")
	private String attribute2;
	
	@Column(name = "attribute3")
	private String attribute3;
	
	@Column(name = "attribute4")
	private String attribute4;
	
	@Column(name = "is_provision")
	private char isProvision = 'N';
	
	public GroupsDetails(){
		
	}
	
	public  GroupsDetails(String groupName,String groupAddress,String attribute1,String attribute2,
					String attribute3,String attribute4) {
			this.groupName = groupName;
			this.groupAddress = groupAddress;
			this.attribute1 = attribute1;
			this.attribute2 = attribute2;
			this.attribute3 = attribute3;
			this.attribute4 = attribute4;
	}
	public static GroupsDetails fromJson(JsonCommand command) {
		
		final String groupName = command.stringValueOfParameterNamed("groupName");
		final String groupAddress = command.stringValueOfParameterNamed("groupAddress");
		final String attribute1 = command.stringValueOfParameterNamed("attribute1");
		final String attribute2 = command.stringValueOfParameterNamed("attribute2");
		final String attribute3 = command.stringValueOfParameterNamed("attribute3");
		final String attribute4 = command.stringValueOfParameterNamed("attribute4");
		return new GroupsDetails(groupName,groupAddress,attribute1,attribute2,attribute3,attribute4);
		
	}
	
	public void provision() {

		if(this.isProvision == 'N'){
			this.isProvision='Y';
		}
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

	public char getIsProvision() {
		return isProvision;
	}

	public void setIsProvision(char isProvision) {
		this.isProvision = isProvision;
	}
	
	
}

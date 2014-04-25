package org.mifosplatform.provisioning.provisioning.domain;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.springframework.data.jpa.domain.AbstractPersistable;

@SuppressWarnings("serial")
@Entity
@Table(name = "b_command")
public class ProvisioningCommand extends AbstractPersistable<Long> {

	@Column(name = "provisioning_system")
	private String provisioningSystem;

	@Column(name = "command_name")
	private String commandName;

	@Column(name = "status")
	private String status="Y";
	
	@Column(name = "is_deleted")
	private char isDeleted='N';

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "commandprovisioning", orphanRemoval = true)
	private List<ProvisioningCommandParameters> commandparameters = new ArrayList<ProvisioningCommandParameters>();
	
	public ProvisioningCommand(){
		
	}
	
	public ProvisioningCommand(String provisioningSystem,String commandName){
		this.provisioningSystem=provisioningSystem;
		this.commandName=commandName;
	}

	public String getProvisioningSystem() {
		return provisioningSystem;
	}

	public String getCommandName() {
		return commandName;
	}

	public String getStatus() {
		return status;
	}

	public List<ProvisioningCommandParameters> getCommandparameters() {
		return commandparameters;
	}
	
	public void addCommandParameters(ProvisioningCommandParameters commandParameters) {
		commandParameters.update(this);
		this.commandparameters.add(commandParameters);

	}

	public char getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(char isDeleted) {
		this.isDeleted = isDeleted;
	}

	public static ProvisioningCommand from(JsonCommand command) {
		
		 final String provisioningSystem = command.stringValueOfParameterNamed("provisioningSystem");
		 final String commandName = command.stringValueOfParameterNamed("commandName");
		 return new ProvisioningCommand(provisioningSystem,commandName);
	}

	public LinkedHashMap<String, Object> UpdateProvisioning(JsonCommand command) {
		final LinkedHashMap<String, Object> actualChanges = new LinkedHashMap<String, Object>(1);
		
		final String provisioningSystem = "provisioningSystem";
		if (command.isChangeInStringParameterNamed(provisioningSystem,this.provisioningSystem)) {
			final String newValue=command.stringValueOfParameterNamed("provisioningSystem");
			actualChanges.put(provisioningSystem, newValue);
			this.provisioningSystem = StringUtils.defaultIfEmpty(newValue, null);
		}
		final String commandName = "commandName";
		if (command.isChangeInStringParameterNamed(commandName,this.commandName)) {
			final String newValue=command.stringValueOfParameterNamed("commandName");
			actualChanges.put(commandName, newValue);
			this.commandName = StringUtils.defaultIfEmpty(newValue, null);
		}

		return actualChanges;
		
	}
	

}

package org.mifosplatform.provisioning.provisioning.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.mifosplatform.infrastructure.core.domain.AbstractAuditableCustom;
import org.mifosplatform.useradministration.domain.AppUser;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "b_command_parameters")
public class ProvisioningCommandParameters extends AbstractPersistable<Long> {
	
	    @ManyToOne
	    @JoinColumn(name="command_id")
		private ProvisioningCommand commandprovisioning;

		@Column(name = "command_param")
		private String commandParam;

		@Column(name = "param_type")
		private String paramType;

		@Column(name = "param_default")
		private String paramDefault;

		public ProvisioningCommandParameters(){
			
		}
		
        public ProvisioningCommandParameters(String commandParam,String paramType,String paramDefault){
  			
        	this.commandParam=commandParam;
        	this.paramType=paramType;
        	this.paramDefault=paramDefault;
		}
		
        
        
		public ProvisioningCommand getCommandprovisioning() {
			return commandprovisioning;
		}

		public String getCommandParam() {
			return commandParam;
		}

		public String getParamType() {
			return paramType;
		}

		public String getParamDefault() {
			return paramDefault;
		}

		public void update(ProvisioningCommand command) {
			this.commandprovisioning=command;
		}

}

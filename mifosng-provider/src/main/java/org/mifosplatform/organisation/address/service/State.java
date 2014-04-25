package org.mifosplatform.organisation.address.service;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.StringUtils;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.springframework.data.jpa.domain.AbstractPersistable;


@Entity
@Table(name="b_state",uniqueConstraints=@UniqueConstraint( name="state_code",columnNames ={ "state_code" }))
public class State extends AbstractPersistable<Long>{

@Column(name="state_code")
private String stateCode;

@Column(name="state_name")
private String stateName;

@Column(name ="parent_code")
private Long parentCode;

@Column(name = "is_delete")
private char isDeleted='N';


public State(){
	
}

public State(String entityCode, String entityName, Long parentEntityId) {
	  this.stateCode=entityCode;
	  this.stateName=entityName;
	  this.parentCode=parentEntityId;


	}


public static State fromJson(JsonCommand command) {
	 final String cityCode = command.stringValueOfParameterNamed("entityCode");
	    final String cityName = command.stringValueOfParameterNamed("entityName");
	    final Long parentEntityId = command.longValueOfParameterNamed("parentEntityId");
	     return new State(cityCode,cityName, parentEntityId);
}
public Map<String, Object> update(JsonCommand command) {
	final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(1);
	final String stateCodeParamName="entityCode";
	if (command.isChangeInStringParameterNamed(stateCodeParamName,this.stateCode)){
		final String newValue = command.stringValueOfParameterNamed(stateCodeParamName);
		actualChanges.put(stateCodeParamName, newValue);
		this.stateCode = StringUtils.defaultIfEmpty(newValue, null);
	}
	final String stateNameParamName="entityName";
	if (command.isChangeInStringParameterNamed(stateNameParamName,this.stateName)){
		final String newValue = command.stringValueOfParameterNamed(stateNameParamName);
		actualChanges.put(stateNameParamName, newValue);
		this.stateName = StringUtils.defaultIfEmpty(newValue, null);
	}
	final String parentCodeParam = "parentEntityId";
    if (command.isChangeInLongParameterNamed(parentCodeParam, this.parentCode)) {
        final Long newValue = command.longValueOfParameterNamed(parentCodeParam);
        actualChanges.put(parentCodeParam, newValue);
        this.parentCode = newValue;
    }
    return actualChanges;
}

public void delete() {
	// TODO Auto-generated method stub
	if(this.isDeleted == 'N'){
		this.isDeleted='Y';
	}
}


}

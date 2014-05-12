package org.mifosplatform.organisation.address.service;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.StringUtils;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.domain.AbstractAuditableCustom;
import org.mifosplatform.useradministration.domain.AppUser;


@Entity
@Table(name="b_city",uniqueConstraints = @UniqueConstraint(name = "city_code", columnNames = { "city_code" }))
public class City extends AbstractAuditableCustom<AppUser, Long> {


@Column(name="city_code")
private String cityCode;

@Column(name="city_name")
private String cityName;

@Column(name="parent_code")
private Long parentCode;

@Column(name = "is_delete")
private char isDeleted='N';

public City(){
	
}
public City(String entityCode, String entityName, Long parentEntityId) {

	 this.cityCode=entityCode;
	 this.cityName=entityName;
	 this.parentCode=parentEntityId;


	}
public static City fromJson(JsonCommand command) {
	 final String cityCode = command.stringValueOfParameterNamed("entityCode");
	    final String cityName = command.stringValueOfParameterNamed("entityName");
	    final Long parentEntityId = command.longValueOfParameterNamed("parentEntityId");
	     return new City(cityCode,cityName, parentEntityId);
}

public Map<String, Object> update(JsonCommand command) {
	final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(1);
	final String cityCodeParamName="entityCode";
	if (command.isChangeInStringParameterNamed(cityCodeParamName,this.cityCode)){
		final String newValue = command.stringValueOfParameterNamed(cityCodeParamName);
		actualChanges.put(cityCodeParamName, newValue);
		this.cityCode = StringUtils.defaultIfEmpty(newValue, null);
	}
	final String cityNameParamName="entityName";
	if (command.isChangeInStringParameterNamed(cityNameParamName,this.cityName)){
		final String newValue = command.stringValueOfParameterNamed(cityNameParamName);
		actualChanges.put(cityNameParamName, newValue);
		this.cityName = StringUtils.defaultIfEmpty(newValue, null);
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
	
	if(this.isDeleted == 'N'){
		this.isDeleted='Y';
	}
}

}

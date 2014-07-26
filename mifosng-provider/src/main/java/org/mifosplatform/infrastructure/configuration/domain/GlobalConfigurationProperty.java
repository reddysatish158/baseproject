/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.configuration.domain;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.mifosplatform.infrastructure.codes.domain.Code;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "c_configuration")
public class GlobalConfigurationProperty extends AbstractPersistable<Long> {

    @SuppressWarnings("unused")
    @Column(name = "name", nullable = false)
    private final String name;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    @Column(name = "value", nullable = false)
    private String value;
    
    protected GlobalConfigurationProperty() {
        this.name = null;
        this.enabled = false;
        this.value=null;
    }

    public GlobalConfigurationProperty(final String name, final boolean enabled,final String value) {
        this.name = name;
        this.enabled = enabled;
        this.value=value;
    }
    
    public GlobalConfigurationProperty(String username, String value) {
		
    	this.name=username;
    	this.value=value;
	}

	public boolean isEnabled() {
        return this.enabled;
    }

    public boolean updateTo(final boolean value) {
        final boolean updated = this.enabled != value;
        this.enabled = value;
        return updated;
    }

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public Map<String, Object> update(JsonCommand command) { 

        final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(7);

        final String enabledParamName = "enabled";
        if (command.isChangeInBooleanParameterNamed(enabledParamName, this.enabled)) {
            final Boolean newValue = command.booleanPrimitiveValueOfParameterNamed(enabledParamName);
            actualChanges.put(enabledParamName, newValue);
            this.enabled = newValue;
        }

        final String valueParamName = "value";
        if (command.isChangeInStringParameterNamed(valueParamName, this.value)) {
            final String newValue = command.stringValueOfParameterNamed(valueParamName);
            actualChanges.put(valueParamName, newValue);
            this.value = newValue;
        }

        return actualChanges;

    }

	public static GlobalConfigurationProperty fromJson(JsonCommand command,
			String userName, String value) {
		
		final String username=userName;
		final String valueOfMailIdAndPasswordAndHostName=value;
		return new GlobalConfigurationProperty(username,valueOfMailIdAndPasswordAndHostName);
	}
}
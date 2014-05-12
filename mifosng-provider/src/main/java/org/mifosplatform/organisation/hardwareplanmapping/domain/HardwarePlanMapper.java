package org.mifosplatform.organisation.hardwareplanmapping.domain;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.apache.commons.lang.StringUtils;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "b_hw_plan_mapping")
public class HardwarePlanMapper  extends AbstractPersistable<Long> {

	@Column(name = "item_code")
	private String itemCode;

	@Column(name = "plan_code")
	private String planCode;
	
	
	public HardwarePlanMapper() {
		// TODO Auto-generated constructor stub
	}


		public HardwarePlanMapper(String planCode, String itemCode) {

		  this.itemCode=itemCode;
		  this.planCode=planCode;
		  
		
		}




	public Map<String, Object> update(JsonCommand command) {
		 final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(1);
		  final String firstnameParamName = "planCode";
	        if (command.isChangeInStringParameterNamed(firstnameParamName, this.planCode)) {
	            final String newValue = command.stringValueOfParameterNamed(firstnameParamName);
	            actualChanges.put(firstnameParamName, newValue);
	            this.planCode = StringUtils.defaultIfEmpty(newValue, null);
	        }
	        
	        final String itemCodeParamName = "itemCode";
	        if (command.isChangeInStringParameterNamed(itemCode, this.itemCode)) {
	            final String newValue = command.stringValueOfParameterNamed(itemCodeParamName);
	            actualChanges.put(firstnameParamName, newValue);
	            this.itemCode = StringUtils.defaultIfEmpty(newValue, null);
	        }
						
	        return actualChanges;

	}

	public static HardwarePlanMapper fromJson(JsonCommand command) {
		 final String planCode = command.stringValueOfParameterNamed("planCode");
		 final String itemCode = command.stringValueOfParameterNamed("itemCode");

		 return new HardwarePlanMapper(planCode,itemCode);
	}

	

	public String getCode() {
	return planCode;
}
public String getPlanCode() {
	return planCode;
}


public String getItemCode() {
	return itemCode;
}


}

	

	


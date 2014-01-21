package org.mifosplatform.billing.promotioncodes.domain;
import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "b_promotion_master")
public class PromotionCode extends AbstractPersistable<Long> {

	
	@Column(name = "promotion_code")
	private String promotionCode;

	@Column(name = "promotion_description")
	private String promotionDescription;

	@Column(name = "duration_type")
	private String durationType;
	
	@Column(name = "duration")
	private Long duration;
	
	@Column(name = "discount_type")
	private String discountType;
	
	@Column(name = "discount_rate")
	private BigDecimal discountRate;
	
	@Column(name = "start_date")
	private Date startDate;
	
	@Column(name = "valid_until")
	private Date validUntil;
	
	@Column(name = "is_delete")
	private char isDeleted='N';


	public PromotionCode() {
		// TODO Auto-generated constructor stub
			
	}

	public PromotionCode(String promotionCode,String promotionDescription,String durationType,Long duration,String discountType,
			BigDecimal discountRate,LocalDate startDate) {
          
		   this.promotionCode=promotionCode;
		   this.promotionDescription=promotionDescription;
		   this.durationType=durationType;
		   this.duration=duration;
		   this.discountType=discountType;
		   this.discountRate=discountRate;
		   this.startDate=startDate.toDate();
		  // this.validUntil=validUntil;
	}

	public String getEventName() {
		return promotionCode;
	}

	public String getActionName() {
		return promotionDescription;
	}

	public String getProcess() {
		return durationType;
	}


	public static PromotionCode fromJson(JsonCommand command) {
			
	/*	this.promotionCode=promotionCode;
		   this.promotionDescription=promotionDescription;
		   this.durationType=durationType;
		   this.duration=duration;
		   this.discountType=discountType;
		   this.discountRate=discountRate;
		   this.startDate=startDate;
		   this.validUntil=validUntil;*/
		
		 	final String promotionCode = command.stringValueOfParameterNamed("promotioncode");
		    final String promotionDescription = command.stringValueOfParameterNamed("description");
		    final String durationType = command.stringValueOfParameterNamed("durationtype");
		    final Long duration=command.longValueOfParameterNamed("duration");
		    final String discountType = command.stringValueOfParameterNamed("discounttype");
		    final BigDecimal discountRate=command.bigDecimalValueOfParameterNamed("discountrate");
		    final LocalDate startDate=command.localDateValueOfParameterNamed("startdate");
		    //final Date validUntil=command.DateValueOfParameterNamed("");
		    
		return new PromotionCode(promotionCode,promotionDescription,durationType,duration,discountType,discountRate,startDate);
	}

	/*public Map<String, Object> update(JsonCommand command) {
		 final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(1);
		  final String eventParamName = "event";
	        if (command.isChangeInStringParameterNamed(eventParamName, this.eventName)) {
	            final String newValue = command.stringValueOfParameterNamed(eventParamName);
	            actualChanges.put(eventParamName, newValue);
	            this.eventName = StringUtils.defaultIfEmpty(newValue, null);
	        }
	        
	        final String actionParamName = "action";
	        if (command.isChangeInStringParameterNamed(actionParamName, this.actionName)) {
	            final String newValue = command.stringValueOfParameterNamed(actionParamName);
	            actualChanges.put(actionParamName, newValue);
	            this.actionName = StringUtils.defaultIfEmpty(newValue, null);
	        }
	        
	        
	        final String processParam = "process";
	        if (command.isChangeInStringParameterNamed(processParam, this.process)) {
	            final String newValue = command.stringValueOfParameterNamed(processParam);
	            actualChanges.put(processParam, newValue);
	            this.process = StringUtils.defaultIfEmpty(newValue, null);
	        }
	        				
	        return actualChanges;

	}*/

	public void delete() {
		
		if(this.isDeleted == 'N'){
			this.isDeleted='Y';
		}
		
	}
 


}

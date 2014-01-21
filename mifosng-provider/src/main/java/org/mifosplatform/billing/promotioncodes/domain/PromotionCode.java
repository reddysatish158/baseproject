package org.mifosplatform.billing.promotioncodes.domain;
import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
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
			
		 	final String promotionCode = command.stringValueOfParameterNamed("promotionCode");
		    final String promotionDescription = command.stringValueOfParameterNamed("description");
		    final String durationType = command.stringValueOfParameterNamed("durationType");
		    final Long duration=command.longValueOfParameterNamed("duration");
		    final String discountType = command.stringValueOfParameterNamed("discountType");
		    final BigDecimal discountRate=command.bigDecimalValueOfParameterNamed("discountRate");
		    final LocalDate startDate=command.localDateValueOfParameterNamed("startDate");
		    //final Date validUntil=command.DateValueOfParameterNamed("");
		    
		return new PromotionCode(promotionCode,promotionDescription,durationType,duration,discountType,discountRate,startDate);
	}

	public Map<String, Object> update(JsonCommand command) {
		 final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(1);
		  final String promotionCodeParamName = "promotionCode";
	        if (command.isChangeInStringParameterNamed(promotionCodeParamName, this.promotionCode)) {
	            final String newValue = command.stringValueOfParameterNamed(promotionCodeParamName);
	            actualChanges.put(promotionCodeParamName, newValue);
	            this.promotionCode = StringUtils.defaultIfEmpty(newValue, null);
	        }
	        
	        final String descriptionParamName = "description";
	        if (command.isChangeInStringParameterNamed(descriptionParamName, this.promotionDescription)) {
	            final String newValue = command.stringValueOfParameterNamed(descriptionParamName);
	            actualChanges.put(descriptionParamName, newValue);
	            this.promotionDescription = StringUtils.defaultIfEmpty(newValue, null);
	        }
	        
	        final String durationTypeParam = "durationType";
	        if (command.isChangeInStringParameterNamed(durationTypeParam, this.durationType)) {
	            final String newValue = command.stringValueOfParameterNamed(durationTypeParam);
	            actualChanges.put(durationTypeParam, newValue);
	            this.durationType = StringUtils.defaultIfEmpty(newValue, null);
	        }
	        
	        final String durationParam = "duration";
	        if (command.isChangeInLongParameterNamed(durationParam, this.duration)) {
	            final Long newValue = command.longValueOfParameterNamed(durationParam);
	            actualChanges.put(durationParam, newValue);
	            this.duration = newValue;
	        }
	        
	        final String discountTypeParam = "discountType";
	        if (command.isChangeInStringParameterNamed(discountTypeParam, this.discountType)) {
	            final String newValue = command.stringValueOfParameterNamed(discountTypeParam);
	            actualChanges.put(discountTypeParam, newValue);
	            this.discountType = StringUtils.defaultIfEmpty(newValue, null);
	        }
	        
	        final String discountRateParamName = "discountRate";
			if (command.isChangeInBigDecimalParameterNamed(discountRateParamName,this.discountRate)) {
				final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(discountRateParamName);
				actualChanges.put(discountRateParamName, newValue);
				this.discountRate=newValue;
			}
			
			final String startDateParamName = "startDate";
			if (command.isChangeInLocalDateParameterNamed(startDateParamName,
					new LocalDate(this.startDate))) {
				final LocalDate newValue = command
						.localDateValueOfParameterNamed(startDateParamName);
				actualChanges.put(startDateParamName, newValue);
			}
	        				
	        return actualChanges;

	}

	public void delete() {
		
		if(this.isDeleted == 'N'){
			this.isDeleted='Y';
		}
		
	}
 


}

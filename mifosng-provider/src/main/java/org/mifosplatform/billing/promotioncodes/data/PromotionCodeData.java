package org.mifosplatform.billing.promotioncodes.data;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.billing.mcodevalues.data.MCodeData;
import org.mifosplatform.billing.paymode.data.McodeData;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;

public class PromotionCodeData {
	
	
	 Long id;
	 String promotionCode;
	 String promotionDescription;
	 String durationType;
	 Long duration;
	 String promotionType;
	 private LocalDate discountStartDate;
	 private BigDecimal discountRate;
	 private Collection<MCodeData> discounTypeData;
	 private List<McodeData> actionData;
	 private List<McodeData> eventData;
	public PromotionCodeData(Long id,String promotionCode,String promotionDescription,String durationType,Long duration,String promotionType,BigDecimal discountRate,List<PromotionCodeData> actionData,List<PromotionCodeData> eventData) {
		// TODO Auto-generated constructor stub
		this.id=id;
		this.promotionCode=promotionCode;
		this.promotionDescription=promotionDescription;
		this.durationType=durationType;
		this.duration=duration;
		this.promotionType=promotionType;
		this.discountRate=discountRate;
	}
	
	/*public String getCodeValue() {
		return discountType;
	}

	public void setCodeValue(String discountType) {
		this.discountType = discountType;
	}*/

	public PromotionCodeData() {
		// TODO Auto-generated constructor stub
	}

	public PromotionCodeData(List<McodeData> actionData,
			List<McodeData> eventData) {
		// TODO Auto-generated constructor stub
		this.actionData=actionData;
		this.eventData=eventData;
	}
	public PromotionCodeData(Collection<MCodeData> discountTypeDate) {
       
        this.discounTypeData=discountTypeDate;
	
	}
	/*public String getBatchName() {
		return promotionCode;
	}
	
	public void setBatchName(String promotionCode) {
		this.promotionCode = promotionCode;
	}*/
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public String getProcess() {
		return promotionCode;
	}

	public void setProcess(String process) {
		this.promotionCode = promotionCode;
	}

	public String getEventName() {
		return promotionType;
	}

	public void setEventName(String eventName) {
		this.promotionType = promotionType;
	}

	public String getActionName() {
		return promotionDescription;
	}

	public void setActionName(String promotionDescription) {
		this.promotionDescription = promotionDescription;
	}

	public List<McodeData> getActionData() {
		return actionData;
	}

	public void setActionData(List<McodeData> actionData) {
		this.actionData = actionData;
	}

	public List<McodeData> getEventData() {
		return eventData;
	}

	public void setEventData(List<McodeData> eventData) {
		this.eventData = eventData;
	}

	
}

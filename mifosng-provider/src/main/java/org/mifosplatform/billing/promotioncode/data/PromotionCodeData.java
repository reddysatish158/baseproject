package org.mifosplatform.billing.promotioncode.data;

import java.math.BigDecimal;

public class PromotionCodeData {
	
	private final Long id;
	private final String promotionCode;
	private final String promotionDescription;
	private final String durationType;
	private final Long duration;
	private final String discountType;
	private final BigDecimal discountRate;

	public PromotionCodeData(Long id, String promotionCode,String promotionDescription, String durationType, 
			Long duration,String discountType, BigDecimal discountRate) {
		
		this.id=id;
		this.promotionCode=promotionCode;
		this.promotionDescription=promotionDescription;
		this.durationType=durationType;
		this.duration=duration;
		this.discountType=discountType;
		this.discountRate=discountRate;
		
	}

	public Long getId() {
		return id;
	}

	public String getPromotionCode() {
		return promotionCode;
	}

	public String getPromotionDescription() {
		return promotionDescription;
	}

	public String getDurationType() {
		return durationType;
	}

	public Long getDuration() {
		return duration;
	}

	public String getDiscountType() {
		return discountType;
	}

	public BigDecimal getDiscountRate() {
		return discountRate;
	}
	
	

}

package org.mifosplatform.billing.order.data;

import java.math.BigDecimal;

public class OrderDiscountData {
	
	private final Long id;
	private final Long priceId;
	private final String discountcode;
	private final String discountdescription;
	private final String discountType;
	private final BigDecimal discountAmount;

	public OrderDiscountData(Long id, Long priceId, String discountCode,String discountdescription, 
			BigDecimal discountAmount,String discountType) {
		
		this.id=id;
		this.priceId=priceId;
		this.discountcode=discountCode;
		this.discountdescription=discountdescription;
		this.discountAmount=discountAmount;
		this.discountType=discountType;
	}

	public Long getId() {
		return id;
	}

	public Long getPriceId() {
		return priceId;
	}

	public String getDiscountcode() {
		return discountcode;
	}

	public String getDiscountdescription() {
		return discountdescription;
	}

	public String getDiscountType() {
		return discountType;
	}

	public BigDecimal getDiscountAmount() {
		return discountAmount;
	}

}

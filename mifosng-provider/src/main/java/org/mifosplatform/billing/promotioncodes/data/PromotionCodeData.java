package org.mifosplatform.billing.promotioncodes.data;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;
import org.mifosplatform.portfolio.contract.data.PeriodData;
public class PromotionCodeData {
	
	
	 Long id;
	 String promotionCode;
	 String promotionDescription;
	 String durationType;
	 Long duration;
	 String discountType;
	 private LocalDate discountStartDate;
	 private BigDecimal discountRate;
	 private Collection<MCodeData> discounTypeData;

	 private List<PeriodData> contractTypedata;

	public PromotionCodeData(Long id,String promotionCode,String promotionDescription,String durationType,Long duration,String discountType,BigDecimal discountRate) {

		this.id=id;
		this.promotionCode=promotionCode;
		this.promotionDescription=promotionDescription;
		this.durationType=durationType;
		this.duration=duration;
		this.discountType=discountType;
		this.discountRate=discountRate;
	}

	public PromotionCodeData() {
		// TODO Auto-generated constructor stub
	}

	public PromotionCodeData(Collection<MCodeData> discountTypeDate,List<PeriodData> contractTypedata) {
       
        this.discounTypeData=discountTypeDate;
        this.contractTypedata=contractTypedata;

	
	}
	
	public Long getId() {
		return id;
	}
	
	public String getPromotionCode() {
		return promotionCode;
	}

	public void setPromotionCode(String promotionCode) {
		this.promotionCode = promotionCode;
	}

	public String getPromotionDescription() {
		return promotionDescription;
	}

	public void setPromotionDescription(String promotionDescription) {
		this.promotionDescription = promotionDescription;
	}

	public String getDurationType() {
		return durationType;
	}

	public void setDurationType(String durationType) {
		this.durationType = durationType;
	}

	public Long getDuration() {
		return duration;
	}

	public void setDuration(Long duration) {
		this.duration = duration;
	}

	public String getPromotionType() {
		return discountType;
	}

	public void setPromotionType(String discountType) {
		this.discountType = discountType;
	}

	public LocalDate getDiscountStartDate() {
		return discountStartDate;
	}

	public void setDiscountStartDate(LocalDate discountStartDate) {
		this.discountStartDate = discountStartDate;
	}

	public BigDecimal getDiscountRate() {
		return discountRate;
	}

	public void setDiscountRate(BigDecimal discountRate) {
		this.discountRate = discountRate;
	}

	public Collection<MCodeData> getDiscounTypeData() {
		return discounTypeData;
	}

	public void setDiscounTypeData(Collection<MCodeData> discounTypeData) {
		this.discounTypeData = discounTypeData;
	}

	public List<PeriodData> getContractTypedata() {
		return contractTypedata;
	}

	public void setContractTypedata(List<PeriodData> contractTypedata) {
		this.contractTypedata = contractTypedata;
	}

	public void setId(Long id) {
		this.id = id;
	}


	
}

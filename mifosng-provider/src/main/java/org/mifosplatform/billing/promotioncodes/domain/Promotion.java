package org.mifosplatform.billing.promotioncodes.domain;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "b_promotion_master")
public class Promotion extends AbstractPersistable<Long>{


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

	@Column(name="start_date")
	private Date startDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "valid_until")
	private Date validuntil;

	@Column(name = "is_delete")
	private char isDeleted;


	 public Promotion() {
		// TODO Auto-generated constructor stub
			
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


	public Date getStartDate() {
		return startDate;
	}


	public Date getValiduntil() {
		return validuntil;
	}


	public char getIsDeleted() {
		return isDeleted;
	}
 
	
	

}

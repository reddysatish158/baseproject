package org.mifosplatform.organisation.randomgenerator.domain;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.springframework.data.jpa.domain.AbstractPersistable;

@SuppressWarnings("serial")
@Entity
@Table(name = "b_pin_master", uniqueConstraints = @UniqueConstraint(name = "batch_name", columnNames = { "batch_name" }))
public class RandomGenerator extends  AbstractPersistable<Long>  {
	
	
	@Column(name = "batch_name", nullable = false)
	private String batchName;

	@Column(name = "batch_description", nullable = false)
	private String batchDescription;

	@Column(name = "length", nullable = false)
	private Long length;

	@Column(name = "begin_with")
	private String beginWith;

	@Column(name = "pin_category", nullable = false)
	private String pinCategory;

	@Column(name = "quantity", nullable = false)
	private Long quantity;
	
	@Column(name = "serial_no")
	private Long serialNo;

	@Column(name = "pin_type")
	private String pinType;
	
	@Column(name = "pin_value")
	private String pinValue;

	@Column(name = "expiry_date")
	private Date expiryDate;
	
	@Column(name = "is_processed")
	private char isProcessed;
	
	/*@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "randomGenerator", orphanRemoval = true)
	private List<RandomGeneratorDetails> randomGeneratorDetails = new ArrayList<RandomGeneratorDetails>();*/
	
	


public RandomGenerator(){
	
}


	public RandomGenerator(String batchName, String batchDescription,
		Long length, String beginWith, String pinCategory, Long quantity,
		Long serialNo, String pinType, String pinValue, Date date) {	
	
		this.batchName=batchName;
		this.batchDescription=batchDescription;
		this.length=length;
		this.beginWith=beginWith;
		this.pinCategory=pinCategory;
		this.quantity=quantity;
		this.serialNo=serialNo;
		this.pinType=pinType;
		this.pinValue=pinValue;
		this.expiryDate=date;
		this.isProcessed='N';
		
}


	public static RandomGenerator fromJson(JsonCommand command) throws ParseException {
		  final String batchName = command.stringValueOfParameterNamed("batchName");
		    final String batchDescription = command.stringValueOfParameterNamed("batchDescription");
		    final BigDecimal length = command.bigDecimalValueOfParameterNamed("length");
		    final String beginWith = command.stringValueOfParameterNamed("beginWith");
		    final String pinCategory = command.stringValueOfParameterNamed("pinCategory");
		    final BigDecimal quantity = command.bigDecimalValueOfParameterNamed("quantity");
		    final BigDecimal serialNo = command.bigDecimalValueOfParameterNamed("serialNo");
		    final String pinType = command.stringValueOfParameterNamed("pinType");
		    final Long pinVal = command.longValueOfParameterNamed("pinValue");
		   // final String pinExtention = command.stringValueOfParameterNamed("pinExtention");
		    final LocalDate expiryDate = command.localDateValueOfParameterNamed("expiryDate");
		    String pinValue=pinVal.toString();//+" "+pinExtention;
		return new RandomGenerator(batchName,batchDescription,length.longValue(),beginWith,pinCategory,quantity.longValue(),serialNo.longValue(),pinType,pinValue,expiryDate.toDate());
	}
	
	/*public List<RandomGeneratorDetails> getRandomGeneratorDetails() {
		return randomGeneratorDetails;
	}


	public void add(RandomGeneratorDetails randomGeneratorDetails) {
		randomGeneratorDetails.update(this);
		this.randomGeneratorDetails.add(randomGeneratorDetails);	
	}*/


	public String getBatchName() {
		return batchName;
	}


	public String getBatchDescription() {
		return batchDescription;
	}


	public Long getLength() {
		return length;
	}


	public String getBeginWith() {
		return beginWith;
	}


	public String getPinCategory() {
		return pinCategory;
	}


	public Long getQuantity() {
		return quantity;
	}


	public Long getSerialNo() {
		return serialNo;
	}


	public String getPinType() {
		return pinType;
	}


	public String getPinValue() {
		return pinValue;
	}


	public Date getExpiryDate() {
		return expiryDate;
	}


	public char getIsProcessed() {
		return isProcessed;
	}


	public void setIsProcessed(char isProcessed) {
		this.isProcessed = isProcessed;
	}
	
	
	
	

}

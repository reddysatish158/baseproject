package org.mifosplatform.organisation.randomgenerator.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "b_pin_details"/*, uniqueConstraints = @UniqueConstraint(name = "serial_no_key", columnNames = { "serial_no" })*/)
public class RandomGeneratorDetails extends AbstractPersistable<Long> {
	
	@ManyToOne
    @JoinColumn(name="pin_id", nullable = false)
    private RandomGenerator randomGenerator;
	
	@Column(name = "pin_no")
	private String pinNo;
	
	@Column(name = "serial_no", nullable = false)
	private Long serialNo;
	
	@Column(name = "client_id", nullable = true)
	private Long clientId;
	
	
	public RandomGeneratorDetails(){
		
	}

	public RandomGeneratorDetails(String name, Long no, RandomGenerator randomGenerator) {
		// TODO Auto-generated constructor stub
		this.pinNo=name;
		this.serialNo=no;
		this.randomGenerator=randomGenerator;
	}

	/*public void update(RandomGenerator randomGenerator) {
		this.randomGenerator=randomGenerator;
	}*/

	public RandomGenerator getRandomGenerator() {
		return randomGenerator;
	}

	public String getPinNo() {
		return pinNo;
	}

	public Long getSerialNo() {
		return serialNo;
	}

	public Long getClientId() {
		return clientId;
	}

	public void setRandomGenerator(RandomGenerator randomGenerator) {
		this.randomGenerator = randomGenerator;
	}

	public void setPinNo(String pinNo) {
		this.pinNo = pinNo;
	}

	public void setSerialNo(Long serialNo) {
		this.serialNo = serialNo;
	}

	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}

	
	

}

package org.mifosplatform.finance.clientbalance.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.mifosplatform.infrastructure.core.api.JsonCommand;

@Entity
@Table(name = "b_client_balance")
public class ClientBalance {

	@Id
	@GeneratedValue
	@Column(name="id")
	private Long id;

	@Column(name = "client_id", nullable = false, length = 20)
	private Long clientId;

	@Column(name = "balance_amount", nullable = false, length = 20)
	private BigDecimal balanceAmount;



	public static ClientBalance create(Long clientId,
			BigDecimal balanceAmount) {
		return new ClientBalance(clientId, balanceAmount);
	}

	public ClientBalance(Long clientId, BigDecimal balanceAmount) {

		this.clientId = clientId;
		this.balanceAmount = balanceAmount;
	}

	public ClientBalance(Long id,Long clientId, BigDecimal balanceAmount,BigDecimal dueAmount) {
		this.id=id;
		this.clientId=clientId;
		this.balanceAmount = balanceAmount;

	}



public ClientBalance()
{

}
	public Long getClientId() {
		return clientId;
	}

	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}

	public BigDecimal getBalanceAmount() {
		return balanceAmount;
	}

	public Long getId() {
		return id;
	}

	public void setBalanceAmount(BigDecimal balanceAmount) {
		this.balanceAmount = balanceAmount;
	}

	public void updateClient(Long clientId){
		this.clientId = clientId;
	}



	public void updateDueAmount(BigDecimal dueAmount) {


	}

	public static ClientBalance fromJson(JsonCommand command) {
		
	    final Long clientId= command.longValueOfParameterNamed("clientId");
	    final BigDecimal balance = command.bigDecimalValueOfParameterNamed("balance");
	    return new ClientBalance(clientId,balance);
	}



}

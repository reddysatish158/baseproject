package org.mifosplatform.portfolio.client.domain;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.mifosplatform.infrastructure.codes.domain.CodeValue;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.domain.AbstractAuditableCustom;
import org.mifosplatform.useradministration.domain.AppUser;

@SuppressWarnings("serial")
@Entity
@Table(name = "m_client_card_details")
public class ClientCardDetails extends AbstractAuditableCustom<AppUser, Long> {

	 private final static String CREDIT_CARD="CreditCard";
	 private final static String ACH_CARD="ACH";
	 
	 @SuppressWarnings("unused")
	 @ManyToOne
	 @JoinColumn(name = "client_id", nullable = false)
	 private Client client;

	 @Column(name = "name", nullable = false)
	 private String cardName;
	 
	 @Column(name = "card_number", nullable = false)
	 private String cardNumber;
	 
	 @Column(name = "aba_routing_number", nullable = false)
	 private String routingNumber;
	 
	 @Column(name = "bank_account_number", nullable = true)
	 private String bankAccountNumber;
	 
	 @Column(name = "bank_name", nullable = true)
	 private String bankName;
	 
	 @Column(name = "account_type", nullable = true)
	 private String accountType;

	 @Column(name = "card_expiry_date", nullable = true)
	 private String expiryDate;
	 
	 @Column(name = "card_type", nullable = false)
	 private String cardType;
	 
	 @Column(name = "is_active", nullable = false)
	 private char isActive;

	 public ClientCardDetails(){
		 
	 }
	
	
	public ClientCardDetails(String cardName, String cardNumber,
			String cardExpiryDate, String cardType) {
		
		this.cardName=cardName;
		this.cardNumber=cardNumber;
		this.expiryDate=cardExpiryDate;
		this.cardType=cardType;
		this.isActive='N';
		
	}



	public ClientCardDetails(String cardName, String routingNumber,
			String bankAccountNumber, String bankName, String accountType, String cardType) {
		this.isActive='N';
		this.cardName=cardName;
		this.routingNumber=routingNumber;
		this.bankAccountNumber=bankAccountNumber;
		this.bankName=bankName;
		this.accountType=accountType;
		this.cardType=cardType;
		
	}



	public static ClientCardDetails fromJson(JsonCommand command) {
		
		String  cardType=command.stringValueOfParameterNamed("cardType");
		String cardName=command.stringValueOfParameterNamed("name");	
		
		ClientCardDetails clientCardDetails = null;
	    if(cardType.equalsIgnoreCase(CREDIT_CARD)){
	    	
	    	String cardNumber=command.stringValueOfParameterNamed("cardNumber");
	    	String  cardExpiryDate=command.stringValueOfParameterNamed("cardExpiryDate");
	    	clientCardDetails = new ClientCardDetails(cardName,cardNumber,cardExpiryDate,cardType);
	    	
		}else if(cardType.equalsIgnoreCase(ACH_CARD)){
			
			String routingNumber=command.stringValueOfParameterNamed("routingNumber");
			String bankAccountNumber=command.stringValueOfParameterNamed("bankAccountNumber");			
			String bankName=command.stringValueOfParameterNamed("bankName");			
			String accountType=command.stringValueOfParameterNamed("accountType");	
			clientCardDetails = new ClientCardDetails(cardName,routingNumber,bankAccountNumber,bankName,accountType,cardType);			
		}
	    
		return clientCardDetails;
	}

	public Map<String, Object> update(JsonCommand command) {
		
		final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(1);
		
		final String cardName = "name";
		if (command.isChangeInStringParameterNamed(cardName,this.cardName)) {
			final String newValue = command.stringValueOfParameterNamed("name");
			actualChanges.put(cardName, newValue);
			this.cardName = StringUtils.defaultIfEmpty(newValue, null);
		}
		
		final String cardType1 = "cardType";
		if (command.isChangeInStringParameterNamed(cardType1,this.cardType)) {
			final String newValue = command.stringValueOfParameterNamed("cardType");
			actualChanges.put(cardType1, newValue);
			this.cardType = StringUtils.defaultIfEmpty(newValue, null);
		}
		
	    if(this.cardType.equalsIgnoreCase(CREDIT_CARD)){	    
	    	
	    	final String cardNumber = "cardNumber";
			if (command.isChangeInStringParameterNamed(cardNumber,this.cardNumber)) {
				final String newValue = command.stringValueOfParameterNamed("cardNumber");
				actualChanges.put(cardNumber, newValue);
				this.cardNumber = StringUtils.defaultIfEmpty(newValue, null);
			}
			
			final String cardExpiryDate = "cardExpiryDate";
			if (command.isChangeInStringParameterNamed(cardExpiryDate,this.expiryDate)) {
				final String newValue = command.stringValueOfParameterNamed("cardExpiryDate");
				actualChanges.put(cardExpiryDate, newValue);
				this.expiryDate = StringUtils.defaultIfEmpty(newValue, null);
			}
		}else if(this.cardType.equalsIgnoreCase(ACH_CARD)){					
			
			final String routingNumber = "routingNumber";
			if (command.isChangeInStringParameterNamed(routingNumber,this.routingNumber)) {
				final String newValue = command.stringValueOfParameterNamed("routingNumber");
				actualChanges.put(routingNumber, newValue);
				this.routingNumber = StringUtils.defaultIfEmpty(newValue, null);
			}
			
			final String bankAccountNumber = "bankAccountNumber";
			if (command.isChangeInStringParameterNamed(bankAccountNumber,this.bankAccountNumber)) {
				final String newValue = command.stringValueOfParameterNamed("bankAccountNumber");
				actualChanges.put(bankAccountNumber, newValue);
				this.bankAccountNumber = StringUtils.defaultIfEmpty(newValue, null);
			}
			
			final String bankName = "bankName";
			if (command.isChangeInStringParameterNamed(bankName,this.bankName)) {
				final String newValue = command.stringValueOfParameterNamed("bankName");
				actualChanges.put(bankName, newValue);
				this.bankName = StringUtils.defaultIfEmpty(newValue, null);
			}
			
			final String accountType = "accountType";
			if (command.isChangeInStringParameterNamed(accountType,this.accountType)) {
				final String newValue = command.stringValueOfParameterNamed("accountType");
				actualChanges.put(accountType, newValue);
				this.accountType = StringUtils.defaultIfEmpty(newValue, null);
			}
			
		}
		return actualChanges;
	}


	public char getIsActive() {
		return isActive;
	}


	public void setIsActive(char isActive) {
		this.isActive = isActive;
	}
	
	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	
	
	

}

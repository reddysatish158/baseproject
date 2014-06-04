package org.mifosplatform.portfolio.client.data;

public class ClientCardDetailsData {

	private Long id;
	private Long clientId;
	private String type;
	private String name;	
	private String cardNumber;
	private String routingNumber;
	private String bankName;
	private String accountType;
	private String cardExpiryDate;
	private String bankAccountNumber;
	private String cardType;
	private String cvvNumber;
	
	
	public ClientCardDetailsData(Long id, Long clientId, String name, String cardNumber, String routingNumber,String bankName,
			String accountType, String cardExpiryDate, String bankAccountNumber, String cardType, String type, String cvvNumber) {
		
		this.id=id;
		this.clientId=clientId;
		this.type=type;
		this.name=name;
		this.cardNumber=cardNumber;
		this.routingNumber=routingNumber;
		this.bankName=bankName;
		this.accountType=accountType;
		this.cardExpiryDate=cardExpiryDate;
		this.bankAccountNumber=bankAccountNumber;
		this.cardType=cardType;
		this.cvvNumber=cvvNumber;
		
	}


	public Long getId() {
		return id;
	}


	public Long getClientId() {
		return clientId;
	}


	public String getType() {
		return type;
	}


	public String getName() {
		return name;
	}


	public String getCardNumber() {
		return cardNumber;
	}


	public String getRoutingNumber() {
		return routingNumber;
	}


	public String getBankName() {
		return bankName;
	}


	public String getAccountType() {
		return accountType;
	}


	public String getCardExpiryDate() {
		return cardExpiryDate;
	}


	public String getBankAccountNumber() {
		return bankAccountNumber;
	}


	public String getCardType() {
		return cardType;
	}


	public String getCvvNumber() {
		return cvvNumber;
	}
	
	

}

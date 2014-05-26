package org.mifosplatform.portfolio.client.data;

public class ClientCardDetailsData {

	private Long id;
	private Long clientId;
	private String cardType;
	private String name;	
	private String cardNumber;
	private String routingNumber;
	private String bankName;
	private String accountType;
	private String cardExpiryDate;
	private String bankAccountNumber;
	
	
	public ClientCardDetailsData(Long id, Long clientId, String name, String cardNumber, String routingNumber,
			String bankName,String accountType, String cardExpiryDate, String bankAccountNumber, String cardType) {
		
		this.id=id;
		this.clientId=clientId;
		this.cardType=cardType;
		this.name=name;
		this.cardNumber=cardNumber;
		this.routingNumber=routingNumber;
		this.bankName=bankName;
		this.accountType=accountType;
		this.cardExpiryDate=cardExpiryDate;
		this.bankAccountNumber=bankAccountNumber;
		
	}

}

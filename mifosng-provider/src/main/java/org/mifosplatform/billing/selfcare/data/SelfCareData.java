package org.mifosplatform.billing.selfcare.data;

import java.util.List;

import org.mifosplatform.crm.ticketmaster.data.TicketMasterData;
import org.mifosplatform.finance.clientbalance.data.ClientBalanceData;
import org.mifosplatform.finance.financialtransaction.data.FinancialTransactionsData;
import org.mifosplatform.finance.payments.data.PaymentData;
import org.mifosplatform.infrastructure.configuration.domain.GlobalConfigurationProperty;
import org.mifosplatform.organisation.address.data.AddressData;
import org.mifosplatform.portfolio.client.data.ClientData;
import org.mifosplatform.portfolio.order.data.OrderData;


public class SelfCareData {

private Long clientId;
	
	private String userName;
	private String password;
	private String uniqueReference;
	private Boolean isDeleted;
	private String email;
	private ClientData clientData;
	private ClientBalanceData clientBalanceData;
	private List<AddressData> addressData;
	private List<OrderData> clientOrdersData;
	private List<FinancialTransactionsData> statementsData;
	private List<PaymentData> paymentsData;
	private List<TicketMasterData> ticketMastersData;
	private GlobalConfigurationProperty paypalConfigData;

	public SelfCareData(Long clientId, String email) {
		this.clientId = clientId;
		this.email = email;
	}

	public SelfCareData() {
		// TODO Auto-generated constructor stub
	}



	public Long getClientId() {
		return clientId;
	}


	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}


	public String getUserName() {
		return userName;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public String getUniqueReference() {
		return uniqueReference;
	}


	public void setUniqueReference(String uniqueReference) {
		this.uniqueReference = uniqueReference;
	}


	public Boolean getIsDeleted() {
		return isDeleted;
	}


	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}



	public String getEmail() {
		return email;
	}



	public void setEmail(String email) {
		this.email = email;
	}



	public void setDetails(ClientData clientsData,
			ClientBalanceData balanceData, List<AddressData> addressData,
			List<OrderData> clientOrdersData,
			List<FinancialTransactionsData> statementsData,
			List<PaymentData> paymentsData,
			List<TicketMasterData> ticketMastersData) {
		
		
		this.clientData = clientsData;
		this.clientBalanceData = balanceData;
		this.addressData = addressData;
		this.clientOrdersData = clientOrdersData;
		this.statementsData = statementsData;
		this.paymentsData = paymentsData;
		this.ticketMastersData = ticketMastersData;
		
	}



	public GlobalConfigurationProperty getPaypalConfigData() {
		return paypalConfigData;
	}



	public void setPaypalConfigData(GlobalConfigurationProperty paypalConfigData) {
		this.paypalConfigData = paypalConfigData;
	}
	
	

}

package org.mifosplatform.portfolio.order.data;

import java.util.Collection;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.billing.paymode.data.McodeData;
import org.mifosplatform.billing.payterms.data.PaytermData;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;
import org.mifosplatform.portfolio.contract.data.SubscriptionData;
import org.mifosplatform.portfolio.plan.data.PlanCodeData;

public class OrderData {
	private Long id;
	private Long pdid;
	private Long orderPriceId;
	private Long clientId;
	private String service_code;
	private String planCode;
	private String planDescription;
	private String chargeCode;
	private double price;
	private String variant;
	private String status;
	private Long period;
	private LocalDate startDate;
	private LocalDate currentDate;
	private LocalDate endDate;
	private String billingFrequency;
	private List<PlanCodeData> plandata;
	private List<PaytermData> paytermdata;
	private List<SubscriptionData> subscriptiondata;
	private List<OrderPriceData> orderPriceData;
	private LocalDate activeDate;
	private String contractPeriod;
//	private boolean flag;
	private Collection<McodeData> disconnectDetails;
	private List<OrderHistoryData> orderHistory;
	private String isPrepaid;
	private String allowtopup;
	private List<OrderData> clientOrders;
	private String userAction;
	private String orderNo;
	private OrderData orderData;
	private String provisioningSys;
	private boolean ispaymentEnable;
	private Collection<McodeData> paymodes;
	private List<OrderLineData> orderServices;
	private List<OrderDiscountData> orderDiscountDatas;
	private LocalDate invoiceTilldate;
	private Collection<MCodeData> extensionReasonDatas;
	private Collection<MCodeData> extensionPeriodDatas;

	public OrderData(List<PlanCodeData> allowedtypes,List<PaytermData> paytermData,
			List<SubscriptionData> contractPeriod, OrderData data) {

		if (data != null) {
			
			this.id = data.getId();
			this.pdid = data.getPdid();
			this.planCode = data.getPlan_code();
			this.status = null;
			this.period = data.getPeriod();
			this.orderPriceId = data.getOrderPriceId();
			this.service_code = null;
			this.startDate = data.getStartDate();
		}
		this.startDate=new LocalDate();
		this.variant = null;
		this.chargeCode = null;
		this.paytermdata = paytermData;
		this.plandata = allowedtypes;
		this.subscriptiondata = contractPeriod;

	}

	public OrderData(Long id, Long planId, String plancode, String status,LocalDate startDate, LocalDate endDate,
			double price,String contractPeriod, String isprepaid, String allowtopup,String userAction,
			String provisioningSys, String orderNo, LocalDate invoiceTillDate, LocalDate activaDate) {
		this.id = id;
		this.pdid = planId;
		this.planCode = plancode;
		this.status = status;
		this.period = null;
		this.startDate = startDate;
		this.currentDate = new LocalDate();
		this.endDate = endDate;
		this.orderPriceId = null;
		this.service_code = null;
		this.price = price;
		this.variant = null;
		this.chargeCode = null;
		this.paytermdata = null;
		this.plandata = null;
		this.subscriptiondata = null;
		this.contractPeriod = contractPeriod;
		this.isPrepaid=isprepaid;
		this.allowtopup=allowtopup;
		this.userAction=userAction;
        this.provisioningSys=provisioningSys;
        this.orderNo=orderNo;
        this.invoiceTilldate=invoiceTillDate;
		this.activeDate=activaDate;

	}

	public OrderData(List<OrderPriceData> priceDatas, List<OrderHistoryData> historyDatas, OrderData orderDetailsData,
			  List<OrderLineData> services, List<OrderDiscountData> discountDatas) {
		this.orderPriceData = priceDatas;
		this.orderHistory=historyDatas;
		this.orderData=orderDetailsData;
		this.orderServices=services;
		this.orderDiscountDatas=discountDatas;
       
	}

	public OrderData(Collection<McodeData> disconnectDetails,List<SubscriptionData> subscriptionDatas, boolean isEnabled) {
		this.disconnectDetails=disconnectDetails;
		this.subscriptiondata=subscriptionDatas;
		 this.ispaymentEnable=isEnabled;
	}

	public OrderData(Long clientId, List<OrderData> clientOrders) {
		this.clientId=clientId;
		this.clientOrders=clientOrders;
	}

	public OrderData(Long orderId, String planCode, String planDescription,
			String billingFreq, String contractPeriod, Double price, LocalDate endDate) {
                 
		            this.id=orderId;
		            this.planCode=planCode;
		            this.planDescription=planDescription;
		            this.billingFrequency=billingFreq;
		            this.contractPeriod=contractPeriod;
		            this.price=price;
		            this.endDate=endDate;
		           
	}
	
	public OrderData(Collection<MCodeData> extensionPeriodDatas,
			Collection<MCodeData> extensionReasonDatas) {
		
		this.extensionPeriodDatas=extensionPeriodDatas;
		this.extensionReasonDatas=extensionReasonDatas;
	}
	
	public Long getId() {
		return id;
	}

	public Long getPdid() {
		return pdid;
	}

	public Long getClientId() {
		return clientId;
	}

	public String getBillingFrequency() {
		return billingFrequency;
	}

	public List<OrderPriceData> getOrderPriceData() {
		return orderPriceData;
	}

	public Long getOrderPriceId() {
		return orderPriceId;
	}

	public String getService_code() {
		return service_code;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public String getStatus() {
		return status;
	}

	public Long getPeriod() {
		return period;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public String getPlan_code() {
		return planCode;
	}

	public double getPrice() {
		return price;
	}

	public String getVariant() {
		return variant;
	}

	public String getChargeCode() {
		return chargeCode;
	}

	public List<PlanCodeData> getPlandata() {
		return plandata;
	}

	public List<PaytermData> getPaytermdata() {
		return paytermdata;
	}

	public List<SubscriptionData> getSubscriptiondata() {
		return subscriptiondata;
	}

	public void setPaytermData(List<PaytermData> data) {
		this.paytermdata = data;
	}

	public void setDisconnectDetails(Collection<McodeData> disconnectDetails) {
	this.disconnectDetails=disconnectDetails;
		
	}

	public void setDuration(String duration) {
		this.contractPeriod=duration;
		
	}

	public void setplanType(String planType) {
	this.isPrepaid=planType;
		
	}

	public void setPaymodeData(Collection<McodeData> data) {
		this.paymodes=data;
		
	}
	
}

package org.mifosplatform.portfolio.order.domain;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.domain.AbstractAuditableCustom;
import org.mifosplatform.portfolio.order.data.OrderStatusEnumaration;
import org.mifosplatform.portfolio.plan.domain.StatusTypeEnum;
import org.mifosplatform.useradministration.domain.AppUser;

@Entity
@Table(name = "b_orders")
public class Order extends AbstractAuditableCustom<AppUser, Long> {

	

	@Column(name = "client_id")
	private Long clientId;

	@Column(name = "plan_id")
	private Long planId;

	@Column(name = "order_status")
	private Long status;

	@Column(name = "transaction_type")
	private String transactionType;

	@Column(name = "billing_frequency")
	private String billingFrequency;

	@Column(name = "next_billable_day")
	private Date nextBillableDay;

	@Column(name = "start_date")
	private Date startDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "end_date")
	private Date endDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "active_date")
	private Date activeDate;

	@Column(name = "contract_period")
	private Long contarctPeriod;

	@Column(name = "is_deleted")
	private char isDeleted;

	@Column(name = "billing_align")
	private char billingAlign;
	
	@Column(name ="disconnect_reason")
	private String disconnectReason;
	
	
	@Column(name ="user_action")
	private String userAction;
	
	@Column(name ="order_no")
	private String orderNo;

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "orders", orphanRemoval = true)
	private List<OrderLine> services = new ArrayList<OrderLine>();

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "orders", orphanRemoval = true)
	private List<OrderPrice> price = new ArrayList<OrderPrice>();
	
	
	//for OrderDiscount
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "orderId", orphanRemoval = true)
	private List<OrderDiscount> orderDiscount = new ArrayList<OrderDiscount>();

	 public Order() {
		// TODO Auto-generated constructor stub
			
	}
 
	
	public Order(Long client_id, Long plan_id, Long status, Long duration_type,String billingFreq,
			LocalDate startDate, LocalDate endDate, Long contract,
			List<OrderLine> serviceDetails, List<OrderPrice> orderprice,char billalign,String userAction) {
		this.clientId = client_id;
		this.planId = plan_id;
		this.status = status;
		this.transactionType = "Add Order";
		this.billingFrequency =billingFreq;
		this.startDate = startDate.toDate();
		if (endDate != null)
			this.endDate = endDate.toDate();
		this.services = serviceDetails;
		this.price = orderprice;
		this.contarctPeriod = contract;
		this.billingAlign=billalign;
		this.isDeleted='n';
		this.userAction=userAction;
		this.orderNo="";
		this.activeDate=startDate.toDate();
	}

public Order(Long clientId, Long planId, Long contractPeriod, String paytermCode, char billAlign,LocalDate startdate) {
	    this.clientId=clientId;
	    this.planId=planId;
	    this.contarctPeriod=contractPeriod;
	    this.billingFrequency=paytermCode;
	    this.billingAlign=billAlign;
	    this.startDate=startdate.toDate();
	    this.activeDate=startdate.toDate();
	}
	public Long getClientId() {
		return clientId;
	}

	public Long getPlanId() {
		return planId;
	}

	public Long getStatus() {
		return status;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public String getBillingFrequency() {
		return billingFrequency;
	}


	public Date getNextBillableDay() {
		return nextBillableDay;
	}

	public Date getStartDate() {
		return startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public List<OrderLine> getServices() {
		return services;
	}

	public List<OrderPrice> getPrice() {
		return price;
	}

	public void addServiceDeatils(OrderLine orderDetail) {
		orderDetail.update(this);
		this.services.add(orderDetail);

	}

	public void addOrderDeatils(OrderPrice price) {
		price.update(this);
		this.price.add(price);

	}

	public Long getContarctPeriod() {
		return contarctPeriod;
	}

	public void delete() {
		this.isDeleted = 'y';
        this.endDate=new LocalDate().toDate();
	}

	public void update(JsonCommand command, Long orderStatus) {

		if (this.status != 3) {
			this.endDate = command.localDateValueOfParameterNamed("disconnectionDate").toDate();
			this.disconnectReason=command.stringValueOfParameterNamed("disconnectReason");
			this.status =orderStatus;
		}

	
	}

	public static Order fromJson(Long clientId, JsonCommand command) {
		 final Long planId = command.longValueOfParameterNamed("planCode");
		 final LocalDate startDate=command.localDateValueOfParameterNamed("start_date");
		    final Long contractPeriod = command.longValueOfParameterNamed("contractPeriod");
		    final String paytermCode = command.stringValueOfParameterNamed("paytermCode");
		    final boolean billAlign=command.booleanPrimitiveValueOfParameterNamed("billAlign");
		    char align=billAlign?'y':'n';
		    return new Order(clientId,planId,contractPeriod,paytermCode,align,startDate);
	}

	public char getbillAlign() {
		// TODO Auto-generated method stub
		return billingAlign;
	}


	public void setNextBillableDay(Date nextBillableDate) {
		this.nextBillableDay=nextBillableDate;
		
	}


	public void addOrderDisount(OrderDiscount orderDiscount) {

		this.orderDiscount.add(orderDiscount);
	}


	public void setEndDate(LocalDate renewalDate) {
		this.endDate=null;
		if(renewalDate!=null){
		this.endDate=renewalDate.toDate();
		}
	}


	public Date getActiveDate() {
		return activeDate;
	}


	public String getOrderNo() {
		return orderNo;
	}


	public void setStartDate(LocalDate startDate) {

		this.startDate=startDate.toDate();
	}

	public void setStatus(Long statusId) {
		this.status=statusId;
		
	}

	public void setuserAction(String actionType) {
		this.userAction=actionType;
	}
	/*
	 * this method is written for retriving price data and for storing that data in transaction hsitory table
	 * */
	public String getAllPriceAsString(){
		StringBuilder sb = new StringBuilder();
		for(OrderPrice p:getPrice()){
			sb.append(String.format("%.2f", p.getPrice())+", ");
		}
		return sb.toString();
	}
	
	public String getAllServicesAsString(){
		StringBuilder sb = new StringBuilder();
		for(OrderLine ol:services){
			sb.append(ol.getServiceType()+", ");
		}
		return sb.toString();
	}


	public char getIsDeleted() {
		return isDeleted;
	}


	public char getBillingAlign() {
		return billingAlign;
	}


	public String getDisconnectReason() {
		return disconnectReason;
	}


	public String getUserAction() {
		return userAction;
	}


	public List<OrderDiscount> getOrderDiscount() {
		return orderDiscount;
	}


	public void updateOrderNum(String orderNo) {
		this.orderNo=orderNo;
		
	}


	public void updateDisconnectionstate() {
		this.endDate =new Date();
		this.disconnectReason="Change Plan";
		this.isDeleted='Y';
		this.status = OrderStatusEnumaration.OrderStatusType(StatusTypeEnum.DISCONNECTED).getId();
		
	}


	public void setRenewalDate(Date date) {
		this.startDate=date;
		
	}


	public void updateActivationDate(Date activeDate) {
	  this.activeDate=activeDate;	
	 
		
	}
	
	
}

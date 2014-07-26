package org.mifosplatform.crm.ticketmaster.data;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;


public class TicketMasterData {
	
	private  List<TicketMasterData> statusType,masterData;
	private  List<EnumOptionData> priorityType;
    private  List<ProblemsData> problemsDatas;
    private  List<UsersData> usersData;
    private  Collection<MCodeData> sourceData;
    private  Long id;
    private String priority;
    private String status;
    private String assignedTo;
    private LocalDate ticketDate;
    private int userId;
    private String lastComment;
    private String problemDescription;
    private String userName;
    private Integer statusCode;
    private String statusDescription;
	private LocalDate createdDate;
	private String attachedFile;
	private String sourceOfTicket;
	private Date dueDate;
	private String resolutionDescription;
	
  	public TicketMasterData(List<EnumOptionData> statusType,
			List<EnumOptionData> priorityType) {
		this.priorityType=priorityType;
		this.problemsDatas=null;
		
		
		
	}

	public TicketMasterData(List<TicketMasterData> data, List<ProblemsData> datas, List<UsersData> userData,TicketMasterData masterData,
						List<EnumOptionData> priorityData,Collection<MCodeData> sourceData) {
		this.statusType=data;
		this.problemsDatas=datas;
		this.usersData=userData;
		this.ticketDate=new LocalDate();
		this.priorityType=priorityData;
		this.sourceData=sourceData;
		if(masterData!=null){
		this.assignedTo=masterData.getAssignedTo();
		this.status=masterData.getStatus();
		this.userId=masterData.getUserId();
		
		}
	}

	public TicketMasterData(Long id, String priority, String status,
			Integer assignedTo, LocalDate ticketDate,String lastComment,String problemDescription,
			String userName,String sourceOfTicket,Date dueDate,String description,String resolutionDescription) {
		
		this.id=id;
		this.priority=priority;
		this.status=status;
		this.userId=assignedTo;
		this.ticketDate=ticketDate;
		this.lastComment=lastComment;
		this.problemDescription=problemDescription;
		this.userName=userName;
		this.sourceOfTicket=sourceOfTicket;
		this.dueDate=dueDate;
		this.statusDescription=description;
		this.resolutionDescription=resolutionDescription;
		
	}

	public TicketMasterData( Integer statusCode, String statusDesc) {
	     this.statusCode=statusCode;
	     this.statusDescription=statusDesc;
	 
	}

	public TicketMasterData(Long id, LocalDate createdDate,
			String assignedTo, String description, String fileName) {
		 this.id=id;
		 this.createdDate=createdDate;
		 this.assignedTo=assignedTo;
	     this.attachedFile=fileName;
	     this.statusDescription=description;
		
	}

	public TicketMasterData(String description, List<TicketMasterData> data) {
		this.problemDescription=description;
		this.masterData=data;

	}

	public List<EnumOptionData> getPriorityType() {
		return priorityType;
	}

	public List<ProblemsData> getProblemsDatas() {
		return problemsDatas;
	}

	public List<UsersData> getUsersDatas() {
		return usersData;
	}

	public List<UsersData> getUsersData() {
		return usersData;
	}

	public Long getId() {
		return id;
	}

	public String getPriority() {
		return priority;
	}

	public String getStatus() {
		return status;
	}

	public String getAssignedTo() {
		return assignedTo;
	}

	public LocalDate getTicketDate() {
		return ticketDate;
	}

	public int getUserId() {
		return userId;
	}

	public String getLastComment() {
		return lastComment;
	}

	public String getProblemDescription() {
		return problemDescription;
	}

	public String getUserName() {
		return userName;
	}

	public Integer getStatusCode() {
		return statusCode;
	}

	public String getStatusDescription() {
		return statusDescription;
	}

	public void setStatusData(List<TicketMasterData> Statusdata) {
		
		this.statusType=Statusdata;
	}

	public String getResolutionDescription() {
		return resolutionDescription;
	}

	public void setResolutionDescription(String resolutionDescription) {
		this.resolutionDescription = resolutionDescription;
	}

	

	

}

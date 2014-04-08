package org.mifosplatform.crm.userchat.data;

import java.util.Collection;
import java.util.List;
import org.joda.time.LocalDate;
import org.mifosplatform.useradministration.data.AppUserData;

public class UserChatData {

	private final Long id;
	private final String userName;
	private final String message;
	private final LocalDate messageDate;
	private final String createdByUser;
	private List<UserChatData> userChatDatas;
	private Collection<AppUserData> appUserDatas;
	private final Boolean isRead;
	public UserChatData(Long id, String userName, LocalDate messageDate,
			String message, String createdByUser,Boolean isRead) {
           
		this.id=id;
		this.userName=userName;
		this.message=message;
		this.messageDate=messageDate;
		this.createdByUser=createdByUser;
		this.isRead=isRead;
	}
	public UserChatData(List<UserChatData> userChatDatas,
			Collection<AppUserData> appUserDatas) {
		this.id=null;
		this.userName=null;
		this.message=null;
		this.messageDate=null;
		this.createdByUser=null;
		this.userChatDatas=userChatDatas;
		this.appUserDatas=appUserDatas;
		this.isRead=null;
	}
	public Long getId() {
		return id;
	}
	public String getUserName() {
		return userName;
	}
	public String getMessage() {
		return message;
	}
	public LocalDate getMessageDate() {
		return messageDate;
	}
	public String getCreatedByUser() {
		return createdByUser;
	}

	
}

package org.mifosplatform.crm.userchat.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "b_userchat")
public class UserChat extends AbstractPersistable<Long>{



	@Column(name = "username")
	private String username;

	@Column(name = "message_date")
	private Date messageDate;

	@Column(name = "message")
	private String message;

	@Column(name = "createdby_user")
	private String createdbyUser;

	
	@Column(name = "is_read", nullable = false)
	private char read='N';
	//private Boolean read=false;
	
	@Column(name = "is_deleted", nullable = false)
	private char isDeleted='N';
	

public UserChat(){
	
}
	
	public UserChat(String userName, Date messageDate, String message, String user) {
		  this.username=userName;
		  this.messageDate=messageDate;
		  this.message=message;
		  this.createdbyUser=user;
		
	}

	public void update() {
		
		this.read='Y';
		
	}

	public void delete() {

     this.isDeleted='Y';
		
	}

		
	}

	

	


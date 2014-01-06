package org.mifosplatform.billing.userchat.domain;

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

	@Column(name ="message")
	private String message;

	@Column(name = "createdby_id")
	private Long createdbyId;

		 public UserChat() {
		// TODO Auto-generated constructor stub
			
	}

		public UserChat(String userName, Date messageDate, String message,Long userId) {
		
			 this.username=userName;
			 this.messageDate=messageDate;
			 this.message=message;
			 this.createdbyId=userId;
		}
 
	
	
}

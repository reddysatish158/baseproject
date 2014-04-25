package org.mifosplatform.crm.userchat.service;

import java.util.List;

import org.mifosplatform.crm.userchat.data.UserChatData;

public interface UserChatReadplatformReadService {

	List<UserChatData> getUserChatDetails();
	List<UserChatData> getUserSentMessageDetails();
    Long getUnreadMessages(String username);	

}

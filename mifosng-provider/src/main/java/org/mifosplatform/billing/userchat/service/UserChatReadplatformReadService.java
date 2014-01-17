package org.mifosplatform.billing.userchat.service;

import java.util.List;

import org.mifosplatform.billing.userchat.data.UserChatData;

public interface UserChatReadplatformReadService {

	List<UserChatData> getUserChatDetails();
	List<UserChatData> getUserSentMessageDetails();
    Long getUnreadMessages(String username);	

}

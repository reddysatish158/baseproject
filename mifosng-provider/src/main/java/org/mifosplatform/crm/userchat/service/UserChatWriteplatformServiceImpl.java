package org.mifosplatform.crm.userchat.service;

import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.crm.userchat.data.UserChatData;
import org.mifosplatform.crm.userchat.domain.UserChat;
import org.mifosplatform.crm.userchat.domain.UserChatRepository;
import org.mifosplatform.crm.userchat.serialization.UserChatCommandFromApiJsonDeserializer;
import org.mifosplatform.crm.userchat.service.UserChatWriteplatformService;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserChatWriteplatformServiceImpl implements UserChatWriteplatformService {
	
	private final static Logger logger = LoggerFactory.getLogger(UserChatWriteplatformServiceImpl.class);
	private final PlatformSecurityContext context;
	private final UserChatRepository userChatRepository;
	private final UserChatCommandFromApiJsonDeserializer apiJsonDeserializer;
	private final UserChatReadplatformReadService userChatReadplatformReadService;
	
@Autowired
public UserChatWriteplatformServiceImpl(final PlatformSecurityContext context,final UserChatRepository userChatRepository,
		final UserChatCommandFromApiJsonDeserializer apiJsonDeserializer,final UserChatReadplatformReadService userChatReadplatformReadService){
	
	this.context=context;
	this.userChatRepository=userChatRepository;
	this.apiJsonDeserializer=apiJsonDeserializer;
	this.userChatReadplatformReadService=userChatReadplatformReadService;
}

@Transactional
@Override
public CommandProcessingResult createUserChat(JsonCommand command) {
 
	try{
		this.apiJsonDeserializer.validateForCreate(command.json());
		final String user=this.context.authenticatedUser().getUsername();
		final String userName=command.stringValueOfParameterNamed("userName");
		final String message=command.stringValueOfParameterNamed("message");
		final LocalDate messageDate=command.localDateValueOfParameterNamed("messageDate");
		
		UserChat userChat=new UserChat(userName,messageDate.toDate(),message,user);
		this.userChatRepository.save(userChat);
		
		return new CommandProcessingResult(userChat.getId());
		
	   }catch(DataIntegrityViolationException exception){
		   handleDataIntegrityIssues(command, exception);
           return CommandProcessingResult.empty();
	}
}

private void handleDataIntegrityIssues(JsonCommand command,DataIntegrityViolationException exception) {

    final Throwable realCause = exception.getMostSpecificCause();
    if (realCause.getMessage().contains("username_org")) {
        final String username = command.stringValueOfParameterNamed("username");
        final StringBuilder defaultMessageBuilder = new StringBuilder("User with username ").append(username)
                .append(" already exists.");
        throw new PlatformDataIntegrityException("error.msg.user.duplicate.username", defaultMessageBuilder.toString(), "username",
                username);
    }
    logger.error(exception.getMessage(), exception);
    throw new PlatformDataIntegrityException("error.msg.unknown.data.integrity.issue", "Unknown data integrity issue with resource.");
}

@Transactional
@Override
public CommandProcessingResult updateUserChatMessage(JsonCommand command,Long entityId) {

try{
	
	 this.context.authenticatedUser();
	 final List<UserChatData> userChatDatas = this.userChatReadplatformReadService.getUserChatDetails();
	 /*for(UserChatData userChatData:userChatDatas){
	 UserChat userChat=this.userChatRepository.findOne(userChatData.getId());
     userChat.update();
     this.userChatRepository.save(userChat);
	 }*/
	 UserChat userChat=this.userChatRepository.findOne(entityId);
	 userChat.update();
     return new CommandProcessingResult(entityId);
     
     
}catch(DataIntegrityViolationException exception){
	
    handleDataIntegrityIssues(command, exception);
	return null;
			
}
}

@Override
public CommandProcessingResult deleteUserChatMessage(Long entityId) {

try{
	this.context.authenticatedUser();
	
	UserChat userChat=this.userChatRepository.findOne(entityId);
	userChat.delete();
	
}catch(DataIntegrityViolationException exception){
	
}
	
	return new CommandProcessingResult(entityId);
}

}

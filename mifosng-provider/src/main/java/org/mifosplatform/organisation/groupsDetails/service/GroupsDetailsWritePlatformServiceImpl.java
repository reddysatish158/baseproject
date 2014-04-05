package org.mifosplatform.organisation.groupsDetails.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.groupsDetails.domain.GroupsDetailsRepository;
import org.mifosplatform.organisation.groupsDetails.domain.GroupsDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GroupsDetailsWritePlatformServiceImpl implements GroupsDetailsWritePlatformService {
	
	private static final Logger logger =LoggerFactory.getLogger(GroupsDetailsReadPlatformServiceImpl.class);
	private final PlatformSecurityContext context;
	private final GroupsDetailsRepository groupsDetailsRepository;
	
	@Autowired
	public GroupsDetailsWritePlatformServiceImpl(final PlatformSecurityContext context,final GroupsDetailsRepository groupsDetailsRepository){
		this.context = context;
		this.groupsDetailsRepository = groupsDetailsRepository;
	}
	
	@Transactional
	@Override
	public CommandProcessingResult addGroup(JsonCommand command) {
		
		try{
			this.context.authenticatedUser();
			final GroupsDetails groupsDetails = GroupsDetails.fromJson(command);
			this.groupsDetailsRepository.save(groupsDetails);
			
			return	new CommandProcessingResult(Long.valueOf(groupsDetails.getId()));		
		}catch(DataIntegrityViolationException dve){
			handleCodeDataIntegrityIssues(command,dve);
			return CommandProcessingResult.empty();
		}
	}
	
	private void handleCodeDataIntegrityIssues(JsonCommand command,DataIntegrityViolationException dve){
		logger.error(dve.getMessage(),dve);
	}

}

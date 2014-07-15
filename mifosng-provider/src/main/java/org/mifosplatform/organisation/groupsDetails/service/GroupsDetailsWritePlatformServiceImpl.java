package org.mifosplatform.organisation.groupsDetails.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONObject;

import org.mifosplatform.finance.billingmaster.service.BillMasterWritePlatformService;
import org.mifosplatform.finance.billingmaster.service.BillWritePlatformService;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.groupsDetails.domain.GroupsDetails;
import org.mifosplatform.organisation.groupsDetails.domain.GroupsDetailsRepository;
import org.mifosplatform.organisation.groupsDetails.serialization.GroupsDetailsCommandFromApiJsonDeserializer;
import org.mifosplatform.provisioning.processrequest.service.ProcessRequestWriteplatformService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service
public class GroupsDetailsWritePlatformServiceImpl implements GroupsDetailsWritePlatformService {
	
	private static final Logger logger =LoggerFactory.getLogger(GroupsDetailsReadPlatformServiceImpl.class);
	private final PlatformSecurityContext context;
	private final FromJsonHelper fromJsonHelper;
	private final GroupsDetailsRepository groupsDetailsRepository;
	private final BillMasterWritePlatformService billWritePlatformService;
	private final GroupsDetailsReadPlatformService groupsDetailsReadPlatformService;
	private final ProcessRequestWriteplatformService processRequestWriteplatformService;
	private final GroupsDetailsCommandFromApiJsonDeserializer groupsDetailsCommandFromApiJsonDeserializer;
	
	
	@Autowired
	public GroupsDetailsWritePlatformServiceImpl(final PlatformSecurityContext context,final GroupsDetailsRepository groupsDetailsRepository,
			final FromJsonHelper fromJsonHelper,final ProcessRequestWriteplatformService processRequestWriteplatformService,
			final GroupsDetailsCommandFromApiJsonDeserializer groupsDetailsCommandFromApiJsonDeserializer,
			final GroupsDetailsReadPlatformService detailsReadPlatformService,final BillMasterWritePlatformService  billWritePlatformService){
		
		this.context = context;
		this.fromJsonHelper = fromJsonHelper;
		this.groupsDetailsRepository = groupsDetailsRepository;
		this.groupsDetailsReadPlatformService=detailsReadPlatformService;
		this.processRequestWriteplatformService = processRequestWriteplatformService;
		this.billWritePlatformService=billWritePlatformService;
		this.groupsDetailsCommandFromApiJsonDeserializer = groupsDetailsCommandFromApiJsonDeserializer;
		
		
	}
	
	@Transactional
	@Override
	public CommandProcessingResult addGroup(JsonCommand command) {
		
		try{
			this.context.authenticatedUser();
			this.groupsDetailsCommandFromApiJsonDeserializer.validateForCreate(command.json());
			final GroupsDetails groupsDetails = GroupsDetails.fromJson(command);
			this.groupsDetailsRepository.save(groupsDetails);
			
			return	new CommandProcessingResult(Long.valueOf(groupsDetails.getId()));		
		}catch(DataIntegrityViolationException dve){
			handleCodeDataIntegrityIssues(command,dve);
			return CommandProcessingResult.empty();
		}
	}
	
	@Transactional
	@Override
	public CommandProcessingResult addProvision(JsonCommand command) {
		
		try {
			
			 context.authenticatedUser();
			 this.groupsDetailsCommandFromApiJsonDeserializer.validateForCreateProvision(command.json());
			 JsonObject sentMsgObj = new JsonObject();
			 sentMsgObj.addProperty("groupName", command.stringValueOfParameterNamed("groupName"));
			 sentMsgObj.addProperty("attribute1", command.stringValueOfParameterNamed("attribute1"));
			 sentMsgObj.addProperty("attribute2", command.stringValueOfParameterNamed("attribute2"));
			 sentMsgObj.addProperty("attribute3", command.stringValueOfParameterNamed("attribute3"));
			 sentMsgObj.addProperty("attribute4", command.stringValueOfParameterNamed("attribute4"));
			 
			 JsonCommand sentMsgCommand = new JsonCommand(null, sentMsgObj.toString(), sentMsgObj, fromJsonHelper, null, null, null, null, null, null, null, null, null, null, null);
			 /*String groupName = command.stringValueOfParameterNamed("groupName");
			 String attr1 = command.stringValueOfParameterNamed("attr1");
			 String attr2 = command.stringValueOfParameterNamed("attr2");
			 String attr3 = command.stringValueOfParameterNamed("attr3");
			 String attr4 = command.stringValueOfParameterNamed("attr4");
			 String sentMsg = "{'groupName':'"+groupName+"','attribute1':'"+attr1+"','attribute2':'"+attr2+"'," +
			 				   "'attribute3':'"+attr3+"','attribute4':'"+attr4+"'}";*/
			 
			 JsonObject json = new JsonObject();
			 json.addProperty("clientId", 0);json.addProperty("orderId", 0);
			 json.addProperty("provisioingSystem","packetspan");json.addProperty("requestType", "Add_Group");
			 json.addProperty("locale", "en");json.addProperty("dateFormat","dd MMMM yyyy");
			 json.addProperty("orderlinId", 0);json.addProperty("serviceId",0);
			 json.addProperty("sentMessage", sentMsgCommand.json());json.addProperty("receiveMessage","success");
			 json.addProperty("hardwareId", "");json.addProperty("startDate",new SimpleDateFormat("dd MMMM yyyy").format(new Date()));
			 json.addProperty("endDate", new SimpleDateFormat("dd MMMM yyyy").format(new Date()));json.addProperty("sentDate",new SimpleDateFormat("dd MMMM yyyy").format(new Date()));
			 json.addProperty("receivedStatus","");json.addProperty("receivedDate", new SimpleDateFormat("dd MMMM yyyy").format(new Date()));
			 JsonCommand commd = new JsonCommand(null, json.toString(), json, fromJsonHelper, null, command.entityId(), null, null, null, null, null, null, null, null, null);
			 this.processRequestWriteplatformService.addProcessRequest(commd);
	          
			 GroupsDetails groupsDetails = this.groupsDetailsRepository.findOne(command.entityId());
			 if(groupsDetails != null)
			  groupsDetails.setIsProvision('Y');
			 return new  CommandProcessingResult(command.entityId());
		}catch(DataIntegrityViolationException dve){
			handleCodeDataIntegrityIssues(command,dve);
			return CommandProcessingResult.empty();
		}
	}
	
	private void handleCodeDataIntegrityIssues(JsonCommand command,
			DataIntegrityViolationException dve) {
		 Throwable realCause = dve.getMostSpecificCause();

	        logger.error(dve.getMessage(), dve);
	        throw new PlatformDataIntegrityException("error.msg.cund.unknown.data.integrity.issue",
	                "Unknown data integrity issue with resource: " + realCause.getMessage());
		
	}

	@Override
	public CommandProcessingResult generateStatment(JsonCommand command,Long groupId) {
		
		try{
			this.context.authenticatedUser();
			List<Long> clientIds=this.groupsDetailsReadPlatformService.retrieveclientIdsByGroupId(groupId);
			
				if(clientIds != null && !clientIds.isEmpty()){
					
					for(Long clientId:clientIds){
						 
						try{
						  JSONObject jsonobject=new  JSONObject();
						  jsonobject.put("dueDate",command.localDateValueOfParameterNamed("dueDate"));
						  jsonobject.put("locale", "en");
						  jsonobject.put("dateFormat", "dd MMMM YYYY");
						  jsonobject.put("message","");
						  final JsonElement parsedCommand = this.fromJsonHelper.parse(jsonobject.toString());
						  final JsonCommand jsonCommand = JsonCommand.from(jsonobject.toString(),parsedCommand,this.fromJsonHelper,
			    	        		 "BILLMASTER",clientId,null,null,clientId,null,null,null,null,null,null);
			    		  this.billWritePlatformService.createBillMaster(jsonCommand,command.entityId());
						
						}catch(Exception exception){
							exception.printStackTrace();
						}
						
						}
					
				   
				}
			
			
			
		}catch(DataIntegrityViolationException dve){
			
		}
		return null;
	}

}

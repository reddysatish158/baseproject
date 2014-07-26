package org.mifosplatform.organisation.ippool.service;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.ippool.data.IpGeneration;
import org.mifosplatform.organisation.ippool.data.IpPoolManagementData;
import org.mifosplatform.organisation.ippool.domain.IpPoolManagementDetail;
import org.mifosplatform.organisation.ippool.domain.IpPoolManagementJpaRepository;
import org.mifosplatform.organisation.ippool.exception.IpAddresNotAvailableException;
import org.mifosplatform.organisation.ippool.serialization.IpPoolManagementCommandFromApiJsonDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IpPoolManagementWritePlatformServiceImpl implements IpPoolManagementWritePlatformService{
	
	private final PlatformSecurityContext context; 
	private final IpPoolManagementCommandFromApiJsonDeserializer apiJsonDeserializer;
	private final IpPoolManagementJpaRepository ipPoolManagementJpaRepository;
	private final IpPoolManagementReadPlatformService ipPoolManagementReadPlatformService;

	
	
	@Autowired
	public IpPoolManagementWritePlatformServiceImpl(final PlatformSecurityContext context,
			final IpPoolManagementJpaRepository ipPoolManagementJpaRepository,final IpPoolManagementCommandFromApiJsonDeserializer apiJsonDeserializer,
			final IpPoolManagementReadPlatformService ipPoolManagementReadPlatformService) {	
		
		this.context = context;
		this.apiJsonDeserializer=apiJsonDeserializer;
		this.ipPoolManagementJpaRepository=ipPoolManagementJpaRepository;
		this.ipPoolManagementReadPlatformService=ipPoolManagementReadPlatformService;
		
		
	}

	@Transactional
	@Override
	public CommandProcessingResult createIpPoolManagement(JsonCommand command) {

		
		try {
			context.authenticatedUser();
			this.apiJsonDeserializer.validateForCreate(command.json());
			String ipAddress=command.stringValueOfParameterNamed("ipAddress");
			Long subnet=command.longValueOfParameterNamed("subnet");
			String notes=command.stringValueOfParameterNamed("notes");
			Long type=command.longValueOfParameterNamed("type");

			Map<String,Object> generatedIPPoolID=new HashedMap();
			
			if(subnet !=null){
				
				String ipData=ipAddress+"/"+subnet;
				IpGeneration util=new IpGeneration(ipData,this.ipPoolManagementReadPlatformService);
				String[] data=util.getInfo().getAllAddresses();
				//String[] data=this.ipGeneration.getInfo().getAllAddresses(ipData);
				
				for(int i=0;i<data.length;i++){
					int j=i+1;
					IpPoolManagementDetail ipPoolManagementDetail= new IpPoolManagementDetail(data[i],'I',type,notes,subnet);
					this.ipPoolManagementJpaRepository.save(ipPoolManagementDetail);
					generatedIPPoolID.put(String.valueOf(j), ipPoolManagementDetail.getId());
				}
				
			}else{
				String i="1";
				IpPoolManagementDetail ipPoolManagementDetail= new IpPoolManagementDetail(ipAddress,'I',type,notes,null);

				this.ipPoolManagementJpaRepository.save(ipPoolManagementDetail);
				generatedIPPoolID.put(i, ipPoolManagementDetail.getId());
			}
	
			return new CommandProcessingResultBuilder().with(generatedIPPoolID).build();

			
		}catch(DataIntegrityViolationException dve){
			
				 Throwable realCause = dve.getMostSpecificCause();
			        if (realCause.getMessage().contains("unique_ip")) {
			            final String name = command.stringValueOfParameterNamed("unique_ip");
			            throw new PlatformDataIntegrityException("error.msg.code.duplicate.name", "A code with name '" + name + "' already exists");
			        }else if (realCause.getMessage().contains("ip_address")) {
			        	 final String name = command.stringValueOfParameterNamed("ip_address");
				            throw new PlatformDataIntegrityException("error.msg.code.duplicate.name", "A code with name '" + name + "' already exists");
			        }
			    	return new CommandProcessingResult(new Long(1));
		}
		
	}

	@Transactional
	@Override
	public CommandProcessingResult editIpPoolManagement(JsonCommand command) {
		
		try {
			context.authenticatedUser();
			this.apiJsonDeserializer.validateForUpdate(command.json());
			
			String statusType = command.stringValueOfParameterNamed("statusType");
			String notes = command.stringValueOfParameterNamed("notes");
			String poolDescription = command.stringValueOfParameterNamed("ipPoolDescription");
			Long type = command.longValueOfParameterNamed("type");
			Long subNet = command.longValueOfParameterNamed("subNet");
			
			IpPoolManagementDetail ipPoolManagementDetail = this.ipPoolManagementJpaRepository.findOne(command.entityId());
			ipPoolManagementDetail.setStatus(statusType.trim().charAt(0));
			ipPoolManagementDetail.setNotes(notes);
			ipPoolManagementDetail.setIpPoolDescription(poolDescription);
			ipPoolManagementDetail.setType(type);
			ipPoolManagementDetail.setSubnet(subNet);
			this.ipPoolManagementJpaRepository.save(ipPoolManagementDetail);
			
			return new CommandProcessingResult(command.entityId());
			
		} catch (DataIntegrityViolationException dve) {
			return CommandProcessingResult.empty();
		} 

	}

	@Override
	public CommandProcessingResult updateIpStatus(Long entityId) {
		
		try{
			
			this.context.authenticatedUser();
			IpPoolManagementDetail ipPoolManagementDetail=this.ipPoolManagementJpaRepository.findOne(entityId);
			
			if(ipPoolManagementDetail == null){
				throw  new IpAddresNotAvailableException(entityId.toString());
			}
			  InetAddress inet = InetAddress.getByName(ipPoolManagementDetail.getIpAddress());
			  boolean status = inet.isReachable(5000);
			  if(status){
				  return new CommandProcessingResult("ACTIVE");
			  }else{
				  return new CommandProcessingResult("DeACTIVE");
			  }
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return CommandProcessingResult.empty();
		}
	}

	@Override
	public CommandProcessingResult updateIpDescription(JsonCommand command) {
		
		context.authenticatedUser();
		this.apiJsonDeserializer.validateForUpdateDecription(command.json());
		Map<String,Object> generatedIPPoolID=new HashedMap();
		
		String search = command.stringValueOfParameterNamed("ipAndSubnet");
		IpGeneration ipGeneration = new IpGeneration(search,this.ipPoolManagementReadPlatformService);
		String[] data = ipGeneration.getInfo().getsubnetAddresses();
		//ipGeneration.getInfo().getNetmask();
		//List<IpPoolManagementData> ipPoolManagementDatas=new ArrayList<IpPoolManagementData>();
		String ipPoolDescription=ipGeneration.getInfo().getNetmask();
		
    	for(int i=0;i<data.length;i++){
    		
			int j=i+1;
			IpPoolManagementData ipPoolManagementData=this.ipPoolManagementReadPlatformService.retrieveIdByIpAddress(data[i]);
			
			
			IpPoolManagementDetail ipPoolManagementDetail = this.ipPoolManagementJpaRepository.findOne(ipPoolManagementData.getId());
			ipPoolManagementDetail.setIpPoolDescription(ipPoolDescription); // netMask id
			
			this.ipPoolManagementJpaRepository.save(ipPoolManagementDetail);
			generatedIPPoolID.put(String.valueOf(j), ipPoolManagementDetail.getId());
		}
    	
    	return new CommandProcessingResultBuilder().with(generatedIPPoolID).build();
		//return null;
	}
	
	

}

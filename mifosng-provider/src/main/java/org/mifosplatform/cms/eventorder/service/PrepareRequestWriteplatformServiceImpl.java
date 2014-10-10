package org.mifosplatform.cms.eventorder.service;



import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.order.domain.Order;
import org.mifosplatform.portfolio.plan.domain.Plan;
import org.mifosplatform.provisioning.preparerequest.domain.PrepareRequest;
import org.mifosplatform.provisioning.preparerequest.domain.PrepareRequsetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class PrepareRequestWriteplatformServiceImpl implements PrepareRequestWriteplatformService{
	private final PlatformSecurityContext context;
	private final PrepareRequsetRepository prepareRequsetRepository; 

	@Autowired
	public PrepareRequestWriteplatformServiceImpl(final PlatformSecurityContext context,
			final PrepareRequsetRepository prepareRequsetRepository	) {
		this.context = context;
		this.prepareRequsetRepository=prepareRequsetRepository;

	}

	@Override
	public CommandProcessingResult prepareNewRequest(Order eventOrder,Plan plan,String requestType) {
  
		try{
		//	this.context.authenticatedUser();
			//String requstStatus= SavingStatusEnumaration.interestCompoundingPeriodType(StatusTypeEnum.CONNECT).getValue();
		
			PrepareRequest prepareRequest=new PrepareRequest(eventOrder.getClientId(),eventOrder.getId(),requestType,plan.getProvisionSystem(),'N',"NONE",plan.getPlanCode());
			
			this.prepareRequsetRepository.save(prepareRequest);
			
			return CommandProcessingResult.commandOnlyResult(prepareRequest.getId());
          			
		} catch (DataIntegrityViolationException dve) {
			return CommandProcessingResult.empty();
		
	}
	}

	

}

package org.mifosplatform.provisioning.processrequest.service;

import java.util.List;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.domain.MifosPlatformTenant;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.service.DataSourcePerTenantService;
import org.mifosplatform.infrastructure.core.service.ThreadLocalContextUtil;
import org.mifosplatform.infrastructure.jobs.api.SchedulerJobApiConstants;
import org.mifosplatform.infrastructure.jobs.service.MiddlewareJobConstants;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.infrastructure.security.service.TenantDetailsService;
import org.mifosplatform.portfolio.client.domain.Client;
import org.mifosplatform.portfolio.client.domain.ClientRepository;
import org.mifosplatform.portfolio.client.domain.ClientStatus;
import org.mifosplatform.portfolio.order.data.OrderStatusEnumaration;
import org.mifosplatform.portfolio.order.domain.Order;
import org.mifosplatform.portfolio.order.domain.OrderRepository;
import org.mifosplatform.portfolio.order.service.OrderReadPlatformService;
import org.mifosplatform.portfolio.plan.domain.Plan;
import org.mifosplatform.portfolio.plan.domain.PlanRepository;
import org.mifosplatform.portfolio.plan.domain.StatusTypeEnum;
import org.mifosplatform.portfolio.plan.domain.UserActionStatusTypeEnum;
import org.mifosplatform.provisioning.preparerequest.data.PrepareRequestData;
import org.mifosplatform.provisioning.preparerequest.domain.PrepareRequest;
import org.mifosplatform.provisioning.preparerequest.domain.PrepareRequsetRepository;
import org.mifosplatform.provisioning.preparerequest.service.PrepareRequestReadplatformService;
import org.mifosplatform.provisioning.processrequest.data.ProcessingDetailsData;
import org.mifosplatform.provisioning.processrequest.domain.ProcessRequest;
import org.mifosplatform.provisioning.processrequest.domain.ProcessRequestDetails;
import org.mifosplatform.provisioning.processrequest.domain.ProcessRequestRepository;
import org.mifosplatform.workflow.eventaction.data.ActionDetaislData;
import org.mifosplatform.workflow.eventaction.service.ActionDetailsReadPlatformService;
import org.mifosplatform.workflow.eventaction.service.ActiondetailsWritePlatformService;
import org.mifosplatform.workflow.eventaction.service.EventActionConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;




@Service(value = "processRequestWriteplatformService")
public class ProcessRequestWriteplatformServiceImpl implements ProcessRequestWriteplatformService{

	  private static final Logger logger =LoggerFactory.getLogger(ProcessRequestReadplatformServiceImpl.class);
	  private final PlatformSecurityContext context;
	  private final TenantDetailsService tenantDetailsService;
	  private final DataSourcePerTenantService dataSourcePerTenantService;
	  private final PrepareRequestReadplatformService prepareRequestReadplatformService;
	  private final OrderReadPlatformService orderReadPlatformService;
	  private final OrderRepository orderRepository;
	  private final ProcessRequestRepository processRequestRepository;
	  private final PrepareRequsetRepository prepareRequsetRepository;
	  private final ClientRepository clientRepository;
	  private final PlanRepository planRepository;
	  private final ActionDetailsReadPlatformService actionDetailsReadPlatformService;
	  private final ActiondetailsWritePlatformService actiondetailsWritePlatformService; 
	  

	    @Autowired
	    public ProcessRequestWriteplatformServiceImpl(final DataSourcePerTenantService dataSourcePerTenantService,final TenantDetailsService tenantDetailsService,
	    		final PrepareRequestReadplatformService prepareRequestReadplatformService,final OrderReadPlatformService orderReadPlatformService,
	    		final OrderRepository orderRepository,final ProcessRequestRepository processRequestRepository,final PrepareRequsetRepository prepareRequsetRepository,
	    		final ClientRepository clientRepository,final PlanRepository planRepository,final ActionDetailsReadPlatformService actionDetailsReadPlatformService,
	    		final ActiondetailsWritePlatformService actiondetailsWritePlatformService,final PlatformSecurityContext context) {
	    	
	            this.dataSourcePerTenantService = dataSourcePerTenantService;
	            this.tenantDetailsService = tenantDetailsService;
	            this.prepareRequestReadplatformService=prepareRequestReadplatformService;
	            this.orderReadPlatformService=orderReadPlatformService;
	            this.orderRepository=orderRepository;
	            this.processRequestRepository=processRequestRepository;
	            this.prepareRequsetRepository=prepareRequsetRepository;
	            this.clientRepository=clientRepository;
	            this.planRepository=planRepository;
	            this.actionDetailsReadPlatformService=actionDetailsReadPlatformService;
	            this.actiondetailsWritePlatformService=actiondetailsWritePlatformService;
	            this.context = context;
	             
	    }

	    @Transactional
	    @Override
		public void ProcessingRequestDetails() {
	        
	        final MifosPlatformTenant tenant = this.tenantDetailsService.loadTenantById("default");
	        ThreadLocalContextUtil.setTenant(tenant);
            List<PrepareRequestData> data=this.prepareRequestReadplatformService.retrieveDataForProcessing();

            for(PrepareRequestData requestData:data){
            	
                       //Get the Order details
                     final List<Long> clientOrderIds = this.prepareRequestReadplatformService.retrieveRequestClientOrderDetails(requestData.getClientId());

                     //Processing the request
                     if(clientOrderIds!=null){
                                     this.processingClientDetails(clientOrderIds,requestData);
                                    //Update RequestData
                                     PrepareRequest prepareRequest=this.prepareRequsetRepository.findOne(requestData.getRequestId());
                                     prepareRequest.updateProvisioning('Y');
                                     this.prepareRequsetRepository.save(prepareRequest);
                            }
            }
	    }
                    
		private void processingClientDetails(List<Long> clientOrderIds,PrepareRequestData requestData) {
			
			for(Long orderId:clientOrderIds){

				final MifosPlatformTenant tenant = this.tenantDetailsService.loadTenantById("default");
			        ThreadLocalContextUtil.setTenant(tenant);
			        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSourcePerTenantService.retrieveDataSource());
			}
		}

		@Override
		public void notifyProcessingDetails(ProcessRequest detailsData) {
			try{
				if(detailsData!=null &&!detailsData.getRequestType().equalsIgnoreCase(MiddlewareJobConstants.Terminate)){
					
				 Order order=this.orderRepository.findOne(detailsData.getOrderId());
				 Client client=this.clientRepository.findOne(order.getClientId());
				 
				 if(detailsData.getRequestType().equalsIgnoreCase(UserActionStatusTypeEnum.ACTIVATION.toString())){
					 
					 order.setStatus(OrderStatusEnumaration.OrderStatusType(StatusTypeEnum.ACTIVE).getId());
					 client.setStatus(ClientStatus.ACTIVE.getValue());
					Plan plan=this.planRepository.findOne(order.getPlanId());
					 
					if(plan.isPrepaid() == 'Y'){
							
							List<ActionDetaislData> actionDetaislDatas=this.actionDetailsReadPlatformService.retrieveActionDetails(EventActionConstants.EVENT_ACTIVE_ORDER);
							if(actionDetaislDatas.size() != 0){
							this.actiondetailsWritePlatformService.AddNewActions(actionDetaislDatas,order.getClientId(), order.getId().toString());
							}
					}
					 
				 }else if(detailsData.getRequestType().equalsIgnoreCase(UserActionStatusTypeEnum.DISCONNECTION.toString())){
					 
					 order.setStatus(OrderStatusEnumaration.OrderStatusType(StatusTypeEnum.DISCONNECTED).getId());
					 Long activeOrders=this.orderReadPlatformService.retrieveClientActiveOrderDetails(order.getClientId(), null);
					 if(activeOrders == 0){

						 client.setStatus(ClientStatus.DEACTIVE.getValue());
					 }
					 
				 }else{
					 order.setStatus(OrderStatusEnumaration.OrderStatusType(StatusTypeEnum.ACTIVE).getId());
					 client.setStatus(ClientStatus.ACTIVE.getValue());
					 this.clientRepository.saveAndFlush(client);
				 }
				 this.orderRepository.save(order);
				 this.clientRepository.saveAndFlush(client);
				 
				// ProcessRequest processRequest=this.processRequestRepository.findOne(detailsData.getId());
				 detailsData.setNotify();
				 
				
				
				}/*else if(detailsData !=null){
					// ProcessRequest processRequest=this.processRequestRepository.findOne(detailsData.getId());
					 detailsData.setNotify();
				}*/
				
				 this.processRequestRepository.save(detailsData);
				
			}catch(Exception exception){
				exception.printStackTrace();
			}
			
		}
		
		@Transactional
		@Override
		public CommandProcessingResult addProcessRequest(JsonCommand command){
			
			try{
				this.context.authenticatedUser();
				ProcessRequest processRequest = ProcessRequest.fromJson(command);
				
				ProcessRequestDetails processRequestDetails = ProcessRequestDetails.fromJson(processRequest,command);	
				
				processRequest.add(processRequestDetails);
				
				this.processRequestRepository.save(processRequest);
				
				return	new CommandProcessingResult(Long.valueOf(processRequest.getPrepareRequestId()));
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

		
}

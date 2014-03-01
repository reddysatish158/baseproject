package org.mifosplatform.billing.scheduledjobs;

import java.util.List;

import org.mifosplatform.billing.action.data.ActionDetaislData;
import org.mifosplatform.billing.action.service.ActionDetailsReadPlatformService;
import org.mifosplatform.billing.action.service.ActiondetailsWritePlatformService;
import org.mifosplatform.billing.action.service.EventActionConstants;
import org.mifosplatform.billing.action.service.EventActionReadPlatformService;
import org.mifosplatform.billing.eventactionmapping.service.EventActionMappingReadPlatformService;
import org.mifosplatform.billing.eventorder.domain.PrepareRequest;
import org.mifosplatform.billing.eventorder.domain.PrepareRequsetRepository;
import org.mifosplatform.billing.order.data.OrderStatusEnumaration;
import org.mifosplatform.billing.order.domain.Order;
import org.mifosplatform.billing.order.domain.OrderRepository;
import org.mifosplatform.billing.order.service.OrderReadPlatformService;
import org.mifosplatform.billing.plan.domain.Plan;
import org.mifosplatform.billing.plan.domain.PlanRepository;
import org.mifosplatform.billing.plan.domain.StatusTypeEnum;
import org.mifosplatform.billing.plan.domain.UserActionStatusTypeEnum;
import org.mifosplatform.billing.preparerequest.data.PrepareRequestData;
import org.mifosplatform.billing.preparerequest.service.PrepareRequestReadplatformService;
import org.mifosplatform.billing.processrequest.data.ProcessingDetailsData;
import org.mifosplatform.billing.processrequest.domain.ProcessRequest;
import org.mifosplatform.billing.processrequest.domain.ProcessRequestRepository;
import org.mifosplatform.infrastructure.core.domain.MifosPlatformTenant;
import org.mifosplatform.infrastructure.core.service.DataSourcePerTenantService;
import org.mifosplatform.infrastructure.core.service.ThreadLocalContextUtil;
import org.mifosplatform.infrastructure.security.service.TenantDetailsService;
import org.mifosplatform.portfolio.client.domain.Client;
import org.mifosplatform.portfolio.client.domain.ClientRepository;
import org.mifosplatform.portfolio.client.domain.ClientStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;




@Service(value = "processRequestWriteplatformService")
public class ProcessRequestWriteplatformServiceImpl implements ProcessRequestWriteplatformService{

	
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
	    		final ActiondetailsWritePlatformService actiondetailsWritePlatformService) {
	    	
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
                                     prepareRequest.updateProvisioning();
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
		public void notifyProcessingDetails(ProcessingDetailsData detailsData) {
			try{
				if(detailsData!=null){
					
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
				 
				 ProcessRequest processRequest=this.processRequestRepository.findOne(detailsData.getId());
				 processRequest.setNotify();
				 
				 this.processRequestRepository.save(processRequest);
				}
				
			}catch(Exception exception){
				exception.printStackTrace();
			}
			
		}

		
}

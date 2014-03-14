package org.mifosplatform.billing.preparerequest.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.mifosplatform.billing.allocation.service.AllocationReadPlatformService;
import org.mifosplatform.billing.eventorder.domain.PrepareRequest;
import org.mifosplatform.billing.eventorder.domain.PrepareRequsetRepository;
import org.mifosplatform.billing.onetimesale.data.AllocationDetailsData;
import org.mifosplatform.billing.order.data.OrderStatusEnumaration;
import org.mifosplatform.billing.order.domain.Order;
import org.mifosplatform.billing.order.domain.OrderLine;
import org.mifosplatform.billing.order.domain.OrderRepository;
import org.mifosplatform.billing.plan.domain.StatusTypeEnum;
import org.mifosplatform.billing.preparerequest.data.PrepareRequestData;
import org.mifosplatform.billing.processrequest.domain.ProcessRequest;
import org.mifosplatform.billing.processrequest.domain.ProcessRequestDetails;
import org.mifosplatform.billing.processrequest.domain.ProcessRequestRepository;
import org.mifosplatform.billing.servicemaster.domain.ProvisionServiceDetails;
import org.mifosplatform.billing.servicemaster.domain.ProvisionServiceDetailsRepository;
import org.mifosplatform.billing.transactionhistory.service.TransactionHistoryWritePlatformService;
import org.mifosplatform.infrastructure.core.service.DataSourcePerTenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class PrepareRequestReadplatformServiceImpl  implements PrepareRequestReadplatformService{

	
	  private final DataSourcePerTenantService dataSourcePerTenantService;
	  private final OrderRepository orderRepository;
	  private final ProcessRequestRepository processRequestRepository;
	  private final PrepareRequsetRepository prepareRequsetRepository;
	  private final AllocationReadPlatformService allocationReadPlatformService;
	  private final ProvisionServiceDetailsRepository provisionServiceDetailsRepository;
	  public final static String PROVISIONGSYS_COMVENIENT="Comvenient";
	  private final TransactionHistoryWritePlatformService transactionHistoryWritePlatformService;
	

	    @Autowired
	    public PrepareRequestReadplatformServiceImpl(final DataSourcePerTenantService dataSourcePerTenantService,
	           final OrderRepository orderRepository,final TransactionHistoryWritePlatformService transactionHistoryWritePlatformService,
		   final ProcessRequestRepository processRequestRepository,final AllocationReadPlatformService allocationReadPlatformService,
		   final PrepareRequsetRepository prepareRequsetRepository,final ProvisionServiceDetailsRepository provisionServiceDetailsRepository) {
	            
	    	    this.dataSourcePerTenantService = dataSourcePerTenantService;
	            this.orderRepository=orderRepository;
	            this.processRequestRepository=processRequestRepository;
	            this.prepareRequsetRepository=prepareRequsetRepository;
	            this.allocationReadPlatformService=allocationReadPlatformService;
	            this.provisionServiceDetailsRepository=provisionServiceDetailsRepository;
	            this.transactionHistoryWritePlatformService=transactionHistoryWritePlatformService;
	        
	    }

	
	@Override
	public List<PrepareRequestData> retrieveDataForProcessing() {
		try {
			
			  
	        
	        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSourcePerTenantService.retrieveDataSource());
			final ClientOrderMapper mapper = new ClientOrderMapper();

			final String sql = "select " + mapper.clientOrderLookupSchema();

			return jdbcTemplate.query(sql, mapper, new Object[] { });
			} catch (EmptyResultDataAccessException e) {
			return null;
			}

			}

			private static final class ClientOrderMapper implements RowMapper<PrepareRequestData> {

			public String clientOrderLookupSchema() {
			return " pr.id AS id,pr.client_id AS clientId,pr.order_id AS orderId,pr.provisioning_sys AS provisioningSystem,c.firstname AS userName," +
					"p.is_hw_req AS hwRequired,pw.plan_code AS planName,pr.request_type AS requestType FROM b_prepare_request pr,m_client c,b_hw_plan_mapping pw," +
					" b_orders o,b_association asoc,b_plan_master p  WHERE pr.client_id = c.id  AND o.plan_id = p.id  AND p.plan_code = pw.plan_code  AND pr.order_id = o.id" +
					" AND o.id=asoc.order_id  AND (pr.is_provisioning = 'N' OR pr.status = 'PENDING') group by pr.order_id desc ";

			}

			@Override
			public PrepareRequestData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

			final Long id = rs.getLong("id");
			final Long clientId = rs.getLong("clientId");
			final Long orderId = rs.getLong("orderId");
			final String requestType = rs.getString("requestType");
		    final String planName=rs.getString("planName");	
			
			final String userName=rs.getString("userName");
			final String ishwReq=rs.getString("hwRequired");
			final String provisioningSys=rs.getString("provisioningSystem");
			
			return new PrepareRequestData(id, clientId,orderId, requestType,null,userName,provisioningSys,planName,ishwReq);
			}
			}
			
			@Override
			public List<Long> retrieveRequestClientOrderDetails(Long clientId) {
				try {
				
			        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSourcePerTenantService.retrieveDataSource());
				   OrderIdMapper planIdMapper = new OrderIdMapper();
				   String sql = "select" + planIdMapper.planIdSchema();
				  return jdbcTemplate.query(sql, planIdMapper,new Object[] { clientId });
				
			
			} catch (EmptyResultDataAccessException e) {
				return null;
				}
			}
			private static final class OrderIdMapper implements RowMapper<Long> {

				@Override
				public Long  mapRow(ResultSet resultSet, int rowNum) throws SQLException {
//					Long orderId = resultSet.getLong("orderId");
//					String durationType = resultSet.getString("durationType");
//					Date billStartDate = resultSet.getDate("billStartDate");
					return resultSet.getLong("orderId");
				}
				

				public String planIdSchema() {
					return "  os.id as orderId FROM b_orders os where os.client_id=?";
							
				}
				
				
			}

			@Override
			public void processingClientDetails(PrepareRequestData requestData) {

					String requestType=null;			        
					  Order order=this.orderRepository.findOne(requestData.getOrderId());
					 AllocationDetailsData detailsData=this.allocationReadPlatformService.getTheHardwareItemDetails(requestData.getOrderId());
					 requestType=requestData.getRequestType();
					  SimpleDateFormat ft =new SimpleDateFormat ("hh:mm:ss a");
					  PrepareRequest prepareRequest=this.prepareRequsetRepository.findOne(requestData.getRequestId());
					 if(requestData.getIshardwareReq().equalsIgnoreCase("Y") && detailsData == null){
						 
						    
						      String status=OrderStatusEnumaration.OrderStatusType(StatusTypeEnum.PENDING).getValue().toString();
         	                  prepareRequest.setStatus(status);
	                          this.prepareRequsetRepository.save(prepareRequest);
	                         
	                         //Update Order Status
	                         order.setStatus(OrderStatusEnumaration.OrderStatusType(StatusTypeEnum.PENDING).getId());
	                         this.orderRepository.saveAndFlush(order);
	                       
	                     /*  this.transactionHistoryWritePlatformService.saveTransactionHistory(order.getClientId(),"Provisioning",new Date(),"Order No:"+order.getOrderNo(),
	                        	"Request Type :"+prepareRequest.getRequestType(),	"Request Id :"+prepareRequest.getId(),"Status:Pending","Reason :Hardware is not allocated",ft.format(new Date())); 
						 */
					 }else {

						 ProcessRequest processRequest=new ProcessRequest(order.getClientId(), order.getId(), requestData.getProvisioningSystem(),
								 'N',requestData.getUserName(),requestType,requestData.getRequestId());
					  
					          List<OrderLine> orderLineData=order.getServices();
					          
					       for(OrderLine orderLine:orderLineData){
						    String HardWareId=null;
						  if(detailsData!=null){
							  HardWareId=detailsData.getSerialNo();
						  }
						  
						  ProvisionServiceDetails provisionServiceDetails=this.provisionServiceDetailsRepository.findOneByServiceId(orderLine.getServiceId());
						
						  if(provisionServiceDetails!=null){
							 /* 
                              if(requestData.getRequestType().equalsIgnoreCase(UserActionStatusTypeEnum.DEVICE_SWAP.toString())){
                            	  
                            	   requestType=UserActionStatusTypeEnum.ACTIVATION.toString();
                            		 AllocationDetailsData allocationDetailsData=this.allocationReadPlatformService.getDisconnectedHardwareItemDetails(requestData.getOrderId(),requestData.getClientId());
                            		 ProcessRequestDetails processRequestDetails=new ProcessRequestDetails(orderLine.getId(),orderLine.getServiceId(),provisionServiceDetails.getServiceIdentification(),"Recieved",
                            				 allocationDetailsData.getSerialNo(),order.getStartDate(),order.getEndDate(),null,null,'N',UserActionStatusTypeEnum.DISCONNECTION.toString());
                            		 processRequest.add(processRequestDetails);
                              }*/
                           
						  ProcessRequestDetails processRequestDetails=new ProcessRequestDetails(orderLine.getId(),orderLine.getServiceId(),provisionServiceDetails.getServiceIdentification(),"Recieved",
								  HardWareId,order.getStartDate(),order.getEndDate(),null,null,'N',requestType);
						  processRequest.add(processRequestDetails);
						  
						/*  this.transactionHistoryWritePlatformService.saveTransactionHistory(order.getClientId(),"Provisioning",new Date(),"Order No:"+order.getOrderNo(),
								  "Request Type :"+prepareRequest.getRequestType(), "Request Id :"+prepareRequest.getId(),"Status:Sent For activation",ft.format(new Date())); */
						  }
					  }
	                       this.processRequestRepository.save(processRequest);				
	                       //PrepareRequest prepareRequest=this.prepareRequsetRepository.findOne(requestData.getRequestId());
                           prepareRequest.updateProvisioning();
                           this.prepareRequsetRepository.save(prepareRequest);
                         
                           
				  }
					 if(requestData.getProvisioningSystem().equalsIgnoreCase("None")){
						 order.setStatus(new Long(1));
						 this.orderRepository.save(order);
					 }
	              
				}

			@Override
			public List<Long> getPrepareRequestDetails(Long id) {

				try {
				
			        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSourcePerTenantService.retrieveDataSource());
				   PrepareRequestIdMapper planIdMapper = new PrepareRequestIdMapper();
				   String sql = "select" + planIdMapper.planIdSchema();
				  return jdbcTemplate.query(sql, planIdMapper,new Object[] { id });
				
			
			} catch (EmptyResultDataAccessException e) {
				return null;
				}
			}
			private static final class PrepareRequestIdMapper implements RowMapper<Long> {

				@Override
				public Long  mapRow(ResultSet resultSet, int rowNum) throws SQLException {
//					
					return resultSet.getLong("id");
				}
				

				public String planIdSchema() {
					return " pr.id as id from b_prepare_request pr " +
							" where pr.order_id=? and pr.is_provisioning ='N' and pr.request_type='ACTIVATION'";
							
				}
				
				
			}
				
			
}

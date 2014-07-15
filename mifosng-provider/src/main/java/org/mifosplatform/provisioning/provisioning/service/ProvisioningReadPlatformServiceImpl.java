package org.mifosplatform.provisioning.provisioning.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifosplatform.billing.paymode.data.McodeData;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationConstants;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.dataqueries.data.GenericResultsetData;
import org.mifosplatform.infrastructure.dataqueries.data.ResultsetColumnHeaderData;
import org.mifosplatform.infrastructure.dataqueries.data.ResultsetRowData;
import org.mifosplatform.infrastructure.dataqueries.service.ReadReportingService;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.ippool.data.IpPoolData;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;
import org.mifosplatform.provisioning.provisioning.data.ProcessRequestData;
import org.mifosplatform.provisioning.provisioning.data.ProvisioningCommandParameterData;
import org.mifosplatform.provisioning.provisioning.data.ProvisioningData;
import org.mifosplatform.provisioning.provisioning.data.ServiceParameterData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProvisioningReadPlatformServiceImpl implements ProvisioningReadPlatformService {
	
	   private final JdbcTemplate jdbcTemplate;
	   private final PlatformSecurityContext context;
	   private final ReadReportingService readReportingService;
	   
	   @Autowired
	    public ProvisioningReadPlatformServiceImpl(final PlatformSecurityContext context, final TenantAwareRoutingDataSource dataSource,
	    		final ReadReportingService readReportingService) {
	        
		    this.context = context;
	        this.jdbcTemplate = new JdbcTemplate(dataSource);
	        this.readReportingService=readReportingService;

	    }
	   
	   @Override
		public ProvisioningData retrieveIdData(Long id) {	
			try {
				context.authenticatedUser();
				
				ProvisioningMapper rm = new ProvisioningMapper();
				
				final String sql = "select "+rm.schema()+" and p.id=?";
				 
				return jdbcTemplate.queryForObject(sql, rm, new Object[] {id});
				} catch (EmptyResultDataAccessException e) {
				return null;
				}
		}

	@Override
	public List<ProvisioningData> getProvisioningData() {						
		try {
			
			 ProvisioningMapper rm = new ProvisioningMapper();
			 final String sql = "select "+rm.schema();
			return jdbcTemplate.query(sql, rm, new Object[] {});
			} catch (EmptyResultDataAccessException e) {
			return null;
			}
	}
	 private static final class ProvisioningMapper implements RowMapper<ProvisioningData> {

		    public String schema() {
				return " p.id as id,p.provisioning_system as ProvisioningSystem,p.command_name as CommandName,p.status as status from b_command p where p.is_deleted='N' ";
			}
		    
	        @Override
	        public ProvisioningData mapRow(final ResultSet rs, final int rowNum) throws SQLException {
			  Long id = rs.getLong("id");
			  String ProvisioningSystem=rs.getString("ProvisioningSystem");
			  String CommandName=rs.getString("CommandName");
			  String status=rs.getString("status");
			  return new ProvisioningData(id,ProvisioningSystem,CommandName,status);
	       }
	}
	 
	 @Override
		public List<McodeData> retrieveProvisioningCategory() {
			context.authenticatedUser();

			SystemDataMapper mapper = new SystemDataMapper();

			String sql = "select " + mapper.schema();

			return this.jdbcTemplate.query(sql, mapper, new Object[] { "Provisioning" });
		}

		@Override
		public List<McodeData> retrievecommands() {
			context.authenticatedUser();

			SystemDataMapper mapper = new SystemDataMapper();

			String sql = "select " + mapper.schema();

			return this.jdbcTemplate.query(sql, mapper, new Object[] { "Command" });
		}

		private static final class SystemDataMapper implements RowMapper<McodeData> {

			public String schema() {


				return " mc.id as id,mc.code_value as codeValue from m_code m,m_code_value mc where m.id = mc.code_id and m.code_name=? ";

			}

			@Override
			public McodeData mapRow(ResultSet rs, int rowNum) throws SQLException {
				
				Long id=rs.getLong("id");
				String codeValue = rs.getString("codeValue");
				return new McodeData(id,codeValue);
				
			}

		}

		@Override
		public List<ProvisioningCommandParameterData> retrieveCommandParams(Long id) {
			try {
				
				 ProvisioningCommandMapper rm = new ProvisioningCommandMapper();
				 final String sql = "select "+rm.schema();
				 return jdbcTemplate.query(sql, rm, new Object[] {id});
				} catch (EmptyResultDataAccessException e) {
				return null;
				}
		}

		 private static final class ProvisioningCommandMapper implements RowMapper<ProvisioningCommandParameterData> {

			    public String schema() {
					return " c.id as id, c.command_param as commandParam,c.param_type as paramType from b_command_parameters c where c.command_id=? ";
				}
			    
		        @Override
		        public ProvisioningCommandParameterData mapRow(final ResultSet rs, final int rowNum) throws SQLException {
				  Long id = rs.getLong("id");
				  String commandParam=rs.getString("commandParam");
				  String paramType=rs.getString("paramType");
				  return new ProvisioningCommandParameterData(id,commandParam,paramType);
		       }
		}

		 @Transactional
		 @Override
		public List<ServiceParameterData> getSerivceParameters(Long orderId) {
				
				Map<String, String> queryParams=new HashMap<String, String>();
				queryParams.put("${orderId}", orderId.toString());
				   List<ServiceParameterData> parameterDatas=new ArrayList<ServiceParameterData>();
				final GenericResultsetData resultsetData=this.readReportingService.retrieveGenericResultset("Service", "parameter", queryParams);
				   List<ResultsetRowData> datas = resultsetData.getData();
				   List<String> row;
				    Integer rSize;
				   for (int i = 0; i < datas.size(); i++) {
		               row = datas.get(i).getRow();
		               rSize = row.size();
		               for (int j = 0; j < rSize-1; j++) {

		            	   String  id=datas.get(i).getRow().get(j);
		            	   j++;
		            	   String paramName=datas.get(i).getRow().get(j);
		            	   j++;
		            	   String paramValue=datas.get(i).getRow().get(j);
		            	   j=j++;
		            	   parameterDatas.add(new ServiceParameterData(new Long(id), paramName, paramValue,null));
		               }
		           }
				 /*  for(int i=0;i<columnHeaderDatas.size();i++){
					   for(int j=0;j<datas.size();j++){
						  String id=null;
						  String paramName=null;
						  String paramValue=null;
						   if(columnHeaderDatas.get(i).getColumnName().equalsIgnoreCase("id")){
							 
							  id=datas.get(i).getRow().get(i);
							   
						   }else if(columnHeaderDatas.get(i).getColumnName().equalsIgnoreCase("paramName")){
							   
							   paramName=datas.get(i).getRow().get(i);
						   }else{
							   paramValue=datas.get(i).getRow().get(i);
						   }
						   parameterDatas.add(new ServiceParameterData(new Long(id), paramName, paramValue));
					   }
				   }*/
				   
				   return parameterDatas;
			 
			 /*
			
			try{
				this.context.authenticatedUser();
				ServiceParameterMapper mapper=new ServiceParameterMapper();
				final String sql="select "+mapper.schema();
				return this.jdbcTemplate.query(sql, mapper,new Object[] {orderId});
				
			}catch(EmptyResultDataAccessException exception){
				return null;
			}
			
		*/}
		
		 private static final class ServiceParameterMapper implements RowMapper<ServiceParameterData> {

			    public String schema() {
					return " sd.id as id,sd.service_identification as paramName,sd.image as paramValue FROM b_orders o, b_plan_master p,b_service s," +
							" b_plan_detail pd,b_prov_service_details sd WHERE  o.id= ? AND p.id = o.plan_id AND pd.plan_id = p.id AND " +
							" pd.service_code = s.service_code and sd.service_id=s.id";
				}
			    
			    
			    public String provisionedschema() {
					return "  s.id AS id,s.parameter_name AS paramName,s.parameter_value AS paramValue  FROM b_service_parameters s " +
							"  WHERE s.order_id = ? and status='ACTIVE'";
				}
			    
		        @Override
		        public ServiceParameterData mapRow(final ResultSet rs, final int rowNum) throws SQLException {
		        	
				  Long id = rs.getLong("id");
				  String paramName=rs.getString("paramName");
				  String paramValue=rs.getString("paramValue");
				  return new ServiceParameterData(id,paramName,paramValue,null);
		       }
		}
		 
		 
		 @Transactional
		 @Override
		public List<ServiceParameterData> getProvisionedSerivceParameters(Long orderId) {
			
			try{
				this.context.authenticatedUser();
				ServiceParameterMapper mapper=new ServiceParameterMapper();
				final String sql="select "+mapper.provisionedschema();
				return this.jdbcTemplate.query(sql, mapper,new Object[] {orderId});
				
			}catch(EmptyResultDataAccessException exception){
				return null;
			}
			
		}

	
		@Override
		public Long getHardwareDetails(String oldHardWare, Long clientId,String name) {
			try {
				
				 String sql=null;
				 if(name.equalsIgnoreCase(ConfigurationConstants.CONFIR_PROPERTY_OWN)){
					 
				  sql = "select a.id as id  from b_owned_hardware a where a.serial_number = ? and a.client_id=? and is_deleted = 'N' limit 1";
				 
				 }else{
					 
					 sql = "select i.id as id from  b_item_detail i where i.provisioning_serialno=? and i.client_id=1 limit 1";
				 }
				 return jdbcTemplate.queryForLong(sql, new Object[] {oldHardWare,clientId});
				} catch (EmptyResultDataAccessException e) {
				return null;
				}
		}
		
		@Override
		public List<ProcessRequestData> getProcessRequestData(String orderNo) {
		      
			context.authenticatedUser();

			ProcessRequestMapper mapper = new ProcessRequestMapper();

			String sql = "select " + mapper.schema();

			return this.jdbcTemplate.query(sql, mapper, new Object[] {orderNo});
		}
		
		 private static final class ProcessRequestMapper implements RowMapper<ProcessRequestData> {

			    public String schema() {
					return "  p.id AS id,p.client_id AS clientId,o.id as orderId,o.order_no AS orderNo,p.request_type AS requestType,p.is_processed AS isProcessed," +
							"pr.hardware_id AS hardwareId,pr.receive_message AS receiveMessage,pr.sent_message AS sentMessage  FROM b_process_request p" +
							" INNER JOIN b_process_request_detail pr ON pr.processrequest_id = p.id left join b_orders o on p.order_id=o.id  WHERE   " +
							"  o.order_no =? AND pr.id = (SELECT max(id) FROM b_process_request_detail prd2 WHERE prd2.processrequest_id = pr.processrequest_id)";
				}
			    public String schemaForId() {
			    	
			    	return " p.id as id,p.client_id as clientId, p.order_id as orderId,p.order_id as orderNo,p.request_type as requestType,p.is_processed as isProcessed, " +
							" pr.hardware_id as hardwareId, pr.receive_message as receiveMessage, pr.sent_message as sentMessage " +
							" from b_process_request p inner join b_process_request_detail pr on pr.processrequest_id=p.id where" +
							" p.order_id=? group by p.id ";
			    }
		        @Override
		        public ProcessRequestData mapRow(final ResultSet rs, final int rowNum) throws SQLException {
				  Long id = rs.getLong("id");
				  Long clientId = rs.getLong("clientId");
				  Long orderId = rs.getLong("orderId");
				  String requestType=rs.getString("requestType");
				  String hardwareId=rs.getString("hardwareId");
				  String receiveMessage=rs.getString("receiveMessage");
				  String sentMessage=rs.getString("sentMessage");
				  String orderNo=rs.getString("orderNo");
				  String isProcessed=rs.getString("isProcessed");
				  return new ProcessRequestData(id,clientId,orderId,requestType,hardwareId,receiveMessage,sentMessage,isProcessed,orderNo);
		       }
		}

		@Override
		public ProcessRequestData getProcessRequestIDData(Long id) {
			
			context.authenticatedUser();

			ProcessRequestMapper mapper = new ProcessRequestMapper();

			String sql = "select " + mapper.schemaForId();

			return this.jdbcTemplate.queryForObject(sql, mapper, new Object[] {id});
		}

		@Override
		public Collection<MCodeData> retrieveVlanDetails(String string) {
			
			Map<String, String> queryParams=new HashMap<String, String>();
			   Collection<MCodeData> codeDatas=new ArrayList<MCodeData>();
			   queryParams.put("${codeName}", string);
			final GenericResultsetData resultsetData=this.readReportingService.retrieveGenericResultset("VLAN_ID", "parameter", queryParams);
			   List<ResultsetColumnHeaderData> columnHeaderDatas=resultsetData.getColumnHeaders();
			   List<ResultsetRowData> datas = resultsetData.getData();
			   
			   List<String> row;
			    Integer rSize;
			   for (int i = 0; i < datas.size(); i++) {
	               row = datas.get(i).getRow();
	               rSize = row.size();
	               for (int j = 0; j < rSize-1; j++) {

	            	   String  id=datas.get(i).getRow().get(j);
	            	   j++;
	            	   String paramValue=datas.get(i).getRow().get(j);
	            	   j=j++;
	            	   codeDatas.add(new MCodeData(new Long(id), paramValue));
	               }
	           }
			   
			/*   for(int i=0;i<columnHeaderDatas.size();i++){
				   for(int j=0;j<datas.size();j++){
					   MCodeData codeData=new MCodeData();
					   if(columnHeaderDatas.get(i).getColumnName().equalsIgnoreCase("id")){
						   codeData.setmCodeValue(datas.get(i).getRow().get(i));
					   }else{
						   codeData.setmCodeValue(datas.get(i).getRow().get(i));
					   }
					   codeDatas.add(codeData);
				   }
			   }*/
			   
			   return codeDatas;
		}
}

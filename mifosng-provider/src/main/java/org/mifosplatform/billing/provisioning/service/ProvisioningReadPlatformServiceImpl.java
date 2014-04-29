package org.mifosplatform.billing.provisioning.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.mifosplatform.billing.paymode.data.McodeData;
import org.mifosplatform.billing.provisioning.data.ProcessRequestData;
import org.mifosplatform.billing.provisioning.data.ProvisioningCommandParameterData;
import org.mifosplatform.billing.provisioning.data.ProvisioningData;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationConstants;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class ProvisioningReadPlatformServiceImpl implements ProvisioningReadPlatformService {
	
	   private final JdbcTemplate jdbcTemplate;
	   private final PlatformSecurityContext context;
	   
	   @Autowired
	    public ProvisioningReadPlatformServiceImpl(final PlatformSecurityContext context, final TenantAwareRoutingDataSource dataSource) {
	        this.context = context;
	        this.jdbcTemplate = new JdbcTemplate(dataSource);

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
		public List<ProcessRequestData> getProcessRequestData(Long clientId) {
		      
			context.authenticatedUser();

			ProcessRequestMapper mapper = new ProcessRequestMapper();

			String sql = "select " + mapper.schema();

			return this.jdbcTemplate.query(sql, mapper, new Object[] {clientId});
		}
		
		 private static final class ProcessRequestMapper implements RowMapper<ProcessRequestData> {

			    public String schema() {
					return "  p.id AS id,p.client_id AS clientId,p.order_id AS orderId,p.request_type AS requestType,p.is_processed AS isProcessed, " +
							"pr.hardware_id AS hardwareId,pr.receive_message AS receiveMessage, pr.sent_message AS sentMessage  " +
							" FROM b_process_request p INNER JOIN b_process_request_detail pr ON pr.processrequest_id = p.id WHERE p.client_id = ?" +
							"  and pr.id=(select max(id) from b_process_request_detail prd2 where prd2.processrequest_id = pr.processrequest_id)";
				}
			    
			    public String schemaForId() {
			    	
			    	return " p.id as id,p.client_id as clientId, p.order_id as orderId,p.request_type as requestType,p.is_processed as isProcessed, " +
							" pr.hardware_id as hardwareId, pr.receive_message as receiveMessage, pr.sent_message as sentMessage " +
							" from b_process_request p inner join b_process_request_detail pr on pr.processrequest_id=p.id where" +
							" p.id=? group by p.id ";
			    	
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
				  String isProcessed=rs.getString("isProcessed");
				  return new ProcessRequestData(id,clientId,orderId,requestType,hardwareId,receiveMessage,sentMessage,isProcessed);
		       }
		}

		@Override
		public ProcessRequestData getProcessRequestIDData(Long id) {
			
			context.authenticatedUser();

			ProcessRequestMapper mapper = new ProcessRequestMapper();

			String sql = "select " + mapper.schemaForId();

			return this.jdbcTemplate.queryForObject(sql, mapper, new Object[] {id});
		}
		
		



}

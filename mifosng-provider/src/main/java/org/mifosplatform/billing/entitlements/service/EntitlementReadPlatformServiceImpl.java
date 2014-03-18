package org.mifosplatform.billing.entitlements.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.mifosplatform.billing.entitlements.data.ClientEntitlementData;
import org.mifosplatform.billing.entitlements.data.EntitlementsData;
import org.mifosplatform.billing.entitlements.data.StakerData;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class EntitlementReadPlatformServiceImpl implements
		EntitlementReadPlatformService {

	private final static Logger logger = LoggerFactory.getLogger(EntitlementReadPlatformServiceImpl.class);
	private final JdbcTemplate jdbcTemplate;
	

	@Autowired
	public EntitlementReadPlatformServiceImpl(
			final TenantAwareRoutingDataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override

	public List<EntitlementsData> getProcessingData(Long id,String provisioningSys) {
		// TODO Auto-generated method stub
		String sql = "";
		ServicesMapper mapper = new ServicesMapper();		
		sql = "select " + mapper.schema();		
		if(provisioningSys != null){
			sql = sql + " and p.provisioing_system = '" + provisioningSys + "' ";
		}		
		if (id != null) {
			sql = sql + " and pr.id limit " + id;
		} 				
		List<EntitlementsData> detailsDatas = jdbcTemplate.query(sql, mapper,new Object[] {});
		return detailsDatas;
	}


	protected static final class ServicesMapper implements
			RowMapper<EntitlementsData> {

		@Override
		public EntitlementsData mapRow(final ResultSet rs, final int rowNum)
				throws SQLException {
			Long id = rs.getLong("id");
			Long serviceId = rs.getLong("serviceId");
			String product = rs.getString("sentMessage");
			Long prdetailsId = rs.getLong("prdetailsId");
			String requestType = rs.getString("requestType");
			String hardwareId = rs.getString("hardwareId");
			String provisioingSystem = rs.getString("provisioingSystem");
			Long clientId = rs.getLong("clientId");
			Long planId= rs.getLong("planId");
			String orderNo= rs.getString("orderNo");

			return new EntitlementsData(id, prdetailsId, requestType,hardwareId, provisioingSystem, product, serviceId, clientId,planId,orderNo);

		}

		public String schema() {
			return " p.id AS id,p.client_id AS clientId,p.provisioing_system AS provisioingSystem,pr.service_id AS serviceId,pr.id AS " +
					"prdetailsId,pr.sent_message AS sentMessage,pr.hardware_id AS hardwareId,pr.request_type AS requestType,o.plan_id AS planId,o.order_no as orderNo " +
					"FROM b_process_request_detail pr,b_process_request p left join b_orders o on o.id=p.order_id where p.id = pr.processrequest_id" +
					" AND p.is_processed = 'N'";
		}

	}

	@Override
	public ClientEntitlementData getClientData(Long clientId) {
		// TODO Auto-generated method stub
		
	    ClientMapper mapper=new ClientMapper();
	    String sql="Select "+ mapper.schema();
	    
		return this.jdbcTemplate.queryForObject(sql, mapper, new Object[] {clientId});
	}
	
	protected static final class ClientMapper implements
			RowMapper<ClientEntitlementData> {
		
		@Override
		public ClientEntitlementData mapRow(final ResultSet rs, final int rowNum) throws SQLException {
			
			String emailId = rs.getString("EmailId");
			String fullName = rs.getString("fullName");	
			String login = rs.getString("login");
			String password = rs.getString("password");
			return new ClientEntitlementData(emailId,fullName,login,password);
		
		}
		
		public String schema() {
			return "c.email as EmailId,c.display_name as fullName,c.login as login,c.password as password from m_client c where c.id=?";
		}
		
		}

	@Override
	public StakerData getData(String macAddress) {
		try{		
			logger.info("Staker Get method called");
			StakerMapper mapper = new StakerMapper();

			String sql = "select " + mapper.schema();

			return this.jdbcTemplate.queryForObject(sql, mapper, new Object[] {macAddress});
			}catch (final EmptyResultDataAccessException e) {
				logger.error("EmptyResultDataAccessException : "+e.getMessage());
				return null;
			}
	}
	
	protected static final class StakerMapper implements RowMapper<StakerData> {

			@Override
			public StakerData mapRow(final ResultSet rs, final int rowNum) throws SQLException {
				
			     String mac=rs.getString("mac");
			     Long Ls1=rs.getLong("Ls");
			     String status=rs.getString("status");
			     String fname=rs.getString("fname");
			     String phone=rs.getString("phone");
			     String end_date=rs.getString("end_date");
			     String tariff=rs.getString("tariff");
			     Long Ls=new Long(12345);
			     logger.info("Retrieving the data is: mac= "+mac+" ,ls= "+Ls+" ,status= "+status+" ,fname= "+fname+" ,phone= "+phone+" ,end_date= "+end_date+" ,tariff= "+tariff);
				return new StakerData(mac,Ls,status,fname,phone,end_date,tariff);
			
			}
			
			public String schema() {
				/*return " DISTINCT a.serial_no AS mac,a.client_id AS ls,o.order_status as status,c.firstname as fname,c.phone as phone," +
						" o.end_date as end_date, 'Ellinika' as tariff FROM b_allocation a, m_client c,b_plan_master pm,b_orders o," +
						" b_item_detail i WHERE a.client_id = c.id AND c.id= o.client_id" +
						" AND o.plan_id=pm.id AND a.serial_no=i.serial_no group by ls and a.serial_no=?";*/
				return "DISTINCT i.provisioning_serialno AS mac,i.client_id AS ls,o.order_status as status,c.firstname as fname," +
						" c.phone as phone,o.end_date as end_date, pm.plan_description as tariff FROM b_allocation a,m_client c,b_plan_master pm," +
						" b_orders o,b_item_detail i WHERE i.client_id = c.id AND c.id= o.client_id and i.serial_no=a.serial_no " +
						" AND o.plan_id=pm.id AND o.order_status =1 AND i.provisioning_serialno=? group by ls";
				
			}

}

}

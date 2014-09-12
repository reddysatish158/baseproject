package org.mifosplatform.provisioning.entitlements.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.provisioning.entitlements.data.ClientEntitlementData;
import org.mifosplatform.provisioning.entitlements.data.EntitlementsData;
import org.mifosplatform.provisioning.entitlements.data.StakerData;
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

	public List<EntitlementsData> getProcessingData(Long id,String provisioningSys,String serviceType) {
		// TODO Auto-generated method stub
		String sql = "";
		ServicesMapper mapper = new ServicesMapper();		
		sql = "select " + mapper.schema();		
		if(provisioningSys != null){
			sql = sql + " and p.provisioing_system = '" + provisioningSys + "' ";
		
		}if(serviceType != null){
			sql = sql + " and pr.service_type = '" + serviceType + "' ";
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
			String servicetype = rs.getString("servicetype");
			String hardwareId = rs.getString("hardwareId");
			String provisioingSystem = rs.getString("provisioingSystem");
			Long clientId = rs.getLong("clientId");
			Long planId= rs.getLong("planId");
			String orderNo= rs.getString("orderNo");
			Long orderId=rs.getLong("orderId");
			LocalDate startDate=JdbcSupport.getLocalDate(rs, "startDate");
		    LocalDate endDate=JdbcSupport.getLocalDate(rs, "endDate");

			return new EntitlementsData(id, prdetailsId, requestType,hardwareId, provisioingSystem, product, serviceId, clientId,planId,
					orderNo,orderId,startDate,endDate,servicetype);


		}

		public String schema() {

			return "  p.id AS id,p.client_id AS clientId,p.provisioing_system AS provisioingSystem,pr.service_id AS serviceId,pr.id AS prdetailsId,pr.service_type as servicetype," +
					"pr.sent_message AS sentMessage,pr.hardware_id AS hardwareId,pr.request_type AS requestType,o.plan_id AS planId,o.order_no AS orderNo," +
					"o.id as orderId,o.start_date as startDate,o.end_date as endDate FROM b_process_request_detail pr, b_process_request p LEFT JOIN b_orders o" +
					" ON o.id = p.order_id WHERE p.id = pr.processrequest_id AND p.is_processed = 'N'";

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
			return "c.email as EmailId,c.display_name as fullName,ifnull(c.login,c.id) as login,ifnull(c.password,'0000') as password from m_client c where c.id=?";

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

	@Override
	public List<EntitlementsData> getBeeniusProcessingData(Long id,String provisioningSystem) {
		
		String sql = "";
		BeeniusServicesMapper mapper = new BeeniusServicesMapper();
		sql = "select " + mapper.schema();
		if (provisioningSystem != null) {
			sql = sql + " and bpr.provisioing_system = '" + provisioningSystem + "' ";
		}
		if (id != null) {
			sql = sql + " and bprd.id limit " + id;
		}

		return jdbcTemplate.query(sql, mapper, new Object[] {});
		
	}
	
	protected static final class BeeniusServicesMapper implements
			RowMapper<EntitlementsData> {

		@Override
		public EntitlementsData mapRow(final ResultSet rs, final int rowNum)
				throws SQLException {
			
			Long id = rs.getLong("id");
			Long prdetailsId = rs.getLong("prdetailsId");
			String provisioingSystem = rs.getString("provisioingSystem");
			
			Long serviceId = rs.getLong("serviceId");
			String product = rs.getString("sentMessage");
			String macId = rs.getString("macId");
			String deviceId = rs.getString("deviceId");
			
			String requestType = rs.getString("requestType");
			String itemCode = rs.getString("itemCode");
			String itemDescription = rs.getString("itemDescription");
			
			Long clientId = rs.getLong("clientId");
			String accountNo = rs.getString("accountNo");
			String firstName = rs.getString("firstName");
			String lastName = rs.getString("lastName");
			
			String officeUID = rs.getString("officeUID");
			String branch = rs.getString("branch");
			String regionCode = rs.getString("regionCode");
			String regionName = rs.getString("regionName");
			String ipAddress = rs.getString("ipAddress");
			
			
			return new EntitlementsData(id,prdetailsId,provisioingSystem,serviceId,product,macId,requestType,itemCode
					,itemDescription,clientId,accountNo,firstName,lastName,officeUID,branch,regionCode,regionName,deviceId,ipAddress);
		}

		public String schema() {

			
			return " bpr.id as id, c.id as clientId, c.account_no as accountNo,c.firstname as firstName,c.lastname as lastName, " +
					" o.external_id as officeUID,o.name as branch," +
					" bpr.provisioing_system AS provisioingSystem,bprd.service_id AS serviceId," +
					" bprd.id AS prdetailsId,bprd.sent_message AS sentMessage," +
					" ifnull(bid.provisioning_serialno ,oh.provisioning_serial_number) AS macId," +
					" ifnull(bid.serial_no ,oh.serial_number) AS deviceId," +
					" bprd.request_type AS requestType," +
					" bipd.ip_address as ipAddress," +
					" bim.item_code as itemCode,bim.item_description as itemDescription," +
					" bprm.priceregion_code as regionCode, bprm.priceregion_name as regionName" +
					" from m_client c" +
					" join m_office o on (o.id = c.office_id)" +
					" join b_process_request bpr on (c.id = bpr.client_id )" +
					" join b_process_request_detail bprd on (bpr.id = bprd.processrequest_id )" +
					" join b_client_address bca on (c.id=bca.client_id and address_key='PRIMARY')" +
					" left join b_state bs on (bca.state = bs.state_name )" +
					" left join b_priceregion_detail bpd on (bpd.state_id=bs.id and bpd.is_deleted='N')" +
					" left join b_priceregion_master bprm on (bpd.priceregion_id = bprm.id ) " +
					" left join b_ippool_details bipd on (bpr.client_id = bipd.client_id)" +
					" left join b_item_detail bid on (bprd.hardware_id = bid.provisioning_serialno) " +
					" left join b_item_master bim on (bid.item_master_id = bim.id) " +
					" left join b_owned_hardware oh on (bprd.hardware_id =oh.provisioning_serial_number AND oh.is_deleted = 'N')" +
					" WHERE bpr.is_processed = 'N'";
		}

	}

}

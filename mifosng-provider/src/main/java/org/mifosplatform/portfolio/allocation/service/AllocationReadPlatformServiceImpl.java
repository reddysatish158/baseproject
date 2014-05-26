package org.mifosplatform.portfolio.allocation.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.mifosplatform.infrastructure.configuration.domain.ConfigurationConstants;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.logistics.onetimesale.data.AllocationDetailsData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;


@Service
public class AllocationReadPlatformServiceImpl implements AllocationReadPlatformService {
	
	
	    private final JdbcTemplate jdbcTemplate;
	    private final PlatformSecurityContext context;
	    
	    @Autowired
	    public AllocationReadPlatformServiceImpl(final PlatformSecurityContext context, final TenantAwareRoutingDataSource dataSource)
	    {
	        this.context = context;
	        this.jdbcTemplate = new JdbcTemplate(dataSource);
	       
	     

	    }

	
	@Override
	public AllocationDetailsData getTheHardwareItemDetails(Long orderId,String configProp) {
		try {
			
			final ClientOrderMapper mapper = new ClientOrderMapper();
			 String sql =null;
			if(configProp.equalsIgnoreCase(ConfigurationConstants.CONFIR_PROPERTY_SALE)){
			
				  sql = "select " + mapper.clientAssociationLookupSchema();
			}else if(configProp.equalsIgnoreCase(ConfigurationConstants.CONFIR_PROPERTY_OWN)){
				
				sql = "select " + mapper.clientOwnHwAssociationLookupSchema();
			}
			return jdbcTemplate.queryForObject(sql, mapper, new Object[] {  orderId });
			} catch (EmptyResultDataAccessException e) {
			return null;
			}

			}

			private static final class ClientOrderMapper implements RowMapper<AllocationDetailsData> {

			public String clientAssociationLookupSchema() {
			return " a.id AS id,a.order_id AS orderId,id.provisioning_serialno AS serialNum,a.client_id AS clientId FROM b_association a, b_item_detail id" +
					" WHERE a.order_id =? and id.serial_no=a.hw_serial_no and a.is_deleted='N' limit 1";
			}
			
			public String clientOwnHwAssociationLookupSchema() {

				return "  a.id AS id,a.order_id AS orderId,o.provisioning_serial_number AS serialNum,a.client_id AS clientId" +
						" FROM b_association a, b_owned_hardware o WHERE a.order_id =?  AND o.serial_number = a.hw_serial_no   " +
						" AND a.is_deleted = 'N' and o.is_deleted = 'N' limit 1";
				}
			
			public String clientDeAssociationLookupSchema() {
				return " a.id AS id, a.order_id AS orderId,a.client_id AS clientId,i.provisioning_serialno as serialNum FROM b_association a, b_item_detail i  " +
						" WHERE order_id = ? and a.hw_serial_no=i.serial_no AND a.id = (SELECT MAX(id) FROM b_association a WHERE  a.client_id =?  and a.is_deleted = 'Y')  limit 1";
				}
			
			public String clientOwnHwDeAssociationLookupSchema() {
				return " a.id AS id,a.order_id AS orderId,a.client_id AS clientId,a.hw_serial_no AS serialNum FROM b_association a  " +
						" WHERE order_id =? AND a.id = (SELECT MAX(id) FROM b_association a  WHERE a.client_id =? AND a.is_deleted = 'Y') LIMIT 1";
				}

			@Override
			public AllocationDetailsData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

			final Long id = rs.getLong("id");
			final Long orderId = rs.getLong("orderId");
			final String serialNum = rs.getString("serialNum");
			final Long clientId = rs.getLong("clientId");	
		
			
			return new AllocationDetailsData(id,orderId,serialNum,clientId);
			}
			}

			@Override
			public List<AllocationDetailsData> retrieveHardWareDetailsByItemCode(Long clientId, String planCode,String associationType) 
			{
				try {
					
					String sql =null;
					final HardwareMapper mapper = new HardwareMapper();
					
					if(associationType.equalsIgnoreCase(ConfigurationConstants.CONFIR_PROPERTY_OWN)){
						sql = "select " + mapper.ownDeviceschema();
					}else if(associationType.equalsIgnoreCase(ConfigurationConstants.CONFIR_PROPERTY_SALE)){
					     sql = "select " + mapper.schema();
					}
					return jdbcTemplate.query(sql, mapper, new Object[] {clientId,planCode});
					} catch (EmptyResultDataAccessException e) {
					return null;
					}

					}

					private static final class HardwareMapper implements RowMapper<AllocationDetailsData> {

					public String schema() {

					return " id.id AS id,id.serial_no AS serialNo,i.item_description AS itemDescription  FROM b_item_master i, b_item_detail id, " +
							"b_association a, b_hw_plan_mapping hm  WHERE id.item_master_id = i.id AND i.item_code =hm.item_code AND id.client_id =? and  " +
							" hm.plan_code=?  GROUP BY id.client_id ";
					}
					
					public String ownDeviceschema() {

						return " o.id AS id,o.serial_number AS serialNo,i.item_description AS itemDescription  FROM b_item_master i," +
								" b_owned_hardware o, b_hw_plan_mapping hm WHERE o.item_type = i.id AND i.item_code = hm.item_code" +
								" AND o.client_id = ?  AND hm.plan_code =? and o.is_deleted = 'N' GROUP BY o.client_id ";
						}

					@Override
					public AllocationDetailsData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

					final Long id = rs.getLong("id");
					final String serialNum = rs.getString("serialNo");
					final String itemDescription = rs.getString("itemDescription");	
				
					
					return new AllocationDetailsData(id, itemDescription, serialNum, null,null);
					}
			}

					@Override
					public List<String> retrieveHardWareDetails(
							Long clientId) {
						try {
							  
							final ClientHardwareMapper mapper = new ClientHardwareMapper();
							final String sql = "select " + mapper.schema();
							return jdbcTemplate.query(sql, mapper, new Object[] {  clientId });
							} catch (EmptyResultDataAccessException e) {
							return null;
							}

							}
			
					private static final class ClientHardwareMapper implements RowMapper<String> {

						public String schema() {
						return " a.serial_no as serialNo from b_allocation a where a.client_id=?";
						}

						@Override
						public String mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

							final String serialNum = rs.getString("serialNo");
						
						return serialNum;
						}
				}

					@Override
					public AllocationDetailsData getDisconnectedHardwareItemDetails(Long orderId,Long clientId,String associationType) {

						try {
							
							final ClientOrderMapper mapper = new ClientOrderMapper();
							String sql=null; 
							if(associationType.equalsIgnoreCase(ConfigurationConstants.CONFIR_PROPERTY_SALE)){
							
								 sql = "select " + mapper.clientDeAssociationLookupSchema();
								 
							}else if(associationType.equalsIgnoreCase(ConfigurationConstants.CONFIR_PROPERTY_OWN)){
								
								 sql = "select " + mapper.clientOwnHwDeAssociationLookupSchema();
							}
							
							return jdbcTemplate.queryForObject(sql, mapper, new Object[] {  orderId,clientId });
							} catch (EmptyResultDataAccessException e) {
							return null;
							}

					}
			

	
}

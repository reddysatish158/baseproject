package org.mifosplatform.billing.allocation.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.mifosplatform.billing.onetimesale.data.AllocationDetailsData;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
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
	public AllocationDetailsData getTheHardwareItemDetails(Long orderId) {
		try {
			
			final ClientOrderMapper mapper = new ClientOrderMapper();
			final String sql = "select " + mapper.clientAssociationLookupSchema();
			return jdbcTemplate.queryForObject(sql, mapper, new Object[] {  orderId });
			} catch (EmptyResultDataAccessException e) {
			return null;
			}

			}

			private static final class ClientOrderMapper implements RowMapper<AllocationDetailsData> {

			public String clientAssociationLookupSchema() {
			return " a.id AS id,a.order_id AS orderId,id.provisioning_serialno AS serialNum,a.client_id AS clientId FROM b_association a, b_item_detail id" +
					" WHERE a.order_id =? and id.serial_no=a.hw_serial_no and a.is_deleted='N'";
			}
			
			public String clientDeAssociationLookupSchema() {
				return " a.id AS id, a.order_id AS orderId,a.client_id AS clientId,i.provisioning_serialno as serialNum FROM b_association a, b_item_detail i  " +
						" WHERE order_id = ? and a.hw_serial_no=i.serial_no AND a.id = (SELECT MAX(id) FROM b_association a WHERE  a.order_id =?  and a.is_deleted = 'Y')  limit 1";


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
			public List<AllocationDetailsData> retrieveHardWareDetailsByItemCode(Long clientId, String planCode) 
			{
				try {
					  
					final HardwareMapper mapper = new HardwareMapper();
					final String sql = "select " + mapper.schema();
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
					public AllocationDetailsData getDisconnectedHardwareItemDetails(Long orderId,Long clientId) {

						try {
							
							final ClientOrderMapper mapper = new ClientOrderMapper();
							final String sql = "select " + mapper.clientDeAssociationLookupSchema();
							return jdbcTemplate.queryForObject(sql, mapper, new Object[] {  orderId,clientId });
							} catch (EmptyResultDataAccessException e) {
							return null;
							}

					}
			

	
}

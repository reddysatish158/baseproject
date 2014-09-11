package org.mifosplatform.logistics.ownedhardware.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.logistics.item.data.ItemData;
import org.mifosplatform.logistics.ownedhardware.data.OwnedHardwareData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
public class OwnedHardwareReadPlatformServiceImp implements	OwnedHardwareReadPlatformService {

	
	private JdbcTemplate jdbcTemplate;
	private PlatformSecurityContext context;
	
	@Autowired
	public OwnedHardwareReadPlatformServiceImp(final TenantAwareRoutingDataSource dataSource, final PlatformSecurityContext context) {
		this.context = context;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	
	@Override
	public List<OwnedHardwareData> retriveOwnedHardwareData(Long clientId) {
		OwnedHardwareMapper mapper = new OwnedHardwareMapper();
		String sql = "select "+mapper.schema()+" where oh.client_id = ? and oh.is_deleted='N'";
		return jdbcTemplate.query(sql, mapper, new Object[]{clientId});
	}
	
	
	@Override
	public List<ItemData> retriveTemplate(){
		ItemDataMapper mapper = new ItemDataMapper();
		String sql = mapper.schema();
		return jdbcTemplate.query(sql, mapper,new Object[]{});
	}
	
	
	private static final class OwnedHardwareMapper implements RowMapper<OwnedHardwareData>{
		@Override
		public OwnedHardwareData mapRow(ResultSet rs, int rowNum)
				throws SQLException {
				Long id = rs.getLong("id");
				Long clientId = rs.getLong("clientId");
				String serialNumber = rs.getString("serialNumber");
				String provisioningSerialNumber = rs.getString("provisioningSerialNumber");
				String status = rs.getString("status");
				LocalDate allocationDate = JdbcSupport.getLocalDate(rs,"allocationDate");
				String itemType = rs.getString("itemType");
			return new OwnedHardwareData(id, clientId, serialNumber, provisioningSerialNumber, allocationDate, status, itemType);
		}
		
		public String schema(){
			String sql = "oh.id as id, oh.client_id as clientId, oh.serial_number as serialNumber, oh.provisioning_serial_number as provisioningSerialNumber, oh.allocated_date as allocationDate, oh.status as status, im.item_description as itemType from b_owned_hardware oh left outer join b_item_master im on oh.item_type=im.id";
			return sql;
		}
		public String schemaForSingleRecord(){
			String sql = "oh.id as id, oh.client_id as clientId, oh.serial_number as serialNumber, oh.provisioning_serial_number as provisioningSerialNumber, oh.allocated_date as allocationDate, oh.status as status, oh.item_type as itemType from b_owned_hardware oh";
			return sql;
		}
	}
	
	private final class SerialNumberMapper implements RowMapper<String>{
		@Override
		public String mapRow(ResultSet rs, int rowNum) throws SQLException {
			String serialNumber = rs.getString("serialNumber");
			return serialNumber;
		}
	}
	
	@Override
	public List<String> retriveSerialNumbers() {
		SerialNumberMapper rowMapper = new SerialNumberMapper();
		String sql = "select serial_number as serialNumber from b_owned_hardware where is_deleted='N'";
		return jdbcTemplate.query(sql,rowMapper);
	}
	
	@Override
	public int retrieveNoOfActiveUsers(Long clientId){
		String sql = "select count(*) from b_owned_hardware h where h.status='ACTIVE' and h.is_deleted='N' and h.client_id=?";
		return this.jdbcTemplate.queryForInt(sql, new Object[]{clientId});
	}
	
	private static final class ItemDataMapper implements RowMapper<ItemData>{
		
		@Override
		public ItemData mapRow(ResultSet rs, int rowNum) throws SQLException {
				Long id = rs.getLong("id");
				String itemDesc = rs.getString("itemDescription");
			return new ItemData(id, "", itemDesc, "", "", "", 0, new BigDecimal(0),null,null,null);
		}
		
		public String schema(){	
			String sql = "select im.id as id, im.item_description as itemDescription from b_item_master im order by id asc";
			return sql;
		}
	}

	@Override
	public List<OwnedHardwareData> retriveSingleOwnedHardwareData(Long id) {
		OwnedHardwareMapper mapper = new OwnedHardwareMapper();
		String sql = "select "+mapper.schemaForSingleRecord()+" where oh.id = ?";
		return jdbcTemplate.query(sql, mapper, new Object[]{id});
	}


	
	@Override
	@Transactional
	public int retrieveClientActiveDevices(Long clientId) {
		try{
			
			this.context.authenticatedUser();
			String sql="select count(*) from b_owned_hardware h where h.is_deleted='N' and  h.client_id=?";
			return this.jdbcTemplate.queryForInt(sql, new Object[]{clientId});
		
		}catch(EmptyResultDataAccessException accessException){
			return 0;	
		}
		
	}
	
	
}

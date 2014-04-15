package org.mifosplatform.billing.ippool.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.mifosplatform.billing.ippool.data.IpPoolData;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class IpPoolManagementReadPlatformServiceImpl implements IpPoolManagementReadPlatformService{
	
	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;
	
	@Autowired	
   public IpPoolManagementReadPlatformServiceImpl(final TenantAwareRoutingDataSource dataSource,final PlatformSecurityContext context){
		
		this.context=context;
		this.jdbcTemplate=new JdbcTemplate(dataSource);
		
	}
	

	@Transactional
	@Override
	public List<IpPoolData> getUnallocatedIpAddressDetailds() {
		
		try{
			
			this.context.authenticatedUser();
			IPPoolDataMapper mapper=new IPPoolDataMapper();
			
			String sql="select "+mapper.schema();
			
			return this.jdbcTemplate.query(sql, mapper, new Object[] {});
		}catch(EmptyResultDataAccessException accessException){
		
			return null;
		}
	}
	private final static class IPPoolDataMapper implements RowMapper<IpPoolData>{

		public String schema() {
			
			return "  pd.id as id,pd.pool_id as poolId,pd.ip_address as ipaddress  from b_ippool_details pd where status='F'";
		}

		@Override
		public IpPoolData mapRow(ResultSet rs, int rowNum) throws SQLException {

			Long id = rs.getLong("id");
			Long poolId = rs.getLong("poolId");
			String ipaddress = rs.getString("ipaddress");
			return new IpPoolData(id,poolId,ipaddress);

		
		}
		
	}
	

}

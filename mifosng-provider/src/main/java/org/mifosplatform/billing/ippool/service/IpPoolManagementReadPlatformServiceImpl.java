package org.mifosplatform.billing.ippool.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.mifosplatform.billing.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.billing.ippool.data.IpPoolManagementData;
import org.mifosplatform.billing.paymentsgateway.data.PaymentGatewayData;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.core.service.PaginationHelper;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class IpPoolManagementReadPlatformServiceImpl implements IpPoolManagementReadPlatformService {
	
	private final PaginationHelper<IpPoolManagementData> paginationHelper = new PaginationHelper<IpPoolManagementData>(); 
	private final JdbcTemplate jdbcTemplate;
	 private final PlatformSecurityContext context;
	 
	 
	  @Autowired
	    public IpPoolManagementReadPlatformServiceImpl(final PlatformSecurityContext context, final TenantAwareRoutingDataSource dataSource) {
	        this.context = context;
	        this.jdbcTemplate = new JdbcTemplate(dataSource);

	    }

	@Override
	public Long checkIpAddress(String ipaddress) {
		try {
			String sql = "select b.id as id from b_ippool_details b where b.ip_address = '"+ ipaddress + "' ";
			return jdbcTemplate.queryForLong(sql);
		} catch (EmptyResultDataAccessException e) {
			return null;
		} 
	}

	@Override
	public List<IpPoolManagementData> retrieveAllData() {
		IpPoolMapper mapper = new IpPoolMapper();
		String sql = "select " + mapper.schema();
		return this.jdbcTemplate.query(sql, mapper, new Object[] {});
	}

	private static final class IpPoolMapper implements
			RowMapper<IpPoolManagementData> {

		public String schema() {

			return "p.id ,p.pool_name as poolName, p.ip_address as ipAddress ,p.status, p.client_id as ClientId from b_ippool_details p";
		}

		@Override
		public IpPoolManagementData mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			
			Long id=rs.getLong("id");
			String ipAddress=rs.getString("ipAddress");
			String poolName=rs.getString("poolName");
			String status=rs.getString("status");
			Long ClientId=rs.getLong("ClientId");
			
			return new IpPoolManagementData(id, ipAddress, poolName,status, ClientId);
		}
	}

	@Override
	public Page<IpPoolManagementData> retrieveIpPoolData(SearchSqlQuery searchIpPoolDetails, String tabType) {
		
		// TODO Auto-generated method stub
		context.authenticatedUser();
		IpPoolMapper mapper=new IpPoolMapper();
		
		String sqlSearch = searchIpPoolDetails.getSqlSearch();
	    String extraCriteria = "";
		StringBuilder sqlBuilder = new StringBuilder(400);
        sqlBuilder.append("select ");
        sqlBuilder.append(mapper.schema());
       
          
        if (tabType!=null ) {
        	
		        	tabType=tabType.trim();
		        	sqlBuilder.append(" where p.status like '"+tabType+"' order by p.id ");
		  
		    	    if (sqlSearch != null) {
		    	    	sqlSearch=sqlSearch.trim();
		    	    	extraCriteria = " and (p.ip_address like '%"+sqlSearch+"%' OR p.pool_name like '%"+sqlSearch+"%') order by p.id";
		    	    }
		            sqlBuilder.append(extraCriteria);
		            
	    }else if (sqlSearch != null) {
    	    	sqlSearch=sqlSearch.trim();
    	    	extraCriteria = " where (p.ip_address like '%"+sqlSearch+"%' OR p.pool_name like '%"+sqlSearch+"%') order by p.id";
    	}else {
    		extraCriteria = " order by p.id ";
    	}
                sqlBuilder.append(extraCriteria);
        
        
        if (searchIpPoolDetails.isLimited()) {
            sqlBuilder.append(" limit ").append(searchIpPoolDetails.getLimit());
        }

        if (searchIpPoolDetails.isOffset()) {
            sqlBuilder.append(" offset ").append(searchIpPoolDetails.getOffset());
        }

		return this.paginationHelper.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()",sqlBuilder.toString(),
                new Object[] {}, mapper);
	
	}

}

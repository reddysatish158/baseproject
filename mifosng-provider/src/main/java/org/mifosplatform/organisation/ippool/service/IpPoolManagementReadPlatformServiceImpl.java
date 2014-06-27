package org.mifosplatform.organisation.ippool.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifosplatform.billing.pricing.service.PriceReadPlatformService;
import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.core.service.PaginationHelper;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.dataqueries.data.GenericResultsetData;
import org.mifosplatform.infrastructure.dataqueries.data.ResultsetColumnHeaderData;
import org.mifosplatform.infrastructure.dataqueries.data.ResultsetRowData;
import org.mifosplatform.infrastructure.dataqueries.service.ReadReportingService;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.ippool.data.IpPoolData;
import org.mifosplatform.organisation.ippool.data.IpPoolManagementData;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
//String serviceDescription = rs.getString("service_description");
// TODO Auto-generated method stub
@Service
public class IpPoolManagementReadPlatformServiceImpl implements IpPoolManagementReadPlatformService {


	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;
	private final ReadReportingService readReportingService;
	private final PaginationHelper<IpPoolManagementData> paginationHelper = new PaginationHelper<IpPoolManagementData>(); 

	@Autowired
	public IpPoolManagementReadPlatformServiceImpl(final PlatformSecurityContext context,final PriceReadPlatformService priceReadPlatformService,
			final TenantAwareRoutingDataSource dataSource,final ReadReportingService readReportingService) {
		
		this.context = context;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.readReportingService=readReportingService;
	}

	@Override
	public List<IpPoolData> getUnallocatedIpAddressDetailds() {
		
		Map<String, String> queryParams=new HashMap<String, String>();
		   List<IpPoolData> ipPoolDatas=new ArrayList<IpPoolData>();
		  
		   
		final GenericResultsetData resultsetData=this.readReportingService.retrieveGenericResultset("IP_ADDRESS", "parameter", queryParams);
		   List<ResultsetRowData> datas = resultsetData.getData();
		   List<String> row;
		    Integer rSize;
		   for (int i = 0; i < datas.size(); i++) {
               row = datas.get(i).getRow();
               rSize = row.size();
               for (int j = 0; j < rSize-1; j++) {

            	   String  id=datas.get(i).getRow().get(j);
            	   j++;
            	   String poolName=datas.get(i).getRow().get(j);
            	   j++;
            	   String ipAddress=datas.get(i).getRow().get(j);
            	   j=j++;
				   ipPoolDatas.add(new IpPoolData(new Long(id), poolName, ipAddress));
               }
           }
		   
		   
		   return ipPoolDatas;
	/*

		context.authenticatedUser();
		ProvisioningMapper mapper = new ProvisioningMapper();

		String sql = "select " + mapper.schema();

		return this.jdbcTemplate.query(sql, mapper, new Object[] {});

	*/}

	private static final class ProvisioningMapper implements RowMapper<IpPoolData> {

		public String schema() {
			return " pd.id as id,pd.pool_name as poolName,pd.ip_address as ipaddress  from b_ippool_details pd where status='F'";

		}

		@Override
		public IpPoolData mapRow(final ResultSet rs,
				@SuppressWarnings("unused") final int rowNum)
				throws SQLException {

			Long id = rs.getLong("id");
			String poolName = rs.getString("poolName");
			String ipaddress = rs.getString("ipaddress");
			//String serviceDescription = rs.getString("service_description");
			return new IpPoolData(id,poolName,ipaddress);

		}
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

			return "  p.id ,p.pool_name as poolName, p.client_id as ClientId,c.display_name as ClientName,p.ip_address as ipAddress ,CASE p.status " +
					" WHEN 'I' THEN 'Intermediate' WHEN 'F' THEN 'Free' WHEN 'A' THEN 'Assigned' WHEN 'B' THEN 'Blocked' ELSE 'Unknown Error' end  as status ," +
					" p.notes from b_ippool_details p left join m_client c on p.client_id = c.id ";
		}

		@Override
		public IpPoolManagementData mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			
			Long id=rs.getLong("id");
			String ipAddress=rs.getString("ipAddress");
			String poolName=rs.getString("poolName");
			String status=rs.getString("status");
			Long ClientId=rs.getLong("ClientId");
			String clientName=rs.getString("ClientName");
			String notes=rs.getString("notes");
			
			return new IpPoolManagementData(id, ipAddress, poolName,status, ClientId,clientName,notes);
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
		        	sqlBuilder.append(" where  p.status like '"+tabType+"'");
		  
		    	   /* if (sqlSearch != null) {
		    	    	sqlSearch=sqlSearch.trim();
		    	    	extraCriteria = " and (p.ip_address like '%"+sqlSearch+"%' OR p.pool_name like '%"+sqlSearch+"%' OR c.display_name LIKE '%"+sqlSearch+"%')";
		    	    }
		            sqlBuilder.append(extraCriteria);*/
		        	   sqlBuilder.append(extraCriteria);   
	    }if (sqlSearch != null) {
    	    	sqlSearch=sqlSearch.trim();
    	    	//extraCriteria = " and (p.ip_address like '%"+sqlSearch+"%' OR p.pool_name like '%"+sqlSearch+"%') order by p.id";
    	    	extraCriteria = " and (p.ip_address like '%"+sqlSearch+"%' OR p.pool_name like '%"+sqlSearch+"%' OR c.display_name LIKE '%"+sqlSearch+"%')";
    	    	   sqlBuilder.append(extraCriteria);
    	}
	    sqlBuilder.append("   group by p.id order by p.id ");
        
        if (searchIpPoolDetails.isLimited()) {
            sqlBuilder.append(" limit ").append(searchIpPoolDetails.getLimit());
        }

        if (searchIpPoolDetails.isOffset()) {
            sqlBuilder.append(" offset ").append(searchIpPoolDetails.getOffset());
        }

		return this.paginationHelper.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()",sqlBuilder.toString(),
                new Object[] {}, mapper);
	
	}
	
	@Override
	public List<String> retrieveIpPoolIDArray(String query) {
		IpAddressPoolingArrayMapper mapper = new IpAddressPoolingArrayMapper();
		String sql = "select " + mapper.schema()+ " and  p.ip_address like '"+ query +"%'";
		return this.jdbcTemplate.query(sql, mapper, new Object[] {});
	}

	private static final class IpAddressPoolingArrayMapper implements RowMapper<String> {

		public String schema() {
		
			return "p.ip_address as ipAddress from b_ippool_details p where p.status='F'";
		}
		
		@Override
		public String mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			
			//Long id=rs.getLong("id");
			String ipAddress=rs.getString("ipAddress");
			/*String poolName=rs.getString("poolName");
			String status=rs.getString("status");
			Long ClientId=rs.getLong("ClientId");*/
			
			return ipAddress;
		}
}

}


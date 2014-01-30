package org.mifosplatform.organisation.address.service;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.mifosplatform.billing.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.core.service.PaginationHelper;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.address.data.AddressManageData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class AddressManageReadPlatformServiceImpl implements AddressManageReadPlatformService {
	
	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;
	private final PaginationHelper<AddressManageData> paginationHelper=new PaginationHelper<AddressManageData>();
	
	@Autowired
	public AddressManageReadPlatformServiceImpl(final PlatformSecurityContext context,final TenantAwareRoutingDataSource dataSource){
		
		this.jdbcTemplate=new JdbcTemplate(dataSource);
		this.context=context;
	}
	
	@Override
	public Page<AddressManageData> retrieveAllAddresses(SearchSqlQuery searchAddresses){
		
		
			context.authenticatedUser();
			AddressActionMapper mapper=new AddressActionMapper();
			
			StringBuilder sqlBuilder = new StringBuilder(200);
			  sqlBuilder.append("select ");
			  sqlBuilder.append(mapper.schema());
			  String sqlSearch=searchAddresses.getSqlSearch();
			  String extraCriteria = "";
			    if (sqlSearch != null) {
			    	sqlSearch=sqlSearch.trim();
			    	extraCriteria = "  where country_name like '%"+sqlSearch+"%' "; 
			    }
			    
			    sqlBuilder.append(extraCriteria);
			    
			    if (searchAddresses.isLimited()) {
		            sqlBuilder.append(" limit ").append(searchAddresses.getLimit());
		        }
			    if (searchAddresses.isOffset()) {
		            sqlBuilder.append(" offset ").append(searchAddresses.getOffset());
		        }
			    return this.paginationHelper.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()",sqlBuilder.toString(),
		                new Object[] {},mapper);
		
	 
   }
	
	public static final class AddressActionMapper implements RowMapper<AddressManageData>{
		public String schema() {
			
			return "city.id as id,city.createdby_id as countryId,city.parent_code as stateId,country.country_name as counryName,state.state_name as stateName,city.city_name as cityName from b_country country  left join b_state state on (state.parent_code=country.id)left join  b_city city on (city.parent_code=state.id)";
			
		}
		@Override
		public AddressManageData mapRow(final ResultSet rs,@SuppressWarnings("unused") final int rowNum)  throws SQLException {
			
				String countryName=rs.getString("counryName");
				String cityName=rs.getString("cityName");
				String stateName=rs.getString("stateName");
				Long cityId=rs.getLong("id");
				Long countryId=rs.getLong("countryId");
				Long stateId=rs.getLong("stateId");
				return new AddressManageData(countryName,cityName,stateName,countryId,stateId,cityId);
			}
		}
}



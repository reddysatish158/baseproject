package org.mifosplatform.organisation.groupsDetails.service;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.core.service.PaginationHelper;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.groupsDetails.data.GroupsDetailsData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;


@Service
public class GroupsDetailsReadPlatformServiceImpl implements GroupsDetailsReadPlatformService{

	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;
	private final PaginationHelper<GroupsDetailsData> paginationHelper = new PaginationHelper<GroupsDetailsData>();
	
	@Autowired
	public GroupsDetailsReadPlatformServiceImpl(final TenantAwareRoutingDataSource dataSource,final PlatformSecurityContext context){
		
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.context = context;
	}
	
	@Override
	public Page<GroupsDetailsData> retrieveAllGroupsData(SearchSqlQuery searchGroupsDetails) {
		
		context.authenticatedUser();
		GroupsDetailsMapper groupsDetailsMapper = new GroupsDetailsMapper();
		StringBuilder sqlBuilder = new StringBuilder(200);
		
		sqlBuilder.append("select ");
		sqlBuilder.append(groupsDetailsMapper.schema());
		
		String sqlSearch = searchGroupsDetails.getSqlSearch();
		String extraCriteria = "";
		if(sqlSearch != null){
			
			sqlSearch = sqlSearch.trim();
			extraCriteria = "where bg.group_name like '%"+sqlSearch+"%' ";
		}
			
		sqlBuilder.append(extraCriteria);
		
		if(searchGroupsDetails.isLimited()){
			sqlBuilder.append(" limit ").append(searchGroupsDetails.getLimit());
		}
		if(searchGroupsDetails.isOffset()){
			sqlBuilder.append(" offset ").append(searchGroupsDetails.getOffset());
		}
		
		return this.paginationHelper.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()", sqlBuilder.toString(),
                                                      new Object[] {}, groupsDetailsMapper);
	}
	
	private class GroupsDetailsMapper implements RowMapper<GroupsDetailsData>{

		@Override
		public GroupsDetailsData mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			Long id = rs.getLong("id");
			String groupName = rs.getString("groupName");
			String groupAddress = rs.getString("groupAddress");
			Long countNo = rs.getLong("countNo");
			
			return new GroupsDetailsData(id, groupName, groupAddress, countNo);
		}
		
		public String schema(){
		
			String sql = "bg.id as id,bg.group_name as groupName,bg.group_address as groupAddress,"+ 
							"ifnull(mc.cnt,0) as countNo "+
							"from b_group bg "+ 
							"left join (select  group_name ,count(*) cnt from m_client group by group_name) mc "+
							"on (bg.group_name=mc.group_name) ";
			return sql;
			
		}
	}

}

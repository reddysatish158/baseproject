package org.mifosplatform.organisation.groupsDetails.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.core.service.PaginationHelper;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.groupsDetails.data.GroupsDetailsData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
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
		extraCriteria = extraCriteria+" order by id desc ";
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
			String attribute1 = rs.getString("attribute1");
			String attribute2 = rs.getString("attribute2");
			String attribute3 = rs.getString("attribute3");
			String attribute4 = rs.getString("attribute4");
			String isProvision = rs.getString("isProvision");
			
			return new GroupsDetailsData(id, groupName, groupAddress, countNo,attribute1,attribute2,attribute3,attribute4,isProvision);
		}
		
		public String schema(){
		
			String sql = "bg.id as id,bg.group_name as groupName,bg.group_address as groupAddress,bg.attribute1 as attribute1, "+
						    "bg.attribute2 as attribute2,bg.attribute3 as attribute3,bg.attribute4 as attribute4,bg.is_provision as isProvision, "+
					        "ifnull(mc.cnt,0) as countNo "+
							"from b_group bg "+ 
							"left join (select  group_name ,count(*) cnt from m_client group by group_name) mc "+
							"on (bg.group_name=mc.group_name) ";
			return sql;
			
		}
	}

	@Override
	public List<Long> retrieveclientIdsByGroupId(Long groupId) {
	try{

		this.context.authenticatedUser();
		ClientIdMapper mapper=new ClientIdMapper();
		final String  sql="select "+mapper.schema();
		return this.jdbcTemplate.query(sql,mapper, new Object[]{groupId});
		
	}catch(EmptyResultDataAccessException dve){
		return null;
		
	}
	}
	
	private class ClientIdMapper implements RowMapper<Long>{

		@Override
		public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			Long clientId = rs.getLong("clientId");
			return clientId;
		}
		public String schema(){
			String sql = "id as clientId from m_client c where group_id=? ";
			return sql;
			
		}
	}


}

package org.mifosplatform.workflow.eventactionmapping.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.mifosplatform.billing.paymode.data.McodeData;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.workflow.eventactionmapping.data.EventActionMappingData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class EventActionMappingReadPlatformServiceImpl implements EventActionMappingReadPlatformService
{

	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;


	@Autowired
	public EventActionMappingReadPlatformServiceImpl(final PlatformSecurityContext context,final TenantAwareRoutingDataSource dataSource) {
		this.context = context;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public List<EventActionMappingData> retrieveAllEventMapping() {
   
	try{	
		context.authenticatedUser();
		EventActionMapper mapper = new EventActionMapper();

		String sql = "select " + mapper.schema();

		return this.jdbcTemplate.query(sql, mapper, new Object[] {});
	}catch(EmptyResultDataAccessException accessException){
		return null;
	}

	}

	private static final class EventActionMapper implements RowMapper<EventActionMappingData> {

		public String schema() {
			return "em.id as id, em.event_name as eventName, em.action_name as actionName," +
				 " em.process as process,em.is_deleted as isDeleted from b_eventaction_mapping em order by em.is_deleted";

		}
		
		public String schemaForSingle() {
			return " em.id as id, em.event_name as eventName, em.action_name as actionName,em.process as process," +
					" em.is_deleted as isDeleted from b_eventaction_mapping em ";
		}
	

		@Override
		public EventActionMappingData mapRow(final ResultSet rs,
				@SuppressWarnings("unused") final int rowNum)
				throws SQLException {

			Long id = rs.getLong("id");
			String eventName = rs.getString("eventName");
			String actionName = rs.getString("actionName");
			String process = rs.getString("process");
			String isDeleted = rs.getString("isDeleted");
			//BigDecimal discountRate = rs.getBigDecimal("discountRate");
			//LocalDate startDate = JdbcSupport.getLocalDate(rs,"startDate");
			//String status = rs.getString("status");
			return new EventActionMappingData(id, eventName, actionName, process,isDeleted);
		}
	}

	
	public List<McodeData> retrieveEventMapData(String str) {

		context.authenticatedUser();
		ItemDataMaper mapper = new ItemDataMaper();

		String sql = "select " + mapper.schema()+" m.code_name=?";

		return this.jdbcTemplate.query(sql, mapper, new Object[] {str});

	}

	private static final class ItemDataMaper implements RowMapper<McodeData> {

		public String schema() {
			return "mc.id as id,mc.code_value as codeValue FROM m_code_value mc,m_code m where mc.code_id=m.id and";

		}

		@Override
		public McodeData mapRow(final ResultSet rs,
				@SuppressWarnings("unused") final int rowNum)
				throws SQLException {
           
			String codeValue = rs.getString("codeValue");
			Long id = rs.getLong("id");
			return new McodeData(id,codeValue);

		}
	}
	
	@Override
	public EventActionMappingData retrieveEventActionDetail(Long discountId) {
		try{	
			context.authenticatedUser();
			EventActionMapper mapper = new EventActionMapper();

			String sql = "select " + mapper.schemaForSingle()+" where em.id=?";
			
			return this.jdbcTemplate.queryForObject(sql, mapper, new Object[] { discountId });
		}catch(EmptyResultDataAccessException accessException){
			return null;
		}

		}
	
	@Override
	public List<EventActionMappingData> retrieveEvents(String event) {
		
		context.authenticatedUser();

		 String sql=null;
		 EventMapper mapper = new EventMapper();
		 sql = "select " + mapper.schema()+"  where b.event_name=? and b.is_deleted='N'";
		return this.jdbcTemplate.query(sql, mapper, new Object[] { event});
	}
	
	private static final class EventMapper implements RowMapper<EventActionMappingData> {

		public String schema() {
			return "b.action_name as actionName from b_eventaction_mapping b";

		}
	

		@Override
		public EventActionMappingData mapRow(final ResultSet rs,
				@SuppressWarnings("unused") final int rowNum)
				throws SQLException {

			String actionName = rs.getString("actionName");
			
			//BigDecimal discountRate = rs.getBigDecimal("discountRate");
			//LocalDate startDate = JdbcSupport.getLocalDate(rs,"startDate");
			//String status = rs.getString("status");
			return new EventActionMappingData(null, null, actionName, null,null,null,null);

		}
	}

	
}


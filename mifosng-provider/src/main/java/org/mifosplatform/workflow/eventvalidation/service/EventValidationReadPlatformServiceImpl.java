package org.mifosplatform.workflow.eventvalidation.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.workflow.eventvalidation.data.EventValidationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class EventValidationReadPlatformServiceImpl implements EventValidationReadPlatformService
{

	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;


	@Autowired
	public EventValidationReadPlatformServiceImpl(final PlatformSecurityContext context,final TenantAwareRoutingDataSource dataSource) {
		this.context = context;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public List<EventValidationData> retrieveAllEventValidation() {
   
	try{	
		context.authenticatedUser();
		EventValidationMapper mapper = new EventValidationMapper();

		String sql = "select " + mapper.schema();

		return this.jdbcTemplate.query(sql, mapper, new Object[] {});
	}catch(EmptyResultDataAccessException accessException){
		return null;
	}

	}

	private static final class EventValidationMapper implements RowMapper<EventValidationData> {

		public String schema() {
			return "be.id as id,be.event_name as eventName,be.process as process,be.pre_post as prePost," +
				 " is_deleted as isDeleted from b_event_validation be order by be.is_deleted";

		}
	

		@Override
		public EventValidationData mapRow(final ResultSet rs,
				@SuppressWarnings("unused") final int rowNum)
				throws SQLException {

			Long id = rs.getLong("id");
			String eventName = rs.getString("eventName");
			String process = rs.getString("process");
			String prePost = rs.getString("prePost");
			String isDeleted = rs.getString("isDeleted");
			
			return new EventValidationData(id, eventName,process,prePost,isDeleted);
		}
	}

}


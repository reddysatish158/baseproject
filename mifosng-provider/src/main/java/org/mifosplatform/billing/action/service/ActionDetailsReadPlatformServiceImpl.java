package org.mifosplatform.billing.action.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Map;

import org.mifosplatform.billing.action.data.ActionDetaislData;
import org.mifosplatform.billing.action.data.EventActionProcedureData;
import org.mifosplatform.billing.scheduledjobs.data.EventActionData;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;

@Service
public class ActionDetailsReadPlatformServiceImpl implements ActionDetailsReadPlatformService{
	
	
	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;
	private final SimpleJdbcCall jdbcCall;
	@Autowired
	public  ActionDetailsReadPlatformServiceImpl(final PlatformSecurityContext context, final TenantAwareRoutingDataSource dataSource) {
		this.context = context;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.jdbcCall= new SimpleJdbcCall(dataSource);
	}



	public List<ActionDetaislData> retrieveActionDetails(String eventType) {
   
		try{

		EventMappingMapper mapper = new EventMappingMapper();
		String sql = "select " + mapper.schema();
		return this.jdbcTemplate.query(sql, mapper, new Object[] { eventType });
		}catch(EmptyResultDataAccessException accessException){
			return null;
		}

	}

	private static final class EventMappingMapper implements RowMapper<ActionDetaislData> {

		public String schema() {
			return "em.id as actionId,em.action_name as actionName,em.process as processName from b_eventaction_mapping em where em.event_name=? and em.is_deleted='N'";

		}

		@Override
		public ActionDetaislData mapRow(final ResultSet rs,
				@SuppressWarnings("unused") final int rowNum)
				throws SQLException {
			Long id = rs.getLong("actionId");
			String procedureName = rs.getString("processName");
			String procedureType = rs.getString("actionName");
			return new ActionDetaislData(id,procedureName,procedureType);

		}
	}

	@Override
	public EventActionProcedureData checkCustomeValidationForEvents(Long clientId,String event, String action, String resourceId) {
       
	
			  jdbcCall.setProcedureName("workflow_events");
			  MapSqlParameterSource parameterSource = new MapSqlParameterSource();
			  parameterSource.addValue("clientid", clientId, Types.BIGINT);
			  parameterSource.addValue("eventname", event, Types.VARCHAR);
			  parameterSource.addValue("actionname", action, Types.VARCHAR);
			  parameterSource.addValue("resourceid", resourceId, Types.VARCHAR);
			    
			  Map<String, Object> out = jdbcCall.execute(parameterSource);
			     
			  String result=(String)out.get("result");
			  String resource=(String)out.get("strjson");
			  
			  boolean isCheck=false;
			  Long orderId=null;
			  if(result.equalsIgnoreCase("true")){

				  isCheck=true;
			  }
			  if(resource!=null){
  		orderId=Long.parseLong(resource);
			  }
			  
  		return new EventActionProcedureData(isCheck,orderId);
	}



	@Override
	public List<EventActionData> retrieveAllActionsForProccessing() {
		try{
			
			EventActionMapper mapper = new EventActionMapper();
			String sql = "select " + mapper.schema();
			return this.jdbcTemplate.query(sql, mapper, new Object[] { });
			
		}catch(EmptyResultDataAccessException accessException){
			
		}
		return null;
	}
	private static final class EventActionMapper implements RowMapper<EventActionData> {

		public String schema() {
			return " a.id as id,a.event_action AS eventaction,a.entity_name AS entityName,a.action_name AS actionName, a.command_as_json as json,a.resource_id as resourceId, " +
					" a.order_id as orderId,a.client_id as clientId FROM b_event_actions a WHERE a.is_processed = 'N'";

		}

		@Override
		public EventActionData mapRow(final ResultSet rs,
				@SuppressWarnings("unused") final int rowNum)
				throws SQLException {
			Long id = rs.getLong("id");
			String eventaction = rs.getString("eventaction");
			String entityName = rs.getString("entityName");
			String actionName = rs.getString("actionName");
			String jsonData = rs.getString("json");
			Long resourceId=rs.getLong("resourceId");
			Long orderId=rs.getLong("orderId");
			Long clientId=rs.getLong("clientId");
			return new EventActionData(id,eventaction,entityName,actionName,jsonData,resourceId,orderId,clientId);

		}
	}
	

}

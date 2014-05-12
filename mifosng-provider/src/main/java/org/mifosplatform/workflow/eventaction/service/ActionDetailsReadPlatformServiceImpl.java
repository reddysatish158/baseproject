package org.mifosplatform.workflow.eventaction.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



import net.sf.json.JSONException;

import org.joda.time.LocalDate;
import org.json.JSONObject;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.contract.domain.Contract;
import org.mifosplatform.portfolio.contract.domain.SubscriptionRepository;
import org.mifosplatform.portfolio.order.data.SchedulingOrderData;
import org.mifosplatform.portfolio.plan.domain.Plan;
import org.mifosplatform.portfolio.plan.domain.PlanRepository;
import org.mifosplatform.portfolio.planservice.data.PlanServiceData;
import org.mifosplatform.scheduledjobs.scheduledjobs.data.EventActionData;
import org.mifosplatform.workflow.eventaction.data.ActionDetaislData;
import org.mifosplatform.workflow.eventaction.data.EventActionProcedureData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonParser;

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
			return "em.id as actionId,em.action_name as actionName,em.process as processName,em.is_synchronous as isSync" +
					" from b_eventaction_mapping em where em.event_name=? and em.is_deleted='N'";

		}

		@Override
		public ActionDetaislData mapRow(final ResultSet rs,
				@SuppressWarnings("unused") final int rowNum)
				throws SQLException {
			Long id = rs.getLong("actionId");
			String procedureName = rs.getString("processName");
			String procedureType = rs.getString("actionName");
			String isSynchronous = rs.getString("isSync");
			return new ActionDetaislData(id,procedureName,procedureType,isSynchronous);

		}
	}

	@Override
	public EventActionProcedureData checkCustomeValidationForEvents(Long clientId,String event, String action, String resourceId) {
       
	
			  jdbcCall.setProcedureName("workflow_events");
			  MapSqlParameterSource parameterSource = new MapSqlParameterSource();
			  parameterSource.addValue("clientid", clientId.toString(), Types.VARCHAR);
			  parameterSource.addValue("eventname", event, Types.VARCHAR);
			  parameterSource.addValue("actionname", action, Types.VARCHAR);
			  parameterSource.addValue("resourceid", resourceId, Types.VARCHAR);
			    
			  Map<String, Object> out = jdbcCall.execute(parameterSource);
			     
			  String result=(String)out.get("result");
			  String resource=(String)out.get("strjson");
			  
			  boolean isCheck=false;
			  String orderresource=null;
			  String actionName=null;
			  Long orderId=null;
			  String planId=null;
			  if(result.equalsIgnoreCase("true") && resource != null){

				  isCheck=true;
				  String[] resultdatas=resource.split(" ");
				  Map<String,String> map=new HashMap<String, String>();
				   
				  for(String resultData:resultdatas){
					   String[] data=resultData.split(":");
					   map.put(data[0],data[1]);
					   
				   }
				  
				  actionName=map.get("action");
				  orderresource=map.get("orderid");
				  planId=map.get("planid");
			  }
			  if(orderresource !=null){
  		   orderId=Long.parseLong(orderresource);
			  }
			  
  		return new EventActionProcedureData(isCheck,orderId,actionName,planId);
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
					" a.order_id as orderId,a.client_id as clientId FROM b_event_actions a WHERE a.is_processed = 'N'  and a.trans_date <=now()";

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
	
	private static final class EventActionOrderMapper implements RowMapper<SchedulingOrderData> {

		public String schema() {
			return " a.id AS id, a.trans_date AS transactiondate,a.command_as_json as json FROM b_event_actions a WHERE  a.client_id = ? " +
					" AND a.entity_name = 'Order' AND a.action_name = 'NEW' and a.is_processed='N' ";

		}

		@Override
		public SchedulingOrderData mapRow(final ResultSet rs,
				@SuppressWarnings("unused") final int rowNum)
				throws SQLException {
			try {
			Long id = rs.getLong("id");
			LocalDate startDate= JdbcSupport.getLocalDate(rs,"transactiondate");
			String jsonData = rs.getString("json");
			
			JSONObject jsonObject = new JSONObject(jsonData);
			String billingfreq=jsonObject.getString("paytermCode");
			Long planId=jsonObject.getLong("planCode");
			Long contractPeriod=jsonObject.getLong("contractPeriod");
			
			return new SchedulingOrderData(id,startDate,planId,contractPeriod,billingfreq);
			} catch (org.json.JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
	}

	@Override
	public Collection<SchedulingOrderData> retrieveClientSchedulingOrders(Long clientId) {
		
		try{
			
			EventActionOrderMapper mapper = new EventActionOrderMapper();
			String sql = "select " + mapper.schema();
			return this.jdbcTemplate.query(sql, mapper, new Object[] { clientId });
			
		}catch(EmptyResultDataAccessException accessException){

			return null;
		}
	}
	

}

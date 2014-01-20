package org.mifosplatform.billing.promotioncodes.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.billing.contract.data.SubscriptionData;
import org.mifosplatform.billing.discountmaster.service.DiscountReadPlatformService;
import org.mifosplatform.billing.eventactionmapping.data.EventActionMappingData;
import org.mifosplatform.billing.hardwareplanmapping.data.HardwarePlanData;
import org.mifosplatform.billing.item.data.ItemData;
import org.mifosplatform.billing.order.data.OrderStatusEnumaration;
import org.mifosplatform.billing.order.data.VolumeTypeEnumaration;
import org.mifosplatform.billing.paymode.data.McodeData;
import org.mifosplatform.billing.plan.data.BillRuleData;
import org.mifosplatform.billing.plan.data.PlanData;
import org.mifosplatform.billing.plan.data.ServiceData;
import org.mifosplatform.billing.plan.data.SystemData;
import org.mifosplatform.billing.plan.domain.StatusTypeEnum;
import org.mifosplatform.billing.plan.domain.VolumeTypeEnum;
import org.mifosplatform.billing.promotioncodes.data.PromotionCodeData;
import org.mifosplatform.billing.randomgenerator.domain.PinCategory;
import org.mifosplatform.billing.randomgenerator.domain.PinType;
import org.mifosplatform.billing.randomgenerator.service.RandomGeneratorEnumeration;
import org.mifosplatform.billing.randomgenerator.service.RandomGeneratorEnumerationType;
import org.mifosplatform.billing.service.DiscountMasterData;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class PromotionCodeReadPlatformServiceImpl implements PromotionCodeReadPlatformService
{

	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;


	@Autowired
	public PromotionCodeReadPlatformServiceImpl(final PlatformSecurityContext context,final TenantAwareRoutingDataSource dataSource) {
		this.context = context;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public List<PromotionCodeData> retrieveAllEventMapping() {
   
	try{	
		context.authenticatedUser();
		EventActionMapper mapper = new EventActionMapper();

		String sql = "select " + mapper.schema();

		return this.jdbcTemplate.query(sql, mapper, new Object[] {});
	}catch(EmptyResultDataAccessException accessException){
		return null;
	}

	}

	private static final class EventActionMapper implements RowMapper<PromotionCodeData> {

		public String schema() {
			return "pm.id as id, pm.promotion_code as promotionCode, pm.promotion_description as promotionDescription," +
				 " pm.duration_type as durationType,pm.duration as duration,pm.discount_type as discountType,"+
					" pm.discount_rate as discountRate from b_promotion_master pm  where pm.is_delete='N' ";

		}
	

		@Override
		public PromotionCodeData mapRow(final ResultSet rs,
				@SuppressWarnings("unused") final int rowNum)
				throws SQLException {

			Long id = rs.getLong("id");
			String promotionCode = rs.getString("promotionCode");
			String promotionDescription = rs.getString("promotionDescription");
			String durationType = rs.getString("durationType");
			Long duration=rs.getLong("duration");
			String discountType = rs.getString("discountType");
			BigDecimal discountRate=rs.getBigDecimal("discountRate");
			
			//BigDecimal discountRate = rs.getBigDecimal("discountRate");
			//LocalDate startDate = JdbcSupport.getLocalDate(rs,"startDate");
			//String status = rs.getString("status");
			return new PromotionCodeData(id, promotionCode, promotionDescription, durationType,duration,discountType,discountRate,null,null);

		}
	}

	
	/*public List<McodeData> retrieveEventMapData(String str) {

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
	public PromotionCodeData retrieveEventActionDetail(Long discountId) {
		try{	
			context.authenticatedUser();
			EventActionMapper mapper = new EventActionMapper();

			String sql = "select " + mapper.schema()+" and em.id=?";

			return this.jdbcTemplate.queryForObject(sql, mapper, new Object[] { discountId });
		}catch(EmptyResultDataAccessException accessException){
			return null;
		}

		}
	
	@Override
	public List<PromotionCodeData> retrieveEvents(String event) {
		
		context.authenticatedUser();

		 String sql=null;
		 EventActionMapper1 mapper = new EventActionMapper1();
		 sql = "select " + mapper.schema()+"  where event_name=?";
		return this.jdbcTemplate.query(sql, mapper, new Object[] { event});
	}
	
	private static final class EventActionMapper1 implements RowMapper<PromotionCodeData> {

		public String schema() {
			return "b.action_name as actionName from b_eventaction_mapping b";

		}
	

		@Override
		public PromotionCodeData mapRow(final ResultSet rs,
				@SuppressWarnings("unused") final int rowNum)
				throws SQLException {

			String actionName = rs.getString("actionName");
			
			//BigDecimal discountRate = rs.getBigDecimal("discountRate");
			//LocalDate startDate = JdbcSupport.getLocalDate(rs,"startDate");
			//String status = rs.getString("status");
			return new PromotionCodeData(null, null, actionName, null,null,null,null,null,null);

		}
	}
*/
	
}


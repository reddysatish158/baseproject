package org.mifosplatform.billing.promotioncodes.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.mifosplatform.billing.promotioncodes.data.PromotionCodeData;
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
			return new PromotionCodeData(id, promotionCode, promotionDescription, durationType,duration,discountType,discountRate);

		}
	}

	@Override
	public PromotionCodeData retriveSingleRecord(Long id) {
		try{	
			context.authenticatedUser();
			EventActionMapper mapper = new EventActionMapper();

			String sql = "select " + mapper.schema()+" and pm.id=?";

			return this.jdbcTemplate.queryForObject(sql, mapper, new Object[] { id });
		}catch(EmptyResultDataAccessException accessException){
			return null;
		}

		}

}


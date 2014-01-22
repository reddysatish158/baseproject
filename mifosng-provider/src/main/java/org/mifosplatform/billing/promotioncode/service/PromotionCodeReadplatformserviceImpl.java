package org.mifosplatform.billing.promotioncode.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.mifosplatform.billing.promotioncode.data.PromotionCodeData;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PromotionCodeReadplatformserviceImpl implements PromotionCodeReadplatformservice{

	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;
	@Autowired
	public PromotionCodeReadplatformserviceImpl(final PlatformSecurityContext context,final TenantAwareRoutingDataSource dataSource) {
		
		this.context = context;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Transactional
	@Override
	public List<PromotionCodeData> retrieveAllPromotionData() {

		context.authenticatedUser();
		PlanMapper mapper = new PlanMapper();
		String sql = "select " + mapper.schema();
		return this.jdbcTemplate.query(sql, mapper, new Object[] {});
	}

	private static final class PlanMapper implements RowMapper<PromotionCodeData> {
		public String schema() {
			return " p.id AS id,p.promotion_code AS promotionCode, p.promotion_description as promotionDescription,p.duration_type AS durationType,p.duration AS duration,p.discount_type AS discountType," +
					" p.discount_rate as discountRate FROM b_promotion_master p WHERE p.is_delete = 'N'";
		}

		@Override
		public PromotionCodeData mapRow(final ResultSet rs,@SuppressWarnings("unused") final int rowNum)
				throws SQLException {

			Long id = rs.getLong("id");
			String promotionCode = rs.getString("promotionCode");
			String promotionDescription = rs.getString("promotionDescription");
			String durationType = rs.getString("durationType");
			
			Long duration = rs.getLong("duration");
			String discountType = rs.getString("discountType");
			BigDecimal discountRate=rs.getBigDecimal("discountRate");
			return new PromotionCodeData(id,promotionCode,promotionDescription,durationType,duration, discountType,discountRate);

		}
	}

}

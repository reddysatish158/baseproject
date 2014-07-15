package org.mifosplatform.portfolio.planmapping.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.plan.data.PlanCodeData;
import org.mifosplatform.portfolio.planmapping.data.PlanMappingData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class PlanMappingReadPlatformServiceImpl implements PlanMappingReadPlatformService {
	
	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;


	@Autowired
	public PlanMappingReadPlatformServiceImpl(final PlatformSecurityContext context,
			final TenantAwareRoutingDataSource dataSource) {
		this.context = context;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public PlanMappingData getPlanMapping(Long planMappingId) {

		PlanMappingMapper rowMapper = new PlanMappingMapper();
		String sql = "select " + rowMapper.schema() + " and ps.id = ?";
		return jdbcTemplate.queryForObject(sql, rowMapper, new Object[] { planMappingId });
	}
	
	private static final class PlanMappingMapper implements
			RowMapper<PlanMappingData> {
		public String schema() {

			return " ps.id as id, ps.plan_id as planId, p.plan_code as planCode, ps.plan_identification as planIdentification, " +
					" ps.status as status from  b_plan_master p, b_prov_plan_details ps where p.plan_status = 1 and ps.plan_id=p.id ";
		}

		@Override
		public PlanMappingData mapRow(final ResultSet rs, final int rowNum)
				throws SQLException {

			Long id = rs.getLong("id");
			String planCode = rs.getString("planCode");
			String planIdentification = rs.getString("planIdentification");
			String status = rs.getString("status");
			Long planId = rs.getLong("planId");

			return new PlanMappingData(id, planCode, planIdentification, status, planId);
		}
	}
	
	@Override
	public List<PlanMappingData> getPlanMapping() {
		PlanMappingMapper mapper = new PlanMappingMapper();
		String sql = "Select " + mapper.schema() + " ORDER BY ps.id";
		return this.jdbcTemplate.query(sql, mapper,new Object[] {});
	}
	
	
	private class planCodeDataMapper implements RowMapper<PlanCodeData> {
		public String schema() {

			return " pm.id as id, pm.plan_code as planCode, pm.plan_description as planDescription from b_plan_master pm "
					+ " where pm.plan_status=1 and pm.is_deleted ='N' order by pm.id";

		}

		public PlanCodeData mapRow(final ResultSet rs, final int rowNum) throws SQLException {
			
			Long id = rs.getLong("id");
			String planCode = rs.getString("planCode");
			String planDescription = rs.getString("planDescription");

			return new PlanCodeData(id, planCode, planDescription);

		}
	}

	@Override
	public List<PlanCodeData> getPlanCode() {

		planCodeDataMapper rowMapper = new planCodeDataMapper();

		String sql = "select " + rowMapper.schema();

		return jdbcTemplate.query(sql, rowMapper);
	}

	
	
	


}

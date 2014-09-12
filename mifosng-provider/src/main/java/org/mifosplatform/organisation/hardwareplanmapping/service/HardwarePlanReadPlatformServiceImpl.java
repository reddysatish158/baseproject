package org.mifosplatform.organisation.hardwareplanmapping.service;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.logistics.item.data.ItemData;
import org.mifosplatform.organisation.hardwareplanmapping.data.HardwareMappingDetailsData;
import org.mifosplatform.organisation.hardwareplanmapping.data.HardwarePlanData;
import org.mifosplatform.portfolio.plan.data.PlanCodeData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class HardwarePlanReadPlatformServiceImpl implements HardwarePlanReadPlatformService {

	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;


	@Autowired
	public HardwarePlanReadPlatformServiceImpl(final PlatformSecurityContext context,
			final TenantAwareRoutingDataSource dataSource) {
		this.context = context;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public List<HardwarePlanData> retrievePlanData(String itemCode) {

		context.authenticatedUser();

		 String sql=null;
		PlanDataMapper mapper = new PlanDataMapper();
		 sql = "select " + mapper.schema();
		return this.jdbcTemplate.query(sql, mapper, new Object[] {});
		
	}

	private static final class PlanDataMapper implements RowMapper<HardwarePlanData> {

		public String schema() {
			return "h.id as id,h.plan_code as planCode,h.item_code as itemCode from b_hw_plan_mapping h";

		}

		@Override
		public HardwarePlanData mapRow(ResultSet rs, int rowNum) throws SQLException {

			Long id = rs.getLong("id");
			String planCode = rs.getString("planCode");
			String itemCode = rs.getString("itemCode");
			return new HardwarePlanData(id, planCode, itemCode);
		}
	}
	
	@Override
	public List<ItemData> retrieveItems() {

		context.authenticatedUser();
		ItemDataMaper mapper = new ItemDataMaper();

		String sql = "select " + mapper.schema();

		return this.jdbcTemplate.query(sql, mapper, new Object[] {});

	}

	private static final class ItemDataMaper implements RowMapper<ItemData> {

		public String schema() {
			return " i.id as id,i.item_code as itemCode,i.item_description as itemDescription from b_item_master i";

		}

		@Override
		public ItemData mapRow(final ResultSet rs,
				@SuppressWarnings("unused") final int rowNum)
				throws SQLException {
            Long id=rs.getLong("id");
			String itemCode = rs.getString("itemCode");
			String itemDescription = rs.getString("itemDescription");
			return new ItemData(id,itemCode,itemDescription, null, null, null, 0, null,null,null,null);

		}
	}
	
	@Override
	public List<PlanCodeData> retrievePlans() {

		context.authenticatedUser();

		PlanDataMaper mapper = new PlanDataMaper();

		String sql = "select " + mapper.schema();

		return this.jdbcTemplate.query(sql, mapper, new Object[] {});
	}

	private static final class PlanDataMaper implements
			RowMapper<PlanCodeData> {

		public String schema() {
			return " p.id as id,p.plan_code as planCode,p.plan_description as planDescription from b_plan_master p where p.is_deleted = 'N'";

		}

		@Override
		public PlanCodeData mapRow(ResultSet rs, int rowNum)
				throws SQLException {

			Long id=rs.getLong("id");
			String planCode = rs.getString("planCode");
			String planDescription = rs.getString("planDescription");
			return  new PlanCodeData(id,planCode,planDescription);
		}

	}
	
	@Override
	public HardwarePlanData retrieveSinglePlanData(Long planId) {
		  context.authenticatedUser();

	        String sql = "select b.id as id,b.item_code as itemcode,b.plan_code as plancode from b_hw_plan_mapping b where id=?";


	        RowMapper<HardwarePlanData> rm = new ServiceMapper();

	        return this.jdbcTemplate.queryForObject(sql, rm, new Object[] { planId });
	}
	
	
	 private static final class ServiceMapper implements RowMapper<HardwarePlanData> {

	        @Override
	        public HardwarePlanData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

	        Long id = rs.getLong("id");
	        String planCode = rs.getString("plancode");
	       
	        String itemCode = rs.getString("itemcode");
	       
	      return new HardwarePlanData(id,planCode,itemCode);
	      
	        }
	}

	@Override
	public List<HardwarePlanData> retrieveItems(String itemCode) {
		
		context.authenticatedUser();

		 String sql=null;
		PlanDataMapper mapper = new PlanDataMapper();
		 sql = "select " + mapper.schema()+" where h.item_code=?";
		return this.jdbcTemplate.query(sql, mapper, new Object[] { itemCode});
	}

	@Override
	public List<HardwareMappingDetailsData> getPlanDetailsByItemCode(
			String itemCode, Long clientId) {
        try
        {
       	 this.context.authenticatedUser();
       	 HardwareMapper mapper=new HardwareMapper();
       	 String sql="select"+mapper.schema();
       		return this.jdbcTemplate.query(sql, mapper, new Object[] {itemCode,clientId});
       		
        }catch(EmptyResultDataAccessException accessException)
        {
       	 return null;
        }
		
	}
	
private static final class HardwareMapper implements RowMapper<HardwareMappingDetailsData>{

		
		public String schema() {
		
			return " p.id AS planId, p.plan_code AS planCode, o.id AS orderId FROM b_orders o,b_plan_master p,b_hw_plan_mapping phw" +
					"  WHERE p.id = o.plan_id  AND phw.plan_code = p.plan_code and phw.item_code =? and o.client_id=?";
		}
		
		@Override
		public HardwareMappingDetailsData mapRow(ResultSet rs, int rowNum)
				throws SQLException {

			Long planId = rs.getLong("planId");
			Long orderId = rs.getLong("orderId");
			String planCode = rs.getString("planCode");
			
			return new HardwareMappingDetailsData(planId,orderId,planCode);
		}

		
		
	}

	}

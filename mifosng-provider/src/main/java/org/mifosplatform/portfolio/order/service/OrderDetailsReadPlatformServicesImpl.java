package org.mifosplatform.portfolio.order.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Map;

import org.mifosplatform.billing.pricing.data.PriceData;
import org.mifosplatform.billing.pricing.service.PriceReadPlatformService;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.order.data.CustomValidationData;
import org.mifosplatform.portfolio.plan.data.ServiceData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailsReadPlatformServicesImpl implements OrderDetailsReadPlatformServices{

	  private final JdbcTemplate jdbcTemplate;
      private final PlatformSecurityContext context;
	  private final SimpleJdbcCall jdbcCall;
	
 @Autowired
 public OrderDetailsReadPlatformServicesImpl(final PlatformSecurityContext context, final TenantAwareRoutingDataSource dataSource,
			final PriceReadPlatformService priceReadPlatformService) {
	        this.context = context;
	        this.jdbcTemplate = new JdbcTemplate(dataSource);
	        this.jdbcCall= new SimpleJdbcCall(dataSource);
	       	     

	    }

	@Override
	public List<ServiceData> retrieveAllServices(Long plan_code) {


		PlanMapper mapper = new PlanMapper();

		String sql = "select " + mapper.schema()+" and da.plan_id = '"+plan_code+"'" ;
		return this.jdbcTemplate.query(sql, mapper, new Object[] {});

	}

	private static final class PlanMapper implements RowMapper<ServiceData> {

		public String schema() {
			return "da.id as id,se.id as serviceId, da.service_code as service_code, da.plan_id as plan_code,se.service_type as serviceType "
					+" from b_plan_detail da,b_service se where da.service_code = se.service_code";

		}

		@Override
		public ServiceData mapRow(final ResultSet rs,
				@SuppressWarnings("unused") final int rowNum)
				throws SQLException {
			Long id = rs.getLong("id");
			String serviceCode = rs.getString("service_code");
			String serviceType = rs.getString("serviceType");
			Long serviceid = rs.getLong("serviceId");

			ServiceData serviceData=new ServiceData(id,serviceid,serviceCode, null,null,null, null);
			
			serviceData.setServiceType(serviceType);


			return serviceData; 

		}
	}
	@Override
		public List<PriceData> retrieveAllPrices(Long plan_code,String billingFreq, Long clientId ) {


			PriceMapper mapper1 = new PriceMapper();

			String sql = "select " + mapper1.schema()+" and da.plan_id = '"+plan_code+"' and (c.billfrequency_code='"+billingFreq+"'  or c.billfrequency_code='Once')" +
					" AND ca.client_id = ?  AND da.price_region_id =pd.priceregion_id AND s.state_name = ca.state And s.parent_code=pd.country_id" +
					" AND pd.state_id = s.id group by da.id";
			return this.jdbcTemplate.query(sql, mapper1, new Object[] { clientId });

		} 

		private static final class PriceMapper implements RowMapper<PriceData> {

			public String schema() {
				return " da.id AS id,if(da.service_code ='None',0, se.id) AS serviceId, da.service_code AS service_code,da.charge_code AS charge_code,da.charging_variant AS charging_variant," +
						"c.charge_type AS charge_type,c.charge_duration AS charge_duration,c.duration_type AS duration_type,da.discount_id AS discountId," +
						"c.tax_inclusive AS taxInclusive,da.price AS price,da.price_region_id,s.id AS stateId,s.parent_code AS countryId,pd.state_id AS regionState," +
						"pd.country_id AS regionCountryId FROM b_plan_pricing da,b_charge_codes c,b_service se,b_client_address ca,b_state s,b_priceregion_detail pd" +
						" WHERE  da.charge_code = c.charge_code  AND ( da.service_code = se.service_code or da.service_code ='None') AND da.is_deleted = 'n' AND ca.address_key='PRIMARY'" ;
					   

			}

			@Override
			public PriceData mapRow(final ResultSet rs,
					@SuppressWarnings("unused") final int rowNum)
					throws SQLException {

				Long id = rs.getLong("id");
				String serviceCode = rs.getString("service_code");
				String chargeCode = rs.getString("charge_code");
				String chargingVariant = rs.getString("charging_variant");
				BigDecimal price=rs.getBigDecimal("price");
				String chargeType = rs.getString("charge_type");
				String chargeDuration = rs.getString("charge_duration");
				String durationType = rs.getString("duration_type");
				Long serviceid = rs.getLong("serviceId");
				Long discountId=rs.getLong("discountId");
				Long stateId = rs.getLong("stateId");
				Long countryId=rs.getLong("countryId");
				Long regionState = rs.getLong("regionState");
				Long regionCountryId=rs.getLong("regionCountryId");
				boolean taxinclusive=rs.getBoolean("taxInclusive");
				
				return new PriceData(id, serviceCode, chargeCode,chargingVariant,price,chargeType,chargeDuration,durationType,
			              serviceid,discountId,taxinclusive,stateId,countryId,regionState,regionCountryId);

			}
	}
		
		@Override
		public List<PriceData> retrieveDefaultPrices(Long planId,String billingFrequency, Long clientId) {
			
			PriceMapper mapper1 = new PriceMapper();
			String sql = "select " + mapper1.schema()+" and da.plan_id = '"+planId+"' and (c.billfrequency_code='"+billingFrequency+"'  or c.billfrequency_code='Once')" +
					" AND ca.client_id = ?  AND da.price_region_id =pd.priceregion_id AND s.state_name = ca.state And s.parent_code=pd.country_id" +
					" AND pd.state_id =0 group by da.id";
			return this.jdbcTemplate.query(sql, mapper1, new Object[] { clientId });
		}



		@Override
		public CustomValidationData checkForCustomValidations(Long clientId,String eventName,String strjson) {
		       
			
					  jdbcCall.setProcedureName("custom_validation");
					  MapSqlParameterSource parameterSource = new MapSqlParameterSource();
					  parameterSource.addValue("p_clientid", clientId, Types.INTEGER);
					  parameterSource.addValue("jsonstr", strjson, Types.VARCHAR);
					  parameterSource.addValue("event_name", eventName, Types.VARCHAR);
					  Map<String, Object> out = jdbcCall.execute(parameterSource);
					  
					  Integer errCode=0;
					  String errMsg=null;
					  if(out != null){
					   errCode=(Integer)out.get("err_code");
					   errMsg=(String)out.get("err_msg");
					  }
					  
					  return new CustomValidationData(errCode.longValue() ,errMsg);
					  
			 		
			}

	}



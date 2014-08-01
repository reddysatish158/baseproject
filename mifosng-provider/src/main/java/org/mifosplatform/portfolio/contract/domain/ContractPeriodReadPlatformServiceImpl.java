package org.mifosplatform.portfolio.contract.domain;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.contract.data.PeriodData;
import org.mifosplatform.portfolio.contract.data.SubscriptionData;
import org.mifosplatform.portfolio.contract.service.ContractPeriodReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;


@Service
public class ContractPeriodReadPlatformServiceImpl implements ContractPeriodReadPlatformService {



	private final PlatformSecurityContext context;
	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public ContractPeriodReadPlatformServiceImpl(final PlatformSecurityContext context,final TenantAwareRoutingDataSource dataSource) {
		this.context=context;
		jdbcTemplate=new JdbcTemplate(dataSource);
	}


	@Override
	public Collection<SubscriptionData> retrieveSubscriptionDetails() {

		this.context.authenticatedUser();
	    SuscriptionMapper depositProductMapper= new SuscriptionMapper();
		String sql="select "+depositProductMapper.contractPeriodSchema ();
		return this.jdbcTemplate.query(sql,depositProductMapper, new Object[]{});

	}

	@Override
	public Collection<SubscriptionData> retrieveAllSubscription() {
		this.context.authenticatedUser();
		SuscriptionMapper mapper= new SuscriptionMapper();
		String sql="select "+mapper.contractPeriodSchema()+"where dp.is_deleted='N'";
		return this.jdbcTemplate.query(sql,mapper, new Object[]{});
	}

	@Override
	public SubscriptionData retrieveSubscriptionData(Long subscriptionId) {
		SuscriptionMapper depositProductMapper=new SuscriptionMapper();
		String sql = "select "+ depositProductMapper.contractPeriodSchema() +" where dp.id = ? and dp.is_deleted= 'N'";

		return this.jdbcTemplate.queryForObject(sql, depositProductMapper, new Object[]{subscriptionId});
	}


private static final class SuscriptionMapper implements RowMapper<SubscriptionData>{

		public String contractPeriodSchema(){
			return " dp.id as id,dp.contract_period as  subscriptionPeriod,dp.contract_type as subscriptionType,dp.contract_duration as units from b_contract_period dp ";

		}

		@Override
		public SubscriptionData mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum)
				throws SQLException {

			Long id = rs.getLong("id");
			String subscriptionPeriod = rs.getString("subscriptionPeriod");
			String subscriptionType = rs.getString("subscriptionType");
			Long units = rs.getLong("units");




			return new SubscriptionData(id,subscriptionPeriod,subscriptionType,units,null,null);
		}



	}


@Override
public List<PeriodData> retrieveAllPlatformPeriod() {
	  context.authenticatedUser();

        String sql = "select distinct s.contract_type as subscriptionType from b_contract_period s";

        RowMapper<PeriodData> rm = new PeriodMapper();

        return this.jdbcTemplate.query(sql, rm, new Object[] {});
}

 private static final class PeriodMapper implements RowMapper<PeriodData> {

        @Override
        public PeriodData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            String type = rs.getString("subscriptionType");
            return new PeriodData(type,null,type);
        }
 }

@Override
public List<SubscriptionData> retrieveSubscriptionDatabyContractType(String contractType, int duration) {
	//this.context.authenticatedUser();
    SuscriptionMapper depositProductMapper= new SuscriptionMapper();
	String sql="select "+depositProductMapper.contractPeriodSchema ()+" where dp.contract_type=? and dp.contract_duration=? and dp.is_deleted='N'";
	return this.jdbcTemplate.query(sql,depositProductMapper, new Object[]{ contractType,duration});

}


@Override
public List<SubscriptionData> retrieveSubscriptionDatabyOrder(Long orderId) {
	//this.context.authenticatedUser();
    SuscriptionMapper depositProductMapper= new SuscriptionMapper();
	String sql="select "+depositProductMapper.contractPeriodSchema ()+" , b_orders o where o.contract_period = dp.id and o.id=? and dp.is_deleted='N'";
	return this.jdbcTemplate.query(sql,depositProductMapper, new Object[]{ orderId });
}




}

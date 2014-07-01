 package org.mifosplatform.billing.emun.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import org.mifosplatform.billing.emun.data.EnumValuesData;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class EnumReadplaformServiceImpl implements EnumReadplaformService{
  
		private JdbcTemplate jdbcTemplate;
		private PlatformSecurityContext context;
		
		@Autowired
		public EnumReadplaformServiceImpl(final PlatformSecurityContext context, final TenantAwareRoutingDataSource dataSource ) {
			this.context = context;
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		}
	
	@Override
	public Collection<EnumValuesData> getEnumValues(String enumName) {
		
		
		MCodeDataMapper rowMapper = new MCodeDataMapper();
		String sql = "select " + rowMapper.codeScheme();
		return jdbcTemplate.query(sql, rowMapper,new Object[]{ enumName });
	}
	
	private final class MCodeDataMapper implements RowMapper<EnumValuesData>{
		
		
		@Override
		public EnumValuesData mapRow(ResultSet rs, int rowNum) throws SQLException {
			final Long id = rs.getLong("id");
			final String value = rs.getString("value");
			final String type = rs.getString("type");
			return new EnumValuesData(id,value,type);
		}
		
		public String codeScheme(){
			return " r.enum_id as id,r.enum_message_property AS type,r.enum_value as value FROM r_enum_value r WHERE r.enum_name = ?";
		}
	}
	

}

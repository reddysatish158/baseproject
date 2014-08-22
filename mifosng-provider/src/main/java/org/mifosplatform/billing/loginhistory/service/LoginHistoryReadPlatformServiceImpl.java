package org.mifosplatform.billing.loginhistory.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.mifosplatform.billing.invoice.data.InvoiceData;
import org.mifosplatform.billing.loginhistory.data.LoginHistoryData;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class LoginHistoryReadPlatformServiceImpl implements LoginHistoryReadPlatformService
{

	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;


	@Autowired
	public LoginHistoryReadPlatformServiceImpl(final PlatformSecurityContext context,final TenantAwareRoutingDataSource dataSource) {
		this.context = context;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public LoginHistoryData retrieveSessionId(String id) {
		
		try{	
			//context.authenticatedUser();
			LoginMapper mapper = new LoginMapper();

			String sql = "select id from b_login_history where session_id=?";

			return this.jdbcTemplate.queryForObject(sql, mapper, new Object[] { id });
		}catch(EmptyResultDataAccessException accessException){
			return null;
		}
		
	}
	
	private static final class LoginMapper implements RowMapper<LoginHistoryData> {

		@Override
		public LoginHistoryData mapRow(final ResultSet rs,@SuppressWarnings("unused") final int rowNum)
				throws SQLException {

			Long id = rs.getLong("id");
			return new LoginHistoryData(id);

		}
	}


}


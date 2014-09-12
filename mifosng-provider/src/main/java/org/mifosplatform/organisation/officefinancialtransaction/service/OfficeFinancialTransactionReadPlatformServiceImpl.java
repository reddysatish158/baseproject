package org.mifosplatform.organisation.officefinancialtransaction.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import org.joda.time.LocalDate;
import org.mifosplatform.finance.financialtransaction.data.FinancialTransactionsData;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class OfficeFinancialTransactionReadPlatformServiceImpl implements OfficeFinancialTransactionReadPlatformService{

	private final PlatformSecurityContext context;
	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public OfficeFinancialTransactionReadPlatformServiceImpl(final PlatformSecurityContext context,
															final TenantAwareRoutingDataSource dataSource) {
						this.context = context;
						this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public Collection<FinancialTransactionsData> retreiveOfficeFinancialTransactionsData(Long officeId) {
		
		context.authenticatedUser();
		OfficeFinancialTransactionMapper mapper = new OfficeFinancialTransactionMapper(); 
		String sql = "select v.* from  office_fin_trans_vw v where v.office_id="+officeId+" order by  transDate desc ";
		return this.jdbcTemplate.query(sql, mapper, new Object[] {});
	}
	
	private static final class OfficeFinancialTransactionMapper implements RowMapper<FinancialTransactionsData> {

		@Override
		public FinancialTransactionsData mapRow(final ResultSet rs, final int rowNum)throws SQLException {
			 Long officeId = rs.getLong("office_id");
			Long transactionId = rs.getLong("TransId");
			String transactionType = rs.getString("TransType");
			BigDecimal debitAmount = rs.getBigDecimal("Dr_amt");
			BigDecimal creditAmount =rs.getBigDecimal("Cr_amt");
			String userName=rs.getString("username");
			String transactionCategory=rs.getString("tran_type");
			boolean flag=rs.getBoolean("flag");
			LocalDate transDate=JdbcSupport.getLocalDate(rs,"TransDate");

			return new FinancialTransactionsData(officeId,transactionId,transDate,transactionType,debitAmount,creditAmount,null,userName,transactionCategory,flag);
		}
     }
}

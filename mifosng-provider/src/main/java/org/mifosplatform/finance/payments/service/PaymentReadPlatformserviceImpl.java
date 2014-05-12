package org.mifosplatform.finance.payments.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.billing.invoice.data.InvoiceData;
import org.mifosplatform.finance.payments.data.PaymentData;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class PaymentReadPlatformserviceImpl implements PaymentReadPlatformservice
{

	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;


	@Autowired
	public PaymentReadPlatformserviceImpl(final PlatformSecurityContext context,final TenantAwareRoutingDataSource dataSource) {
		this.context = context;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public List<PaymentData> retrieveClientPaymentDetails(Long clientId) {
   
	try{	
		context.authenticatedUser();
		InvoiceMapper mapper = new InvoiceMapper();
		String sql = "select " + mapper.schema();
		return this.jdbcTemplate.query(sql, mapper, new Object[] {clientId});
	}catch(EmptyResultDataAccessException accessException){
		return null;
	}

	}

	private static final class InvoiceMapper implements RowMapper<PaymentData> {

		public String schema() {
			return  "  p.id AS id,p.payment_date AS paymentdate,p.amount_paid AS amount,p.receipt_no AS recieptNo,p.amount_paid - (ifnull((SELECT SUM(amount)" +
					"  FROM b_credit_distribution WHERE payment_id = p.id),0)) AS availAmount FROM b_payments p left join b_credit_distribution cd on p.client_id = cd.client_id" +
					"  WHERE p.client_id =? AND p.invoice_id IS NULL GROUP BY p.id ";
		}
	

		@Override
		public PaymentData mapRow(final ResultSet rs,@SuppressWarnings("unused") final int rowNum)
				throws SQLException {

			Long id = rs.getLong("id");
			LocalDate paymentdate=JdbcSupport.getLocalDate(rs,"paymentdate");
			BigDecimal amount=rs.getBigDecimal("amount");
			BigDecimal availAmount=rs.getBigDecimal("availAmount");
			String  recieptNo=rs.getString("recieptNo");
			
			return new PaymentData(id,paymentdate,amount,recieptNo,availAmount);

		}
	}

	

}



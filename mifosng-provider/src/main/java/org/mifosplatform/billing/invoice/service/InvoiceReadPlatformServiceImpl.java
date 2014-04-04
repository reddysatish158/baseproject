package org.mifosplatform.billing.invoice.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.mifosplatform.billing.invoice.data.InvoiceData;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class InvoiceReadPlatformServiceImpl implements InvoiceReadPlatformService
{

	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;


	@Autowired
	public InvoiceReadPlatformServiceImpl(final PlatformSecurityContext context,final TenantAwareRoutingDataSource dataSource) {
		this.context = context;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public List<InvoiceData> retrieveInvoiceDetails(Long id) {
   
	try{	
		context.authenticatedUser();
		InvoiceMapper mapper = new InvoiceMapper();

		String sql = "select " + mapper.schema();

		return this.jdbcTemplate.query(sql, mapper, new Object[] {id});
	}catch(EmptyResultDataAccessException accessException){
		return null;
	}

	}

	private static final class InvoiceMapper implements RowMapper<InvoiceData> {

		public String schema() {
			return "bi.id as id,bi.invoice_date as invoiceDate,bi.invoice_amount as invoiceAmount,bi.due_amount as dueAmount,bi.bill_id as billId " +
					" from b_invoice bi where bi.client_id=? and due_amount !=0 ";

		}
	

		@Override
		public InvoiceData mapRow(final ResultSet rs,@SuppressWarnings("unused") final int rowNum)
				throws SQLException {

			Long id = rs.getLong("id");
			Date invoiceDate=rs.getDate("invoiceDate");
			BigDecimal invoiceAmount=rs.getBigDecimal("invoiceAmount");
			Long dueAmount=rs.getLong("dueAmount");
			Long billId=rs.getLong("billId");
			return new InvoiceData(id, invoiceAmount, dueAmount, invoiceDate,billId);

		}
	}

	@Override
	public List<InvoiceData> retrieveDueAmountInvoiceDetails(Long clientId) {
		   
		try{	
			context.authenticatedUser();
			InvoiceMapper mapper = new InvoiceMapper();

			String sql = "select " + mapper.schema()+" and due_amount !=0";

			return this.jdbcTemplate.query(sql, mapper, new Object[] { clientId });
		}catch(EmptyResultDataAccessException accessException){
			return null;
		}

		}


}


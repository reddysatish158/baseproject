package org.mifosplatform.billing.paymentsgateway.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.billing.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.billing.paymentsgateway.data.PaymentEnum;
import org.mifosplatform.billing.paymentsgateway.data.PaymentGatewayData;
import org.mifosplatform.billing.paymentsgateway.domain.PaymentEnumClass;
import org.mifosplatform.infrastructure.core.data.MediaEnumoptionData;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.core.service.PaginationHelper;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class PaymentGatewayReadPlatformServiceImpl implements PaymentGatewayReadPlatformService {

	private final PaginationHelper<PaymentGatewayData> paginationHelper = new PaginationHelper<PaymentGatewayData>();
	private final PlatformSecurityContext context;
	private final JdbcTemplate jdbcTemplate;
	
	@Autowired
	public PaymentGatewayReadPlatformServiceImpl (final PlatformSecurityContext context,final TenantAwareRoutingDataSource dataSource) {
		this.context = context;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public Long retrieveClientIdForProvisioning(String serialNum) {
		try{
			this.context.authenticatedUser();
			String serialNumber=null;
			if(serialNum.charAt(0) == 'N' ||serialNum.charAt(0) == 'n'){
				serialNumber=serialNum;
			}else{
				
				serialNumber="N"+serialNum;
			}
		String sql = "select client_id as clientId from b_item_detail where serial_no = '"+serialNumber+"' ";
		return jdbcTemplate.queryForLong(sql);
		} catch(EmptyResultDataAccessException e){
			return null;
		}
	}
	
	private static final class PaymentMapper implements RowMapper<PaymentGatewayData> {

		public String schema() {
			return " p.id as id,p.key_id as serialNo,p.party_id as phoneNo,p.payment_date as paymentDate," +
					" p.amount_paid as amountPaid,p.receipt_no as receiptNo,p.t_details as clientName,p.status as status," +
					" p.Remarks as remarks,p.obs_id as paymentId from b_paymentgateway p";
		}
		
		@Override
		public PaymentGatewayData mapRow(ResultSet rs, int rowNum) throws SQLException {
			Long id = rs.getLong("id");
			String serialNo = rs.getString("serialNo");
			String phoneNo = rs.getString("phoneNo");
			LocalDate paymentDate=JdbcSupport.getLocalDate(rs,"paymentDate");
			BigDecimal amountPaid = rs.getBigDecimal("amountPaid");
			String receiptNo = rs.getString("receiptNo");
			String clientName = rs.getString("clientName");
			String status = rs.getString("status");
			Long paymentId = rs.getLong("paymentId");
			String remarks = rs.getString("remarks");
			
			
			return new PaymentGatewayData(id,serialNo,phoneNo,paymentDate,amountPaid,receiptNo,clientName,status,paymentId,remarks);
		}

	}
	
	@Override
	public List<MediaEnumoptionData> retrieveTemplateData() {
		this.context.authenticatedUser();
		MediaEnumoptionData FINISHED = PaymentEnumClass.enumPaymentData(PaymentEnum.FINISHED);
		MediaEnumoptionData INVALID = PaymentEnumClass.enumPaymentData(PaymentEnum.INVALID);
		
		List<MediaEnumoptionData> categotyType = Arrays.asList(FINISHED,INVALID);
		return categotyType;
	}

	@Override
	public PaymentGatewayData retrievePaymentGatewayIdData(Long id) {
		try{
			this.context.authenticatedUser();
			PaymentMapper mapper=new PaymentMapper();
			String sql = "select "+mapper.schema()+ " where p.id=?";
			return jdbcTemplate.queryForObject(sql, mapper, new Object[] {id});
			} catch(EmptyResultDataAccessException e){
				return null;
			}
	}

	@Override
	public Page<PaymentGatewayData> retrievePaymentGatewayData(SearchSqlQuery searchPaymentDetail
			,String tabType) {	
		// TODO Auto-generated method stub
		context.authenticatedUser();
		PaymentMapper mapper=new PaymentMapper();
		
		String sqlSearch = searchPaymentDetail.getSqlSearch();
	    String extraCriteria = "";
		StringBuilder sqlBuilder = new StringBuilder(200);
        sqlBuilder.append("select ");
        sqlBuilder.append(mapper.schema());
       
          
        if (tabType!=null ) {
        	
		        	tabType=tabType.trim();
		        	sqlBuilder.append(" where p.status like '"+tabType+"' order by payment_date desc ");
		  
		    	    if (sqlSearch != null) {
		    	    	sqlSearch=sqlSearch.trim();
		    	    	extraCriteria = " and (p.key_id like '%"+sqlSearch+"%' OR p.receipt_no like '%"+sqlSearch+"%') order by payment_date desc";
		    	    }
		                sqlBuilder.append(extraCriteria);
	    }else if (sqlSearch != null) {
    	    	sqlSearch=sqlSearch.trim();
    	    	extraCriteria = " where (p.key_id like '%"+sqlSearch+"%' OR p.receipt_no like '%"+sqlSearch+"%') order by payment_date desc ";
    	}else {
    		extraCriteria = " order by payment_date desc ";
    	}
                sqlBuilder.append(extraCriteria);
        
        
        if (searchPaymentDetail.isLimited()) {
            sqlBuilder.append(" limit ").append(searchPaymentDetail.getLimit());
        }

        if (searchPaymentDetail.isOffset()) {
            sqlBuilder.append(" offset ").append(searchPaymentDetail.getOffset());
        }

		return this.paginationHelper.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()",sqlBuilder.toString(),
                new Object[] {}, mapper);
	}

	@Override
	public String findReceiptNo(String receiptNo) {
		try{
			this.context.authenticatedUser();
			PaymentReceiptMapper mapper=new PaymentReceiptMapper();
			String sql = "select "+mapper.schema()+ " where p.receipt_no=?";
			return jdbcTemplate.queryForObject(sql, mapper, new Object[] {receiptNo});
		} catch(EmptyResultDataAccessException e){
			return null;
		}
	}
	
	private static final class PaymentReceiptMapper implements RowMapper<String> {

		public String schema() {
			return " p.receipt_no as receiptNo from b_payments p";
		}
		
		@Override
		public String mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			String receiptNo = rs.getString("receiptNo");
			return receiptNo;
		}

	}

	@Override
	public Long GetReceiptNoId(String receipt) {
		try{
			this.context.authenticatedUser();
			String sql = "select a.id from b_payments a where  a.receipt_no like '"+receipt+"' ";
			return jdbcTemplate.queryForLong(sql);
			} catch(EmptyResultDataAccessException e){
				return null;
			}
	}
	
	
}

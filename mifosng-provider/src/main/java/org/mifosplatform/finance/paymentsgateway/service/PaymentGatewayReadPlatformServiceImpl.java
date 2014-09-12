package org.mifosplatform.finance.paymentsgateway.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.finance.paymentsgateway.data.PaymentEnum;
import org.mifosplatform.finance.paymentsgateway.data.PaymentGatewayData;
import org.mifosplatform.finance.paymentsgateway.data.PaymentGatewayDownloadData;
import org.mifosplatform.finance.paymentsgateway.domain.PaymentEnumClass;
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
			//String serialNumber=null;
			serialNum=serialNum.trim();
			/*if(serialNum.charAt(0) == 'N' ||serialNum.charAt(0) == 'n'){
=======
			String serialNumber=null;
			if(serialNum.charAt(0) == 'N' ||serialNum.charAt(0) == 'n'){
>>>>>>> obsplatform-1.01:mifosng-provider/src/main/java/org/mifosplatform/finance/paymentsgateway/service/PaymentGatewayReadPlatformServiceImpl.java
				serialNumber=serialNum;
			}else{
				
				serialNumber="N"+serialNum;
<<<<<<< HEAD:mifosng-provider/src/main/java/org/mifosplatform/finance/paymentsgateway/service/PaymentGatewayReadPlatformServiceImpl.java
			}*/
		String sql = " select client_id as clientId from b_item_detail  " +
				" where serial_no = ? or provisioning_serialno = ? and client_id is not null  limit 1";
		
		return jdbcTemplate.queryForLong(sql, new Object[] {serialNum,serialNum});
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
	public Page<PaymentGatewayData> retrievePaymentGatewayData(SearchSqlQuery searchPaymentDetail,String tabType,String source) {	

		// TODO Auto-generated method stub
		context.authenticatedUser();
		PaymentMapper mapper=new PaymentMapper();
		
		String sqlSearch = searchPaymentDetail.getSqlSearch();
	    String extraCriteria = "";
		StringBuilder sqlBuilder = new StringBuilder(200);
        sqlBuilder.append("select ");
        sqlBuilder.append(mapper.schema());
        sqlBuilder.append(" where p.id is not null  ");
        
       if(source != null && !source.equalsIgnoreCase("all")){
    	   
    	   sqlBuilder.append(" and  p.source like '%"+source+"%'  ");
       }

          
        if (tabType!=null ) {
        	
		        	tabType=tabType.trim();
		        	sqlBuilder.append(" and  p.status like '"+tabType+"'  ");
		  
		    	    if (sqlSearch != null) {
		    	    	sqlSearch=sqlSearch.trim();
		    	    	extraCriteria = " and (p.key_id like '%"+sqlSearch+"%' OR p.receipt_no like '%"+sqlSearch+"%') ";
		    	    }
		                sqlBuilder.append(extraCriteria);
		                
	    }else if (sqlSearch != null) {
    	    	sqlSearch=sqlSearch.trim();
    	    	extraCriteria = " and    (p.key_id like '%"+sqlSearch+"%' OR p.receipt_no like '%"+sqlSearch+"%')  ";
    	}
        
       // extraCriteria = " order by payment_date desc ";
        sqlBuilder.append(extraCriteria);
        sqlBuilder.append(" order by payment_date desc ");
                

        
        
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
	
	
	@Override
	public List<PaymentGatewayDownloadData> retriveDataForDownload(String source,String startDate, String endDate,String status){
		
		StringBuilder builder = new StringBuilder(200);
		builder.append("select pg.id as id, pg.receipt_no as receiptNo, pg.key_id as serialNumber, pg.payment_date as paymentDate, pg.amount_paid as amountPaid," +
					"pg.party_id as PhoneMSISDN, pg.Remarks as remarks, pg.obs_id as paymentId, pg.status as status from b_paymentgateway pg ");
		
		if(!source.equalsIgnoreCase("All")){
			builder.append("where source='"+source+"' and ");
			
			if(!status.equalsIgnoreCase("All")){
				builder.append(" status='"+status+"' and ");
			}
		}else{
			if(!status.equalsIgnoreCase("All")){
				builder.append("where status='"+status+"' and ");
			}else{
				builder.append("where ");
			}
		}
		
		builder.append("payment_date between '"+startDate+"' and '"+endDate+"' order by id asc");
		
		
		DownloadPaymentGatewayMapper mapper = new DownloadPaymentGatewayMapper();
			
		return jdbcTemplate.query(builder.toString(),mapper,new Object[]{});
		

			/*final String sql = "select pg.id as id, pg.receipt_no as receiptNo, pg.party_id as serialNumber, pg.payment_date as paymentDate, pg.amount_paid as amountPaid," +
					"pg.party_id as PhoneMSISDN, pg.Remarks as remarks, pg.status as status from b_paymentgateway pg " +
					"where source=? and payment_date between ? and ? order by id asc";
			DownloadPaymentGatewayMapper mapper = new DownloadPaymentGatewayMapper();
			
			return jdbcTemplate.query(sql,mapper,source,startDate,endDate);*/
		
		
		
	}
	
	private static final class DownloadPaymentGatewayMapper implements RowMapper<PaymentGatewayDownloadData>{
		/* (non-Javadoc)
		 * @see org.springframework.jdbc.core.RowMapper#mapRow(java.sql.ResultSet, int)
		 */
		@Override
		public PaymentGatewayDownloadData mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			//final Long Id = rs.getLong("id");
			final String SerialNumber = rs.getString("serialNumber");
			final LocalDate PaymentDate = JdbcSupport.getLocalDate(rs, "paymentDate");
			final BigDecimal AmountPaid = rs.getBigDecimal("amountPaid");
			final String PhoneMSISDN = rs.getString("PhoneMSISDN");
			final String Remarks = rs.getString("remarks");
			final String Status = rs.getString("status");
			final String ReceiptNo = rs.getString("receiptNo");
			final String paymentId = rs.getString("paymentId");
			return new PaymentGatewayDownloadData(SerialNumber,PaymentDate,AmountPaid,PhoneMSISDN,Remarks,Status,ReceiptNo,paymentId);
		}
	}
	
}
	
/**
 * 
 * 
 */


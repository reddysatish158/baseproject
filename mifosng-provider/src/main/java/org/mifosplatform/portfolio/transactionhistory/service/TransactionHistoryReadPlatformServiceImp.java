package org.mifosplatform.portfolio.transactionhistory.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.core.service.PaginationHelper;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.transactionhistory.data.TransactionHistoryData;
import org.mifosplatform.portfolio.transactionhistory.domain.TransactionHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;


@Service
public class TransactionHistoryReadPlatformServiceImp implements TransactionHistoryReadPlatformService{

	
	
	private JdbcTemplate jdbcTemplate;
	private PlatformSecurityContext context;
	private final PaginationHelper<TransactionHistoryData> paginationHelper = new PaginationHelper<TransactionHistoryData>();
	
	@Autowired
	public TransactionHistoryReadPlatformServiceImp(final TenantAwareRoutingDataSource dataSource, final TransactionHistoryRepository transactionHistoryRepository,final PlatformSecurityContext context) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.context = context;
	}
	
	@Override
	public List<TransactionHistoryData> retriveTransactionHistoryTemplate() {
		   
		 return retriveAll();
		
	}
	
	public Page<TransactionHistoryData> retriveTransactionHistoryById(SearchSqlQuery searchTransactionHistory,final Long clientId) {
		
		return  retriveById(searchTransactionHistory,clientId);
	}
	
	private String query(){
		
		return " SQL_CALC_FOUND_ROWS th.id AS id,th.client_id AS clientId,th.transaction_type AS transactionType,th.transaction_date AS transactionDate,th.history AS history," +
				" a.username as userName  FROM b_transaction_history th,m_appuser a WHERE a.id = th.createdby_id ";
	}
	
	private Page<TransactionHistoryData> retriveById(SearchSqlQuery searchTransactionHistory,Long id){
		try{
			context.authenticatedUser();
			String sql = "select "+query()+" and  th.client_id = ? order by transactionDate desc ";
			TransactionHistoryMapper rowMapper = new TransactionHistoryMapper();
			StringBuilder sqlBuilder = new StringBuilder(200);
		     //   sqlBuilder.append("select ");
		        sqlBuilder.append(sql);
		     //   sqlBuilder.append(" where a.id = th.createdby_id ");
		        
		        String sqlSearch = searchTransactionHistory.getSqlSearch();
		        String extraCriteria = "";
			    if (sqlSearch != null) {
			    	sqlSearch=sqlSearch.trim();
			    	extraCriteria = " and (th.transaction_type like '%"+sqlSearch+"%' OR "
			    				+ " th.transaction_date like '%"+sqlSearch+"%' OR "
			    				+ " a.username like '%"+sqlSearch+"%' OR "
			    				+ " th.history like '%"+sqlSearch+"%') " ;
			    }
			   
		            sqlBuilder.append(extraCriteria);
		        
		        /*if (StringUtils.isNotBlank(extraCriteria)) {
		            sqlBuilder.append(extraCriteria);
		        }*/


		        if (searchTransactionHistory.isLimited()) {
		            sqlBuilder.append(" limit ").append(searchTransactionHistory.getLimit());
		        }

		        if (searchTransactionHistory.isOffset()) {
		            sqlBuilder.append(" offset ").append(searchTransactionHistory.getOffset());
		        }
				
				
				
				

				//	return jdbcTemplate.query(sql, rowMapper,new Object[]{id});
				return this.paginationHelper.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()",sqlBuilder.toString(),
			            new Object[] {id}, rowMapper);
				
		}catch(DataIntegrityViolationException dve){
			throw new PlatformDataIntegrityException("", "", "");
		}
		
	}
	
	public List<TransactionHistoryData> retriveAll(){
		try{
		context.authenticatedUser();
		String sql = "select "+query();
		TransactionHistoryMapper rowMapper = new TransactionHistoryMapper();
		return jdbcTemplate.query(sql, rowMapper,new Object[]{});
		}catch(DataIntegrityViolationException dve){
			 throw new PlatformDataIntegrityException("transactionhistoryEXCEPTION-1", "transactionhistoryEXCEPTION-2", "transactionhistoryEXCEPTION-3");
		}
	}
	
	
	private class TransactionHistoryMapper implements RowMapper<TransactionHistoryData>{
		
		@Override
		public TransactionHistoryData mapRow(ResultSet rs, int rowNum) throws SQLException {
			Long id = rs.getLong("id");
			Long clientId = rs.getLong("clientId");
			String transactionType = rs.getString("transactionType");
			Date transactionDate = rs.getDate("transactionDate");
			String history = rs.getString("history");
			String user=rs.getString("userName");
			return new TransactionHistoryData(id,clientId, transactionType, transactionDate, history,user);
		}

	}
}

package org.mifosplatform.billing.inventory.mrn.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.billing.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.billing.inventory.mrn.data.InventoryTransactionHistoryData;
import org.mifosplatform.billing.inventory.mrn.data.MRNDetailsData;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.core.service.PaginationHelper;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class MRNDetailsReadPlatformServiceImp implements MRNDetailsReadPlatformService{

	private final JdbcTemplate jdbcTemplate;
	private PlatformSecurityContext context;
	
	private final PaginationHelper<MRNDetailsData> paginationHelper = new PaginationHelper<MRNDetailsData>();
	private final PaginationHelper<InventoryTransactionHistoryData> paginationHelper2 = new PaginationHelper<InventoryTransactionHistoryData>();
	
	@Autowired
	public MRNDetailsReadPlatformServiceImp(final PlatformSecurityContext context, final TenantAwareRoutingDataSource dataSource) {
		this.context = context;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	
	private final class MRNDetailsMapper implements RowMapper<MRNDetailsData>{
		@Override
		public MRNDetailsData mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			final Long id = rs.getLong("mrnId");
			final LocalDate requestedDate =JdbcSupport.getLocalDate(rs,"requestedDate");
			final String fromOffice = rs.getString("fromOffice");
			final String toOffice = rs.getString("toOffice");
			final Long orderdQuantity = rs.getLong("orderdQuantity");
			final Long receivedQuantity = rs.getLong("receivedQuantity");
			final String status = rs.getString("status");
			final String itemDescription = rs.getString("item");
			
			return new MRNDetailsData(id, requestedDate, fromOffice, toOffice, orderdQuantity, receivedQuantity, status,itemDescription);
		}
	}
	
	private final class MRNDetailsTemplateMapper implements RowMapper<MRNDetailsData>{
		@Override
		public MRNDetailsData mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			final Long officeId = rs.getLong("officeId");
			final Long parentId = rs.getLong("parentId");
			final String officeName = rs.getString("officeName");
			return new MRNDetailsData(officeId,parentId,officeName);
		}
	}
	
	
	private final class MRNDetailsItemMasterDetailsMapper implements RowMapper<MRNDetailsData>{
		@Override
		public MRNDetailsData mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			final Long itemId = rs.getLong("itemId");
			final String itemCode = rs.getString("itemCode");
			final String itemDescription = rs.getString("itemDescription");
			return new MRNDetailsData(itemId, itemCode, itemDescription);
		}
	}
	
	private final class MRNDetailsMrnIDsMapper implements RowMapper<MRNDetailsData>{
		@Override
		public MRNDetailsData mapRow(ResultSet rs, int rowNum) throws SQLException {
			final Long mrnId = rs.getLong("mrnId");
			final Long itemMasterId = rs.getLong("itemMasterId");
			final String itemDescription = rs.getString("itemDescription");
			return new MRNDetailsData(mrnId,itemDescription,itemMasterId);
		}
	}
	
	private final class MRNDetailsSerialMapper implements RowMapper<String>{
		
		@Override
		public String mapRow(ResultSet rs, int rowNum)
				throws SQLException {
				final String serialNumber = rs.getString("serialNumber");
			return serialNumber;
		}
	}
	
	private final class MRNDetailsItemMasterId implements RowMapper<Long>{
		@Override
		public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
			final Long itemMasterId = rs.getLong("itemMasterId");
			return itemMasterId;
		}
	}
	
	private final class InventoryItemIdMapper implements RowMapper<Long>{
		@Override
		public Long mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			final Long itemId = rs.getLong("id");
			return itemId; 
		}
	}
	
	private final class MRNDetailsFromAndToMapper implements RowMapper<MRNDetailsData>{
		@Override
		public MRNDetailsData mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			final Long fromOffice = rs.getLong("fromOffice");
			final Long toOffice = rs.getLong("toOffice");
			return new MRNDetailsData(fromOffice,toOffice);
		}
	}
	
	private final class MRNDetailsHistoryMapper implements RowMapper<InventoryTransactionHistoryData>{
		@Override
		public InventoryTransactionHistoryData mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			final Long id = rs.getLong("id");
			final Long mrnId = rs.getLong("mrnId");
			final String itemDescription = rs.getString("itemDescription");
			final String serialNumber = rs.getString("serialNumber");
			final LocalDate transactionDate =JdbcSupport.getLocalDate(rs,"transactionDate");
			final String fromOffice = rs.getString("source");
			final String toOffice = rs.getString("destination");
			final String refType = rs.getString("refType");
			final String movement = rs.getString("movement");
			return new InventoryTransactionHistoryData(id, transactionDate, mrnId, itemDescription, fromOffice, toOffice,serialNumber,refType,movement);
		}
	}
	
	@Override
	public Page<MRNDetailsData> retriveMRNDetails(SearchSqlQuery searchMRNDetails) {
		final String sql = "SQL_CALC_FOUND_ROWS mrn.id as mrnId, mrn.requested_date as requestedDate, "
				+ "(select item_description from b_item_master where id=mrn.item_master_id) as item,"
				+ "(select name from m_office where id=mrn.from_office) as fromOffice, "
				+ "(select name from m_office where id = mrn.to_office) as toOffice, "
				+ "mrn.orderd_quantity as orderdQuantity, mrn.received_quantity as receivedQuantity, "
				+ "mrn.status as status from b_mrn mrn ";
		MRNDetailsMapper rowMapper = new MRNDetailsMapper();
		
		StringBuilder sqlBuilder = new StringBuilder(200);
        sqlBuilder.append("select ");
        sqlBuilder.append(sql);
        sqlBuilder.append(" where mrn.status = 'Completed' | 'New' | 'Pending' ");
        
         String sqlSearch = searchMRNDetails.getSqlSearch();
        String extraCriteria = "";
	    if (sqlSearch != null) {
	    	sqlSearch=sqlSearch.trim();
	    	extraCriteria = " and ((select item_description from b_item_master where id=mrn.item_master_id) like '%"+sqlSearch+"%' OR "
	    				+ " (select name from m_office where id=mrn.from_office) like '%"+sqlSearch+"%' OR "
	    				+ " (select name from m_office where id = mrn.to_office) like '%"+sqlSearch+"%' OR "
	    				+ " mrn.status like '%"+sqlSearch+"%')" ;
	    }
            sqlBuilder.append(extraCriteria);
        
        /*if (StringUtils.isNotBlank(extraCriteria)) {
            sqlBuilder.append(extraCriteria);
        }*/


        if (searchMRNDetails.isLimited()) {
            sqlBuilder.append(" limit ").append(searchMRNDetails.getLimit());
        }

        if (searchMRNDetails.isOffset()) {
            sqlBuilder.append(" offset ").append(searchMRNDetails.getOffset());
        }
		

		return this.paginationHelper.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()",sqlBuilder.toString(),
	            new Object[] {}, rowMapper);
	}
	
	@Override
	public InventoryTransactionHistoryData retriveSingleMovedMrn(Long mrnId) {
		final String sql = "select id as id,ref_id as mrnId,ref_type as refType,(select item_description from b_item_master where id = item_master_id) as itemDescription, serial_number as serialNumber, transaction_date as transactionDate, 'From Office to To Office' movement,(select name from m_office where id = from_office) as source, (select name from m_office where id = to_office) as destination from b_item_history where id = ?";
		MRNDetailsHistoryMapper rowMapper = new MRNDetailsHistoryMapper();
		return jdbcTemplate.queryForObject(sql, rowMapper,new Object[]{mrnId});
	}
	
	
	@Override
	public List<MRNDetailsData> retriveMRNDetails() {
		final String sql = "select mrn.id as mrnId, mrn.requested_date as requestedDate, (select item_description from b_item_master where id=mrn.item_master_id) as item,(select name from m_office where id=mrn.from_office) as fromOffice, (select name from m_office where id = mrn.to_office) as toOffice, mrn.orderd_quantity as orderdQuantity, mrn.received_quantity as receivedQuantity, mrn.status as status from b_mrn mrn";
		MRNDetailsMapper rowMapper = new MRNDetailsMapper();
		return jdbcTemplate.query(sql, rowMapper);
	}
	
	@Override
	public List<MRNDetailsData> retriveMrnDetailsTemplate() {
		final String sql = "select o.id as officeId, o.parent_id as parentId, o.name as officeName from m_office o";
		MRNDetailsTemplateMapper rowMapper = new MRNDetailsTemplateMapper();
		return jdbcTemplate.query(sql, rowMapper);
	}
	
	@Override
	public List<MRNDetailsData> retriveItemMasterDetails(){
		final String sql = "select im.id as itemId, im.item_code as itemCode,  im.item_description as itemDescription from b_item_master im where im.is_deleted='n'";
		MRNDetailsItemMasterDetailsMapper rowMapper = new MRNDetailsItemMasterDetailsMapper();
		return jdbcTemplate.query(sql, rowMapper);
	}
	
	
	
	@Override
	public Collection<MRNDetailsData> retriveMrnIds() {
		final String sql = "select id as mrnId,(select item_description from b_item_master where id=item_master_id) as itemDescription, item_master_id as itemMasterId from b_mrn where orderd_quantity>received_quantity order by requested_date desc";//"select id as mrnId,(select item_description from b_item_master where id=item_master_id) as itemDescription, item_master_id as itemMasterId from b_mrn order by requested_date desc";
		MRNDetailsMrnIDsMapper rowMapper = new MRNDetailsMrnIDsMapper();
		return jdbcTemplate.query(sql,rowMapper);
	}
	
	@Override
	public List<String> retriveSerialNumbers(Long fromOffice, Long mrnId) {
		final String sql = "select idt.serial_no as serialNumber from b_mrn ots left join b_item_detail idt on idt.item_master_id = ots.item_master_id where ots.id = ? and idt.client_id is null and idt.office_id=?";//"select serial_no as serialNumber from b_item_detail where item_master_id=? and client_id is null";
		final MRNDetailsSerialMapper rowMapper = new MRNDetailsSerialMapper();
		return jdbcTemplate.query(sql,rowMapper,new Object[]{mrnId,fromOffice});
	}
	
	@Override
	public List<Long> retriveItemMasterId(Long mrnId) {
		final String sql = "select item_master_id as itemMasterId from b_mrn where id = ?";
		final MRNDetailsItemMasterId rowMapper = new MRNDetailsItemMasterId();
		return jdbcTemplate.query(sql,rowMapper,new Object[]{mrnId});
	}
	
	
	@Override
	public List<Long> retriveItemDetailsId(String serialNumber, Long itemMasterId) {
		final String sql = "select i.id as id from b_item_detail i where i.serial_no=? and i.item_master_id=?";
		InventoryItemIdMapper rowMapper = new InventoryItemIdMapper();
		return jdbcTemplate.query(sql, rowMapper,new Object[]{serialNumber,itemMasterId});
	}
	
	@Override
	public MRNDetailsData retriveFromAndToOffice(Long mrnId) {
		final String sql = "select from_office as fromOffice, to_office as toOffice from b_mrn where id=?";
		final MRNDetailsFromAndToMapper rowMapper = new MRNDetailsFromAndToMapper(); 
		return jdbcTemplate.queryForObject(sql,rowMapper,new Object[]{mrnId});
	}
	
	@Override
	public Page<InventoryTransactionHistoryData> retriveHistory(SearchSqlQuery searchItemHistory) {
		//final String sql = "select id as id, ref_id as mrnId, ref_type as refType, (select item_description from b_item_master where id=item_master_id) as itemDescription, serial_number as serialNumber, transaction_date as transactionDate, (select name from m_office where id=from_office) as fromOffice, (select name from m_office where id=to_office) as toOffice from b_item_history";
		
		final String sql = " SQL_CALC_FOUND_ROWS * from (select id as id, ref_id as mrnId, ref_type as refType,"+ 
""+" (select item_description from b_item_master where id=item_master_id) as itemDescription,"+ 
""+" serial_number as serialNumber, transaction_date as transactionDate, 'From Office to To Office' movement,"+
""+" (select name from m_office where id=from_office) as source,"+
""+"(select name from m_office where id=to_office) as destination from b_item_history where ref_type='MRN'"+
""+" union all"+
""+" select id as id, ref_id as mrnId, ref_type as refType,"+ 
""+" (select item_description from b_item_master where id=item_master_id) as itemDescription,"+ 
""+" serial_number as serialNumber, transaction_date as transactionDate, 'From Supplier to To Office' movement,"+
""+" (select b.supplier_description from b_grn a, b_supplier b where a.supplier_id=b.id and a.id=from_office) as source,"+ 
""+" (select name from m_office where id=to_office) as destination from b_item_history where ref_type='Item Detail'"+
""+" union all"+
""+" select id as id, ref_id as mrnId, ref_type as refType,"+ 
""+" (select item_description from b_item_master where id=item_master_id) as itemDescription,"+ 
""+" serial_number as serialNumber, transaction_date as transactionDate, 'From Office to To Client' movement,"+
""+" (select name from m_office where id=from_office) as source,"+ 
""+" (Select concat(id,' - ', display_name)  from m_client where id=to_office) as destination"+ 
""+" from b_item_history where ref_type='Allocation' ) a  where id is not null ";
		
		MRNDetailsHistoryMapper detailsHistoryMapper = new MRNDetailsHistoryMapper();
		
		StringBuilder sqlBuilder = new StringBuilder(200);
        sqlBuilder.append("select ");
        sqlBuilder.append(sql);
      //  sqlBuilder.append(" where item.office_id = office.id ");
        
        String sqlSearch = searchItemHistory.getSqlSearch();
        String extraCriteria = "";
	    if (sqlSearch != null) {
	    	sqlSearch=sqlSearch.trim();
	    	extraCriteria = " and (itemDescription like '%"+sqlSearch+"%' OR "
    				+ " source like '%"+sqlSearch+"%' OR "
    				+ " destination like '%"+sqlSearch+"%' OR "
    				+ " movement like '%"+sqlSearch+"%' OR "
    				+ " serialNumber like '%"+sqlSearch+"%') " ;
	    }
            sqlBuilder.append(extraCriteria);
        
        /*if (StringUtils.isNotBlank(extraCriteria)) {
            sqlBuilder.append(extraCriteria);
        }*/


        if (searchItemHistory.isLimited()) {
            sqlBuilder.append(" limit ").append(searchItemHistory.getLimit());
        }

        if (searchItemHistory.isOffset()) {
            sqlBuilder.append(" offset ").append(searchItemHistory.getOffset());
        }

		
		
		return this.paginationHelper2.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()",sqlBuilder.toString(),
	            new Object[] {}, detailsHistoryMapper);
	}
	@Override
	public MRNDetailsData retriveSingleMrnDetail(Long mrnId) {
		final String sql = "select mrn.id as mrnId, mrn.requested_date as requestedDate, (select item_description from b_item_master where id=mrn.item_master_id) as item,(select name from m_office where id=mrn.from_office) as fromOffice, (select name from m_office where id = mrn.to_office) as toOffice, mrn.orderd_quantity as orderdQuantity, mrn.received_quantity as receivedQuantity, mrn.status as status from b_mrn mrn where mrn.id=?";
		final MRNDetailsMapper rowMapper = new MRNDetailsMapper();
		return jdbcTemplate.queryForObject(sql,rowMapper,new Object[]{mrnId});
	}
	
}


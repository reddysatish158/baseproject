package org.mifosplatform.logistics.itemdetails.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.mifosplatform.billing.paymode.data.McodeData;
import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.core.service.PaginationHelper;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.logistics.itemdetails.data.InventoryGrnData;
import org.mifosplatform.logistics.itemdetails.domain.InventoryGrnRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class InventoryGrnReadPlatformServiceImp implements InventoryGrnReadPlatformService{

	
	private final JdbcTemplate jdbcTemplate;
	private final InventoryGrnRepository inventoryGrnRepository;
	private final PaginationHelper<InventoryGrnData> paginationHelper = new PaginationHelper<InventoryGrnData>();
	@Autowired
	public InventoryGrnReadPlatformServiceImp(final TenantAwareRoutingDataSource dataSource,InventoryGrnRepository inventoryGrnRepository){
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.inventoryGrnRepository = inventoryGrnRepository;
		
	}
	
	
	

	@Override
	public Collection<InventoryGrnData> retriveGrnDetails() {

		GrnMapperForDetails grn = new GrnMapperForDetails();
		String sql = "select g.id as id, f.name as officeName, g.purchase_date as purchaseDate, g.supplier_id as supplierId, g.item_master_id as itemMasterId, g.orderd_quantity as orderdQuantity, g.received_quantity as receivedQuantity, im.item_description as itemDescription, s.supplier_description as supplierDescription from b_grn g left outer join m_office f on g.office_id=f.id left outer join b_item_master im on g.item_master_id = im.id left outer join b_supplier s on g.supplier_id=s.id";
		return jdbcTemplate.query(sql,grn,new Object[]{});
	}

	
		
	public boolean validateForExist(final Long grnId){
		
		boolean exist = inventoryGrnRepository.exists(grnId);
		if(!exist){
			return true;
		}
		else{
			return false;
		}
	}

	/*@Override
	public Page<InventoryGrnData> retriveGrnDetails(Long limit, Long offset) {
		GrnMapperForDetails grn = new GrnMapperForDetails();
		String sql = "select SQL_CALC_FOUND_ROWS g.id as id, f.name as officeName, g.purchase_date as purchaseDate, g.supplier_id as supplierId, g.item_master_id as itemMasterId, g.orderd_quantity as orderdQuantity, g.received_quantity as receivedQuantity, im.item_description as itemDescription, s.supplier_description as supplierDescription from b_grn g left outer join m_office f on g.office_id=f.id left outer join b_item_master im on g.item_master_id = im.id left outer join b_supplier s on g.supplier_id=s.id limit ? offset ?";
		return this.paginationHelper.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()",sql,
	            new Object[] {limit,offset}, grn);
	}*/
	
	
public Page<InventoryGrnData> retriveGrnDetails(SearchSqlQuery searchGrn) {
		
		GrnMapperForDetails grn = new GrnMapperForDetails();
		String sql = "SQL_CALC_FOUND_ROWS g.id as id, f.name as officeName, g.purchase_date as purchaseDate, "
				+ "g.supplier_id as supplierId, g.item_master_id as itemMasterId, g.orderd_quantity as orderdQuantity, "
				+ "g.received_quantity as receivedQuantity, g.po_no as purchaseNo, im.item_description as itemDescription, "
				+ "s.supplier_description as supplierDescription "
				+ "from b_grn g left outer join m_office f on g.office_id=f.id "
				+ "left outer join b_item_master im on g.item_master_id = im.id left outer join b_supplier s on g.supplier_id=s.id ";
		
		
		StringBuilder sqlBuilder = new StringBuilder(200);
        sqlBuilder.append("select ");
        sqlBuilder.append(sql);
        sqlBuilder.append(" where g.id IS NOT NULL ");
        
        String sqlSearch = searchGrn.getSqlSearch();
        String extraCriteria = "";
	    if (sqlSearch != null) {
	    	sqlSearch=sqlSearch.trim();
	    	extraCriteria = " and (f.name like '%"+sqlSearch+"%' OR" 
	    			+ " g.id like '%"+sqlSearch+"%' OR"
	    			+ " g.purchase_date like '%"+sqlSearch+"%' OR"
	    			+ " s.supplier_description like '%"+sqlSearch+"%' OR"
	    			+ " im.item_description like '%"+sqlSearch+"%')";
	    }
            sqlBuilder.append(extraCriteria);
        
        /*if (StringUtils.isNotBlank(extraCriteria)) {
            sqlBuilder.append(extraCriteria);
        }*/
        
        sqlBuilder.append(" ORDER BY g.id ");

        if (searchGrn.isLimited()) {
            sqlBuilder.append(" limit ").append(searchGrn.getLimit());
        }

        if (searchGrn.isOffset()) {
            sqlBuilder.append(" offset ").append(searchGrn.getOffset());
        }
	
		return this.paginationHelper.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()",sqlBuilder.toString(),
	            new Object[] {}, grn);
}
	
	@Override
	public InventoryGrnData retriveGrnDetailTemplate(final Long grnId){
		
		GrnMapperForTemplate grn = new GrnMapperForTemplate();
		String sql = "select g.id as id, g.purchase_date as purchaseDate, g.supplier_id as supplierId, g.item_master_id as itemMasterId, g.po_no as purchaseNo,g.office_id as officeId,g.orderd_quantity as orderdQuantity, g.received_quantity as receivedQuantity, im.item_description as itemDescription, s.supplier_description as supplierDescription from b_grn g left outer join b_item_master im on g.item_master_id = im.id left outer join b_supplier s on g.supplier_id = s.id where g.id = ?";
		return jdbcTemplate.queryForObject(sql,grn,new Object[]{grnId});
	}




	@Override
	public Collection<InventoryGrnData> retriveGrnIds() {
		GrnIds rowMapper = new GrnIds();
		String sql = "select id,(select item_description from b_item_master where id=item_master_id) as itemDescription from b_grn where orderd_quantity>received_quantity order by id desc";
		return jdbcTemplate.query(sql, rowMapper);
	}
	
	
	private class GrnMapperForDetails implements RowMapper<InventoryGrnData>{

		@Override
		public InventoryGrnData mapRow(ResultSet rs, int rowNum)
			throws SQLException {
			
			Long id = rs.getLong("id");
			LocalDate purchaseDate =JdbcSupport.getLocalDate(rs,"purchaseDate");
			Long supplierId = rs.getLong("supplierId");
			Long itemMasterId = rs.getLong("itemMasterId");
			Long orderedQuantity = rs.getLong("orderdQuantity");
			Long receivedQuantity = rs.getLong("receivedQuantity");
			String purchaseNo = rs.getString("purchaseNo");
			String itemDescription = rs.getString("itemDescription");
			String supplierName = rs.getString("supplierDescription");
			String officeName = rs.getString("officeName");
			return new InventoryGrnData(id,purchaseDate,supplierId,itemMasterId,orderedQuantity,receivedQuantity,itemDescription,supplierName,officeName,purchaseNo);
			
		}
		
	}
	
	private class GrnMapperForTemplate implements RowMapper<InventoryGrnData>{

		@Override
		public InventoryGrnData mapRow(ResultSet rs, int rowNum)
			throws SQLException {
			
			Long id = rs.getLong("id");
			LocalDate purchaseDate =JdbcSupport.getLocalDate(rs,"purchaseDate");
			Long supplierId = rs.getLong("supplierId");
			Long itemMasterId = rs.getLong("itemMasterId");
			Long orderedQuantity = rs.getLong("orderdQuantity");
			Long receivedQuantity = rs.getLong("receivedQuantity");
			String itemDescription = rs.getString("itemDescription");
			String supplierName = rs.getString("supplierDescription");
			String purchaseNo = rs.getString("purchaseNo");
			Long officeId = rs.getLong("officeId");
			return new InventoryGrnData(id,purchaseDate,itemMasterId,orderedQuantity,receivedQuantity,itemDescription,supplierName,purchaseNo,supplierId,officeId);
			
		}
		
	}
	
	private class GrnIds implements RowMapper<InventoryGrnData>{

		@Override
		public InventoryGrnData mapRow(ResultSet rs, int rowNum)throws SQLException {
			
			final Long id = rs.getLong("id");
			final String itemDescription = rs.getString("itemDescription");
			return new InventoryGrnData(id,itemDescription);
		}
		
	}

	@Override
	public List<McodeData> retrieveItemQualityData(String str) {
		ItemDataMaper mapper = new ItemDataMaper();

		String sql = "select " + mapper.schema()+" m.code_name=? order by mc.id";

		return this.jdbcTemplate.query(sql, mapper, new Object[] {str});
	}
	private static final class ItemDataMaper implements RowMapper<McodeData> {

		public String schema() {
			return "mc.id as id,mc.code_value as codeValue FROM m_code_value mc,m_code m where mc.code_id=m.id and";

		}

		@Override
		public McodeData mapRow(final ResultSet rs,
				@SuppressWarnings("unused") final int rowNum)
				throws SQLException {
           
			String codeValue = rs.getString("codeValue");
			Long id = rs.getLong("id");
			return new McodeData(id,codeValue);

		}
	}
	
	
}

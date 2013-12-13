package org.mifosplatform.billing.inventory.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mifosplatform.billing.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.billing.inventory.data.InventoryItemDetailsData;
import org.mifosplatform.billing.inventory.data.InventoryItemSerialNumberData;
import org.mifosplatform.billing.inventory.data.ItemMasterIdData;
import org.mifosplatform.billing.inventory.data.QuantityData;
import org.mifosplatform.billing.inventory.domain.InventoryItemDetails;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
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
public class InventoryItemDetailsReadPlatformServiceImp implements InventoryItemDetailsReadPlatformService {

	
	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;
	private final PaginationHelper<InventoryItemDetailsData> paginationHelper = new PaginationHelper<InventoryItemDetailsData>();
	@Autowired
	InventoryItemDetailsReadPlatformServiceImp(final TenantAwareRoutingDataSource dataSource,final PlatformSecurityContext context){
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.context = context;
	}
	
	private class ItemDetailsMapper implements RowMapper<InventoryItemDetailsData>{

		@Override
		public InventoryItemDetailsData mapRow(ResultSet rs, int rowNum) throws SQLException {
			Long id = rs.getLong("id");
			Long itemMasterId = rs.getLong("itemMasterId");
			String serialNumber = rs.getString("serialNumber");
			Long grnId = rs.getLong("grnId");
			String provisioningSerialNumber = rs.getString("provisioningSerialNumber");
			String quality= rs.getString("quality");
			String status = rs.getString("status");
			Long warranty = rs.getLong("warranty");
			String remarks = rs.getString("remarks");
			String itemDescription = rs.getString("itemDescription");
			String supplier = rs.getString("supplier");
			Long clientId = rs.getLong("clientId");
			String officeName = rs.getString("officeName");
			String accountNumber = rs.getString("accountNumber");
			return new InventoryItemDetailsData(id,itemMasterId,serialNumber,grnId,provisioningSerialNumber,quality,status,warranty,remarks,itemDescription,supplier,clientId,officeName,accountNumber);
		}
		
		public String schema(){
			//String sql = "item.id as id,item.item_master_id as itemMasterId, item.serial_no as serialNumber, item.grn_id as grnId, item.provisioning_serialno as provisioningSerialNumber, item.quality as quality, item.status as status, item.warranty as warranty, item.remarks as remarks, master.item_description as itemDescription from b_item_detail item left outer join b_item_master master on item.item_master_id = master.id";
			String sql = "SQL_CALC_FOUND_ROWS item.id as id, office.name as officeName,item.item_master_id as itemMasterId, "
					+ "item.serial_no as serialNumber, item.grn_id as grnId, "
					+ "(select supplier_description from b_supplier where id = (select supplier_id from b_grn where b_grn.id=item.grn_id)) as supplier,"
					+ "item.provisioning_serialno as provisioningSerialNumber, item.quality as quality, item.status as status, "
					+ "item.warranty as warranty, item.remarks as remarks, master.item_description as itemDescription, "
					+ "item.client_id as clientId, "
					+ "(select account_no from m_client where id = client_id) as accountNumber "
					+ "from b_item_detail item left outer join b_item_master master on item.item_master_id = master.id left outer join m_office office on item.office_id=office.id ";
			
			/*String sql = "SQL_CALC_FOUND_ROWS item.id as id, office.name as officeName,item.item_master_id as itemMasterId, "
					+ "item.serial_no as serialNumber, item.grn_id as grnId, "
					+ "(select supplier_description from b_supplier where id = (select supplier_id from b_grn where b_grn.id=item.grn_id)) as supplier,"
					+ "item.provisioning_serialno as provisioningSerialNumber, item.quality as quality, item.status as status, "
					+ "item.warranty as warranty, item.remarks as remarks, master.item_description as itemDescription, "
					+ "item.client_id as clientId from b_item_detail item left outer join b_item_master master on item.item_master_id = master.id "
					+ "left outer join m_office office on item.office_id=office.id ";
*/
			
			return sql;
		}
		
	}
 
	private class ItemDetailsMapper2 implements RowMapper<InventoryItemDetailsData>{

		@Override
		public InventoryItemDetailsData mapRow(ResultSet rs, int rowNum) throws SQLException {
			Long id = rs.getLong("id");
			Long itemMasterId = rs.getLong("itemMasterId");
			String serialNumber = rs.getString("serialNumber");
			Long grnId = rs.getLong("grnId");
			String provisioningSerialNumber = rs.getString("provisioningSerialNumber");
			String quality= rs.getString("quality");
			String status = rs.getString("status");
			Long warranty = rs.getLong("warranty");
			String remarks = rs.getString("remarks");
			String itemDescription = rs.getString("itemDescription");
			String supplier = rs.getString("supplier");
			Long clientId = rs.getLong("clientId");
			String officeName = rs.getString("officeName");
			return new InventoryItemDetailsData(id,itemMasterId,serialNumber,grnId,provisioningSerialNumber,quality,status,warranty,remarks,itemDescription,supplier,clientId,officeName);
		}
		
		public String schema(){
			//String sql = "item.id as id,item.item_master_id as itemMasterId, item.serial_no as serialNumber, item.grn_id as grnId, item.provisioning_serialno as provisioningSerialNumber, item.quality as quality, item.status as status, item.warranty as warranty, item.remarks as remarks, master.item_description as itemDescription from b_item_detail item left outer join b_item_master master on item.item_master_id = master.id";
			String sql = "SQL_CALC_FOUND_ROWS item.id as id, office.name as officeName,item.item_master_id as itemMasterId, "
					+ "item.serial_no as serialNumber, item.grn_id as grnId, "
					+ "(select supplier_description from b_supplier where id = (select supplier_id from b_grn where b_grn.id=item.grn_id)) as supplier,"
					+ "item.provisioning_serialno as provisioningSerialNumber, item.quality as quality, item.status as status, "
					+ "item.warranty as warranty, item.remarks as remarks, master.item_description as itemDescription, "
					+ "item.client_id as clientId, "
					+ "(select account_no from m_client where id = client_id) as accountNumber "
					+ "from b_item_detail item left outer join b_item_master master on item.item_master_id = master.id left outer join m_office office on item.office_id=office.id ";
			
			/*String sql = "SQL_CALC_FOUND_ROWS item.id as id, office.name as officeName,item.item_master_id as itemMasterId, "
					+ "item.serial_no as serialNumber, item.grn_id as grnId, "
					+ "(select supplier_description from b_supplier where id = (select supplier_id from b_grn where b_grn.id=item.grn_id)) as supplier,"
					+ "item.provisioning_serialno as provisioningSerialNumber, item.quality as quality, item.status as status, "
					+ "item.warranty as warranty, item.remarks as remarks, master.item_description as itemDescription, "
					+ "item.client_id as clientId from b_item_detail item left outer join b_item_master master on item.item_master_id = master.id "
					+ "left outer join m_office office on item.office_id=office.id ";
*/
			
			return sql;
		}
		
	}

	/*@Override
	public Collection<InventoryItemDetailsData> retriveAllItemDetails() {
		// TODO Auto-generated method stub
		context.authenticatedUser();
		ItemDetailsMapper itemDetails = new ItemDetailsMapper();
		String sql = "select "+itemDetails.schema();
		return this.jdbcTemplate.query(sql, itemDetails, new Object[] {});
	}*/

	/*@Override
	public Page<InventoryItemDetailsData> retriveAllItemDetails(final Long limit, final Long offset) {	
		// TODO Auto-generated method stub
		context.authenticatedUser();
		ItemDetailsMapper itemDetails = new ItemDetailsMapper();
		String sql = "select "+itemDetails.schema();
		return this.paginationHelper.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()",sql,
                new Object[] {limit,offset}, itemDetails);
	}*/


	public Page<InventoryItemDetailsData> retriveAllItemDetails(SearchSqlQuery searchItemDetails) {	
		// TODO Auto-generated method stub
		context.authenticatedUser();
		ItemDetailsMapper itemDetails = new ItemDetailsMapper();
		
		StringBuilder sqlBuilder = new StringBuilder(200);
        sqlBuilder.append("select ");
        sqlBuilder.append(itemDetails.schema());
        sqlBuilder.append(" where item.office_id = office.id ");
        
        String sqlSearch = searchItemDetails.getSqlSearch();
        String extraCriteria = "";
	    if (sqlSearch != null) {
	    	sqlSearch=sqlSearch.trim();
	    	extraCriteria = " and (master.item_description like '%"+sqlSearch+"%' OR" 
	    			+ " item.serial_no like '%"+sqlSearch+"%' OR"
	    			+ " office.name like '%"+sqlSearch+"%' OR"
	    			+ " item.quality like '%"+sqlSearch+"%' OR"
	    			+ " item.status like '%"+sqlSearch+"%' )";
	    			//+ " (select supplier_description from b_supplier where id = (select supplier_id from b_grn where b_grn.id=item.grn_id)) like '%"+sqlSearch+"%' ";
	    }
	   
	 
            sqlBuilder.append(extraCriteria);
        
        /*if (StringUtils.isNotBlank(extraCriteria)) {
            sqlBuilder.append(extraCriteria);
        }*/


        if (searchItemDetails.isLimited()) {
            sqlBuilder.append(" limit ").append(searchItemDetails.getLimit());
        }

        if (searchItemDetails.isOffset()) {
            sqlBuilder.append(" offset ").append(searchItemDetails.getOffset());
        }

		return this.paginationHelper.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()",sqlBuilder.toString(),
                new Object[] {}, itemDetails);
	}

	/*
	 * this method is not implemented or being used, who ever wants to use this method please remove this comment and give a message.
	 * */
	
	@Override
	public InventoryItemDetailsData retriveIndividualItemDetails() {
		// TODO Auto-generated method stub
		context.authenticatedUser();
		return null;
	}


	private class SerialNumberMapper implements RowMapper<String>{

		@Override
		public String mapRow(ResultSet rs, int rowNum)throws SQLException {
			String serialNumber = rs.getString("serialNumber");
			return serialNumber;
		}
	}
	
	private class QuantityMapper implements RowMapper<QuantityData>{
		
		@Override
		public QuantityData mapRow(ResultSet rs,int rowNum)throws SQLException{
			Long quantity = rs.getLong("quantity");
			return new QuantityData(quantity);
		}
	}
	
	private class ItemMasterMapper implements RowMapper<ItemMasterIdData>{
		
		@Override
		public ItemMasterIdData mapRow(ResultSet rs, int rowNum)throws SQLException{
			Long itemMasterId = rs.getLong("itemMasterId");
			return new ItemMasterIdData(itemMasterId);
		}
	}
	
	private final class ItemDetailMapper implements RowMapper<InventoryItemDetails>{
		
		@Override
		public InventoryItemDetails mapRow(ResultSet rs, int rowNum)throws SQLException{
			
			Long id = rs.getLong("id");
			Long clientId = rs.getLong("clientId");
			
			return new InventoryItemDetails(id,clientId);
		}
	}  
	
	private class SerialNumberForItemMasterIDMapper implements RowMapper<Long>{
		@Override
		public Long mapRow(ResultSet rs,int rowNum)throws SQLException{
			Long itemMasterId = rs.getLong("itemMasterId");
			return itemMasterId;
		}
	}
	
private final class SerialNumberForValidation implements RowMapper<String>{
		
		@Override
		public String mapRow(ResultSet rs, int rowNum)throws SQLException{
			String serialNumber = rs.getString("serialNumber");
			return serialNumber;
		}
	}
	
	@Override
	public List<String> retriveSerialNumbers() {
		context.authenticatedUser();
		SerialNumberForValidation rowMapper = new SerialNumberForValidation();
		String sql = "select serial_no as serialNumber from b_item_detail item";
		return jdbcTemplate.query(sql, rowMapper);
	}
	
	@Override
	public List<String> retriveSerialNumbers(Long oneTimeSaleId) {
		
		context.authenticatedUser();
		SerialNumberMapper rowMapper = new SerialNumberMapper();
		String sql = "select idt.serial_no as serialNumber from b_onetime_sale ots left join b_item_detail idt on idt.item_master_id = ots.item_id where ots.id = ? and idt.client_id is null";/*"select serial_no as serialNumber from b_item_detail where item_master_id=(select item_id from b_onetime_sale where id=?) and client_id is null";*/
		return this.jdbcTemplate.query(sql,rowMapper,new Object[]{oneTimeSaleId});
	}
	
	@Override
	public QuantityData retriveQuantity(Long oneTimeSaleId){
		context.authenticatedUser();
		QuantityMapper rowMapper = new QuantityMapper();
		String sql = "select ots.quantity as quantity from b_onetime_sale ots where ots.id = ?";/*String sql = "select ots.quantity as quantity from b_onetime_sale ots left join b_item_detail idt on idt.item_master_id = ots.item_id where ots.id = ? limit 1";*/
		return this.jdbcTemplate.queryForObject(sql, rowMapper, new Object[]{oneTimeSaleId});
	}
	
	@Override
	public ItemMasterIdData retriveItemMasterId(Long oneTimeSaleId){
		context.authenticatedUser();
		ItemMasterMapper rowMapper = new ItemMasterMapper();
		String sql = "select idt.item_master_id as itemMasterId from b_onetime_sale ots left join b_item_detail idt on idt.item_master_id = ots.item_id where ots.id = ? limit 1";
		return this.jdbcTemplate.queryForObject(sql, rowMapper,new Object[]{oneTimeSaleId});
	}
	
	
	public InventoryItemDetails retriveInventoryItemDetail(String serialNumber,Long itemMasterId){
		
		try{
		context.authenticatedUser();
		ItemDetailMapper rowMapper = new ItemDetailMapper();
		String sql = "select id,client_id as clientId from b_item_detail i where i.serial_no=? and i.item_master_id=?";
		return this.jdbcTemplate.queryForObject(sql,rowMapper,new Object[]{serialNumber,itemMasterId});
		}catch(EmptyResultDataAccessException accessException){
			return null;
		}
	}
	
	@Override
	public InventoryItemSerialNumberData retriveAllocationData(List<String> itemSerialNumbers,QuantityData quantityData, ItemMasterIdData itemMasterIdData){
		
		return new InventoryItemSerialNumberData(itemSerialNumbers, quantityData.getQuantity(), itemMasterIdData.getItemMasterId());
	}


	@Override
	public List<Long> retriveSerialNumberForItemMasterId(String serialNumber) {
		SerialNumberForItemMasterIDMapper rowMapper = new SerialNumberForItemMasterIDMapper();
		String sql = "select i.item_master_id as itemMasterId from b_item_detail i where i.serial_no =?";
		return jdbcTemplate.query(sql, rowMapper, new Object[]{serialNumber});
	}

	@Override
	public InventoryItemDetailsData retriveSingleItemDetail(Long itemId) {
		try{
		String sql = "select item.id as id,office.name as officeName, item.item_master_id as itemMasterId, item.serial_no as serialNumber, item.grn_id as grnId, (select supplier_description from b_supplier where id = (select supplier_id from b_grn where b_grn.id=item.grn_id)) as supplier,item.provisioning_serialno as provisioningSerialNumber, item.quality as quality, item.status as status, (select warranty from b_item_master where id = item.item_master_id) as warranty, item.remarks as remarks, master.item_description as itemDescription, item.client_id as clientId from b_item_detail item left outer join b_item_master master on item.item_master_id = master.id left outer join m_office office on item.office_id = office.id where item.id=?";
		ItemDetailsMapper2 rowMapper = new ItemDetailsMapper2();
		return jdbcTemplate.queryForObject(sql, rowMapper,new Object[]{itemId});
		}catch(EmptyResultDataAccessException accessException){
			throw new PlatformDataIntegrityException("validation.error.msg.inventory.item.invalid.item.id", "validation.error.msg.inventory.item.invalid.item.id", "validation.error.msg.inventory.item.invalid.item.id","");
		}
	}
	
	@Override
	public List<String> retriveSerialNumbersOnKeyStroke(final Long oneTimeSaleId, final String query) {
		
		context.authenticatedUser();
		SerialNumberMapper rowMapper = new SerialNumberMapper();
		String sql = "select idt.serial_no as serialNumber from b_onetime_sale ots left join b_item_detail idt on idt.item_master_id = ots.item_id where ots.id = ? and idt.client_id is null and idt.serial_no like '%"+query+"%' and quality='Good' order by idt.id limit 20";
		return this.jdbcTemplate.query(sql,rowMapper,new Object[]{oneTimeSaleId});
	}
	@Override
	public InventoryItemSerialNumberData retriveAllocationData(List<String> itemSerialNumbers){
		
		return new InventoryItemSerialNumberData(itemSerialNumbers);
	}
	
	

}

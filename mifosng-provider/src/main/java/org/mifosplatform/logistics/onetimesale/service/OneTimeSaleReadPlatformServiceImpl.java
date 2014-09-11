package org.mifosplatform.logistics.onetimesale.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.logistics.item.data.ItemData;
import org.mifosplatform.logistics.itemdetails.domain.InventoryItemDetailsAllocation;
import org.mifosplatform.logistics.onetimesale.data.AllocationDetailsData;
import org.mifosplatform.logistics.onetimesale.data.OneTimeSaleData;
import org.pentaho.reporting.engine.classic.core.EmptyReportException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class OneTimeSaleReadPlatformServiceImpl implements	OneTimeSaleReadPlatformService {

	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;

	@Autowired
	public OneTimeSaleReadPlatformServiceImpl(final PlatformSecurityContext context,final TenantAwareRoutingDataSource dataSource) {
		this.context = context;
		this.jdbcTemplate = new JdbcTemplate(dataSource);

	}

	@Override
	public List<ItemData> retrieveItemData() {
		context.authenticatedUser();

		TicketDataMapper mapper = new TicketDataMapper();

		String sql = "select " + mapper.schema() + " where i.is_deleted='n'";

		return this.jdbcTemplate.query(sql, mapper, new Object[] {});
	}

	private static final class TicketDataMapper implements RowMapper<ItemData> {

		public String schema() {
			return "i.id AS id,i.item_description AS itemCode,i.units AS units,i.unit_price AS unitPrice FROM b_item_master i  ";

		}

		@Override
		public ItemData mapRow(ResultSet rs, int rowNum) throws SQLException {

			Long id = rs.getLong("id");
			String itemCode = rs.getString("itemCode");
			String units = rs.getString("units");
			BigDecimal unitPrice = rs.getBigDecimal("unitPrice");

			return new ItemData(id, itemCode, units, units, units, null, 0,
					unitPrice,null,null,null);

		}

	}

	@Override
	public List<OneTimeSaleData> retrieveClientOneTimeSalesData(Long clientId) {
		context.authenticatedUser();

		SalesDataMapper mapper = new SalesDataMapper();

		String sql = "select " + mapper.schema()
				+ " where o.item_id=i.id  and o.client_id=? and o.is_deleted = 'N' group by o.id order by o.id";

		return this.jdbcTemplate.query(sql, mapper, new Object[] { clientId });
	}

	private static final class SalesDataMapper implements
			RowMapper<OneTimeSaleData> {

		public String schema() {
			return "o.id AS id,i.item_code AS itemCode, i.item_class as itemClass, a.serial_no as serialNo,o.sale_date as saleDate,o.charge_code AS chargeCode,"
					+ "o.quantity as quantity,o.total_price as totalPrice,o.hardware_allocated as hardwareAllocated  FROM b_item_master i,b_onetime_sale o" +
					" left join b_allocation a on a.order_id=o.id and a.is_deleted = 'N' ";

		}

		@Override
		public OneTimeSaleData mapRow(ResultSet rs, int rowNum)
				throws SQLException {

			Long id = rs.getLong("id");
			LocalDate saleDate = JdbcSupport.getLocalDate(rs, "saleDate");
			String itemCode = rs.getString("itemCode");
			String chargeCode = rs.getString("chargeCode");
			String quantity = rs.getString("quantity");
			BigDecimal totalPrice = rs.getBigDecimal("totalPrice");
			String haardwareAllocated = rs.getString("hardwareAllocated");
			String itemClass = rs.getString("itemClass");
			String serialNo = rs.getString("serialNo");
			return new OneTimeSaleData(id, saleDate, itemCode, chargeCode,quantity, totalPrice,haardwareAllocated,itemClass,serialNo);

		}

	}
	
	
	@Override
	public List<OneTimeSaleData> retrieveOnetimeSaleDate(Long clientId) {
		context.authenticatedUser();

		OneTimeSalesDataMapper mapper = new OneTimeSalesDataMapper();

		String sql = "select " + mapper.schema()
				+ " where ots.client_id = ? ";

		return this.jdbcTemplate.query(sql, mapper, new Object[] { clientId });
	}

	private static final class OneTimeSalesDataMapper implements
			RowMapper<OneTimeSaleData> {

		public String schema() {
			return " ots.id as oneTimeSaleId, ots.client_id AS clientId,ots.units AS units,ots.charge_code AS chargeCode,ots.unit_price AS unitPrice,"
				 + " ots.quantity AS quantity,ots.total_price AS totalPrice,ots.is_invoiced as isInvoiced,ots.item_id as itemId,ots.discount_id as discountId" +
				   " FROM b_onetime_sale ots ";

		}

		@Override
		public OneTimeSaleData mapRow(ResultSet rs, int rowNum)
				throws SQLException {

			Long oneTimeSaleId = rs.getLong("oneTimeSaleId");
			Long clientId = rs.getLong("clientId");
			String units = rs.getString("units");
			String chargeCode = rs.getString("chargeCode");
			BigDecimal unitPrice = rs.getBigDecimal("unitPrice");
			String quantity = rs.getString("quantity");
			BigDecimal totalPrice = rs.getBigDecimal("totalPrice");
			String isInvoiced = rs.getString("isInvoiced");
			Long itemId = rs.getLong("itemId");
			Long discountId=rs.getLong("discountId");
			return new OneTimeSaleData(oneTimeSaleId,clientId, units, chargeCode, unitPrice,quantity, totalPrice,isInvoiced,itemId,discountId);

		}

	}

	@Override
	public OneTimeSaleData retrieveSingleOneTimeSaleDetails(Long saleId) {
		context.authenticatedUser();

		OneTimeSalesDataMapper mapper = new OneTimeSalesDataMapper();

		String sql = "select " + mapper.schema()
				+ " where ots.id = ? ";

		return this.jdbcTemplate.queryForObject(sql, mapper, new Object[] { saleId });
	}

	@Override
	public List<AllocationDetailsData> retrieveAllocationDetails(Long orderId) {
		AllocationDataMapper mapper = new AllocationDataMapper();

		String sql = "select " + mapper.schema()+ " and a.order_id=? ";

		return this.jdbcTemplate.query(sql, mapper, new Object[] { orderId });
	}

	private static final class AllocationDataMapper implements	RowMapper<AllocationDetailsData> {

		public String schema() {
			return "  a.id AS id,id.id AS itemDetailId,i.item_description AS itemDescription,a.serial_no AS serialNo,a.allocation_date AS allocationDate" +
					" FROM b_allocation a, b_item_master i, b_item_detail id WHERE  a.item_master_id = i.id   AND a.is_deleted = 'N' and id.client_id = a.client_id " +
					"  and id.serial_no = a.serial_no ";

		}

		@Override
		public AllocationDetailsData mapRow(ResultSet rs, int rowNum)
				throws SQLException {

			Long id = rs.getLong("id");
			Long itemDetailId = rs.getLong("itemDetailId");
			String itemDescription = rs.getString("itemDescription");
			String serialNo = rs.getString("serialNo");
			LocalDate allocationDate=JdbcSupport.getLocalDate(rs,"allocationDate");
			return new AllocationDetailsData(id,itemDescription,serialNo,allocationDate,itemDetailId);

		}
	}

	@Override
	public AllocationDetailsData retrieveAllocationDetailsBySerialNo(String serialNo) {
 
		try{
		AllocationDataMapper mapper = new AllocationDataMapper();

		String sql = "select " + mapper.schema()+ " and a.serial_no=?";

		return this.jdbcTemplate.queryForObject(sql, mapper, new Object[] { serialNo });
		
		}catch(EmptyResultDataAccessException accessException){
			return null;
		}
	
	}
	}


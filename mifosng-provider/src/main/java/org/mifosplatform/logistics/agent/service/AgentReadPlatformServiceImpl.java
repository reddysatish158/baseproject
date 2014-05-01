package org.mifosplatform.logistics.agent.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.logistics.agent.data.AgentItemSaleData;
import org.mifosplatform.portfolio.plan.data.ServiceData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class AgentReadPlatformServiceImpl implements AgentReadPlatformService{
	
private final JdbcTemplate jdbcTemplate;
private final PlatformSecurityContext context;
	
@Autowired	
public AgentReadPlatformServiceImpl(final PlatformSecurityContext context,final TenantAwareRoutingDataSource dataSource){
	
	this.jdbcTemplate=new JdbcTemplate(dataSource);
	this.context=context;
		
	}


@Transactional
@Override
public List<AgentItemSaleData> retrieveAllData() {

	 try{
		 
		 this.context.authenticatedUser();
		 ItemSaleMapper mapper=new ItemSaleMapper();
		 final String sql="select "+mapper.schema();
		 return this.jdbcTemplate.query(sql,mapper,new Object[]{});
		 
	 }catch(EmptyResultDataAccessException exception){
		 return null;
	 }

	
	
}

private static final class ItemSaleMapper implements RowMapper<AgentItemSaleData> {

	public String schema() {
		return "  it.id as id,it.item_id as itemId,it.agent_id as agentId,im.item_description AS itemName,o.name AS agentName," +
				" it.purchase_date AS purchaseDate,it.order_quantity AS orderQunatity,i.charge_amount AS , " +
				" i.tax_percantage AS tax,i.invoice_amount AS invoiceAmount FROM b_itemsale it, m_invoice i,b_item_master im," +
				" m_office o WHERE it.item_id = im.id AND it.agent_id = o.id AND it.id = i.sale_id";

	}

	@Override
	public AgentItemSaleData mapRow(final ResultSet rs,
			@SuppressWarnings("unused") final int rowNum)
			throws SQLException {

		Long id = rs.getLong("id");
		Long itemId=rs.getLong("itemId");
		Long agentId=rs.getLong("agentId");
		Long orderQunatity=rs.getLong("orderQunatity");
		String itemName = rs.getString("itemName");
		String agentName = rs.getString("agentName");
		BigDecimal chargeAmount=rs.getBigDecimal("chargeAmount");
	    BigDecimal tax=rs.getBigDecimal("tax");
	    BigDecimal invoiceAmount=rs.getBigDecimal("invoiceAmount");
		return new AgentItemSaleData(id,itemId,agentId,itemName,agentName,orderQunatity,chargeAmount,tax,invoiceAmount);

	}
}


}

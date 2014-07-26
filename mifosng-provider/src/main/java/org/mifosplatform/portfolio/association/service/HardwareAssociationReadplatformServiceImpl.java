package org.mifosplatform.portfolio.association.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.mifosplatform.infrastructure.configuration.domain.ConfigurationConstants;
import org.mifosplatform.infrastructure.configuration.domain.GlobalConfigurationProperty;
import org.mifosplatform.infrastructure.configuration.domain.GlobalConfigurationRepository;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.association.data.AssociationData;
import org.mifosplatform.portfolio.association.data.HardwareAssociationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class HardwareAssociationReadplatformServiceImpl implements HardwareAssociationReadplatformService{
	
	
	 private final JdbcTemplate jdbcTemplate;
	 private final PlatformSecurityContext context;
	 private final GlobalConfigurationRepository configurationRepository;
	
	  
	    @Autowired
	    public HardwareAssociationReadplatformServiceImpl(final PlatformSecurityContext context,final GlobalConfigurationRepository configurationRepository, 
	    		final TenantAwareRoutingDataSource dataSource)
	    {
	        this.context = context;
	        this.jdbcTemplate = new JdbcTemplate(dataSource);
	        this.configurationRepository=configurationRepository;
	    }

		@Override
		public List<HardwareAssociationData> retrieveClientHardwareDetails(Long clientId)
		{

              try
              {
            	  String sql=null;
            	  GlobalConfigurationProperty configurationProperty=this.configurationRepository.findOneByName(ConfigurationConstants.CPE_TYPE);
            	  HarderwareMapper mapper = new HarderwareMapper();
            	  
            	  if(configurationProperty.getValue().equalsIgnoreCase(ConfigurationConstants.CONFIR_PROPERTY_SALE)){
			       sql = "select " + mapper.schema();
			      
            	  }else if(configurationProperty.getValue().equalsIgnoreCase(ConfigurationConstants.CONFIR_PROPERTY_OWN)){
            		  
            		  sql = "select " + mapper.ownDeviceSchema();
            	  }
			      return this.jdbcTemplate.query(sql, mapper, new Object[] {clientId});

		}catch(EmptyResultDataAccessException accessException){
			return null;
		}
		}
		private static final class HarderwareMapper implements RowMapper<HardwareAssociationData> {

			public String schema() {
				return "  a.id AS id, a.serial_no AS serialNo  FROM b_allocation a  WHERE    NOT EXISTS (SELECT * FROM  b_association s" +
						" WHERE  s.hw_serial_no=a.serial_no) and a.client_id=?";

			}
			
			public String ownDeviceSchema() {
				return "  o.id AS id, o.serial_number  AS serialNo FROM  b_owned_hardware o WHERE NOT EXISTS (SELECT *" +
						" FROM b_association s WHERE s.hw_serial_no = o.serial_number ) AND o.client_id = ?";

			}

			@Override
			public HardwareAssociationData mapRow(final ResultSet rs,
					@SuppressWarnings("unused") final int rowNum)
					throws SQLException {
				Long id = rs.getLong("id");
				String serialNo = rs.getString("serialNo");
				
				HardwareAssociationData associationData=new HardwareAssociationData(id,serialNo,null,null,null);
				return associationData; 
			}
		}
		@Override
		public List<HardwareAssociationData> retrieveClientAllocatedPlan(Long clientId,String itemCode) {
            try
            {

          	  PlanMapper mapper = new PlanMapper();

			String sql = "select " + mapper.schema();
			return this.jdbcTemplate.query(sql, mapper, new Object[] {clientId,itemCode});

		}catch(EmptyResultDataAccessException accessException){
			return null;
		}
		}
		private static final class PlanMapper implements RowMapper<HardwareAssociationData> {

			public String schema() {
				return " o.id AS id, o.plan_id AS planId,hm.item_code as itemCode  FROM b_orders o,b_hw_plan_mapping hm, b_plan_master p  WHERE NOT EXISTS  (SELECT *FROM b_association a" +
						" WHERE  a.order_id=o.id  AND a.client_id = o.client_id  AND a.is_deleted = 'N') AND o.client_id =? AND hm.plan_code = p.plan_code" +
						" AND o.plan_id = p.id and hm.item_code=?  and o.id =(select max(id) from b_orders where client_id=o.client_id )";


			}

			@Override
			public HardwareAssociationData mapRow(final ResultSet rs,
					@SuppressWarnings("unused") final int rowNum)
					throws SQLException {
				Long id = rs.getLong("id");
				Long planId = rs.getLong("planId");
				Long orderId=rs.getLong("id");
				String itemCode = rs.getString("itemCode");
				HardwareAssociationData associationData=new HardwareAssociationData(id,null,planId,orderId,itemCode);

				return associationData; 

			}
		}
		@Override
		public List<AssociationData> retrieveClientAssociationDetails(Long clientId) {
            try
            {

          	  HarderwareAssociationMapper mapper = new HarderwareAssociationMapper();
			  String sql = "select " + mapper.schema();
			   return this.jdbcTemplate.query(sql, mapper, new Object[] {clientId});

		    }catch(EmptyResultDataAccessException accessException){
			return null;
		  }
		}
		private static final class HarderwareAssociationMapper implements RowMapper<AssociationData> {

			public String schema() {
				return "a.id as id,a.order_id AS orderId,p.plan_code as planCode,i.item_code as itemCode, a.hw_serial_no AS serialNum "
                       +" FROM b_association a,b_plan_master p,b_allocation al,b_item_master i"
                       +" where a.plan_id=p.id and a.hw_serial_no=al.serial_no and al.item_master_id=i.id and a.client_id = ?";

			}

			@Override
			public AssociationData mapRow(final ResultSet rs,
					@SuppressWarnings("unused") final int rowNum)
					throws SQLException {
				Long id= rs.getLong("id");
				Long orderId = rs.getLong("orderId");
				String planCode = rs.getString("planCode");
				String itemCode = rs.getString("itemCode");
				String serialNum = rs.getString("serialNum");
				
				return  new AssociationData(orderId,id,planCode,itemCode,serialNum,null);

			}
		}
		
        @Transactional
		@Override
		public List<AssociationData> retrieveHardwareData(Long clientId) {
			try
            {
				
				  String sql=null;
			  	  GlobalConfigurationProperty configurationProperty=this.configurationRepository.findOneByName(ConfigurationConstants.CPE_TYPE);
			  	  AssociationMapper mapper = new AssociationMapper();
			  	  
			  	 if(configurationProperty.getValue().equalsIgnoreCase(ConfigurationConstants.CONFIR_PROPERTY_SALE)){
			             sql = "select " + mapper.schema();
			      
			  	  }else if(configurationProperty.getValue().equalsIgnoreCase(ConfigurationConstants.CONFIR_PROPERTY_OWN)){
			  		  
			  		  sql = "select " + mapper.ownDeviceSchema();
			  	  }
			   return this.jdbcTemplate.query(sql, mapper, new Object[] { clientId });

		    }catch(EmptyResultDataAccessException accessException){
			return null;
		  }
		}
		
		private static final class AssociationMapper implements RowMapper<AssociationData> {

			public String schema() {
				return " b.serial_no AS serialNum,b.provisioning_serialno as provisionNum   FROM  b_item_detail b " +
						/*" WHERE NOT EXISTS (SELECT *FROM b_association a WHERE a.hw_serial_no = b.serial_no and a.is_deleted ='N') and " +*/
						" where  b.client_id=?"; 
			}
			
			public String ownDeviceSchema() {
				return " o.serial_number  AS serialNum, o.provisioning_serial_number  AS provisionNum FROM b_owned_hardware o " +
						" WHERE o.client_id = ? and o.is_deleted = 'N'"; 
			}

			@Override
			public AssociationData mapRow(final ResultSet rs,
					@SuppressWarnings("unused") final int rowNum)
					throws SQLException {
				String serialNum = rs.getString("serialNum");				
				String provisionNumber = rs.getString("provisionNum");
				return new AssociationData(serialNum,provisionNumber); 
			}
		}
		@Override
		public List<AssociationData> retrieveplanData(Long clientId) {
			
			try
            {
          	  AssociationPlanMapper mapper = new AssociationPlanMapper();
			  String sql = "select " + mapper.schema();
			   return this.jdbcTemplate.query(sql, mapper, new Object[] {clientId});

		    }catch(EmptyResultDataAccessException accessException){
			return null;
		  }
		}
		
		private static final class AssociationPlanMapper implements RowMapper<AssociationData> {

			public String schema() {
				return "p.plan_code as planCode,p.id as id,o.id as orderId from b_orders o,b_plan_master p" +
						" where o.plan_id=p.id and NOT EXISTS(Select * from  b_association a WHERE   a.order_id =o.id and a.is_deleted='N') and o.client_id=? ";
			}

			@Override
			public AssociationData mapRow(final ResultSet rs,
					@SuppressWarnings("unused") final int rowNum)
					throws SQLException {
				Long planId= rs.getLong("id");
				String planCode = rs.getString("planCode");
			    Long id=rs.getLong("orderId");
				return new AssociationData(planId,planCode,id);
			}
		}
		@Override
		public AssociationData retrieveSingleDetails(Long id) {
			 try
	            {
				 
				 String sql=null;
			  	  GlobalConfigurationProperty configurationProperty=this.configurationRepository.findOneByName(ConfigurationConstants.CPE_TYPE);
			  	  Mapper mapper = new Mapper();
			  	  
			  	 if(configurationProperty.getValue().equalsIgnoreCase(ConfigurationConstants.CONFIR_PROPERTY_SALE)){
			             sql = "select " + mapper.schema();
			      
			  	  }else if(configurationProperty.getValue().equalsIgnoreCase(ConfigurationConstants.CONFIR_PROPERTY_OWN)){
			  		  
			  		  sql = "select " + mapper.ownDeviceSchema();
			  	  }
	          	
				 
				   return this.jdbcTemplate.queryForObject(sql, mapper, new Object[] {id});

			    }catch(EmptyResultDataAccessException accessException){
				return null;
			  }
		}
		
		private static final class Mapper implements RowMapper<AssociationData> {

			public String schema() {
				return "  a.id AS id,a.client_id AS clientId,a.order_id AS orderId,i.id as itemId,a.hw_serial_no AS serialNo,p.plan_code AS planCode,id.serial_no AS serialNum," +
					   " p.id AS planId,i.item_code AS itemCode,os.id as saleId FROM b_association a,b_plan_master p,b_item_detail id,b_item_master i, b_onetime_sale os" +
					   "  WHERE p.id = a.plan_id AND a.order_id = ? AND id.serial_no = a.hw_serial_no AND id.item_master_id = i.id   AND a.is_deleted = 'N' and " +
					   "  os.item_id =i.id and os.client_id = a.client_id group by id";

			}
			

			public String ownDeviceSchema() {
				return "  a.id AS id,a.client_id AS clientId,a.order_id AS orderId,i.id AS itemId,a.hw_serial_no AS serialNo,p.plan_code AS planCode," +
						" o.serial_number AS serialNum, p.id AS planId,i.item_code AS itemCode, null as saleId FROM b_association a,b_plan_master p,b_owned_hardware o," +
						" b_item_master i WHERE p.id = a.plan_id AND a.order_id =? AND o.serial_number = a.hw_serial_no AND o.item_type = i.id " +
						" AND a.is_deleted = 'N' GROUP BY id";

			}

			@Override
			public AssociationData mapRow(final ResultSet rs,final int rowNum)
					throws SQLException {
				Long id= rs.getLong("id");
				Long clientId=rs.getLong("clientId");
				Long orderId = rs.getLong("orderId");
				String planCode = rs.getString("planCode");
				String itemCode = rs.getString("itemCode");
				String provNum = rs.getString("serialNo");
				String serialNum = rs.getString("serialNum");
				Long planId=rs.getLong("planId");
				Long saleId=rs.getLong("saleId");
				Long itemId=rs.getLong("itemId");
				return  new AssociationData(orderId,planCode,provNum,id,planId,clientId,serialNum,itemCode,saleId,itemId);

			}

		}
		
		@Transactional
		@Override
		public List<HardwareAssociationData> retrieveClientAllocatedHardwareDetails(Long clientId) {
            try
            {
          	  String sql=null;
          	  GlobalConfigurationProperty configurationProperty=this.configurationRepository.findOneByName(ConfigurationConstants.CPE_TYPE);
          	ClientHarderwareMapper mapper = new ClientHarderwareMapper();
          	  
          	  if(configurationProperty.getValue().equalsIgnoreCase(ConfigurationConstants.CONFIR_PROPERTY_SALE)){
			       sql = "select " + mapper.schema();
			      
          	  }else if(configurationProperty.getValue().equalsIgnoreCase(ConfigurationConstants.CONFIR_PROPERTY_OWN)){
          		  
          		  sql = "select " + mapper.ownDeviceSchema();
          	  }
			      return this.jdbcTemplate.query(sql, mapper, new Object[] {clientId});

		}catch(EmptyResultDataAccessException accessException){
			return null;
		}
		}
		private static final class ClientHarderwareMapper implements RowMapper<HardwareAssociationData> {

			public String schema() {
				return " max(a.id) AS id,a.serial_no AS serialNo,a.provisioning_serialno AS provSerialNum  " +
					   " FROM b_item_detail a, b_allocation l where a.serial_no = l.serial_no and l.client_id = ? " +
					   " and l.is_deleted = 'Y' and a.client_id is null";

			}
			
			public String ownDeviceSchema() {
				return " o.id as id ,o.serial_number as serialNo,o.provisioning_serial_number as provSerialNum   FROM b_owned_hardware o" +
						" where o.client_id = ? and o.is_deleted='Y' and o.id=(select max(id) from b_owned_hardware a where a.client_id= o.client_id )";

			}

			@Override
			public HardwareAssociationData mapRow(ResultSet rs, int rowNum)
					throws SQLException {
				
					Long id= rs.getLong("id");
					String serialNo = rs.getString("serialNo");
					String provSerialNum = rs.getString("provSerialNum");
					
					return  new HardwareAssociationData(id, serialNo,provSerialNum);

				}
			}
		@Override
		public Long retrieveOrderAssociationDetails(Long orderid, Long clientId) {
			  try
	            {
	          	  String sql=null;
	          	 // GlobalConfigurationProperty configurationProperty=this.configurationRepository.findOneByName(ConfigurationConstants.CPE_TYPE);
	          //	ClientHarderwareMapper mapper = new ClientHarderwareMapper();
	          	  
	          	 
				       sql = "select id as id from b_association where order_id=? and client_id=? limit 1";
				      
	          	
				      return this.jdbcTemplate.queryForLong(sql, new Object[] {orderid,clientId});

			}catch(EmptyResultDataAccessException accessException){
				return null;
			}
			}
}

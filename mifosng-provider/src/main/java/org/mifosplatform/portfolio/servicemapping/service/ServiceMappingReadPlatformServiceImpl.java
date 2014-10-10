package org.mifosplatform.portfolio.servicemapping.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.portfolio.servicemapping.data.ServiceCodeData;
import org.mifosplatform.portfolio.servicemapping.data.ServiceMappingData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;


@Service
public class ServiceMappingReadPlatformServiceImpl implements ServiceMappingReadPlatformService{
	
	private final JdbcTemplate jdbcTemplate;
	
	@Autowired
	public ServiceMappingReadPlatformServiceImpl(final TenantAwareRoutingDataSource tenantAwareRoutingDataSource){
		this.jdbcTemplate = new JdbcTemplate(tenantAwareRoutingDataSource);
	}

	
	private static final class ServiceMappingMapper implements RowMapper<ServiceMappingData> {
		public String schema() {
			
			return " ps.id as id, bs.service_code as serviceCode, ps.service_identification as serviceIdentification, bs.status as status,ps.image as image, "
					+ "ps.category as category,ps.sub_category as subCategory from  b_service bs,  b_prov_service_details ps where bs.status='ACTIVE' and ps.service_id=bs.id ORDER BY ps.id ";
			
		}
		
		@Override
		public ServiceMappingData mapRow(final ResultSet rs, final int rowNum) throws SQLException {
			Long id = rs.getLong("id");
			String serviceCode = rs.getString("serviceCode");
			String serviceIdentification = rs.getString("serviceIdentification");
			String status = rs.getString("status");
			String image = rs.getString("image");
			String category=rs.getString("category");
			String subCategory=rs.getString("subCategory");
			return new ServiceMappingData(id,serviceCode, serviceIdentification, status, image, category, subCategory);
		}
	}
	
	
	public List<ServiceMappingData> getServiceMapping() {
		ServiceMappingMapper mapper = new ServiceMappingMapper();
		String sql = "Select " + mapper.schema();
		return this.jdbcTemplate.query(sql, mapper,new Object[] {});
	}
	

	
	private class ServiceCodeDataMapper implements RowMapper<ServiceCodeData>{
		public ServiceCodeData mapRow(final ResultSet rs , final int rowNum)throws SQLException{
			Long id = rs.getLong("id");
			String serviceCode = rs.getString("serviceCode");
			String serviceType = rs.getString("serviceType");
			
			return new ServiceCodeData(id,serviceCode,serviceType);
		}
	}

	public List<ServiceCodeData> getServiceCode(){
		String sql = "select bs.id as id,bs.service_code as serviceCode,bs.service_type as serviceType  from b_service bs " +
				"where bs.status='ACTIVE' and bs.is_deleted ='N'  order by bs.id";
		ServiceCodeDataMapper rowMapper = new ServiceCodeDataMapper();
		return jdbcTemplate.query(sql, rowMapper);
	}
	
	
private class ServiceMappingDataByIdRowMapper implements RowMapper<ServiceMappingData>{
	
	public String schema() {
		
		return " bs.id as serviceId,bs.service_code as serviceCode, ps.service_identification as serviceIdentification, bs.status as status," +
				"ps.image as image,ps.category as category,ps.sub_category as subCategory  from b_service bs, b_prov_service_details ps ";
		
	}
		
		@Override
		public ServiceMappingData mapRow(ResultSet rs, int rowNum)throws SQLException{
			
			Long serviceId=rs.getLong("serviceId");
			String serviceCode = rs.getString("serviceCode");
			String serviceIdentification = rs.getString("serviceIdentification");
			String status = rs.getString("status");
			String image = rs.getString("image");
			String category =rs.getString("category");
			String subCategory =rs.getString("subCategory");
			return new ServiceMappingData(serviceId, serviceCode, serviceIdentification, status, image,category,subCategory);
		}
	}
	
	
	public ServiceMappingData getServiceMapping(Long serviceMappingId){
		
		ServiceMappingDataByIdRowMapper rowMapper = new ServiceMappingDataByIdRowMapper();
		String sql = "select"+rowMapper.schema()+" where ps.service_id=bs.id and ps.id=?";
		return jdbcTemplate.queryForObject(sql, rowMapper,new Object[]{serviceMappingId});
	}
	
}

package org.mifosplatform.organisation.address.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.core.service.PaginationHelper;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.address.data.AddressData;
import org.mifosplatform.organisation.address.data.AddressDetails;
import org.mifosplatform.organisation.address.data.CountryDetails;
import org.mifosplatform.organisation.address.domain.AddressEnum;
import org.mifosplatform.portfolio.order.data.AddressStatusEnumaration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;



@Service
public class AddressReadPlatformServiceImpl implements AddressReadPlatformService {
	
	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;
	private final PaginationHelper<AddressDetails> paginationHelper=new PaginationHelper<AddressDetails>();

	@Autowired
	public AddressReadPlatformServiceImpl(final PlatformSecurityContext context,
			final TenantAwareRoutingDataSource dataSource) {
		this.context = context;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}


	@Override
	public List<AddressData> retrieveAddressDetails(Long clientId) {

		try{
		context.authenticatedUser();
		AddressMapper mapper = new AddressMapper();
		String sql = "select " + mapper.schema()+" where is_deleted='n' and a.address_key='PRIMARY' and a.client_id="+clientId;
		return this.jdbcTemplate.query(sql, mapper, new Object[] {});
		}catch (final EmptyResultDataAccessException e) {
			return null;
		}
	}

	private static final class AddressMapper implements RowMapper<AddressData> {

		public String schema() {
			return "a.address_id as id,a.client_id as clientId,a.address_key as addressKey,a.address_no as addressNo,a.street as street,a.zip as zip,a.city as city,"
				  +"a.state as state,a.country as country from b_client_address a";

		}

		@Override
		public AddressData mapRow(final ResultSet rs,
				@SuppressWarnings("unused") final int rowNum)
				throws SQLException {

			Long id = rs.getLong("id");
			Long clientId = rs.getLong("clientId");
			String addressKey = rs.getString("addressKey");
			String addressNo = rs.getString("addressNo");
			String street = rs.getString("street");
			String zip = rs.getString("zip");
			String city = rs.getString("city");
			String state = rs.getString("state");
			String country = rs.getString("country");
			
			return new AddressData(id,clientId,null,addressNo,street,zip,city,state, country,addressKey,null);

		}
	}

	@Override
	public List<AddressData> retrieveSelectedAddressDetails(String selectedname) {
		
		AddressMapper mapper = new AddressMapper();
		String sql = "select " + mapper.schema()+" where a.city=? or a.state =? or a.country =? and a.is_deleted='n'";

		return this.jdbcTemplate.query(sql, mapper, new Object[]  { selectedname,selectedname,selectedname });
	}
	@Override
	public List<AddressData> retrieveAddressDetails() {

		context.authenticatedUser();
		AddressMapper mapper = new AddressMapper();

		String sql = "select " + mapper.schema()+" where is_deleted='n'";

		return this.jdbcTemplate.query(sql, mapper, new Object[] {});

	}


	@Override
	public List<String> retrieveCountryDetails() {
		context.authenticatedUser();
		AddressMapper1 mapper = new AddressMapper1();

		String sql = "select " + mapper.sqlschema("country_name","country");

		return this.jdbcTemplate.query(sql, mapper, new Object[] {});

	}

	private static final class AddressMapper1 implements RowMapper<String> {

		public String sqlschema(String placeholder,String tablename) {
			return placeholder+" as data from b_"+tablename;

		}

		@Override
		public String mapRow(final ResultSet rs,
				@SuppressWarnings("unused") final int rowNum)
				throws SQLException {

			
			String country = rs.getString("data");
			return country;
		

		}

	
	}

	@Override
	public List<String> retrieveStateDetails() {
		context.authenticatedUser();
		AddressMapper1 mapper = new AddressMapper1();

		String sql = "select " + mapper.sqlschema("state_name","state");

		return this.jdbcTemplate.query(sql, mapper, new Object[] {});

	}


	@Override
	public List<String> retrieveCityDetails() {
		context.authenticatedUser();
		AddressMapper1 mapper = new AddressMapper1();

		String sql = "select " + mapper.sqlschema("city_name","city");

		return this.jdbcTemplate.query(sql, mapper, new Object[] {});

	}


	@Override
	public List<AddressData> retrieveCityDetails(String selectedname) {
		context.authenticatedUser();
		DataMapper mapper = new DataMapper();

		String sql = "select " + mapper.schema(selectedname);

		return this.jdbcTemplate.query(sql, mapper, new Object[] {});

	}

	private static final class DataMapper implements RowMapper<AddressData> {

		public String schema(String placeHolder) {
			return "id as id,"+placeHolder+"_name as data from b_"+placeHolder;

		}

		@Override
		public AddressData mapRow(final ResultSet rs,
				@SuppressWarnings("unused") final int rowNum)
				throws SQLException {

			Long id = rs.getLong("id");
			String data = rs.getString("data");
		
			//String serviceDescription = rs.getString("service_description");
			return new AddressData(id,data);

		}
	}

	@Override
	public List<EnumOptionData> addressType() {
		
		EnumOptionData primary = AddressStatusEnumaration.enumOptionData(AddressEnum.PRIMARY);
		EnumOptionData billing =AddressStatusEnumaration.enumOptionData(AddressEnum.BILLING);
		List<EnumOptionData> categotyType = Arrays.asList(primary,billing);
			return categotyType;
	}


	@Override
	public AddressData retrieveName(String Name) {
        try{
        	
		context.authenticatedUser();
		String sql;
		retrieveMapper mapper=new retrieveMapper();
	    sql = "SELECT  " + mapper.schema();
	
		return this.jdbcTemplate.queryForObject(sql, mapper, new Object[] { Name });
	}catch (EmptyResultDataAccessException e) {
		return null;
	}
}
	private static final class retrieveMapper implements RowMapper<AddressData> {

		
		public String schema() {
			return " c.city_name as cityName,s.state_name as stateName,co.country_name as countryName" +
					"  FROM b_city c,b_state s,b_country co  WHERE c.parent_code = s.id and s.parent_code = co.id" +
					"  and c.city_name =?";

		}

		@Override
		public AddressData mapRow(final ResultSet rs, final int rowNum)	throws SQLException {
			String city = rs.getString("cityName");
			String state = rs.getString("stateName");
			String country=rs.getString("countryName");
			return new AddressData(city,state,country);
		}
	}

	@Override
	public List<CountryDetails> retrieveCountries() {
		try{
			context.authenticatedUser();
			CountryMapper mapper = new CountryMapper();

			String sql = "select " + mapper.schema();

			return this.jdbcTemplate.query(sql, mapper, new Object[] {});
			}catch (final EmptyResultDataAccessException e) {
				return null;
			}
		}

		private static final class CountryMapper implements RowMapper<CountryDetails> {

			public String schema() {
				return " c.id as id,c.country_name as countryName FROM b_country c";

			}

			@Override
			public CountryDetails mapRow(final ResultSet rs,
					@SuppressWarnings("unused") final int rowNum)
					throws SQLException {

				Long id = rs.getLong("id");
				String countryName = rs.getString("countryName");
			
				return new CountryDetails(id,countryName);

			}
		}

		@Override
		public List<AddressData> retrieveClientAddressDetails(Long clientId) {
			try{
				context.authenticatedUser();
				AddressMapper mapper = new AddressMapper();

				String sql = "select " + mapper.schema()+" where a.is_deleted='n' and a.client_id=?";

				return this.jdbcTemplate.query(sql, mapper, new Object[] {clientId});
				}catch (final EmptyResultDataAccessException e) {
					return null;
				}
			}
		
		@Override
		public Page<AddressDetails> retrieveAllAddresses(SearchSqlQuery searchAddresses){
			try{
				context.authenticatedUser();
				AddressActionMapper mapper=new AddressActionMapper();
				
				StringBuilder sqlBuilder = new StringBuilder(200);
				  sqlBuilder.append("select ");
				  sqlBuilder.append(mapper.schema());
				  String sqlSearch=searchAddresses.getSqlSearch();
				  String extraCriteria = "";
				    if (sqlSearch != null) {
				    	sqlSearch=sqlSearch.trim();
				    	extraCriteria = "  where country_name like '%"+sqlSearch+"%' "; 
				    }
				    
				    sqlBuilder.append(extraCriteria);
				    
				    if (searchAddresses.isLimited()) {
			            sqlBuilder.append(" limit ").append(searchAddresses.getLimit());
			        }
				    if (searchAddresses.isOffset()) {
			            sqlBuilder.append(" offset ").append(searchAddresses.getOffset());
			        }
				    return this.paginationHelper.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()",sqlBuilder.toString(),
			                new Object[] {},mapper);
			}catch (final EmptyResultDataAccessException e) {
				return null;
			}
			
		 
	   }
		
		public static final class AddressActionMapper implements RowMapper<AddressDetails>{
			public String schema() {
				
				return "country.id as countryId,country.country_code as countryCode,country.country_name as counryName,"+
						"state.id as stateId,state.state_code as stateCode,state.state_name as stateName,"+
						"city.id as cityId,city.city_code as cityCode,city.city_name as cityName "+ 
						"from b_country country "+  
						"left join b_state state on (state.parent_code=country.id and state.is_delete='N') "+
						"left join  b_city city on (city.parent_code=state.id and state.is_delete='N' and city.is_delete='N')"+
						"where country.is_active='Y'";
				
			}
			@Override
			public AddressDetails mapRow(final ResultSet rs,@SuppressWarnings("unused") final int rowNum)  throws SQLException {
				
					String countryCode=rs.getString("countryCode");
					String countryName=rs.getString("counryName");
					String cityCode=rs.getString("cityCode");
					String cityName=rs.getString("cityName");
					String stateCode=rs.getString("stateCode");
					String stateName=rs.getString("stateName");
					Long cityId=rs.getLong("cityId");
					Long countryId=rs.getLong("countryId");
					Long stateId=rs.getLong("stateId");
					return new AddressDetails(countryCode,countryName,cityCode,cityName,stateCode,stateName,countryId,stateId,cityId);
				}
			}


}



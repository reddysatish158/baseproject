package org.mifosplatform.organisation.randomgenerator.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ws.rs.core.StreamingOutput;

import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.dataqueries.data.GenericResultsetData;
import org.mifosplatform.infrastructure.dataqueries.data.ResultsetColumnHeaderData;
import org.mifosplatform.infrastructure.dataqueries.data.ResultsetRowData;
import org.mifosplatform.infrastructure.dataqueries.service.GenericDataService;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.randomgenerator.data.RandomGeneratorData;
import org.mifosplatform.organisation.randomgenerator.domain.PinCategory;
import org.mifosplatform.organisation.randomgenerator.domain.PinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class RandomGeneratorReadPlatformServiceImpl implements RandomGeneratorReadPlatformService {

	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;
	private final GenericDataService genericDataService;

	@Autowired
	public  RandomGeneratorReadPlatformServiceImpl(final PlatformSecurityContext context, final TenantAwareRoutingDataSource dataSource,
			final GenericDataService genericDataService) {
		this.context = context;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.genericDataService=genericDataService;
	}
	
	
	@Override
	public String retrieveIndividualPin(String pinNo) {
		  try{
	        	
				context.authenticatedUser();
				String sql;
				retrieveMapper mapper=new retrieveMapper();
			    sql = "SELECT  " + mapper.schema();
			
				return this.jdbcTemplate.queryForObject(sql, mapper, new Object[] { pinNo });
			}catch (EmptyResultDataAccessException e) {
				return null;
			}
	}
	
private static final class retrieveMapper implements RowMapper<String> {

		
		public String schema() {
			return " d.pin_no as pinNo from b_pin_details d where d.pin_no =?";

		}

		@Override
		public String mapRow(final ResultSet rs, final int rowNum)	throws SQLException {
			String pinNo = rs.getString("pinNo");
			
			return pinNo;
		}
	}

   @Override
   public List<EnumOptionData> pinCategory() {
	
		EnumOptionData Numeric = RandomGeneratorEnumeration.enumOptionData(PinCategory.NUMERIC);
		EnumOptionData Alpha =RandomGeneratorEnumeration.enumOptionData(PinCategory.ALPHA);
		EnumOptionData AlphaNumeric = RandomGeneratorEnumeration.enumOptionData(PinCategory.ALPHANUMERIC);
		List<EnumOptionData> categotyType = Arrays.asList(Numeric,Alpha,AlphaNumeric);
		return categotyType;
}
   @Override
   public List<EnumOptionData> pinType() {
	
		EnumOptionData Value = RandomGeneratorEnumerationType.enumOptionData(PinType.VALUE);
		EnumOptionData Duration =RandomGeneratorEnumerationType.enumOptionData(PinType.DURATION);
		//EnumOptionData AlphaNumeric = RandomGeneratorEnumerationType.enumOptionData(PinType.PRODUCTION);
		List<EnumOptionData> categotyType = Arrays.asList(Value,Duration);
		return categotyType;
}


	@Override
	public List<RandomGeneratorData> getAllData() {
				try{
			    	
					context.authenticatedUser();
					String sql;
					retrieveRandomMapper mapper=new retrieveRandomMapper();
				    sql = "SELECT  " + mapper.schema();
				
					return this.jdbcTemplate.query(sql, mapper, new Object[] {});
				}catch (EmptyResultDataAccessException e) {
					return null;
				}
			}
			
	private static final class retrieveRandomMapper implements RowMapper<RandomGeneratorData> {
			
			
			public String schema() {
				return "m.id as id, m.batch_name as batchName,m.batch_description as batchDescription,m.length as length," +
						"m.begin_with as beginWith,m.pin_category as pinCategory,m.quantity as quantity," +
						"m.serial_no as serialNo,m.pin_type as pinType,m.pin_value as pinValue,m.expiry_date as expiryDate, " +
						"case m.pin_type when 'VALUE' then p.plan_code=null when 'PRODUCT' then p.plan_code end as planCode, "+
						"m.is_processed as isProcessed from b_pin_master m  "+
						"left join b_plan_master p on m.pin_value=p.id";

			
			}
			
			@Override
			public RandomGeneratorData mapRow(final ResultSet rs, final int rowNum)	throws SQLException {
				
				 Long id=rs.getLong("id");
				 String batchName=rs.getString("batchName");
				 String batchDescription=rs.getString("batchDescription");
				 Long length=rs.getLong("length");
				 String pinCategory=rs.getString("pinCategory");
				 String pinType=rs.getString("pinType");
				 Long quantity=rs.getLong("quantity");
				 String serial=rs.getString("serialNo");
				 Date expiryDate=rs.getDate("expiryDate");
				 String beginWith=rs.getString("beginWith");
				 String pinValue=rs.getString("pinValue");
				 String planCode=rs.getString("planCode");
				 String isProcessed=rs.getString("isProcessed");	
				 
				return new RandomGeneratorData(batchName,batchDescription,length,pinCategory,pinType,quantity,serial,expiryDate,beginWith,pinValue,id,planCode,isProcessed);

			}
		}
	
	
	@Override
	public Long retrieveMaxNo(Long minNo,Long maxNo) {
		  try{
				context.authenticatedUser();
				String sql;
				Mapper mapper=new Mapper();
			    sql = "SELECT  " + mapper.schema();
			
				return this.jdbcTemplate.queryForObject(sql, mapper, new Object[] { minNo,maxNo });
			}catch (EmptyResultDataAccessException e) {
				return new Long(0);
			}
	}
	
private static final class Mapper implements RowMapper<Long> {

		
		public String schema() {
			return "max(m.serial_no) as serialNo from b_pin_details m where serial_no BETWEEN ? AND ?";

		}

		@Override
		public Long mapRow(final ResultSet rs, final int rowNum)	throws SQLException {
			Long serialNo = rs.getLong("serialNo");
			
			return serialNo;
		}
	}

@Override
public StreamingOutput retrieveVocherDetailsCsv(final Long batchId) {
	this.context.authenticatedUser();
	  return new StreamingOutput() {
		  
		  @Override
          public void write(final OutputStream out) {
	try{
	
	final String sql="SELECT pm.id AS batchId, pd.serial_no AS serialNum, pd.pin_no AS hiddenNum FROM b_pin_master pm, b_pin_details pd" +
			" WHERE pd.pin_id = pm.id AND pm.id ="+batchId+" order by serialNum desc ";
	GenericResultsetData result = genericDataService.fillGenericResultSet(sql);
	StringBuffer sb = generateCsvFileBuffer(result);
	 InputStream in = new ByteArrayInputStream(sb.toString().getBytes("UTF-8"));
	   byte[] outputByte = new byte[4096];
       Integer readLen = in.read(outputByte, 0, 4096);
       while (readLen != -1) {
           out.write(outputByte, 0, readLen);
           readLen = in.read(outputByte, 0, 4096);
       }
	}catch(Exception e){
		
		  throw new PlatformDataIntegrityException("error.msg.exception.error", e.getMessage());
	}
		  }
};
}
private StringBuffer generateCsvFileBuffer(final GenericResultsetData result) {
    StringBuffer writer = new StringBuffer();

    List<ResultsetColumnHeaderData> columnHeaders = result.getColumnHeaders();
  //  logger.info("NO. of Columns: " + columnHeaders.size());
    Integer chSize = columnHeaders.size();
    for (int i = 0; i < chSize; i++) {
        writer.append('"' + columnHeaders.get(i).getColumnName() + '"');
        if (i < (chSize - 1)) writer.append(",");
    }
    writer.append('\n');

    List<ResultsetRowData> data = result.getData();
    List<String> row;
    Integer rSize;
    // String currCol;
    String currColType;
    String currVal;
    String doubleQuote = "\"";
    String twoDoubleQuotes = doubleQuote + doubleQuote;
    //logger.info("NO. of Rows: " + data.size());
    for (int i = 0; i < data.size(); i++) {
        row = data.get(i).getRow();
        rSize = row.size();
        for (int j = 0; j < rSize; j++) {
            // currCol = columnHeaders.get(j).getColumnName();
            currColType = columnHeaders.get(j).getColumnType();
            currVal = row.get(j);
            if (currVal != null) {
                if (currColType.equals("DECIMAL") || currColType.equals("DOUBLE") || currColType.equals("BIGINT")
                        || currColType.equals("SMALLINT") || currColType.equals("INT"))
                    writer.append(currVal);
                else
                    writer.append('"' + genericDataService.replace(currVal, doubleQuote, twoDoubleQuotes) + '"');

            }
            if (j < (rSize - 1)) writer.append(",");
        }
        writer.append('\n');
    }

    return writer;
}

}








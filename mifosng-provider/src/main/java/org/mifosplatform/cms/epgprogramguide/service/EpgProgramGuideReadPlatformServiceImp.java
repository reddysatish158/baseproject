package org.mifosplatform.cms.epgprogramguide.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.mifosplatform.cms.epgprogramguide.data.EpgProgramGuideData;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class EpgProgramGuideReadPlatformServiceImp implements
		EpgProgramGuideReadPlatformService {

	
	private final JdbcTemplate jdbcTemplate;
	private PlatformSecurityContext context; 
	
	@Autowired
	public EpgProgramGuideReadPlatformServiceImp(final TenantAwareRoutingDataSource dataSource, final PlatformSecurityContext context) {
		this.context = context;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Transactional
	@Override
	public List<EpgProgramGuideData> retrivePrograms(String channelName,final Date  progDate) {
	
		/*String sql = "select p.channel_name as channelName, p.channel_icon as channelIcon, p.program_date as programDate, p.start_time as startTime, " +
				"p.stop_time as stopTime,p.program_title as programTitle, p.program_desc as programDescription, p.type as type, p.genre as genre" +
				" from b_program_guide p where p.channel_name=?  AND date(program_date) = date(now()) and time_format(start_time,'%H:%i') >=time_format(now(),'%H:%i')  limit ?,5";*/
		String sql="SELECT p.channel_name AS channelName,p.channel_icon AS channelIcon,p.program_date AS programDate,p.start_time AS startTime," +
				"p.stop_time AS stopTime,p.program_title AS programTitle, p.program_desc AS programDescription, p.type AS type, p.genre AS genre FROM b_program_guide p" +
				" WHERE p.channel_name = ?   AND Date_format( program_date,'%Y-%m-%d') =  Date_format(?,'%Y-%m-%d')";
		ChannelRowMapper channelRowMapper = new ChannelRowMapper();
		return jdbcTemplate.query(sql, channelRowMapper,new Object[]{channelName,progDate});
	}
	
	
	
	private static final class ChannelRowMapper implements RowMapper<EpgProgramGuideData>{
		@Override
		public EpgProgramGuideData mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			String channelName = rs.getString("channelName");
			String channelIcon = rs.getString("channelIcon");
			String programDate = rs.getString("programDate");
			String startTime = rs.getString("startTime");
			String stopTime = rs.getString("stopTime");
			
			/*
			 * String programDate = JdbcSupport.getString(rs,"programDate");
			String startTime = JdbcSupport.getString(rs, "startTime");
			String stopTime = JdbcSupport.getString(rs,"stopTime");
			 * */
			
			String programTitle = rs.getString("programTitle");
			String programDescription = rs.getString("programDescription");
			String type = rs.getString("type");
			String genre = rs.getString("genre");
			
			
			return new EpgProgramGuideData(channelName, channelIcon, programDate, startTime, stopTime, programTitle, programDescription, type, genre);
		}
		
	}



}

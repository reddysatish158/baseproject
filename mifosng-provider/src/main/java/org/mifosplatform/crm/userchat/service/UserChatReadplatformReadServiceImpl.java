
package org.mifosplatform.crm.userchat.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.crm.userchat.data.UserChatData;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserChatReadplatformReadServiceImpl implements UserChatReadplatformReadService{

	private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;


    
    @Autowired
    public UserChatReadplatformReadServiceImpl(final PlatformSecurityContext context, final TenantAwareRoutingDataSource dataSource) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);


    }


    @Transactional
	@Override
	public List<UserChatData> getUserChatDetails() {
        
    	try{
             
    		final String userName=this.context.authenticatedUser().getUsername();
    		UserChatDataMapper mapper = new UserChatDataMapper();
    		String sql = "select " + mapper.schema()+" where u.username=? and u.is_deleted='N'  order by u.message_date desc ";
    		return this.jdbcTemplate.query(sql, mapper, new Object[] { userName });
    		
    		
    	}catch(EmptyResultDataAccessException accessException){
    		return null;
    	}
		
    
    }
    
    private static final class UserChatDataMapper implements RowMapper<UserChatData> {

		public String schema() {
			return " u.id AS id,u.username AS userName,u.message_date AS messageDate,u.message AS message, u.createdby_user AS createdbyUser,u.is_read As isRead" +
					" FROM b_userchat u ";

		}

		@Override
		public UserChatData mapRow(final ResultSet rs,
				@SuppressWarnings("unused") final int rowNum)
				throws SQLException {
			Long id = rs.getLong("id");
			String userName = rs.getString("userName");
			String message = rs.getString("message");
			String createdByUser = rs.getString("createdbyUser");
			LocalDate messageDate=JdbcSupport.getLocalDate(rs, "messageDate");
			Boolean isRead =rs.getBoolean("isRead");
			return new UserChatData(id,userName,messageDate,message,createdByUser,isRead);

		}
	}

    private static final class UserUnreadMessageMapper implements RowMapper<Long> {

		public String schema() {
			return " us.username as username, count(*) as unread from b_userchat us ";

		}

		@Override
		public Long mapRow(final ResultSet rs,
				@SuppressWarnings("unused") final int rowNum)
				throws SQLException {
			
			Long unread = rs.getLong("unread");
			
			return unread;

		}
	}

    
	@Override
	public List<UserChatData> getUserSentMessageDetails() {
        
    	try{
             
    		final String userName=this.context.authenticatedUser().getUsername();
    		UserChatDataMapper mapper = new UserChatDataMapper();
    		String sql = "select " + mapper.schema()+" where u.createdby_user = ?  order by u.message_date desc";
    		return this.jdbcTemplate.query(sql, mapper, new Object[] { userName });
    	}catch(EmptyResultDataAccessException accessException){
    		return null;
    	}
		
     
    }


	@Override
	public Long getUnreadMessages(String userName) {
		try{
            
    		
    		UserUnreadMessageMapper mapper = new UserUnreadMessageMapper();
    		String sql = "select " + mapper.schema()+" where us.username = ?  and  us.is_read='N'";
    		return this.jdbcTemplate.queryForObject(sql, mapper, new Object[] { userName });
    		
    	 }catch(EmptyResultDataAccessException accessException){
    		return null;
    	}
	}

}
